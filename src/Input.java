import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.NormalResetRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

import java.io.IOException;
import java.util.ArrayList;

public class Input extends Thread {
    private final RequestPool globalRequest;
    private final ArrayList<Elevator> elevators;
    private static int count = 0;
    private static int sum = 0;

    public Input(ArrayList<Elevator> elevators, RequestPool globalRequest) {
        this.elevators = elevators;
        this.globalRequest = globalRequest;
    }

    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest(); // 读取新请求
            if (request == null) {
                // 通知Scheduler结束
                Scheduler.changeSignal();
                synchronized (globalRequest) {
                    globalRequest.notifyAll();
                }
                break;
            } else {
                if (request instanceof NormalResetRequest) { // 处理NormalReset
                    processNormalReset((NormalResetRequest) request);
                } else if (request instanceof DoubleCarResetRequest) { // 处理DoubleCarReset
                    processDoubleReset((DoubleCarResetRequest) request);
                } else if (request instanceof PersonRequest) { // 处理person
                    sum++;
                    synchronized (globalRequest) {
                        globalRequest.addRequest((PersonRequest) request);
                        globalRequest.notifyAll();
                    }
                }
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processNormalReset(NormalResetRequest resetRequest) {
        int id = resetRequest.getElevatorId();
        int capacity = resetRequest.getCapacity();
        int moveTime = (int) (resetRequest.getSpeed() * 1000);
        for (int i = 0; i < elevators.size(); i++) {
            if (elevators.get(i).getID() == id) {
                Elevator elevator = elevators.get(i);
                synchronized (elevator.getOutRequest()) {
                    elevator.setReset("normalReset", capacity, moveTime,-1);
                    elevator.getOutRequest().notifyAll();
                }
                break;
            }
        }
    }

    public void processDoubleReset(DoubleCarResetRequest resetRequest) {
        int id = resetRequest.getElevatorId();
        int capacity = resetRequest.getCapacity();
        int moveTime = (int) (resetRequest.getSpeed() * 1000);
        int transferFloor = resetRequest.getTransferFloor();
        for (int i = 0; i < elevators.size(); i++) {
            if (elevators.get(i).getID() == id) {
                Elevator elevator = elevators.get(i);
                synchronized (elevator.getOutRequest()) {
                    elevator.setReset("doubleReset",capacity, moveTime, transferFloor);
                    elevator.getOutRequest().notifyAll();
                }
                break;
            }
        }
    }

    public static void addCount() {
        count++;
    }

    public static int getCount() {
        return count;
    }

    public static int getSum() {
        return sum;
    }
}

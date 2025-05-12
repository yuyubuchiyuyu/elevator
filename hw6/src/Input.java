import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;
import com.oocourse.elevator2.ResetRequest;

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
                if (request instanceof ResetRequest) { // 处理reset
                    processReset((ResetRequest) request);
                }
                if (request instanceof PersonRequest) { // 处理person
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

    public void processReset(ResetRequest resetRequest) {
        int id = resetRequest.getElevatorId();
        int capacity = resetRequest.getCapacity();
        int moveTime = (int) (resetRequest.getSpeed() * 1000);
        resetElevator(elevators.get(id - 1), capacity, moveTime);
    }

    public void resetElevator(Elevator elevator, int capacity, int moveTime) {
        synchronized (elevator.getOutRequest()) {
            elevator.reset(capacity, moveTime);
            elevator.getOutRequest().notifyAll();
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

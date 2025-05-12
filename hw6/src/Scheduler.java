import com.oocourse.elevator2.PersonRequest;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class Scheduler extends Thread {
    private final ArrayList<Elevator> elevators;
    private final RequestPool globalRequest;
    private static Integer endSignal = 0;

    public Scheduler(ArrayList<Elevator> elevators, RequestPool globalRequest) {
        this.elevators = elevators;
        this.globalRequest = globalRequest;
    }

    public void run() {
        while (true) {
            synchronized (globalRequest) {
                while (globalRequest.getSize() > 0) {
                    processPerson(globalRequest.getRequest(0));
                    globalRequest.removeRequest(globalRequest.getRequest(0));
                }
                globalRequest.notifyAll();
            }
            if (globalRequest.getSize() == 0) {
                if (endSignal == 1 && judgeReset() == 1
                        && Input.getCount() == Input.getSum()) {
                    // 外部输入结束，所有电梯都不处于重置状态，且所有请求被处理完，通知电梯结束
                    for (int i = 0; i < 6; i++) {
                        elevators.get(i).changSignal();
                        synchronized (elevators.get(i).getOutRequest()) {
                            elevators.get(i).getOutRequest().notifyAll();
                        }
                    }
                    return;
                } else {
                    synchronized (globalRequest) {
                        try {
                            globalRequest.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public int judgeReset() { // 判断所有电梯是否处于不重置
        for (int i = 0; i < 6; i++) {
            if (elevators.get(i).getReset() == 1) {
                return 0;
            }
        }
        return 1; // 所有电梯都不处于重置状态
    }

    public static void changeSignal() {
        endSignal = 1;
    }

    public void processPerson(PersonRequest personRequest) {
        int flag1 = way1(personRequest);
        int flag2 = way2(personRequest);
        int flag3 = way3(personRequest);
        int flag4 = way4(personRequest);
        if (flag1 > -1) {
            addRequest(flag1, personRequest);
        } else if (flag4 > -1) {
            addRequest(flag4, personRequest);
        } else if (flag2 > -1) {
            addRequest(flag2, personRequest);
        } else if (flag3 > -1) {
            addRequest(flag3, personRequest);
        }
    }

    public void addRequest(int flag, PersonRequest request) {
        Elevator elevator = elevators.get(flag);
        synchronized (elevator.getOutRequest()) {
            elevator.addRequest(request);
            elevator.getOutRequest().notifyAll();
        }
    }

    public int way1(PersonRequest request) { // 方案一，请求与电梯同向且电梯没满(优先既近又快的)，可能不存在这样的电梯，flag可能为-1
        int flag = -1;
        int pos = 0;
        int direction = Integer.compare(request.getToFloor() - request.getFromFloor(), 0); // 该人的方向
        int minTemp = 10000000;
        while (pos < 6) {
            Elevator elevator = elevators.get(pos % 6);
            if ((direction == elevator.getDirection()
                    || elevator.getDirection() == 0) // 请求与电梯同向
                    && elevator.getRequestSize() < elevator.getCapacity()) { // 电梯没满
                if (elevator.getDirection() == 0 ||
                        (direction == 1 && request.getFromFloor() > elevator.getNowFloor()) ||
                        (direction == -1 && request.getFromFloor() < elevator.getNowFloor())) {
                    int temp = abs(elevator.getReset()) * 1200 + elevator.getSpeed() * (
                            abs(request.getFromFloor() - elevator.getNowFloor())
                                    + abs(request.getFromFloor() - request.getToFloor()));
                    if (temp < minTemp) {
                        minTemp = temp;
                        flag = pos % 6;
                    }
                }
            }
            pos++;
        }
        return flag;
    }

    public int way2(PersonRequest request) { // 方案二：出发地在电梯运行方向上（优先既近又快的）
        int flag = -1;
        int pos = 0;
        int minTemp = 10000000;
        while (pos < 6) {
            Elevator elevator = elevators.get(pos % 6);
            if (elevator.getRequestSize() < elevator.getCapacity()) { // 电梯没满
                if (elevator.getDirection() == 0 ||
                        (elevator.getDirection() == 1
                                && request.getFromFloor() > elevator.getNowFloor()) ||
                        (elevator.getDirection() == -1
                                && request.getFromFloor() < elevator.getNowFloor())) {
                    int temp = abs(elevator.getReset()) * 1200 + elevator.getSpeed() * (
                            abs(request.getFromFloor() - elevator.getNowFloor())
                                    + abs(request.getFromFloor() - request.getToFloor()));
                    if (temp < minTemp) {
                        minTemp = temp;
                        flag = pos % 6;
                    }
                }
            }
            pos++;
        }
        return flag;
    }

    public int way3(PersonRequest request) { // 方案三：寻找既近又快的电梯
        int flag = -1;
        int pos = 0;
        int minTemp = 10000000;
        while (pos < 6) {
            Elevator elevator = elevators.get(pos % 6);
            if (elevator.getRequestSize() < elevator.getCapacity()) { // 电梯没满
                int temp = abs(elevator.getReset()) * 1200 + elevator.getSpeed() * (
                        abs(request.getFromFloor() - elevator.getNowFloor())
                                + abs(request.getFromFloor() - request.getToFloor()));
                if (temp < minTemp) {
                    minTemp = temp;
                    flag = pos % 6;
                }
            }
            pos++;
        }
        return flag;
    }

    public int way4(PersonRequest request) { // 方案四，寻找现有请求最少的电梯(优先既近又快的)
        int flag = -1;
        int pos = 0;
        int least = 150;
        int minTemp = 10000000;
        while (pos < 6) {
            Elevator elevator = elevators.get(pos % 6);
            int temp = abs(elevator.getReset()) * 1200 + elevator.getSpeed() * (
                    abs(request.getFromFloor() - elevator.getNowFloor())
                            + abs(request.getFromFloor() - request.getToFloor()));
            if (elevator.getRequestSize() < least) {
                least = elevator.getRequestSize();
                minTemp = temp;
                flag = pos % 6;
            } else if (elevator.getRequestSize() == least) {
                if (temp < minTemp) {
                    minTemp = temp;
                    flag = pos % 6;
                }
            }
            pos++;
        }
        return flag;
    }
}

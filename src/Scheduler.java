import com.oocourse.elevator3.PersonRequest;

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
                    for (int i = 0; i < elevators.size(); i++) {
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
        for (int i = 0; i < elevators.size(); i++) {
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
        int flag11 = mainWay1(personRequest);
        int flag12 = mainWay2(personRequest);
        int flag13 = mainWay3(personRequest);
        int flag21 = way1(personRequest);
        int flag22 = way2(personRequest);
        int flag23 = way3(personRequest);
        if (flag11 > -1) {
            addRequest(flag11, personRequest);
        } else if (flag12 > -1) {
            addRequest(flag12, personRequest);
        } else if (flag13 > -1) {
            addRequest(flag13, personRequest);
        } else if (flag21 > -1) {
            addRequest(flag21, personRequest);
        } else if (flag22 > -1) {
            addRequest(flag22, personRequest);
        } else if (flag23 > -1) {
            addRequest(flag23, personRequest);
        }
    }

    public void addRequest(int flag, PersonRequest request) {
        Elevator elevator = elevators.get(flag);
        elevator.addRequest(request);
    }

    public int judgeContain(PersonRequest request, int lowFloor, int highFloor) { // 判断该请求是否在两层之间
        if ((request.getFromFloor() >= lowFloor && request.getFromFloor() <= highFloor)
                && (request.getToFloor() >= lowFloor && request.getToFloor() <= highFloor)) {
            return 1;
        } else {
            return 0;
        }
    }

    public int mainWay1(PersonRequest request) { // 请求与电梯同向且电梯没满(优先既近又快的)，可能不存在这样的电梯，flag可能为-1
        int flag = -1;
        int pos = 0;
        int direction = Integer.compare(request.getToFloor() - request.getFromFloor(), 0); // 该人的方向
        int minTemp = 10000000;
        while (pos < elevators.size()) {
            Elevator elevator = elevators.get(pos);
            if (elevator.getEnd() == 0) {
                if (elevator.getDoubleReset() == 1 &&
                        (judgeContain(request, 1, elevator.getNewTransferFloor()) == 1
                                || judgeContain(request, elevator.getNewTransferFloor(), 11) == 1)
                ) {
                    if (elevator.getRequestSize() < elevator.getCapacity()) {
                        int temp = elevator.getDirection() * 1200 + elevator.getSpeed() * (
                                abs(request.getFromFloor() - elevator.getNowFloor())
                                        + abs(request.getFromFloor() - request.getToFloor()));
                        if (temp < minTemp) {
                            minTemp = temp;
                            flag = pos;
                        }
                    }
                } else if (judgeContain(request,
                        elevator.getLowFloor(), elevator.getHighFloor()) == 1) {
                    if ((direction == elevator.getDirection()) // 请求与电梯同向
                            && elevator.getRequestSize() < elevator.getCapacity()) { // 电梯没满
                        if ((direction == 1
                                && request.getFromFloor() > elevator.getNowFloor())
                                || (direction == -1
                                && request.getFromFloor() < elevator.getNowFloor())) {
                            int temp = elevator.getNormalReset() * 1200 + elevator.getSpeed()
                                    * (abs(request.getFromFloor() - elevator.getNowFloor())
                                    + abs(request.getFromFloor() - request.getToFloor()));
                            if (elevator.getType() != 'I' && temp < minTemp) {
                                minTemp = temp;
                                flag = pos;
                            }
                        }
                    }
                }
            }
            pos++;
        }
        return flag;
    }

    public int mainWay2(PersonRequest request) { // 出发地在电梯运行方向上（优先既近又快的）
        int flag = -1;
        int pos = 0;
        int minTemp = 10000000;
        while (pos < elevators.size()) {
            Elevator elevator = elevators.get(pos);
            if (elevator.getEnd() == 0) {
                if (elevator.getDoubleReset() == 1 &&  // 这部电梯即将分裂
                        (judgeContain(request, 1, elevator.getNewTransferFloor()) == 1
                                || judgeContain(request, elevator.getNewTransferFloor(), 11) == 1)
                ) {
                    if (elevator.getRequestSize() < elevator.getCapacity()) { // 电梯没满
                        if ((elevator.getDirection() == 1
                                && request.getFromFloor() > elevator.getNowFloor())
                                || (elevator.getDirection() == -1
                                && request.getFromFloor() < elevator.getNowFloor())) {
                            int temp = abs(elevator.getReset()) * 1200 + elevator.getSpeed() * (
                                    abs(request.getFromFloor() - elevator.getNowFloor())
                                            + abs(request.getFromFloor() - request.getToFloor()));
                            if (temp < minTemp) {
                                minTemp = temp;
                                flag = pos;
                            }
                        }
                    }
                } else if (judgeContain(request,
                        elevator.getLowFloor(), elevator.getHighFloor()) == 1) {
                    if (elevator.getRequestSize() < elevator.getCapacity()) { // 电梯没满
                        if ((elevator.getDirection() == 1
                                && request.getFromFloor() > elevator.getNowFloor())
                                || (elevator.getDirection() == -1
                                && request.getFromFloor() < elevator.getNowFloor())) {
                            int temp = abs(elevator.getReset()) * 1200 + elevator.getSpeed() * (
                                    abs(request.getFromFloor() - elevator.getNowFloor())
                                            + abs(request.getFromFloor() - request.getToFloor()));
                            if (elevator.getType() != 'I' && temp < minTemp) {
                                minTemp = temp;
                                flag = pos;
                            }
                        }
                    }
                }
            }

            pos++;
        }
        return flag;
    }

    public int mainWay3(PersonRequest request) { // 寻找现有请求最少的电梯(优先既近又快的)
        int flag = -1;
        int pos = 0;
        int least = 150;
        int minTemp = 10000000;
        while (pos < elevators.size()) {
            Elevator elevator = elevators.get(pos);
            if (elevator.getEnd() == 0) {
                if (elevator.getDoubleReset() == 1 &&
                        (judgeContain(request, 1, elevator.getNewTransferFloor()) == 1
                                || judgeContain(request, elevator.getNewTransferFloor(), 11) == 1)
                ) {
                    int temp = elevator.getNormalReset() * 1200 + elevator.getSpeed() * (
                            abs(request.getFromFloor() - elevator.getNowFloor())
                                    + abs(request.getFromFloor() - request.getToFloor()));
                    if (elevator.getRequestSize() < least) {
                        least = elevator.getRequestSize();
                        minTemp = temp;
                        flag = pos;
                    } else if (elevator.getRequestSize() == least) {
                        if (temp < minTemp) {
                            minTemp = temp;
                            flag = pos;
                        }
                    }
                } else if (judgeContain(request,
                        elevator.getLowFloor(), elevator.getHighFloor()) == 1
                        && elevator.getType() != 'I') {
                    int temp = elevator.getNormalReset() * 1200 + elevator.getSpeed() * (
                            abs(request.getFromFloor() - elevator.getNowFloor())
                                    + abs(request.getFromFloor() - request.getToFloor()));
                    if (elevator.getRequestSize() < least) {
                        least = elevator.getRequestSize();
                        minTemp = temp;
                        flag = pos;
                    } else if (elevator.getRequestSize() == least) {
                        if (temp < minTemp) {
                            minTemp = temp;
                            flag = pos;
                        }
                    }
                }

            }
            pos++;
        }
        return flag;
    }

    public int way1(PersonRequest request) { // 请求与电梯同向且电梯没满(优先既近又快的)，可能不存在这样的电梯，flag可能为-1
        int flag = -1;
        int pos = 0;
        int direction = Integer.compare(request.getToFloor() - request.getFromFloor(), 0); // 该人的方向
        int minTemp = 10000000;
        while (pos < elevators.size()) {
            Elevator elevator = elevators.get(pos);
            if (elevator.getEnd() == 0 && request.getFromFloor() >= elevator.getLowFloor()
                    && request.getFromFloor() <= elevator.getHighFloor()) {
                if ((direction == elevator.getDirection()) // 请求与电梯同向
                        && elevator.getRequestSize() < elevator.getCapacity()) { // 电梯没满
                    if ((direction == 1 && request.getFromFloor() > elevator.getNowFloor()) ||
                            (direction == -1 && request.getFromFloor() < elevator.getNowFloor())) {
                        int temp = elevator.getReset() * 1200 + elevator.getSpeed() * (
                                abs(request.getFromFloor() - elevator.getNowFloor())
                                        + abs(request.getFromFloor() - request.getToFloor()));
                        if (temp < minTemp) {
                            minTemp = temp;
                            flag = pos;
                        }
                    }
                }
            }
            pos++;
        }
        return flag;
    }

    public int way2(PersonRequest request) { // 出发地在电梯运行方向上（优先既近又快的）
        int flag = -1;
        int pos = 0;
        int minTemp = 10000000;
        while (pos < elevators.size()) {
            Elevator elevator = elevators.get(pos);
            if (elevator.getEnd() == 0 && request.getFromFloor() >= elevator.getLowFloor()
                    && request.getFromFloor() <= elevator.getHighFloor()) {
                if (elevator.getRequestSize() < elevator.getCapacity()) { // 电梯没满
                    if ((elevator.getDirection() == 1
                            && request.getFromFloor() > elevator.getNowFloor())
                            || (elevator.getDirection() == -1
                            && request.getFromFloor() < elevator.getNowFloor())) {
                        int temp = abs(elevator.getReset()) * 1200 + elevator.getSpeed() * (
                                abs(request.getFromFloor() - elevator.getNowFloor())
                                        + abs(request.getFromFloor() - request.getToFloor()));
                        if (temp < minTemp) {
                            minTemp = temp;
                            flag = pos;
                        }
                    }
                }
            }
            pos++;
        }
        return flag;
    }

    public int way3(PersonRequest request) { // 寻找现有请求最少的电梯(优先既近又快的)
        int flag = -1;
        int pos = 0;
        int least = 150;
        int minTemp = 10000000;
        while (pos < elevators.size()) {
            Elevator elevator = elevators.get(pos);
            if (elevator.getEnd() == 0 && request.getFromFloor() >= elevator.getLowFloor()
                    && request.getFromFloor() <= elevator.getHighFloor()) {
                int temp = abs(elevator.getReset()) * 1200 + elevator.getSpeed() * (
                        abs(request.getFromFloor() - elevator.getNowFloor())
                                + abs(request.getFromFloor() - request.getToFloor()));
                if (elevator.getRequestSize() < least) {
                    least = elevator.getRequestSize();
                    minTemp = temp;
                    flag = pos;
                } else if (elevator.getRequestSize() == least) {
                    if (temp < minTemp) {
                        minTemp = temp;
                        flag = pos;
                    }
                }
            }
            pos++;
        }
        return flag;
    }
}

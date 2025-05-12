import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.TimableOutput;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Elevator extends Thread {
    private final Integer id;
    private Integer moveTime = 400;
    private final Integer openTime = 200;
    private final Integer closeTime = 200;
    private Integer capacity = 6;
    private Integer nowFloor = 1;

    private Integer direction = 0; // 0停止，1上行，-1下行

    private final RequestPool outRequest = new RequestPool(); // 电梯外面的请求
    private final RequestPool inRequest = new RequestPool();// 电梯里面的请求
    private final RequestPool requestPool = new RequestPool();

    private Integer endSignal = 0; // 输入结束信号
    private Integer doorState = 0; //电梯门的状态，0关闭，1开着
    private long arrivePoint;
    private long leavePoint;
    private long openPoint;
    private long closePoint;
    private int reset = 0; // 0表示不重置，1表示开启重置
    private int count = 0;
    private int newCapacity;
    private int newMoveTime;
    private final RequestPool globalRequest;

    public Elevator(Integer id, RequestPool globalRequest) {
        this.id = id;
        this.globalRequest = globalRequest;
    }

    public void Up() {
        nowFloor++;
        direction = 1;
        Arrive();
    }

    public void Down() {
        nowFloor--;
        direction = -1;
        Arrive();
    }

    public void Arrive() {
        arrivePoint = System.currentTimeMillis();
        try {
            sleep(max(moveTime - (arrivePoint - leavePoint), 0));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TimableOutput.println("ARRIVE-" + nowFloor + "-" + id);
        leavePoint = System.currentTimeMillis();
        if (reset == 1) {
            count++;
            if (count == 2) {
                resetBegin();
            }
        }
    }

    public Integer judgeOpen() {
        for (int i = 0; i < inRequest.getSize(); i++) { // 电梯中有人出去，开
            PersonRequest request = inRequest.getRequest(i);
            if (request.getToFloor() == nowFloor) {
                return 1;
            }
        }
        for (int i = 0; i < outRequest.getSize(); i++) { // 电梯未满员，外有人进且同向，开
            PersonRequest request = outRequest.getRequest(i);
            int directionFlag = Integer.compare(request.getToFloor() - nowFloor, 0);
            if (inRequest.getSize() < capacity && request.getFromFloor() == nowFloor
                    && directionFlag == direction) {
                return 1;
            }
        }
        if (inRequest.getSize() == 0 && outRequest.getSize() > 0 &&
                outRequest.getRequest(0).getFromFloor() == nowFloor) { // 空电梯来接人
            return 1;
        }
        return 0;
    }

    public void open_1() {
        // 电梯中有人出去
        for (int i = 0; i < inRequest.getSize(); i++) {
            PersonRequest request = inRequest.getRequest(i);
            if (request.getToFloor() == nowFloor) {
                TimableOutput.println("OUT-" +
                        request.getPersonId() + '-' + nowFloor + "-" + id);
                Input.addCount();
                synchronized (globalRequest) {
                    globalRequest.notifyAll();
                }
                inRequest.removeRequest(request);
                i--;
            }
        }
    }

    public void open_2() {
        // 电梯里未满员，电梯外有人进且同向 或者 电梯空，电梯外有人进且同向
        for (int i = 0; i < outRequest.getSize(); i++) {
            PersonRequest request = outRequest.getRequest(i);
            int directionFlag = Integer.compare(request.getToFloor() - nowFloor, 0);
            if (inRequest.getSize() < capacity && request.getFromFloor() == nowFloor
                    && directionFlag == direction) {
                TimableOutput.println("IN-"
                        + request.getPersonId() + '-' + nowFloor + "-" + id);
                outRequest.removeRequest(request);
                inRequest.addRequest(request);
                i--;
            }
        }
    }

    public void open_3() {
        // 电梯空，找同向的请求
        if (inRequest.getSize() == 0 && outRequest.getSize() != 0) {
            int flag = -1;
            for (int i = 0; i < outRequest.getSize(); i++) {
                PersonRequest requestTemp = outRequest.getRequest(i);
                int directionFlag = Integer.compare(requestTemp.getFromFloor() - nowFloor, 0);
                if (directionFlag == direction &&
                        abs(requestTemp.getFromFloor() - nowFloor) > 0) {
                    flag = i;
                    break;
                }
            }
            if (flag == -1) {
                open_4();
            }
        }
    }

    public void open_4() {
        // 电梯空，找同层出发的反向请求
        if (inRequest.getSize() == 0 && outRequest.getSize() != 0) {
            int flag = -1;
            int distance = 0;
            for (int i = 0; i < outRequest.getSize(); i++) {
                PersonRequest requestTemp = outRequest.getRequest(i);
                if (requestTemp.getFromFloor() == nowFloor
                        && abs(requestTemp.getToFloor() - nowFloor) > distance) {
                    distance = abs(requestTemp.getToFloor() - nowFloor);
                    flag = i;
                }
            }
            if (flag != -1) {
                direction = -direction; // 确实有反向请求
                PersonRequest request = outRequest.getRequest(flag);
                TimableOutput.println("IN-"
                        + request.getPersonId() + '-' + nowFloor + "-" + id);
                outRequest.removeRequest(request);
                inRequest.addRequest(request);
            }
        }
        if (inRequest.getSize() != 0) {
            // 电梯里有人且未满员，电梯外有人进且同向
            direction = Integer.compare(
                    inRequest.getRequest(0).getToFloor()
                            - nowFloor, 0);
            for (int i = 0; i < outRequest.getSize(); i++) {
                PersonRequest request = outRequest.getRequest(i);
                int directionFlag = Integer.compare(request.getToFloor() - nowFloor, 0);
                if (inRequest.getSize() < capacity && request.getFromFloor() == nowFloor
                        && directionFlag == direction) {
                    TimableOutput.println("IN-"
                            + request.getPersonId() + '-' + nowFloor + "-" + id);
                    outRequest.removeRequest(request);
                    inRequest.addRequest(request);
                    i--;
                }
            }
        }
    }

    public void Open() {
        if (judgeOpen() == 1) {
            // 开门
            TimableOutput.println("OPEN-" + nowFloor + "-" + id);
            openPoint = System.currentTimeMillis();
            doorState = 1;
            open_1();
            open_2();
            open_3();
        }
    }

    public void Close() {
        if (doorState == 1) {
            // 关门
            closePoint = System.currentTimeMillis();
            try {
                sleep(max(openTime + closeTime - (closePoint - openPoint), 0));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            TimableOutput.println("CLOSE-" + nowFloor + "-" + id);
            leavePoint = System.currentTimeMillis();
            doorState = 0;
        }

    }

    public void run() {
        while (true) {
            if (inRequest.getSize() == 0 && reset == 1) {
                resetBegin();
            }
            if (inRequest.getSize() != 0) { // 电梯里有人
                if (inRequest.getRequest(0).getToFloor() > nowFloor) { // 上行送人
                    Up();
                } else if (inRequest.getRequest(0).getToFloor() < nowFloor) { // 下行送人
                    Down();
                }
                Open();
                Close();
            } else { // 电梯里没人
                if (outRequest.getSize() != 0) { // 电梯外有人
                    searchMain();
                    if (outRequest.getRequest(0).getFromFloor() > nowFloor) { // 上行接人
                        Up();
                    } else if (outRequest.getRequest(0).getFromFloor() < nowFloor) { // 下行接人
                        Down();
                    }
                    Open();
                    Close();
                } else { // 电梯外没人
                    if (endSignal == 1) {
                        return;
                    } else {
                        synchronized (outRequest) {
                            try {
                                outRequest.wait();
                                leavePoint = System.currentTimeMillis();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }

    public void addRequest(PersonRequest request) {
        if (reset == 0) {
            printReceive(request);
            outRequest.addRequest(request);
        } else {
            requestPool.addRequest(request);
        }
    }

    public void printReceive(PersonRequest request) {
        TimableOutput.println("RECEIVE-" + request.getPersonId() + "-" + id);
    }

    public void changSignal() {
        endSignal = 1;
    }

    public RequestPool getOutRequest() {
        return outRequest;
    }

    public void searchMain() {
        int distance = 0;
        int flag = -1;
        for (int i = 0; i < outRequest.getSize(); i++) { // 优先接同向状态最远的客人
            PersonRequest requestTemp = outRequest.getRequest(i);
            int directionTemp = Integer.compare(
                    requestTemp.getFromFloor() - nowFloor, 0);
            if (directionTemp == direction &&
                    abs(requestTemp.getFromFloor() - nowFloor) >= distance) {
                distance = abs(requestTemp.getFromFloor() - nowFloor);
                flag = i;
            }
        }
        if (flag != -1) {
            outRequest.changeResult(flag);
            return;
        }
        for (int i = 0; i < outRequest.getSize(); i++) { // 接最远的客人
            PersonRequest requestTemp = outRequest.getRequest(i);
            if (abs(requestTemp.getFromFloor() - nowFloor) > distance) {
                distance = abs(requestTemp.getFromFloor() - nowFloor);
                flag = i;
            }
        }
        if (flag != -1) {
            outRequest.changeResult(flag);
        }
    }

    public int getDirection() {
        return direction;
    }

    public int getNowFloor() {
        return nowFloor;
    }

    public Integer getCapacity() {
        if (reset == 1) {
            return newCapacity;
        } else {
            return capacity;
        }
    }

    public int getRequestSize() {
        return inRequest.getSize() + outRequest.getSize() + requestPool.getSize();
    }

    public void reset(int capacity, int moveTime) {
        reset = 1;
        newCapacity = capacity;
        newMoveTime = moveTime;
        synchronized (outRequest) {
            outRequest.notifyAll();
        }
    }

    public void cleanIn() {
        if (inRequest.getSize() > 0) { // 清空电梯中的人
            TimableOutput.println("OPEN-" + nowFloor + "-" + id);
            openPoint = System.currentTimeMillis();
            while (inRequest.getSize() > 0) {
                PersonRequest request = inRequest.getRequest(0);
                TimableOutput.println("OUT-" +
                        request.getPersonId() + '-' + nowFloor + "-" + id);
                if (request.getToFloor() == nowFloor) {
                    Input.addCount();
                    synchronized (globalRequest) {
                        globalRequest.notifyAll();
                    }
                } else {
                    outRequest.addRequest(
                            new PersonRequest(nowFloor,
                                    request.getToFloor(), request.getPersonId()));
                }
                inRequest.removeRequest(request);
            }
            closePoint = System.currentTimeMillis();
            try {
                sleep(max(openTime + closeTime - (closePoint - openPoint), 0));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            TimableOutput.println("CLOSE-" + nowFloor + "-" + id);
        }
    }

    public void cleanOut() {
        synchronized (globalRequest) {
            while (outRequest.getSize() > 0) {
                PersonRequest request = outRequest.getRequest(0);
                globalRequest.addRequest(request);
                outRequest.removeRequest(request);
            }
            globalRequest.notifyAll();
        }
    }

    public void resetBegin() {
        cleanIn();
        TimableOutput.println("RESET_BEGIN-" + id);
        cleanOut();
        try {
            sleep(max(1200, 0));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TimableOutput.println("RESET_END-" + id);
        capacity = newCapacity;
        moveTime = newMoveTime;
        count = 0;
        reset = 0;
        while (requestPool.getSize() > 0) {
            printReceive(requestPool.getRequest(0));
            outRequest.addRequest(requestPool.getRequest(0));
            requestPool.removeRequest(requestPool.getRequest(0));
        }
        synchronized (globalRequest) {
            globalRequest.notifyAll();
        }
        leavePoint = System.currentTimeMillis();
    }

    public int getReset() {
        return reset;
    }

    public int getSpeed() {
        if (reset == 1) {
            return newMoveTime;
        } else {
            return moveTime;
        }
    }
}

import com.oocourse.elevator3.TimableOutput;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();  // 初始化时间戳
        RequestPool globalRequest = new RequestPool();
        ArrayList<Elevator> elevators = new ArrayList<>();
        for (int i = 0; i < 6; i++) { // 6部电梯
            Elevator elevator = new Elevator(
                    elevators,i + 1,'I',400,6,0,0,
                    new RequestPool(),globalRequest);
            elevators.add(elevator);
            elevator.start();
        }
        Thread scheduler = new Scheduler(elevators,globalRequest);
        scheduler.start();
        Thread input = new Input(elevators,globalRequest);
        input.start();
    }
}
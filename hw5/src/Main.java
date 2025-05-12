import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();  // 初始化时间戳
        ArrayList<Elevator> elevators = new ArrayList<>();
        for (int i = 0; i < 6; i++) { // 6部电梯
            Elevator elevator = new Elevator(i + 1);
            elevators.add(elevator);
            elevator.start();
        }
        Thread scheduler = new Scheduler(elevators);
        scheduler.start();
    }
}
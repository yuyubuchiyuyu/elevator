import com.oocourse.library3.LibraryBookId;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NormalBook {
    private LibraryBookId bookId;
    private int time;
    private LocalDate lastDate;
    private boolean due; // 是否到期

    public NormalBook(LibraryBookId bookId) {
        this.bookId = bookId;
    }

    public boolean judgeTime(LocalDate date) {
        time = time - (int) ChronoUnit.DAYS.between(lastDate, date);
        lastDate = date;
        if (time >= 0 && time <= 4) {
            return true;
        } else {
            return false;
        }
    }

    public void longTime() {
        time = time + 30;
    }

    public boolean judgeOverdue(LocalDate date) {
        time = time - (int) ChronoUnit.DAYS.between(lastDate, date);
        lastDate = date;
        if (time < 0) {
            return false; // 过期
        } else {
            return true; // 没过期
        }
    }

    public void setTime(LocalDate date) {
        this.lastDate = date;
        if (bookId.isTypeB()) {
            this.time = 30;
        } else if (bookId.isTypeC()) {
            this.time = 60;
        }
        this.due = false;
    }

    public boolean judgeDue(String str, LocalDate date) {
        time = time - (int) ChronoUnit.DAYS.between(lastDate, date);
        lastDate = date;
        if (!due && str.equals("open") && time < 0) { // 开馆时已逾期
            due = true;
            return true;
        } else if (!due && str.equals("close") && time == 0) { // 闭馆刚好到期
            due = true;
            return true;
        } else {
            return false;
        }
    }
}

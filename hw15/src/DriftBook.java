import com.oocourse.library3.LibraryBookId;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DriftBook {
    private LibraryBookId bookId;
    private int count; // 借阅次数
    private int time;
    private LocalDate lastDate;
    private Person from;
    private boolean due; // 是否到期

    public DriftBook(LibraryBookId bookId) {
        this.bookId = bookId;
        this.count = 0;
    }

    public void addCount() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public LibraryBookId getBookId() {
        return bookId;
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
        if (bookId.isTypeBU()) {
            this.time = 7;
        } else if (bookId.isTypeCU()) {
            this.time = 14;
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

    public void setFrom(Person person) {
        this.from = person;
    }

    public Person getFrom() {
        return from;
    }
}

import com.oocourse.library2.LibraryBookId;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Appointment {
    private final LibraryBookId bookId;
    private final Person person;
    private Integer count;
    private LocalDate lastDate;

    public Appointment(LibraryBookId bookId, Person person, LocalDate lastDate) {
        this.bookId = bookId;
        this.person = person;
        this.lastDate = lastDate;
    }

    public void setCount(int num) {
        count = num;
    }

    public void setDate(LocalDate date) {
        lastDate = date;
    }

    public Person getPerson() {
        return person;
    }

    public LibraryBookId getBookId() {
        return bookId;
    }

    public int getCount() {
        return count;
    }

    public void subCount(LocalDate date) {
        int daysBetween = (int) ChronoUnit.DAYS.between(lastDate, date);
        count = count - daysBetween;
        lastDate = date;
        if (count < 0) {
            count = 0;
        }
    }
}

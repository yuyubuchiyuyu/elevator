import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryMoveInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Library {
    private final HashMap<LibraryBookId, Integer> bookshelf; // 书架
    private final ArrayList<Appointment> appointment = new ArrayList<>(); // 预约处
    private final HashMap<LibraryBookId, Integer> borrowReturn = new HashMap<>(); // 借还处
    private final ArrayList<Appointment> wait = new ArrayList<>(); // 将要送至预约处

    public Library(HashMap<LibraryBookId, Integer> bookshelf) {
        this.bookshelf = bookshelf;
    }

    public int queried(LibraryBookId book) {
        return bookshelf.get(book);
    }

    public boolean borrowed(LibraryBookId book, Person person) { // borrowed优先级高于ordered
        if (bookshelf.get(book) == 0) { // 无余本在架
            return false;
        } else if (book.isTypeA()) { // A 类图书无法借阅
            return false;
        } else if (judgeReasonableness(book, person)) { // 符合借阅数量限制，借阅成功，用户持有
            bookshelf.replace(book, bookshelf.get(book) - 1);
            if (book.isTypeB()) {
                person.addNumB();
            } else {
                person.addBookC(book.getUid());
            }
            return true;
        } else { // 不符合借阅数量限制，借阅失败，借还处持有
            bookshelf.replace(book, bookshelf.get(book) - 1);
            addToBorrowReturn(book);
            return false;
        }
    }

    public boolean ordered(LibraryBookId book, Person person, LocalDate date) {
        if (book.isTypeA()) { // A 类书无法预约
            return false;
        } else if (judgeReasonableness(book, person)) { // 可以预定
            Appointment app = new Appointment(book, person, date);
            wait.add(app);
            return true;
        }
        return false;
    }

    public void returned(LibraryBookId book, Person person) {
        if (book.isTypeB()) {
            person.subNumB();
        } else {
            person.deleteBookC(book.getUid());
        }
        addToBorrowReturn(book);
    }

    public boolean picked(LibraryBookId book, Person person) {
        for (int i = 0; i < appointment.size(); i++) {
            Appointment app = appointment.get(i);
            Person appPerson = app.getPerson();
            LibraryBookId bookId = app.getBookId();
            if (appPerson.getId().equals(person.getId())
                    && book.equals(bookId)// 存在一本为该用户保留的图书
                    && judgeReasonableness(bookId, person)) { // 且取书后该用户持有的书仍然满足借阅数量限制
                if (bookId.isTypeB()) {
                    person.addNumB();
                } else {
                    person.addBookC(bookId.getUid());
                }
                appointment.remove(app);
                return true;
            }
        }
        return false;
    }

    public boolean judgeReasonableness(LibraryBookId book, Person person) {
        if (book.isTypeB()) {
            if (person.getNumB() > 0) { // 一人同一时刻仅能持有一个 B 类书的副本
                return false;
            } else {
                return true;
            }
        } else { // C类书
            String uid = book.getUid();
            if (person.judgeHasBook(uid)) { // 对于每一个书号，一人同一时刻仅能持有一个具有该书号的书籍副本。
                return false;
            } else {
                return true;
            }
        }
    }

    public void addToBorrowReturn(LibraryBookId book) { // 往借还处加书
        if (borrowReturn.containsKey(book)) {
            borrowReturn.replace(book, borrowReturn.get(book) + 1);
        } else {
            borrowReturn.put(book, 1);
        }
    }

    public List<LibraryMoveInfo> tidyClose() {
        List<LibraryMoveInfo> list = new ArrayList<>();
        // 借还处到书架
        for (LibraryBookId bookId : borrowReturn.keySet()) {
            for (int i = 0; i < borrowReturn.get(bookId); i++) {
                bookshelf.replace(bookId, bookshelf.get(bookId) + 1);
                LibraryMoveInfo moveInfo = new LibraryMoveInfo(bookId, "bro", "bs");
                list.add(moveInfo);
            }
        }
        borrowReturn.clear();
        return list;
    }

    public List<LibraryMoveInfo> tidyOpen(LocalDate date) {
        List<LibraryMoveInfo> list = new ArrayList<>();
        //预约处到书架
        for (int i = 0; i < appointment.size(); i++) {
            Appointment app = appointment.get(i);
            if (app.getCount() > 0) {
                app.subCount(date);
            }
            if (app.getCount() == 0) {
                LibraryBookId bookId = app.getBookId();
                bookshelf.replace(bookId, bookshelf.get(bookId) + 1);
                appointment.remove(app);
                i--;
                LibraryMoveInfo moveInfo = new LibraryMoveInfo(bookId, "ao", "bs");
                list.add(moveInfo);
            }
        }
        // 书架到预约处
        for (int i = 0; i < wait.size(); i++) {
            Appointment app = wait.get(i);
            LibraryBookId bookId = app.getBookId();
            Person person = app.getPerson();
            if (bookshelf.get(bookId) > 0) {
                bookshelf.replace(bookId, bookshelf.get(bookId) - 1);
                app.setCount(5);
                app.setDate(date);
                appointment.add(app);
                wait.remove(app);
                i--;
                LibraryMoveInfo moveInfo = new LibraryMoveInfo(bookId, "bs", "ao", person.getId());
                list.add(moveInfo);
            }
        }
        return list;
    }
}

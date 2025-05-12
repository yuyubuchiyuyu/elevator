import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryMoveInfo;
import com.oocourse.library3.annotation.Trigger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Library {
    private final HashMap<LibraryBookId, Integer> bookshelf; // 书架
    private final ArrayList<Appointment> appointment = new ArrayList<>(); // 预约处
    private final HashMap<LibraryBookId, Integer> borrowReturn = new HashMap<>(); // 借还处
    private final ArrayList<Appointment> wait = new ArrayList<>(); // 将要送至预约处
    private final HashMap<LibraryBookId, DriftBook> driftCorner = new HashMap<>(); // 图书漂流处
    private final HashMap<LibraryBookId, DriftBook> borrowReturnU = new HashMap<>(); // 借还处

    public Library(HashMap<LibraryBookId, Integer> bookshelf) {
        this.bookshelf = bookshelf;
    }

    public int queried(LibraryBookId book) {
        if (book.isTypeA() || book.isTypeB() || book.isTypeC()) {
            return bookshelf.get(book);
        } else {
            if (driftCorner.containsKey(book)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public boolean borrowed(LibraryBookId book, Person person, LocalDate date) { // borrow优先级高于order
        if (book.isTypeA() || book.isTypeAU()) {
            return false;
        } else {
            if (book.isTypeB() || book.isTypeC()) {
                return borrowFromLibrary(book, person, date);
            } else {
                return borrowFromDriftCorner(book, person, date);
            }
        }
    }

    @Trigger(from = "bookshelf", to = {"person", "borrow and return office"})
    public boolean borrowFromLibrary(LibraryBookId book, Person person, LocalDate date) {
        if (bookshelf.get(book) == 0) { // 无余本在架
            return false;
        } else if (book.isTypeA()) { // A 类图书无法借阅
            return false;
        } else if (judgeReasonableness(book, person) && person.getCredit() >= 0) { // 符合借阅数量限制
            bookshelf.replace(book, bookshelf.get(book) - 1);
            if (book.isTypeB()) {
                person.addBookB(book, date);
            } else {
                person.addBookC(book, date);
            }
            return true;
        } else { // 不符合借阅数量限制，借阅失败，借还处持有
            bookshelf.replace(book, bookshelf.get(book) - 1);
            addToBorrowReturn(book);
            return false;
        }
    }

    @Trigger(from = "bookshelf", to = {"person", "borrow and return office"})
    public boolean borrowFromDriftCorner(LibraryBookId book, Person person, LocalDate date) {
        if (!driftCorner.containsKey(book)) {
            return false;
        } else if (book.isTypeAU()) {
            return false;
        } else if (judgeReasonableness(book, person) && person.getCredit() >= 0) {
            DriftBook driftBook = driftCorner.get(book);
            if (book.isTypeBU()) {
                person.addBookBU(driftBook, date);
            } else {
                person.addBookCU(driftBook, date);
            }
            driftCorner.remove(book);
            return true;
        } else { // 不符合借阅数量限制，借阅失败，借还处持有
            DriftBook driftBook = driftCorner.get(book);
            driftCorner.remove(book);
            addToBorrowReturnU(driftBook);
            return false;
        }
    }

    public boolean orderNewBook(LibraryBookId book, Person person, LocalDate date) {
        if (person.getCredit() < 0) { // 若用户的信用积分为负，则此次借阅必定失败
            return false;
        } else {
            if (book.isTypeA() || book.isTypeB() || book.isTypeC()) {
                if (book.isTypeA()) { // A 类书无法预约
                    return false;
                } else if (book.isTypeB()) {
                    if (person.getNumB() > 0) { // 一人同一时刻仅能持有一个 B 类书的副本
                        return false;
                    } else if (!person.getAppB().isEmpty()) { // 用户存在对某B类书正在生效的预约，则该用户对于B类书的预约一定失败。
                        return false;
                    }
                } else { // C类书
                    if (person.judgeHasBook(book) || person.judgeHasBookU(book)) {
                        // 对于每一个书号，一人同一时刻仅能持有一个具有该书号的书籍副本。
                        return false;
                    } else if (person.getAppC().contains(book)) {
                        // 用户存在对某书号C类书正在生效的预约，则该用户对于该书号C类书的预约一定失败
                        return false;
                    }
                }
            } else { // 漂流角图书不能被预约
                return false;
            }
        }
        // 可以预定
        Appointment app = new Appointment(book, person, date);
        wait.add(app);
        person.addApp(book); // 同步
        return true;
    }

    public String returned(LibraryBookId book, Person person, LocalDate date) {
        String str;
        if (person.judgeOverdue(book, date)) {
            str = "not overdue";
            person.addCredit(1); // 用户每次还书期限内还书成功信用分立即+1
        } else {
            str = "overdue";
        }
        if (book.isTypeA() || book.isTypeB() || book.isTypeC()) {
            returnBook(book, person);
        } else {
            returnBookU(book, person);
        }
        return str;
    }

    @Trigger(from = "bookshelf", to = "borrow and return office")
    public void returnBook(LibraryBookId book, Person person) {
        if (book.isTypeB()) {
            person.deleteBookB(book);
        } else {
            person.deleteBookC(book);
        }
        addToBorrowReturn(book);
    }

    @Trigger(from = "bookshelf", to = "borrow and return office")
    public void returnBookU(LibraryBookId book, Person person) {
        DriftBook driftBook = person.getBookU(book);
        if (book.isTypeBU()) {
            person.deleteBookBU(driftBook);
        } else {
            person.deleteBookCU(driftBook);
        }
        driftBook.addCount(); // 借还次数增加 1
        addToBorrowReturnU(driftBook);
    }

    @Trigger(from = "appointment office", to = "person")
    public boolean getOrderedBook(LibraryBookId book, Person person, LocalDate date) {
        if (book.isTypeA() || book.isTypeB() || book.isTypeC()) {
            for (int i = 0; i < appointment.size(); i++) {
                Appointment app = appointment.get(i);
                Person appPerson = app.getPerson();
                LibraryBookId bookId = app.getBookId();
                if (appPerson.getId().equals(person.getId())
                        && book.equals(bookId)// 存在一本为该用户保留的图书
                        && judgeReasonableness(bookId, person)) { // 且取书后该用户持有的书仍然满足借阅数量限制
                    if (bookId.isTypeB()) {
                        person.addBookB(bookId, date);
                    } else {
                        person.addBookC(bookId, date);
                    }
                    appointment.remove(app);
                    person.deleteApp(book); //删除预约
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Trigger(from = "person", to = "book drift corner")
    public boolean donated(LibraryBookId bookId, Person person) {
        if (bookshelf.containsKey(bookId)) {
            return false;
        } else {
            DriftBook driftBook = new DriftBook(bookId);
            driftBook.setFrom(person);
            driftCorner.put(bookId, driftBook);
            person.addCredit(2); // 用户捐献图书成功信用分+2
            return true;
        }
    }

    public boolean renewed(LibraryBookId bookId, Person person, LocalDate date) {
        if (person.getCredit() < 0) {
            return false;
        } else {
            if (bookId.isTypeA() || bookId.isTypeB() || bookId.isTypeC()) {
                int flag1 = 0;
                for (int i = 0; i < appointment.size(); i++) {
                    if (appointment.get(i).getBookId().equals(bookId)) {
                        flag1 = 1;
                        break;
                    }
                }
                int flag2 = 0;
                for (int i = 0; i < wait.size(); i++) {
                    if (wait.get(i).getBookId().equals(bookId)) {
                        flag2 = 1;
                        break;
                    }
                }
                if (bookshelf.containsKey(bookId) && bookshelf.get(bookId) == 0
                        && (flag1 == 1 || flag2 == 1)) {
                    return false;
                }
                if (person.judgeTime(bookId, date)) { // 在时间范围内
                    person.longTime(bookId, date);
                    return true;
                } else {
                    return false;
                }
            } else { // 漂流角图书不能被续借
                return false;
            }
        }
    }

    public boolean judgeReasonableness(LibraryBookId book, Person person) {
        if (book.isTypeB()) {
            if (person.getNumB() > 0) { // 一人同一时刻仅能持有一个 B 类书的副本
                return false;
            } else {
                return true;
            }
        } else if (book.isTypeBU()) {
            if (person.getNumBU() > 0) { // 一人同一时刻仅能持有一个 BU 类书的副本
                return false;
            } else {
                return true;
            }
        } else { // C类书
            if (person.judgeHasBook(book) || person.judgeHasBookU(book)) {
                // 对于每一个书号，一人同一时刻仅能持有一个具有该书号的书籍副本。
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

    public void addToBorrowReturnU(DriftBook book) {
        borrowReturnU.put(book.getBookId(), book);
    }

    public List<LibraryMoveInfo> broToBs() { // 借还处到书架，不涉及credit
        List<LibraryMoveInfo> list = new ArrayList<>();
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

    @Trigger(from = "borrow and return office", to = {"bookshelf", "book drift corner"})
    @Trigger(from = "appointment office", to = "bookshelf")
    @Trigger(from = "bookshelf", to = "appointment office")
    public List<LibraryMoveInfo> tidyOpen(LocalDate date) {
        List<LibraryMoveInfo> list = broToBs(); // 借还处到书架
        //预约处到书架
        for (int i = 0; i < appointment.size(); i++) {
            Appointment app = appointment.get(i);
            if (app.getCount() > 0) {
                app.subCount(date);
            }
            if (app.getCount() == 0) {
                LibraryBookId bookId = app.getBookId();
                Person person = app.getPerson();
                person.addCredit(-3); // 用户在规定的时间内未能取走该书
                person.deleteApp(bookId);
                bookshelf.replace(bookId, bookshelf.get(bookId) + 1);
                appointment.remove(app);
                i--;
                LibraryMoveInfo moveInfo = new LibraryMoveInfo(bookId, "ao", "bs");
                list.add(moveInfo);
            }
        }
        // 借还处到书架/漂流处
        for (LibraryBookId bookId : borrowReturnU.keySet()) {
            DriftBook driftBook = borrowReturnU.get(bookId);
            if (driftBook.getCount() == 2) { // 放回书架
                Person from = driftBook.getFrom();
                from.addCredit(+2); // 书升级为正式图书的时刻捐献该书的用户信用分额外+2
                LibraryBookId.Type type;
                if (bookId.isTypeAU()) {
                    type = LibraryBookId.Type.A;
                } else if (bookId.isTypeBU()) {
                    type = LibraryBookId.Type.B;
                } else {
                    type = LibraryBookId.Type.C;
                }
                LibraryBookId newBookId = new LibraryBookId(type, bookId.getUid());
                bookshelf.put(newBookId, 1);
                LibraryMoveInfo moveInfo = new LibraryMoveInfo(bookId, "bro", "bs");
                list.add(moveInfo);
            } else { // 放回漂流处
                driftCorner.put(bookId, driftBook);
                LibraryMoveInfo moveInfo = new LibraryMoveInfo(bookId, "bro", "bdc");
                list.add(moveInfo);
            }
        }
        borrowReturnU.clear();
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

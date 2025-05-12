import com.oocourse.library3.LibraryBookId;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

public class Person {
    private final String studentId;
    private final HashMap<LibraryBookId, NormalBook> bookB = new HashMap<>();
    private final HashMap<LibraryBookId, NormalBook> bookC = new HashMap<>();
    private final HashMap<LibraryBookId, DriftBook> bookBU = new HashMap<>();
    private final HashMap<LibraryBookId, DriftBook> bookCU = new HashMap<>();
    private final HashSet<LibraryBookId> appB = new HashSet<>();
    private final HashSet<LibraryBookId> appC = new HashSet<>();
    private int credit = 10;

    public Person(String studentId) {
        this.studentId = studentId;
    }

    public String getId() {
        return studentId;
    }

    public int getNumB() {
        return bookB.size();
    }

    public int getNumBU() {
        return bookBU.size();
    }

    public boolean judgeHasBook(LibraryBookId bookId) {
        return bookC.containsKey(bookId);
    }

    public boolean judgeHasBookU(LibraryBookId bookId) {
        return bookCU.containsKey(bookId);
    }

    public void addBookB(LibraryBookId bookId, LocalDate date) {
        NormalBook normalBook = new NormalBook(bookId);
        normalBook.setTime(date);
        bookB.put(bookId, normalBook);
    }

    public void deleteBookB(LibraryBookId bookId) {
        bookB.remove(bookId);
    }

    public void addBookC(LibraryBookId bookId, LocalDate date) {
        NormalBook normalBook = new NormalBook(bookId);
        normalBook.setTime(date);
        bookC.put(bookId, normalBook);
    }

    public void deleteBookC(LibraryBookId bookId) {
        bookC.remove(bookId);
    }

    public void addBookBU(DriftBook book, LocalDate date) {
        book.setTime(date);
        bookBU.put(book.getBookId(), book);
    }

    public void deleteBookBU(DriftBook book) {
        bookBU.remove(book.getBookId());
    }

    public void addBookCU(DriftBook book, LocalDate date) {
        book.setTime(date);
        bookCU.put(book.getBookId(), book);
    }

    public void deleteBookCU(DriftBook book) {
        bookCU.remove(book.getBookId());
    }

    public DriftBook getBookU(LibraryBookId bookId) {
        if (bookBU.containsKey(bookId)) {
            return bookBU.get(bookId);
        } else {
            return bookCU.get(bookId);
        }
    }

    public boolean judgeTime(LibraryBookId bookId, LocalDate date) {
        if (bookId.isTypeB()) {
            return bookB.get(bookId).judgeTime(date);
        } else if (bookId.isTypeC()) {
            return bookC.get(bookId).judgeTime(date);
        } else if (bookId.isTypeBU()) {
            return bookBU.get(bookId).judgeTime(date);
        } else {
            return bookCU.get(bookId).judgeTime(date);
        }
    }

    public void longTime(LibraryBookId bookId, LocalDate date) {
        if (bookId.isTypeB()) {
            bookB.get(bookId).longTime();
        } else if (bookId.isTypeC()) {
            bookC.get(bookId).longTime();
        } else if (bookId.isTypeBU()) {
            bookBU.get(bookId).longTime();
        } else {
            bookCU.get(bookId).longTime();
        }
    }

    public boolean judgeOverdue(LibraryBookId book, LocalDate date) {
        if (book.isTypeB()) {
            return bookB.get(book).judgeOverdue(date);
        } else if (book.isTypeC()) {
            return bookC.get(book).judgeOverdue(date);
        } else if (book.isTypeBU()) {
            return bookBU.get(book).judgeOverdue(date);
        } else {
            return bookCU.get(book).judgeOverdue(date);
        }
    }

    public int getCredit() {
        return credit;
    }

    public void addApp(LibraryBookId book) {
        if (book.isTypeB()) {
            appB.add(book);
        } else {
            appC.add(book);
        }
    }

    public HashSet<LibraryBookId> getAppB() {
        return appB;
    }

    public HashSet<LibraryBookId> getAppC() {
        return appC;
    }

    public void deleteApp(LibraryBookId book) {
        if (appB.contains(book)) {
            appB.remove(book);
        } else {
            appC.remove(book);
        }
    }

    public void addCredit(int num) {
        credit = credit + num;
        if (credit > 20) {
            credit = 20;
        }
    }

    public void processCredit(String str, LocalDate date) {
        for (NormalBook book : bookB.values()) {
            if (book.judgeDue(str, date)) { // 到期
                credit = credit - 2;
            }
        }
        for (NormalBook book : bookC.values()) {
            if (book.judgeDue(str, date)) { // 到期
                credit = credit - 2;
            }
        }
        for (DriftBook book : bookBU.values()) {
            if (book.judgeDue(str, date)) { // 到期
                credit = credit - 2;
            }
        }
        for (DriftBook book : bookCU.values()) {
            if (book.judgeDue(str, date)) { // 到期
                credit = credit - 2;
            }
        }
    }
}

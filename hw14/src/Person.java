import com.oocourse.library2.LibraryBookId;

import java.time.LocalDate;
import java.util.HashMap;

public class Person {
    private final String studentId;
    private final HashMap<LibraryBookId, NormalBook> bookB = new HashMap();
    private final HashMap<LibraryBookId, NormalBook> bookC = new HashMap<>();
    private final HashMap<LibraryBookId, DriftBook> bookBU = new HashMap<>();
    private final HashMap<LibraryBookId, DriftBook> bookCU = new HashMap<>();

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
}

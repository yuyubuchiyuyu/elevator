import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCloseCmd;
import com.oocourse.library3.LibraryCommand;
import com.oocourse.library3.LibraryMoveInfo;
import com.oocourse.library3.LibraryOpenCmd;
import com.oocourse.library3.LibraryQcsCmd;
import com.oocourse.library3.LibraryReqCmd;
import com.oocourse.library3.LibraryRequest;
import com.oocourse.library3.LibrarySystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Operation {
    public static void handleCommand(LibraryCommand command,
                                     Library library, HashMap<String, Person> persons) {
        LocalDate date = command.getDate();
        if (command instanceof LibraryOpenCmd) {
            tidyOpenCredit(persons, date);
            List<LibraryMoveInfo> info = library.tidyOpen(date);
            if (info != null) {
                LibrarySystem.PRINTER.move(date, info);
            }
        } else if (command instanceof LibraryCloseCmd) {
            tidyCloseCredit(persons, date);
            List<LibraryMoveInfo> list = new ArrayList<>();
            LibrarySystem.PRINTER.move(date, list);
        } else if (command instanceof LibraryQcsCmd) {
            String studentId = ((LibraryQcsCmd) command).getStudentId();
            Person person = getPerson(persons,studentId);
            LibrarySystem.PRINTER.info(date, studentId, person.getCredit());
        } else {
            LibraryRequest request = ((LibraryReqCmd) command).getRequest();
            LibraryRequest.Type type = request.getType();
            LibraryBookId bookId = request.getBookId();
            if (type == LibraryRequest.Type.QUERIED) { // 用户查询书籍信息
                int count = library.queried(bookId);
                LibrarySystem.PRINTER.info(date, bookId, count);
            } else {
                String studentId = request.getStudentId();
                Person person = getPerson(persons,studentId);
                boolean result = true;
                String str = null;
                if (type == LibraryRequest.Type.BORROWED) { // 用户借书
                    result = library.borrowed(bookId, person, date);
                } else if (type == LibraryRequest.Type.ORDERED) {
                    result = library.orderNewBook(bookId, person, date);
                } else if (type == LibraryRequest.Type.RETURNED) {
                    str = library.returned(bookId, person, date);
                } else if (type == LibraryRequest.Type.PICKED) {
                    result = library.getOrderedBook(bookId, person, date);
                } else if (type == LibraryRequest.Type.DONATED) {
                    result = library.donated(bookId, person);
                } else if (type == LibraryRequest.Type.RENEWED) {
                    result = library.renewed(bookId, person, date);
                }
                if (result) {
                    if (type == LibraryRequest.Type.RETURNED) {
                        LibrarySystem.PRINTER.accept(command, str);
                    } else {
                        LibrarySystem.PRINTER.accept(command);
                    }
                } else {
                    LibrarySystem.PRINTER.reject(command);
                }
            }
        }
    }

    public static void tidyOpenCredit(HashMap<String,Person> persons, LocalDate date) {
        for (Person temp : persons.values()) {
            temp.processCredit("open",date);
        }
    }

    public static void tidyCloseCredit(HashMap<String, Person> persons, LocalDate date) {
        for (Person temp : persons.values()) {
            temp.processCredit("close",date);
        }
    }

    public static Person getPerson(HashMap<String, Person> persons, String studentId) {
        Person person;
        if (persons.containsKey(studentId)) {
            person = persons.get(studentId);
        } else {
            person = new Person(studentId);
            persons.put(studentId, person);
        }
        return person;
    }
}

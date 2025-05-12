import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryCommand;
import com.oocourse.library1.LibraryMoveInfo;
import com.oocourse.library1.LibraryRequest;
import com.oocourse.library1.LibrarySystem;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class Operation {
    public static void handleCommand(LibraryCommand<?> command,
                                     Library library, HashMap<String, Person> persons) {
        LocalDate date = command.getDate();
        if (command.getCmd().equals("OPEN")) {
            List<LibraryMoveInfo> info = library.tidyOpen(date);
            if (info != null) {
                LibrarySystem.PRINTER.move(date, info);
            }
        } else if (command.getCmd().equals("CLOSE")) {
            List<LibraryMoveInfo> info = library.tidyClose();
            if (info != null) {
                LibrarySystem.PRINTER.move(date, info);
            }
        } else {
            LibraryRequest request = (LibraryRequest) command.getCmd();
            LibraryRequest.Type type = request.getType();
            LibraryBookId bookId = request.getBookId();
            if (type == LibraryRequest.Type.QUERIED) { // 用户查询书籍信息
                int count = library.queried(bookId);
                LibrarySystem.PRINTER.info(date, bookId, count);
            } else {
                String studentId = request.getStudentId();
                Person person;
                if (persons.containsKey(studentId)) {
                    person = persons.get(studentId);
                } else {
                    person = new Person(studentId);
                    persons.put(studentId, person);
                }
                boolean result = true;
                if (type == LibraryRequest.Type.BORROWED) { // 用户借书
                    result = library.borrowed(bookId, person);
                } else if (type == LibraryRequest.Type.ORDERED) {
                    result = library.ordered(bookId, person, date);
                } else if (type == LibraryRequest.Type.RETURNED) {
                    library.returned(bookId, person);
                } else if (type == LibraryRequest.Type.PICKED) {
                    result = library.picked(bookId, person);
                }
                if (result) {
                    LibrarySystem.PRINTER.accept(date, request);
                } else {
                    LibrarySystem.PRINTER.reject(date, request);
                }
            }
        }
    }
}

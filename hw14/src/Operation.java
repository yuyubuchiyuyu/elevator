import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryCloseCmd;
import com.oocourse.library2.LibraryCommand;
import com.oocourse.library2.LibraryMoveInfo;
import com.oocourse.library2.LibraryOpenCmd;
import com.oocourse.library2.LibraryReqCmd;
import com.oocourse.library2.LibraryRequest;
import com.oocourse.library2.LibrarySystem;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class Operation {
    public static void handleCommand(LibraryCommand command,
                                     Library library, HashMap<String, Person> persons) {
        LocalDate date = command.getDate();
        if (command instanceof LibraryOpenCmd) {
            List<LibraryMoveInfo> info = library.tidyOpen(date);
            if (info != null) {
                LibrarySystem.PRINTER.move(date, info);
            }
        } else if (command instanceof LibraryCloseCmd) {
            List<LibraryMoveInfo> info = library.tidyClose();
            if (info != null) {
                LibrarySystem.PRINTER.move(date, info);
            }
        } else {
            LibraryRequest request = ((LibraryReqCmd) command).getRequest();
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
                String str = null;
                if (type == LibraryRequest.Type.BORROWED) { // 用户借书
                    result = library.borrowed(bookId, person, date);
                } else if (type == LibraryRequest.Type.ORDERED) {
                    result = library.ordered(bookId, person, date);
                } else if (type == LibraryRequest.Type.RETURNED) {
                    str = library.returned(bookId, person, date);
                } else if (type == LibraryRequest.Type.PICKED) {
                    result = library.picked(bookId, person, date);
                } else if (type == LibraryRequest.Type.DONATED) {
                    result = library.donated(bookId);
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
}

import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCommand;
import com.oocourse.library3.LibrarySystem;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        HashMap<LibraryBookId, Integer> bookshelf =
                (HashMap<LibraryBookId, Integer>) LibrarySystem.SCANNER.getInventory();
        Library library = new Library(bookshelf);
        HashMap<String, Person> persons = new HashMap<>();
        LibraryCommand command = LibrarySystem.SCANNER.nextCommand();
        while (command != null) {
            Operation.handleCommand(command, library, persons);
            command = LibrarySystem.SCANNER.nextCommand();
        }
    }
}

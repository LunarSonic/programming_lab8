package forms;
import exceptions.FormBreak;
import objects.OrganizationType;
import utility.AppConsole;
import java.io.Serial;
import java.util.NoSuchElementException;

/**
 * Класс для формирования типа организации
 */
public class OrganizationTypeForm extends BasicFormation<OrganizationType> {
    @Serial
    private static final long serialVersionUID = 7L;
    private final AppConsole console;

    public OrganizationTypeForm() {
        this.console = AppConsole.getConsoleInstance();
    }

    @Override
    public OrganizationType form() throws FormBreak {
        String line = console.readInput().trim();
        if (line.equals("exit")) throw new FormBreak();
        if (line.isEmpty()) return null;

        try {
            return OrganizationType.valueOf(line);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new FormBreak();
        }
    }
}

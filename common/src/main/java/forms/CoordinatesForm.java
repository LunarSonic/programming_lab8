package forms;
import exceptions.FormBreak;
import exceptions.NotInLimitsException;
import objects.Coordinates;
import utility.AppConsole;
import java.io.Serial;
import java.util.NoSuchElementException;

/**
 * Класс для формирования координат
 */
public class CoordinatesForm extends BasicFormation<Coordinates> {
    @Serial
    private static final long serialVersionUID = 5L;
    private final AppConsole console;

    public CoordinatesForm() {
        this.console = AppConsole.getConsoleInstance();
    }

    @Override
    public Coordinates form() throws FormBreak {
        Double x = askX();
        Long y = askY();
        return new Coordinates(x, y);
    }

    private Double askX() throws FormBreak {
        double x;
        String line = console.readInput().trim();
        if (line.equals("exit")) throw new FormBreak();
        if (line.isEmpty()) throw new FormBreak();

        try {
            x = Double.parseDouble(line);
            if (x <= -947) throw new NotInLimitsException();
        } catch (NotInLimitsException | NumberFormatException | NoSuchElementException e) {
            throw new FormBreak();
        }

        return x;
    }

    private Long askY() throws FormBreak {
        long y;
        String line = console.readInput().trim();
        if (line.equals("exit")) throw new FormBreak();
        if (line.isEmpty()) throw new FormBreak();

        try {
            y = Long.parseLong(line);
        } catch (NumberFormatException | NoSuchElementException e) {
            throw new FormBreak();
        }
        return y;
    }
}

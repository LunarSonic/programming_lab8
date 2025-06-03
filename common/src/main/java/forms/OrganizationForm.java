package forms;
import exceptions.FormBreak;
import exceptions.NotInLimitsException;
import objects.*;
import utility.AppConsole;
import utility.SessionHandler;
import java.io.Serial;
import java.time.LocalDateTime;

/**
 * Класс для формирования организации
 */
public class OrganizationForm extends BasicFormation<Organization> {
    @Serial
    private static final long serialVersionUID = 6L;
    private final AppConsole console;

    public OrganizationForm() {
        this.console = AppConsole.getConsoleInstance();
    }

    @Override
    public Organization form() {
        try {
            String name = askName();
            Coordinates coordinates = askCoordinates();
            long turnover = askAnnualTurnover();
            OrganizationType type = askOrganizationType();
            Address address = askAddress();
            return new Organization(1L, name, coordinates, LocalDateTime.now(), turnover, type, address, SessionHandler.getCurrentUser().getLogin());
        } catch (FormBreak e) {
            return null;
        }
    }

    private String askName() throws FormBreak {
        String name = console.readInput().trim();
        if (name.equals("exit")) {
            throw new FormBreak();
        }
        if (name.isEmpty()) {
            throw new FormBreak();
        }
        return name;
    }

    private Coordinates askCoordinates() throws FormBreak {
        Coordinates coordinates = new CoordinatesForm().form();
        if (coordinates == null) {
            throw new FormBreak();
        }
        return coordinates;
    }


    private long askAnnualTurnover() throws FormBreak {
        String line = console.readInput().trim();
        if (line.equals("exit")) {
            throw new FormBreak();
        }
        if (line.isEmpty()) {
            throw new FormBreak();
        }

        try {
            long annualTurnover = Long.parseLong(line);
            if (annualTurnover <= 0) {
                throw new NotInLimitsException();
            }
            return annualTurnover;
        } catch (NumberFormatException | NotInLimitsException e) {
            throw new FormBreak();
        }
    }

    private OrganizationType askOrganizationType() throws FormBreak {
        OrganizationType type = new OrganizationTypeForm().form();
        if (type == null) {
            throw new FormBreak();
        }
        return type;
    }

    private Address askAddress() throws FormBreak {
        Address address = new AddressForm().form();
        if (address == null) {
            throw new FormBreak();
        }
        return address;
    }
}

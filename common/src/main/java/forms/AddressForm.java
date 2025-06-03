package forms;
import exceptions.FormBreak;
import objects.Address;
import utility.AppConsole;
import java.io.Serial;

/**
 * Класс для формирования адреса
 */
public class AddressForm extends BasicFormation<Address> {
    @Serial
    private static final long serialVersionUID = 3L;
    private final AppConsole console;

    public AddressForm() {
        this.console = AppConsole.getConsoleInstance();
    }

    @Override
    public Address form() throws FormBreak {
        String street = askStreet();
        return new Address(street);
    }

    private String askStreet() throws FormBreak {
        String street = console.readInput().trim();
        if (street.equals("exit") || street.isEmpty()) {
            throw new FormBreak();
        }
        return street;
    }
}

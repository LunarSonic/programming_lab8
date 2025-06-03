package app.commands;
import app.database.DatabaseUserManager;
import app.managers.CollectionManager;
import commands.CommandName;
import network.ExecutionResponse;
import objects.Organization;
import utility.*;
import java.io.Serializable;

public class AddIfMinCommand extends ServerCommand {
    private final CollectionManager collectionManager;
    private final DatabaseUserManager databaseUserManager;

    /**
     * Конструктор класса AddIfMinCommand
     */
    public AddIfMinCommand() {
        super(CommandName.add_if_min.name(), "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
        this.collectionManager = CollectionManager.getInstance();
        this.databaseUserManager = DatabaseUserManager.getInstance();
    }

    /**
     * Выполняется команда добавления нового элемента Organization в коллекцию, если его значение annualTurnover
     * меньше min значения, которое есть в коллекции
     * @return успешность выполнения команды
     */
    @Override
    public ExecutionResponse execute(String[] args, Serializable objectArg, User user) {
        if (args.length != 1)
            return new ExecutionResponse(false, LocaleManager.getInstance().get("wrongAmountOfArgs"));
        if (databaseUserManager.checkUser(user.getLogin(), user.getPassword())) {
            Organization organization = (Organization) objectArg;
            Long id = databaseUserManager.addIfMinOrganization(organization, user.getLogin());
            if (id == -1L) {
                return new ExecutionResponse(false, LocaleManager.getInstance().get("executionFailed"));
            } else if (id == -2L) {
                return new ExecutionResponse(false, LocaleManager.getInstance().get("notMin"));
            }
            organization.setId(id);
            collectionManager.addOrganization(organization);
            return new ExecutionResponse(LocaleManager.getInstance().get("minElementAdded"));
        }
        return null;
    }
}

package app.commands;
import app.database.DatabaseUserManager;
import app.managers.CollectionManager;
import utility.LocaleManager;
import network.ExecutionResponse;
import objects.Organization;
import utility.User;
import commands.*;
import java.io.Serializable;

/**
 * Класс команды add_if_max
 */
public class AddIfMaxCommand extends ServerCommand{
    private final CollectionManager collectionManager;
    private final DatabaseUserManager databaseUserManager;

    /**
     * Конструктор класса AddIfMaxCommand
     */
    public AddIfMaxCommand() {
        super(CommandName.add_if_max.name(), "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции");
        this.collectionManager = CollectionManager.getInstance();
        this.databaseUserManager = DatabaseUserManager.getInstance();
    }

    /**
     * Выполняется команда добавления нового элемента Organization в коллекцию, если его значение annualTurnover
     * больше max значения, которое есть в коллекции
     * @return успешность выполнения команды
     */
    @Override
    public ExecutionResponse execute(String[] args, Serializable objectArg, User user) {
        if (args.length != 1)
            return new ExecutionResponse(false, LocaleManager.getInstance().get("wrongAmountOfArgs"));
        if (databaseUserManager.checkUser(user.getLogin(), user.getPassword())) {
            Organization organization = (Organization) objectArg;
            Long id = databaseUserManager.addIfMaxOrganization(organization, user.getLogin());
            if (id == -1L) {
                return new ExecutionResponse(false, LocaleManager.getInstance().get("executionFailed"));
            } else if (id == -2L) {
                return new ExecutionResponse(false, LocaleManager.getInstance().get("notMax"));
            }
            organization.setId(id);
            collectionManager.addOrganization(organization);
            return new ExecutionResponse(LocaleManager.getInstance().get("maxElementAdded"));
        }
        return null;
    }
}

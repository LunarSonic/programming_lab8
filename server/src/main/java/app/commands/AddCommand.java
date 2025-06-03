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
 * Класс команды add
 */
public class AddCommand extends ServerCommand {
    private final CollectionManager collectionManager;
    private final DatabaseUserManager databaseUserManager;


    /**
     * Конструктор класса AddCommand
     */
    public AddCommand() {
        super(CommandName.add.name(), "добавить новый элемент в коллекцию");
        this.collectionManager = CollectionManager.getInstance();
        this.databaseUserManager = DatabaseUserManager.getInstance();
    }

    /**
     * Выполняется команда добавления нового элемента Organization в коллекцию
     * @return успешность выполнения команды
     */
    @Override
    public ExecutionResponse execute(String[] args, Serializable objectArg, User user) {
        if (args.length != 1) {
            return new ExecutionResponse(false, LocaleManager.getInstance().get("wrongAmountOfArgs"));
        }
        if (databaseUserManager.checkUser(user.getLogin(), user.getPassword())) {
            Organization organization = (Organization) objectArg;
            Long id = databaseUserManager.addOrganization(organization, user.getLogin());
            if (id == -1L) {
                return new ExecutionResponse(false, LocaleManager.getInstance().get("errorAddingOrganization"));
            }
            organization.setId(id);
            collectionManager.addOrganization(organization);
            return new ExecutionResponse(LocaleManager.getInstance().get("messageForAdd"));
        } else {
            return new ExecutionResponse(false, LocaleManager.getInstance().get("loginAndPasswordMismatch"));
        }
    }
}

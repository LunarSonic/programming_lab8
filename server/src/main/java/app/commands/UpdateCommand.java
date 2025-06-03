package app.commands;
import app.database.DatabaseUserManager;
import app.managers.CollectionManager;
import network.ExecutionResponse;
import objects.Organization;
import utility.LocaleManager;
import utility.User;
import commands.*;
import java.io.Serializable;

/**
 * Класс команды update
 */
public class UpdateCommand extends ServerCommand {
    private final CollectionManager collectionManager;
    private final DatabaseUserManager databaseUserManager;

    /**
     * Конструктор класса UpdateCommand
     */
    public UpdateCommand() {
        super(CommandName.update.name(), "обновить значение элемента коллекции, id которого равен заданному");
        this.collectionManager = CollectionManager.getInstance();
        this.databaseUserManager = DatabaseUserManager.getInstance();
    }

    /**
     * Выполняется команда обновления значения элемента коллекции, у которого id равен заданному
     * @return успешность выполнения команды
     */
    @Override
    public ExecutionResponse execute(String[] args, Serializable objectArg, User user) {
        if (args[1].isEmpty())
            return new ExecutionResponse(false, LocaleManager.getInstance().get("wrongAmountOfArgs"));
        if (databaseUserManager.checkUser(user.getLogin(), user.getPassword())) {
            Long id = Long.parseLong(args[1]);
            Organization organization = (Organization) objectArg;
            if (databaseUserManager.checkOrganizationExistence(id)) {
                if (databaseUserManager.updateOrganizationById(id, organization, user.getLogin())) {
                    organization.setId(id);
                    collectionManager.removeByIdFromCollection(id);
                    collectionManager.addOrganization(organization);
                    return new ExecutionResponse(LocaleManager.getInstance().get("updatedElement"));
                } else {
                    return new ExecutionResponse(false, LocaleManager.getInstance().get("noRightsToUpdate"));
                }
            } else {
                return new ExecutionResponse(false, LocaleManager.getInstance().get("noElementWithId"));
            }
        } else {
            return new ExecutionResponse(false, LocaleManager.getInstance().get("loginAndPasswordMismatch"));
        }
    }
}

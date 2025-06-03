package app.commands;
import app.database.DatabaseUserManager;
import app.managers.CollectionManager;
import network.ExecutionResponse;
import utility.LocaleManager;
import utility.SessionHandler;
import utility.User;
import commands.*;
import java.io.Serializable;

/**
 * Класс команды remove_by_id
 */
public class RemoveByIdCommand extends ServerCommand {
    private final CollectionManager collectionManager;
    private final DatabaseUserManager databaseUserManager;

    /**
     * Конструктор класса RemoveByIdCommand
     */
    public RemoveByIdCommand() {
        super(CommandName.remove_by_id.name(), "удалить элемент из коллекции по его id");
        this.collectionManager = CollectionManager.getInstance();
        this.databaseUserManager = DatabaseUserManager.getInstance();
    }

    /**
     * Выполняется команда удаления элемента Organization из коллекции по id
     * @return успешность выполнения команды
     */
    @Override
    public ExecutionResponse execute(String[] args, Serializable objectArg, User user) {
        if (args[1].isEmpty())
            return new ExecutionResponse(false, LocaleManager.getInstance().get("wrongAmountOfArgs"));
        if (databaseUserManager.checkUser(user.getLogin(), user.getPassword())) {
            Long id = Long.parseLong(args[1]);
            if (databaseUserManager.checkOrganizationExistence(id)) {
                if (databaseUserManager.removeById(id, user.getLogin())) {
                    collectionManager.removeByIdFromCollection(id);
                    return new ExecutionResponse(LocaleManager.getInstance().get("removedElementById"));
                } else {
                    return new ExecutionResponse(false, LocaleManager.getInstance().get("noRightsToRemove"));
                }
            } else {
                return new ExecutionResponse(false, LocaleManager.getInstance().get("noElementWithId"));
            }
        } else {
            return new ExecutionResponse(false, LocaleManager.getInstance().get("loginAndPasswordMismatch"));
        }
    }
}

package app.commands;
import app.database.DatabaseUserManager;
import app.managers.CollectionManager;
import utility.LocaleManager;
import network.ExecutionResponse;
import utility.User;
import commands.*;
import java.io.Serializable;

/**
 * Класс команды clear
 */
public class ClearCommand extends ServerCommand {
    private final CollectionManager collectionManager;
    private final DatabaseUserManager databaseUserManager;

    /**
     * Конструктор класса ClearCommand
     */
    public ClearCommand() {
        super(CommandName.clear.name(), "очистить коллекцию");
        this.collectionManager = CollectionManager.getInstance();
        this.databaseUserManager = DatabaseUserManager.getInstance();
    }

    /**
     * Выполняется команда очистки коллекции
     * @return успешность выполнения команды
     */
    @Override
    public ExecutionResponse execute(String[] args, Serializable objectArg, User user) {
        if (args.length != 1)
            return new ExecutionResponse(false, LocaleManager.getInstance().get("wrongAmountOfArgs"));
        databaseUserManager.clear(user.getLogin());
        if (collectionManager.getOrganizationCollection().isEmpty()) {
            return new ExecutionResponse(LocaleManager.getInstance().get("emptyCollection"));
        } else {
            collectionManager.clearCollection();
            collectionManager.setOrganizationCollection(databaseUserManager.loadCollection());
            return new ExecutionResponse(LocaleManager.getInstance().get("deleteElements"));
        }
    }
}

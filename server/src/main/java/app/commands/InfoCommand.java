package app.commands;
import app.managers.CollectionManager;
import utility.LocaleManager;
import network.ExecutionResponse;
import utility.User;
import commands.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Класс команды info
 */
public class InfoCommand extends ServerCommand {
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса InfoCommand
     */
    public InfoCommand() {
        super(CommandName.info.name(), "вывести в стандартный поток вывода информацию о коллекции");
        this.collectionManager = CollectionManager.getInstance();
    }

    /**
     * Выполняется команда вывода информации о коллекции
     * @return успешность выполнения команды
     */
    @Override
    public ExecutionResponse execute(String[] args, Serializable objectArg, User user) {
        if (args.length != 1)
            return new ExecutionResponse(false, LocaleManager.getInstance().get("wrongAmountOfArgs"));
        LocalDateTime lastInitTime = collectionManager.getLastInitTime();
        String lastInitTimeString = (lastInitTime == null) ? LocaleManager.getInstance().get("noInit") : String.valueOf(lastInitTime);
        return new ExecutionResponse(LocaleManager.getInstance().get("info") + "\n" + LocaleManager.getInstance().get("typeOfCol") + " " + collectionManager.getOrganizationCollection().getClass() + "\n" +
                LocaleManager.getInstance().get("numOfElements") + " " + collectionManager.getOrganizationCollection().size() + "\n" + LocaleManager.getInstance().get("lastInit") + lastInitTimeString + "\n");
    }
}

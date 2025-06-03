package app.commands;
import app.managers.CollectionManager;
import network.ExecutionResponse;
import utility.LocaleManager;
import utility.User;
import commands.*;
import java.io.Serializable;

/**
 * Класс команды show
 */
public class ShowCommand extends ServerCommand {
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса ShowCommand
     */
    public ShowCommand() {
        super(CommandName.show.name(), "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.collectionManager = CollectionManager.getInstance();
    }

    /**
     * Выполняется команда вывода всех элементов коллекции в строковом виде
     * @return успешность выполнения команды
     */
    @Override
    public ExecutionResponse execute(String[] args, Serializable objectArg, User user) {
        if (args.length != 1)
            return new ExecutionResponse(false, LocaleManager.getInstance().get("wrongAmountOfArgs"));
        collectionManager.sort();
        String collectionData = collectionManager.toString();
        return new ExecutionResponse(collectionData);
    }
}

package app.commands;
import app.database.DatabaseUserManager;
import app.managers.CollectionManager;
import network.ExecutionResponse;
import utility.LocaleManager;
import utility.User;
import commands.*;
import java.io.Serializable;
import java.util.List;

/**
 * Класс команды remove_all_by_annual_turnover
 */
public class RemoveAllByAnnualTurnoverCommand extends ServerCommand {
    private final CollectionManager collectionManager;
    private final DatabaseUserManager databaseUserManager;

    /**
     * Конструктор класса RemoveAllByAnnualTurnover
     */
    public RemoveAllByAnnualTurnoverCommand() {
        super(CommandName.remove_all_by_annual_turnover.name(), "удалить из коллекции все элементы, значение поля annualTurnover которого эквивалентно заданному");
        this.collectionManager = CollectionManager.getInstance();
        this.databaseUserManager = DatabaseUserManager.getInstance();
    }

    /**
     * Выполняется команда удаления всех элементов из коллекции, у которых annualTurnover равен заданному
     * @return успешность выполнения команды
     */
    @Override
    public ExecutionResponse execute(String[] args, Serializable objectArg, User user) {
        if (args[1].isEmpty())
            return new ExecutionResponse(false, LocaleManager.getInstance().get("wrongAmountOfArgs"));
        if (databaseUserManager.checkUser(user.getLogin(), user.getPassword())) {
            List<Long> ids = databaseUserManager.getIdsOfUsersElements(user.getLogin());
            long annualTurnover = Long.parseLong(args[1]);
            if (databaseUserManager.checkAnnualTurnoverExistence(annualTurnover)) {
                if (databaseUserManager.removeAllByAnnualTurnover(annualTurnover, user.getLogin())) {
                    collectionManager.removeAllByAnnualTurnover(annualTurnover, ids);
                    return new ExecutionResponse(LocaleManager.getInstance().get("removedElementsByAnTurnover"));
                } else {
                    return new ExecutionResponse(false, LocaleManager.getInstance().get("noElementsCreatedByYou"));
                }
            } else {
                return new ExecutionResponse(false, LocaleManager.getInstance().get("noElementWithAnTurnover"));
            }
        } else {
            return new ExecutionResponse(false, LocaleManager.getInstance().get("loginAndPasswordMismatch"));
        }
    }
}


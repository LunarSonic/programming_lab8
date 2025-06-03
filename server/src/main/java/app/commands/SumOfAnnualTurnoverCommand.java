package app.commands;
import app.managers.CollectionManager;
import network.ExecutionResponse;
import objects.Organization;
import utility.LocaleManager;
import utility.User;
import commands.*;
import java.io.Serializable;

/**
 * Класс команды sum_of_annualTurnover
 */
public class SumOfAnnualTurnoverCommand extends ServerCommand {
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса SumOfAnnualTurnoverCommand
     */
    public SumOfAnnualTurnoverCommand() {
        super(CommandName.sum_of_annual_turnover.name(), "вывести сумму значений поля annualTurnover для всех элементов коллекции");
        this.collectionManager = CollectionManager.getInstance();
    }

    /**
     * Выполняется команда вывода суммы всех значений annualTurnover
     * @return успешность выполнения команды
     */
    @Override
    public ExecutionResponse execute(String[] args, Serializable objectArg, User user) {
        if (args.length != 1)
            return new ExecutionResponse(false, LocaleManager.getInstance().get("wrongAmountOfArgs"));
        long sum = 0;
        for (Organization org : collectionManager.getOrganizationCollection()) {
            sum += org.getAnnualTurnover();
        }
        return new ExecutionResponse(LocaleManager.getInstance().get("sumOfAnTurnover") + ": " + sum);
    }
}

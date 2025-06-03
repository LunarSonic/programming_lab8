package app.utility;
import app.commands.ExecuteScriptCommand;
import app.commands.ExitCommand;
import app.commands.HelpCommand;
import commands.CommandName;
import forms.OrganizationForm;
import network.ExecutionResponse;
import objects.Organization;
import utility.LocaleManager;
import utility.User;
import java.util.Map;

/**
 * Класс для исполнения команд
 */
public class Runner {
    private User user;
    private Map<CommandName, String[]> commands;
    private final ScriptManager scriptManager;
    private final CommandHandler commandHandler;
    private final LocaleManager localeManager;

    /**
     * Конструктор класса Runner
     */
    public Runner(CommandHandler commandHandler) {
        this.scriptManager = new ScriptManager();
        this.commandHandler = commandHandler;
        this.localeManager = LocaleManager.getInstance();
        this.commands = initCommands();
    }

    /**
     * Метод для инициализации команд, используем его для команды help
     * @return Map с командами
     */
    private Map<CommandName, String[]> initCommands() {
        return Map.ofEntries(
                Map.entry(CommandName.help, new String[]{localeManager.get("helpCommand"), localeManager.get("helpDesc")}),
                Map.entry(CommandName.exit, new String[]{localeManager.get("exitCommand"), localeManager.get("exitDesc")}),
                Map.entry(CommandName.execute_script, new String[]{localeManager.get("executeScriptCommand"), localeManager.get("scriptDesc")}),
                Map.entry(CommandName.add, new String[]{localeManager.get("addCommand"), localeManager.get("addDesc")}),
                Map.entry(CommandName.add_if_max, new String[]{localeManager.get("addIfMaxCommand"), localeManager.get("addIfMaxDesc")}),
                Map.entry(CommandName.add_if_min, new String[]{localeManager.get("addIfMinCommand"), localeManager.get("addIfMinDesc")}),
                Map.entry(CommandName.clear, new String[]{localeManager.get("clearCommand"), localeManager.get("clearDesc")}),
                Map.entry(CommandName.history, new String[]{localeManager.get("historyCommand"), localeManager.get("historyDesc")}),
                Map.entry(CommandName.info, new String[]{localeManager.get("infoCommand"), localeManager.get("infoDesc")}),
                Map.entry(CommandName.max_by_postal_address, new String[]{localeManager.get("maxByPostalAddressCommand"), localeManager.get("maxByPADesc")}),
                Map.entry(CommandName.remove_all_by_annual_turnover, new String[]{localeManager.get("removeAllByAnnualTurnoverCommand"), localeManager.get("removeAllByATDesc")}),
                Map.entry(CommandName.remove_by_id, new String[]{localeManager.get("removeByIdCommand"), localeManager.get("removeByIdDesc")}),
                Map.entry(CommandName.show, new String[]{localeManager.get("showCommand"), localeManager.get("showDesc")}),
                Map.entry(CommandName.sum_of_annual_turnover, new String[]{localeManager.get("sumOfAnnualTurnoverCommand"), localeManager.get("sumOfATDesc")}),
                Map.entry(CommandName.update, new String[]{localeManager.get("updateCommand"), localeManager.get("updateDesc")})
        );
    }

    public void reinitCommands() {
        this.commands = initCommands();
    }

    /**
     * Метод, который отвечает за запуск команд
     * @param userCommand массив строк, представляющих из себя команду и её аргументы
     * @return результат выполнения команды
     */
    public ExecutionResponse launchCommand(String[] userCommand) {
        if (userCommand[0].isEmpty()) {
            return new ExecutionResponse(false, LocaleManager.getInstance().get("notEmptyCommand"));
        }
        CommandName command;
        try {
            command = CommandName.valueOf(userCommand[0]);
        } catch (IllegalArgumentException e) {
            return new ExecutionResponse(false, LocaleManager.getInstance().get("unknown") + userCommand[0]);
        }
        try {
            switch (userCommand[0]) {
                case "execute_script":
                    ExecutionResponse response1 = new ExecuteScriptCommand().execute(userCommand, user);
                    if (!response1.getResponse()) {
                        return response1;
                    }
                    ExecutionResponse response2 = scriptManager.executeScript(userCommand[1], this);
                    if (response2 == null) {
                        return new ExecutionResponse(false, "Ошибка при выполнении скрипта: пустой ответ");
                    }
                    return new ExecutionResponse(true, response1.getMessage() + "\n" + response2.getMessage().trim());
                case "exit":
                    return new ExitCommand().execute(userCommand, user);
                case "help":
                    return new HelpCommand(commands).execute(userCommand, user);
                case "add":
                case "add_if_max":
                case "add_if_min":
                    OrganizationForm orgForm = new OrganizationForm();
                    Organization org = orgForm.form();
                    if (org == null) {
                        return new ExecutionResponse(false,  LocaleManager.getInstance().get("errorCreatingOrg"));
                    }
                    commandHandler.getRequestCreator().setTemporaryOrganization(org);
                    ExecutionResponse handlerResponse = commandHandler.handleCommand(command, userCommand);
                    return handlerResponse != null ? handlerResponse : new ExecutionResponse(false, LocaleManager.getInstance().get("noExecution"));
                case "update":
                    if (userCommand.length < 2) {
                        return new ExecutionResponse(false, LocaleManager.getInstance().get("idNeeded"));
                    }
                    Long updateId;
                    try {
                        updateId = Long.parseLong(userCommand[1]);
                    } catch (NumberFormatException e) {
                        return new ExecutionResponse(false, LocaleManager.getInstance().get("argIsNum"));
                    }
                    OrganizationForm updateForm = new OrganizationForm();
                    Organization updatedOrg = updateForm.form();
                    if (updatedOrg == null) {
                        return new ExecutionResponse(false, LocaleManager.getInstance().get("errorCreatingOrg"));
                    }
                    commandHandler.getRequestCreator().setTemporaryOrganization(updatedOrg);
                    String[] updateCommandArgs = new String[]{String.valueOf(updateId)};
                    ExecutionResponse updateResponse = commandHandler.handleCommand(command, updateCommandArgs);
                    return updateResponse != null ? updateResponse : new ExecutionResponse(false, LocaleManager.getInstance().get("noExecution"));
                case "remove_all_by_annual_turnover":
                case "remove_by_id":
                    if (userCommand.length < 2) {
                        return new ExecutionResponse(false, LocaleManager.getInstance().get("idNeeded"));
                    }
                    Long arg;
                    try {
                        arg = Long.parseLong(userCommand[1]);
                    } catch (NumberFormatException e) {
                        return new ExecutionResponse(false, LocaleManager.getInstance().get("argIsNum"));
                    }
                    ExecutionResponse response = commandHandler.handleCommand(command, new String[]{String.valueOf(arg)});
                    return response != null ? response : new ExecutionResponse(false, LocaleManager.getInstance().get("noExecution"));
                default:
                    ExecutionResponse defaultResponse = commandHandler.handleCommand(command, userCommand);
                    return defaultResponse != null ? defaultResponse : new ExecutionResponse(false, LocaleManager.getInstance().get("noExecution"));
            }
        } catch (Exception e) {
            return new ExecutionResponse(false, LocaleManager.getInstance().get("errorExecutingCommand"));
        }
    }

    public void setUser(User user) {
        this.user = user;
    }
}



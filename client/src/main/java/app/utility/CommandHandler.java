package app.utility;
import app.network.NetworkHandler;
import app.network.RequestCreator;
import commands.CommandName;
import network.ExecutionResponse;
import network.Request;
import utility.LocaleManager;
import utility.User;

/**
 * Класс, предназначенный для отправки запроса от клиента на сервер
 */
public class CommandHandler {
    private final NetworkHandler networkHandler;
    private final RequestCreator requestCreator;

    public CommandHandler(NetworkHandler networkHandler, User user) {
        this.networkHandler = networkHandler;
        this.requestCreator = new RequestCreator(user);
    }

    /**
     * Обработка команды клиента, создание запроса и его отправка на сервер
     * @param command команда для выполнения
     * @param userCommand аргументы команды
     * @return результат выполнения команды от сервера
     */
    public ExecutionResponse handleCommand(CommandName command, String[] userCommand) {
        Request request = requestCreator.createRequest(command, userCommand);
        request.setLocale(LocaleManager.getInstance().getCurrentLocale());
        if (request == null) {
            return new ExecutionResponse(false, LocaleManager.getInstance().get("errorCreatingOrg"));
        }
        return networkHandler.sendAndReceive(request);
    }

    public RequestCreator getRequestCreator() {
        return requestCreator;
    }
}
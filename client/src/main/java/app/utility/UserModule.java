package app.utility;
import app.network.NetworkHandler;
import app.network.RequestCreator;
import network.ExecutionResponse;
import utility.SessionHandler;
import utility.User;

/**
 * Класс, отвечающий за отправку запросов, связанных с авторизацией/регистрацией
 */
public class UserModule {
    private NetworkHandler networkHandler;

    public ExecutionResponse logInUser(String login, String password) {
        User user = new User(login, password);
        SessionHandler.setCurrentUser(user);
        RequestCreator requestCreator = new RequestCreator(user);
        return networkHandler.sendAndReceive(requestCreator.createLoginRequest(user));
    }

    public ExecutionResponse registerUser(String login, String password) {
        User user = new User(login, password);
        SessionHandler.setCurrentUser(user);
        RequestCreator requestCreator = new RequestCreator(user);
        return networkHandler.sendAndReceive(requestCreator.createRegisterRequest(user));
    }

    /**
     * Сеттер для объекта класса NetworkHandler
     * @param networkHandler объекта класса NetworkHandler
     */
    public void setNetworkHandler(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }
}

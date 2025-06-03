package app.gui.userWindow;
import utility.LocaleManager;
import utility.SessionHandler;
import app.utility.UserModule;
import network.ExecutionResponse;
import utility.User;
import java.util.Locale;

/**
 * Класс, который управляет процессом аутентификации (регистрация и авторизация)
 */
public class AuthController {
    private final UserModule userModule;
    private final LoginAndRegisterPanel view;
    private final LocaleManager localeManager;
    private Runnable authIsSuccessful;

    public AuthController(UserModule userModule, LoginAndRegisterPanel view) {
        localeManager = LocaleManager.getInstance();
        localeManager.setLocale(Locale.CANADA);
        this.userModule = userModule;
        this.view = view;
        addEventListeners();
    }

    /**
     * Метод, который добавляет слушателей к кнопкам входа и регистрации
     */
    private void addEventListeners() {
        view.getSignInButton().addActionListener(e -> performLogin());
        view.getSignUpButton().addActionListener(e -> performRegister());
    }

    /**
     * Метод, который выполняет авторизацию пользователя
     */
    public void performLogin() {
        String login = view.getLoginUserText().getText().trim();
        String password = new String(view.getLoginPassword().getPassword()).trim();
        if (validateLoginAndPassword(login, password)) {
            ExecutionResponse response = userModule.logInUser(login, password);
            String message = localeManager.get(response.getMessage());
            if (response.getResponse()) {
                view.showSuccessfulMessage(message);
                User user = new User(login, password);
                SessionHandler.setCurrentUser(user);
                authIsSuccessful.run();
            } else {
                view.showUnsuccessfulMessage(message);
            }
        }
    }

    /**
     * Метод, который выполняет регистрацию пользователя
     */
    public void performRegister() {
        String login = view.getRegisterUserText().getText().trim();
        String password = new String(view.getRegisterPassword().getPassword()).trim();
        if (validateLoginAndPassword(login, password)) {
            ExecutionResponse response = userModule.registerUser(login, password);
            String message = localeManager.get(response.getMessage());
            if (response.getResponse()) {
                view.showSuccessfulMessage(message);
                User user = new User(login, password);
                SessionHandler.setCurrentUser(user);
                authIsSuccessful.run();
            } else {
                view.showUnsuccessfulMessage(message);
            }
        }
    }

    /**
     * Метод, который проверяет корректность введённых логина и пароля
     * @param login логин
     * @param password пароль
     * @return true, если данные корректны, иначе false
     */
    public boolean validateLoginAndPassword(String login, String password) {
        if (login.isEmpty() && password.isEmpty()) {
            view.showUnsuccessfulMessage(localeManager.get("login_and_password_are_empty"));
            return false;
        } else {
            if (login.isEmpty()) {
                view.showUnsuccessfulMessage(localeManager.get("login_is_empty"));
                return false;
            }
            if (password.isEmpty()) {
                view.showUnsuccessfulMessage(localeManager.get("password_is_empty"));
                return false;
            }
        }
        return true;
    }

    /**
     * Обновляет локализованные тексты
     */
    public void updateLocaleTexts() {
        view.updateLocaleTexts();
    }

    /**
     * Устанавливает действие, которое будет выполнено при успешной аутентификации
     * @param authIsSuccessful
     */
    public void setAuthIsSuccessful(Runnable authIsSuccessful) {
        this.authIsSuccessful = authIsSuccessful;
    }
}


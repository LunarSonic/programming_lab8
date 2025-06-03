package app.gui.userWindow;
import app.gui.components.buttons.Button;
import app.gui.components.fields.PasswordField;
import app.gui.components.fields.TextField;
import utility.LocaleManager;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Класс, представляющий собой панель для отображения форм входа и регистрации пользователей
 */
public class LoginAndRegisterPanel extends JLayeredPane {
    private final JPanel panelContainer = new JPanel(new CardLayout());
    private final LocaleManager localeManager;
    private final JPanel loginPanel = new JPanel();
    private final JPanel registerPanel = new JPanel();
    private JLabel registerLabel;
    private JLabel loginLabel;
    private final JLabel messageLabel = new JLabel();
    private TextField loginUserText;
    private PasswordField loginPassword;
    private TextField registerUserText;
    private PasswordField registerPassword;
    private Button signInButton;
    private Button signUpButton;

    public LoginAndRegisterPanel() {
        localeManager = LocaleManager.getInstance();
        localeManager.setLocale(new Locale("en", "CA"));
        initRegister();
        initLogin();
        setLayout(new MigLayout("fill"));
        panelContainer.add(registerPanel, "register");
        panelContainer.add(loginPanel, "login");
        add(panelContainer, "pos 0.5al 0.5al, w 80%, h 80%");
        add(messageLabel, "dock south, gapbottom 15, align center");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        showRegistration(true);
    }

    /**
     * Метод, который инициализирует панель регистрации
     */
    private void initRegister() {
        registerPanel.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]15[]25[]push"));
        registerLabel = new JLabel(localeManager.get("create_account"));
        registerLabel.setFont(new Font("sanserif", Font.BOLD, 30));
        registerLabel.setForeground(new Color(120, 219, 208));
        registerPanel.add(registerLabel);
        registerUserText = new TextField();
        registerUserText.setPrefixIcon(new ImageIcon(getClass().getResource("/user.png")));
        registerUserText.setHint(localeManager.get("login"));
        registerPanel.add(registerUserText, "w 100%, wrap");
        registerPassword = new PasswordField();
        registerPassword.setPrefixIcon(new ImageIcon(getClass().getResource("/pass.png")));
        registerPassword.setHint(localeManager.get("password"));
        registerPanel.add(registerPassword, "w 100%");
        signUpButton = new Button();
        signUpButton.setText(localeManager.get("button_sign_up"));
        signUpButton.setBackground(new Color(120, 219, 208));
        signUpButton.setForeground(Color.WHITE);
        registerPanel.add(signUpButton, "w 40%, h 30");
    }

    /**
     * Метод, который инициализирует панель авторизации
     */
    private void initLogin() {
        loginPanel.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]15[]25[]push"));
        loginLabel = new JLabel(localeManager.get("sign_in"));
        loginLabel.setFont(new Font("sanserif", Font.BOLD, 30));
        loginLabel.setForeground(new Color(120, 219, 208));
        loginPanel.add(loginLabel, "wrap");
        loginUserText = new TextField();
        loginUserText.setPrefixIcon(new ImageIcon(getClass().getResource("/user.png")));
        loginUserText.setHint(localeManager.get("login"));
        loginPanel.add(loginUserText, "w 100%, wrap");
        loginPassword = new PasswordField();
        loginPassword.setPrefixIcon(new ImageIcon(getClass().getResource("/pass.png")));
        loginPassword.setHint(localeManager.get("password"));
        loginPanel.add(loginPassword, "w 100%");
        signInButton = new Button();
        signInButton.setText(localeManager.get("button_sign_in"));
        signInButton.setBackground(new Color(120, 219, 208));
        signInButton.setForeground(Color.WHITE);
        loginPanel.add(signInButton, "w 40%, h 30");
    }

    /**
     * Переключение между панелями регистрации и авторизации
     * @param show true, если надо показать панель регистрации, иначе false
     */
    public void showRegistration(boolean show) {
        CardLayout cl = (CardLayout)(panelContainer.getLayout());
        if (show) {
            cl.show(panelContainer, "register");
        } else {
            cl.show(panelContainer, "login");
        }
    }

    /**
     * Обновляет текст всех компонентов согласно текущей локали
     */
    public void updateLocaleTexts() {
        registerLabel.setText(localeManager.get("create_account"));
        registerUserText.setHint(localeManager.get("login"));
        registerPassword.setHint(localeManager.get("password"));
        signUpButton.setText(localeManager.get("button_sign_up"));
        loginLabel.setText(localeManager.get("sign_in"));
        loginUserText.setHint(localeManager.get("login"));
        loginPassword.setHint(localeManager.get("password"));
        signInButton.setText(localeManager.get("button_sign_in"));
    }

    /**
     * Метод, который показывает сообщение, если всё прошло успешно
     * @param message сообщение от сервера
     */
    public void showSuccessfulMessage(String message) {
        messageLabel.setForeground(new Color(120, 219, 208));
        messageLabel.setFont(new Font("sanserif", Font.BOLD, 13));
        messageLabel.setText(message);
    }

    /**
     * Метод, который показывает сообщение, если произошла ошибка
     * @param message сообщение от сервера
     */
    public void showUnsuccessfulMessage(String message) {
        messageLabel.setForeground(new Color(135, 52, 27));
        messageLabel.setFont(new Font("sanserif", Font.BOLD, 13));
        messageLabel.setText(message);
    }

    /**
     * Очистка от сообщений
     */
    public void clearMessages() {
        messageLabel.setText("");
    }

    public TextField getLoginUserText() {
        return loginUserText;
    }

    public PasswordField getLoginPassword() {
        return loginPassword;
    }

    public TextField getRegisterUserText() {
        return registerUserText;
    }

    public PasswordField getRegisterPassword() {
        return registerPassword;
    }

    public Button getSignInButton() {
        return signInButton;
    }

    public Button getSignUpButton() {
        return signUpButton;
    }
}

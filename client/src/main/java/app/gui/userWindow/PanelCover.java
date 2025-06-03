package app.gui.userWindow;
import app.gui.components.buttons.ButtonOutline;
import utility.LocaleManager;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

/**
 * Класс, который представляет из себя панель с текстом приветствия и кнопками
 */
public class PanelCover extends JPanel {
    private ActionListener event;
    private final MigLayout layout;
    private JLabel title;
    private ButtonOutline button;
    private JLabel description;
    private boolean isLogin;
    private final LocaleManager localeManager;

    public PanelCover() {
        localeManager = LocaleManager.getInstance();
        setOpaque(false);
        layout = new MigLayout("wrap, fill", "[center]", "push[]25[]25[]push");
        setLayout(layout);
        init();
    }

    private void init() {
        localeManager.setLocale(new Locale("en", "CA"));
        title = new JLabel(localeManager.get("welcome_title"));
        title.setFont(new Font("sansserif", Font.BOLD, 20));
        title.setForeground(new Color(245, 245, 245));
        add(title);
        description = new JLabel(localeManager.get("welcome_message"));
        description.setFont(new Font("sansserif", Font.BOLD, 14));
        description.setForeground(new Color(245, 245, 245));
        add(description);
        button = new ButtonOutline();
        button.setBackground(new Color(245, 245, 245));
        button.setForeground(new Color(245, 245, 245));
        button.setText(localeManager.get("button_sign_in"));
        button.setFont(new Font("sansserif", Font.PLAIN, 14));
        button.addActionListener(this::JButtonActionPerformed);
        add(button, "w 40%, h 30");
    }

    /**
     * Обработчик события нажатия кнопки
     * @param e событие нажатия на кнопку
     */
    private void JButtonActionPerformed(ActionEvent e) {
        event.actionPerformed(e);
    }

    /**
     * Метод, который отрисовывает фон панели
     * @param g графический контекст
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(0, 0, new Color(143, 221, 213), 0, getHeight(), new Color(94, 186, 178));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    /**
     * Добавляет обработчик событий для кнопки
     * @param event обработчик событий
     */
    public void addEvent(ActionListener event) {
        this.event = event;
    }

    /**
     * Устанавливает компоненты в режим регистрации и сдвигает их влево
     * @param v величина сдвига в процентах
     */
    public void registerLeft(double v) {
        v = Double.parseDouble(String.format("%.2f", v));
        login(false);
        layout.setComponentConstraints(title, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(description, "pad 0 -" + v + "% 0 0");
    }

    /**
     * Устанавливает компоненты в режим регистрации и сдвигает их вправо
     * @param v величина сдвига в процентах
     */
    public void registerRight(double v) {
        v = Double.parseDouble(String.format("%.2f", v));
        login(false);
        layout.setComponentConstraints(title, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(description, "pad 0 -" + v + "% 0 0");
    }

    /**
     * Устанавливает компоненты в режим авторизации и сдвигает их вправо
     * @param v величина сдвига в процентах
     */
    public void loginLeft(double v) {
        v = Double.parseDouble(String.format("%.2f", v));
        login(true);
        layout.setComponentConstraints(title, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(description, "pad 0 " + v + "% 0 " + v + "%");
    }

    /**
     * Устанавливает компоненты в режим авторизации и сдвигает их влево
     * @param v величина сдвига в процентах
     */
    public void loginRight(double v) {
        v = Double.parseDouble(String.format("%.2f", v));
        login(true);
        layout.setComponentConstraints(title, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(description, "pad 0 " + v + "% 0 " + v + "%");
    }

    /**
     * Переключает режим панели и обновляет надписи
     */
    public void login(boolean login) {
        if (this.isLogin != login) {
            if (login) {
                title.setText(localeManager.get("hello_message"));
                description.setText(localeManager.get("create_message"));
                button.setText(localeManager.get("button_sign_up"));
            } else {
                title.setText(localeManager.get("welcome_title"));
                description.setText(localeManager.get("welcome_message"));
                button.setText(localeManager.get("button_sign_in"));
            }
            this.isLogin = login;
        }
    }

    /**
     * Обновляет тексты компонентов и учитывает текущую локаль
     */
    public void updateLocaleTexts() {
        if (isLogin) {
            title.setText(localeManager.get("hello_message"));
            description.setText(localeManager.get("create_message"));
            button.setText(localeManager.get("button_sign_up"));
        } else {
            title.setText(localeManager.get("welcome_title"));
            description.setText(localeManager.get("welcome_message"));
            button.setText(localeManager.get("button_sign_in"));
        }
    }
}


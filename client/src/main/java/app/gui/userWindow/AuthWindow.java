package app.gui.userWindow;
import javax.swing.*;
import utility.LocaleManager;
import utility.SessionHandler;
import app.utility.UserModule;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

/**
 * Класс, который представляет собой главное окно входа/регистрации
 */
public class AuthWindow extends JFrame {
    private final JLayeredPane bg = new JLayeredPane();
    private final PanelCover cover =  new PanelCover();
    private JComboBox<String> languageSelector;
    private final LoginAndRegisterPanel loginAndRegisterCover;
    private boolean isLogin;
    private final double addSize = 30;
    private final double loginSize = 60;
    private LocaleManager localeManager;
    private AuthController authController;
    double coverSize = 40;

    public AuthWindow(UserModule userModule) {
        localeManager = LocaleManager.getInstance();
        localeManager.setLocale(new Locale("en", "CA"));
        SessionHandler.setCurrentLocale(localeManager.getCurrentLocale());
        loginAndRegisterCover = new LoginAndRegisterPanel();
        authController = new AuthController(userModule, loginAndRegisterCover);
        init();
    }

    /**
     * Метод, который инициализирует все компоненты интерфейса и логику анимации
     */
    private void init() {
        MigLayout layout = new MigLayout("fill, insets0");
        Animator animator = getAnimator(layout);
        bg.setLayout(layout);
        String[] languages = {"English (CA)", "Русский", "Norsk", "Lietuvių"};
        languageSelector = new JComboBox<>(languages);
        languageSelector.setForeground(new Color(182, 188, 186));
        languageSelector.setSelectedIndex(0);
        languageSelector.addActionListener(e -> {
            String selectedLanguage = (String) languageSelector.getSelectedItem();
            switch (selectedLanguage) {
                case "Русский" -> localeManager.setLocale(new Locale("ru", "RU"));
                case "Norsk" -> localeManager.setLocale(new Locale("no", "NO"));
                case "Lietuvių" -> localeManager.setLocale(new Locale("lt", "LT"));
                default -> localeManager.setLocale(new Locale("en", "CA"));
            }
            SessionHandler.setCurrentLocale(localeManager.getCurrentLocale());
            loginAndRegisterCover.updateLocaleTexts();
            cover.updateLocaleTexts();
            authController.updateLocaleTexts();
            revalidate();
            repaint();
        });
        bg.add(languageSelector, "pos 0.01al 0.01al, w 200, h 25");
        bg.add(cover, "width " + coverSize + "%, pos 0al 0 n 100%");
        bg.add(loginAndRegisterCover, "width " + loginSize + "%, pos 1al 0 n 100%");
        cover.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!animator.isRunning()) {
                    animator.start();
                }
            }
        });
        setSize(1000, 600);
        setContentPane(bg);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Создает и возвращает объект Animator, который управляет переключением панелей
     */
    private Animator getAnimator(MigLayout layout) {
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                double fractionCover;
                double fractionLogin;
                double size = coverSize;
                if (fraction <= 0.5f) {
                    size += fraction * addSize;
                } else {
                    size += addSize - fraction * addSize;
                }
                if (isLogin) {
                    fractionCover = 1f - fraction;
                    fractionLogin = fraction;
                    if (fraction >= 0.5f) {
                        cover.registerRight(fractionCover * 100);
                    } else {
                        cover.loginRight(fractionLogin * 100);
                    }
                } else {
                    fractionCover = fraction;
                    fractionLogin = 1f - fraction;
                    if (fraction <= 0.5f) {
                        cover.registerLeft(fractionCover * 100);
                    } else {
                        cover.loginLeft((1f - fraction) * 150);
                    }
                }
                if (fraction >= 0.5f) {
                    loginAndRegisterCover.clearMessages();
                    loginAndRegisterCover.showRegistration(isLogin);
                }
                String fractionStr = String.format("%.5f", fractionCover);
                String loginStr = String.format("%.5f", fractionLogin);
                layout.setComponentConstraints(cover, "width " + size + "%, pos " + fractionStr + "al 0 n 100%");
                layout.setComponentConstraints(loginAndRegisterCover, "width " + loginSize + "%, pos " + loginStr + "al 0 n 100%");
                bg.revalidate();
            }
            @Override
            public void end() {
                isLogin = !isLogin;
            }
        };
        Animator animator = new Animator(650, target);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.setResolution(0);
        return animator;
    }

    /**
     * Задает действие, которое будет выполнено после успешной авторизации
     */
    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        authController.setAuthIsSuccessful(onLoginSuccess);
    }
}

package app.gui.mainWindow;
import app.gui.components.buttons.Button;
import app.gui.drawer.MyDrawerCreator;
import app.gui.drawer.NavigationDrawer;
import app.network.NetworkHandler;
import app.utility.OrganizationObserver;
import network.ExecutionResponse;
import network.Request;
import network.RequestType;
import objects.Organization;
import raven.popup.GlassPanePopup;
import raven.toast.Notifications;
import utility.ChangeLanguageObserver;
import utility.LocaleManager;
import utility.SessionHandler;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Locale;

/**
 * Класс главного окна приложения
 */
public class MainWindow extends JFrame implements ChangeLanguageObserver, OrganizationObserver {
    private Timer updateTimer;
    private long lastUpdateTime = 0;
    private JTabbedPane tabbedPane;
    private final LocaleManager localeManager;
    private final NetworkHandler networkHandler;
    private final OrganizationTableController tableController;
    private final OrganizationActionHandler actionHandler;
    private Button menuButton;

    public MainWindow(NetworkHandler networkHandler) {
        startUpdateCheck();
        this.networkHandler = networkHandler;
        this.localeManager = LocaleManager.getInstance();
        initLocale();
        this.tableController = new OrganizationTableController(networkHandler, localeManager, this);
        this.actionHandler = new OrganizationActionHandler(networkHandler, localeManager, tableController);
        initUI();
        setupNavigationDrawer();
        Notifications.getInstance().setJFrame(this);
        GlassPanePopup.install(this);
    }

    /**
     * Инициализация локали
     */
    private void initLocale() {
        Locale locale = SessionHandler.getCurrentLocale();
        if (locale == null || !Arrays.asList("en_CA", "ru_RU", "no_NO", "lt_LT")
                .contains(locale.getLanguage() + "_" + locale.getCountry())) {
            locale = new Locale("en", "CA");
        }
        localeManager.setLocale(locale);
        SessionHandler.setCurrentLocale(locale);
        localeManager.registerObserver(this);
    }

    /**
     * Инициализация UI-компонентов и настройка окна
     */
    private void initUI() {
        initComponents();
        setupLayout();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Создает и настраивает основные компоненты интерфейса
     */
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        menuButton = new Button();
        menuButton.setText(localeManager.get("menu"));
        menuButton.setBackground(new Color(120, 219, 208));
        menuButton.addActionListener(this::showNavigationDrawer);
        tabbedPane.add(localeManager.get("tableView"), tableController.getTablePanel());
        tabbedPane.add(localeManager.get("graphicView"), tableController.getMapPanel());
    }

    /**
     * Настраивает расположение компонентов в окне
     */
    private void setupLayout() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.add(menuButton);
        topPanel.add(actionHandler.getAddButton());
        topPanel.add(actionHandler.getRemoveButton());
        topPanel.add(actionHandler.getUpdateButton());
        JPanel tabContainer = new JPanel(new BorderLayout());
        tabContainer.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        tabContainer.add(tabbedPane, BorderLayout.CENTER);
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(topPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabContainer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(topPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(tabContainer));
    }

    private void setupNavigationDrawer() {
        MyDrawerCreator drawerCreator = new MyDrawerCreator();
        NavigationDrawer.getInstance().setMyDrawerCreator(drawerCreator);
    }

    /**
     * Запускает таймер для периодической проверки обновлений
     */
    private void startUpdateCheck() {
        updateTimer = new Timer(1000, e -> checkUpdates());
        updateTimer.start();
    }

    /**
     * Проверяет наличие обновлений на сервере и обновляет таблицу при необходимости
     */
    private void checkUpdates() {
        Request request = new Request(RequestType.CHECK_UPDATES, lastUpdateTime, SessionHandler.getCurrentUser());
        request.setLocale(localeManager.getCurrentLocale());
        ExecutionResponse response = networkHandler.sendAndReceive(request);
        if (response.getResponse() && response.hasUpdates()) {
            lastUpdateTime = response.getLastUpdate();
            tableController.loadCollection();
        }
    }

    /**
     * Показывает навигационное меню при нажатии на кнопку меню
     * @param e событие нажатия на кнопку
     */
    private void showNavigationDrawer(ActionEvent e) {
        NavigationDrawer.getInstance().showDrawer();
    }

    /**
     * Метод, который вызывается при смене языка в приложении и обновляет локализованные компоненты
     * @param locale новая локаль
     */
    @Override
    public void onLanguageChanged(Locale locale) {
        updateComponents();
        tableController.updateOnLanguageChange();
        updateNavigationDrawer();
    }

    /**
     * Обновляет содержимое навигационного меню с учетом новой локали
     */
    public void updateNavigationDrawer() {
        NavigationDrawer.getInstance().refreshDrawer();
        SwingUtilities.updateComponentTreeUI(NavigationDrawer.getInstance().getDrawerPanel());
    }

    /**
     * Обновляет компоненты окна
     */
    public void updateComponents() {
        menuButton.setText(localeManager.get("menu"));
        tabbedPane.setTitleAt(0, localeManager.get("tableView"));
        tabbedPane.setTitleAt(1, localeManager.get("graphicView"));
        actionHandler.updateOnLanguageChange();
        SwingUtilities.updateComponentTreeUI(this);
        revalidate();
        repaint();
    }

    /**
     * Вызывается при редактировании организации
     * Обновляет данные организации, вызывая метод в OrganizationActionHandler
     * @param org организация, которая была отредактирована
     */
    @Override
    public void onOrganizationEdited(Organization org) {
        SwingUtilities.invokeLater(() -> actionHandler.editOrganizationFromMap(org));
    }

    /**
     * Вызывается при удалении организации
     * Удаляет организацию, вызывая метод в OrganizationActionHandler
     * @param org организация, которая была удалена
     */
    @Override
    public void onOrganizationRemoved(Organization org) {
        SwingUtilities.invokeLater(() -> actionHandler.removeOrganizationFromMap(org));
    }

    @Override
    public void dispose() {
        super.dispose();
        updateTimer.stop();
    }

    public OrganizationActionHandler getActionHandler() {
        return actionHandler;
    }
}
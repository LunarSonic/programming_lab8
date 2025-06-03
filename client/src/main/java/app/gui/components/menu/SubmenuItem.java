package app.gui.components.menu;
import app.gui.mainWindow.*;
import commands.CommandName;
import raven.popup.GlassPanePopup;
import utility.LocaleManager;
import utility.SessionHandler;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import raven.utils.FlatLafStyleUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Класс, представляющий собой элемент подменю
 */
public class SubmenuItem extends JPanel {
    private int menuLevel;
    private int levelSpace = 18;
    private SubmenuLayout menuLayout;
    private boolean menuShow;
    private final Item menu;
    private final int[] ind;
    private final int[] validationInd;
    private int iconWidth;
    private final MyMenu myMenu;
    private final MenuOption menuOption;
    private final LocaleManager localeManager;

    public SubmenuItem(Item menu, int[] ind, int[] validationInd, int menuLevel, MyMenu myMenu, MenuOption menuOption) {
        this.menu = menu;
        this.ind = ind;
        this.validationInd = validationInd;
        this.menuLevel = menuLevel;
        this.myMenu = myMenu;
        this.menuOption = menuOption;
        localeManager = LocaleManager.getInstance();
        init();
    }

    /**
     * Устанавливает значение анимации для отображения подменю
     * @param animate значение анимации от 0 до 1
     */
    public void setAnimate(float animate) {
        menuLayout.setAnimate(animate);
        repaint();
        revalidate();
    }

    /**
     * Метод для инициализации компонента
     */
    private void init() {
        menuLayout = new SubmenuLayout();
        setLayout(menuLayout);
        setOpaque(menuLevel == 0); //делаем панель непрозрачной, если у нас главное меню
        if (menuOption.menuStyle != null) {
            menuOption.menuStyle.styleMenuPanel(this, myMenu.copyArray(this.ind));
        }
        FlatLafStyleUtils.appendStyleIfAbsent(this, " " + "background:null");
        iconWidth = 22;
        int index = 0;
        int validationIndex = -1;
        int nextMenuLevel = menuLevel + 1;
        //создание основной кнопки в меню
        JButton mainButton;
        if (menuLevel == 0) {
            //главный уровень
            mainButton = myMenu.createMenuItem(menu.getName(), menu.getIcon(), this.ind, menuLevel);
        } else {
            //для подменю добавляется отступ в зависимости от уровня меню
            int addSpace = menuLevel > 1 ? (menuLevel - 1) * levelSpace : 0;
            mainButton = createSubmenuItem(menu.getName(), this.ind, iconWidth + addSpace);
        }
        if (mainButton.getIcon() != null) {
            iconWidth = UIScale.unscale(mainButton.getIcon().getIconWidth());
        }
        createMainMenuEvent(mainButton);
        myMenu.applyMenuEvent(mainButton, this.ind);
        add(mainButton);
        //создание подменю
        for (int i = 0; i < menu.getSubMenu().size(); i++) {
            int[] arrIndex = createArrayIndex(this.ind, index);
            int[] arrValidationInd = createArrayIndex(this.validationInd, ++validationIndex);
            boolean validation = menuOption.menuValidation.menuValidation(myMenu.copyArray(arrValidationInd));
            if (validation) {
                Item item = menu.getSubMenu().get(i);
                if (item.isSubmenuAble()) {
                    add(myMenu.createSubmenuItem(item, arrIndex, arrValidationInd, nextMenuLevel, menuOption));
                } else {
                    //создание 1 пункта меню
                    int addSpace = menuLevel * levelSpace;
                    JButton button = createSubmenuItem(item.getName(), arrIndex, iconWidth + addSpace);
                    myMenu.applyMenuEvent(button, arrIndex);
                    add(button);
                }
            }
            if (validation || menuOption.menuValidation.keepMenuValidationIndex) {
                index++;
            }
        }
    }

    /**
     * Метод, который создаёт новый индекс для вложенного элемента
     * @param index массив индексов позиции
     * @param subIndex индекс текущего элемента
     * @return новый индекс
     */
    private int[] createArrayIndex(int[] index, int subIndex) {
        int[] newArr = new int[index.length + 1];
        System.arraycopy(index, 0, newArr, 0, index.length);
        newArr[newArr.length - 1] = subIndex;
        return newArr;
    }

    /**
     * Метод, который добавляет обработчика для главной кнопки
     * @param button кнопка
     */
    private void createMainMenuEvent(JButton button) {
        button.addActionListener(e -> {
            menuShow = !menuShow;
            new Animation(this).run(menuShow);
        });
    }

    /**
     * Метод, который создаёт кнопку подменю
     * @param name название пункта меню
     * @param index массив индексов позиции
     * @param gap отступ от края
     * @return объект JButton
     */
    public JButton createSubmenuItem(String name, int[] index, int gap) {
        JButton button = createBaseButton(name, index, gap);
        performButtonAction(button, name);
        return button;
    }

    /**
     * Метод, который создаёт базовую кнопку
     * @param name название пункта меню
     * @param index массив индексов позиции
     * @param gap отступ от края
     * @return объект JButton
     */
    private JButton createBaseButton(String name, int[] index, int gap) {
        JButton button = new JButton(name);
        button.setHorizontalAlignment(SwingConstants.LEADING);
        button.setFont(new Font("sanserif", Font.PLAIN, 12));
        if (menuOption.menuStyle != null) {
            menuOption.menuStyle.styleMenuItem(button, myMenu.copyArray(index));
        }
        boolean ltr = !GlassPanePopup.isInit() || GlassPanePopup.getMainFrame().getComponentOrientation().isLeftToRight();
        String margin = ltr ? ("7," + (gap + 25) + ",7,30") : ("7,30,7," + (gap + 25));
        FlatLafStyleUtils.appendStyleIfAbsent(button, " " +
                "arc:0;" +
                "margin:" + margin + ";" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "background:null;");
        return button;
    }

    /**
     * Метод, который назначает действие для кнопки в зависимости от её типа (кнопка для команды или изменения языка)
     * @param button кнопка меню
     * @param name название пункта меню
     */
    private void performButtonAction(JButton button, String name) {
        if (isLanguageButton(name)) {
            button.addActionListener(e -> switchLanguage(name));
        } else if (isCommandButton(name)) {
            button.addActionListener(e -> handleCommandAction(name));
        }
    }

    /**
     * Выполняет команду из подменю
     * @param actionHandler обработчик действий, связанных с организациями
     * @param localizedCommandName локализованное название команды
     */
    private void performCommand(OrganizationActionHandler actionHandler, String localizedCommandName) {
        Map<String, Runnable> commandMap = new HashMap<>();
        commandMap.put(localeManager.get("addIfMaxCommand"),
                () -> actionHandler.executeAddIfMaxAndAddIfMinCommand(CommandName.add_if_max));
        commandMap.put(localeManager.get("addIfMinCommand"),
                () -> actionHandler.executeAddIfMaxAndAddIfMinCommand(CommandName.add_if_min));
        commandMap.put(localeManager.get("clearCommand"),
                actionHandler::executeClearCommand);
        commandMap.put(localeManager.get("showCommand"),
                () -> actionHandler.executeCommandWithoutArgs(CommandName.show));
        commandMap.put(localeManager.get("exitCommand"),
                () -> System.exit(0));
        commandMap.put(localeManager.get("executeScriptCommand"),
                actionHandler::performExecuteScriptCommand);
        commandMap.put(localeManager.get("sumOfAnnualTurnoverCommand"),
                () -> actionHandler.executeCommandWithoutArgs(CommandName.sum_of_annual_turnover));
        commandMap.put(localeManager.get("infoCommand"),
                () -> actionHandler.executeCommandWithoutArgs(CommandName.info));
        commandMap.put(localeManager.get("helpCommand"),
                actionHandler::executeHelpCommand);
        commandMap.put(localeManager.get("historyCommand"),
                () -> actionHandler.executeCommandWithoutArgs(CommandName.history));
        commandMap.put(localeManager.get("maxByPostalAddressCommand"),
                () -> actionHandler.executeCommandWithoutArgs(CommandName.max_by_postal_address));
        commandMap.put(localeManager.get("removeAllByAnnualTurnoverCommand"),
                actionHandler::executeRemoveAllByAnnualTurnoverCommand);
        Runnable command = commandMap.get(localizedCommandName);
        if (command != null) {
            command.run();
        }
    }

    /**
     * Обрабатывает действие команды меню
     * @param name название команды
     */
    private void handleCommandAction(String name) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainWindow mainWindow) {
            performCommand(mainWindow.getActionHandler(), name);
        }
    }

    /**
     * Проверяет, является ли кнопка переключателем языка
     * @param name название пункта меню
     * @return true, если является, иначе false
     */
    private boolean isLanguageButton(String name) {
        return name.equals(localeManager.get("english")) ||
                name.equals(localeManager.get("russian")) ||
                name.equals(localeManager.get("norwegian")) ||
                name.equals(localeManager.get("lithuanian"));
    }

    /**
     * Проверяет, является ли кнопка командой
     * @param name название пункта меню
     * @return true, если является, иначе false
     */
    private boolean isCommandButton(String name) {
        return name.equals(localeManager.get("addIfMaxCommand")) ||
                name.equals(localeManager.get("addIfMinCommand")) ||
                name.equals(localeManager.get("clearCommand")) ||
                name.equals(localeManager.get("executeScriptCommand")) ||
                name.equals(localeManager.get("exitCommand")) ||
                name.equals(localeManager.get("infoCommand")) ||
                name.equals(localeManager.get("helpCommand")) ||
                name.equals(localeManager.get("historyCommand")) ||
                name.equals(localeManager.get("maxByPostalAddressCommand")) ||
                name.equals(localeManager.get("removeAllByAnnualTurnoverCommand")) ||
                name.equals(localeManager.get("showCommand")) ||
                name.equals(localeManager.get("sumOfAnnualTurnoverCommand"));
    }

    /**
     * Метод, который переключает язык интерфейса
     * @param language выбранный язык
     */
    private void switchLanguage(String language) {
        Locale newLocale = null;
        if (language.equals(localeManager.get("english"))) {
            newLocale = new Locale("en", "CA");
        } else if (language.equals(localeManager.get("russian"))) {
            newLocale = new Locale("ru", "RU");
        } else if (language.equals(localeManager.get("lithuanian"))) {
            newLocale = new Locale("lt", "LT");
        } else if (language.equals(localeManager.get("norwegian"))) {
            newLocale = new Locale("no", "NO");
        }
        if (newLocale != null) {
            localeManager.setLocale(newLocale);
            SessionHandler.setCurrentLocale(newLocale);
            SwingUtilities.invokeLater(() -> {
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof MainWindow mainWindow) {
                    mainWindow.updateComponents();
                    mainWindow.updateNavigationDrawer();
                }
            });
        }
    }

    /**
     * Отрисовывает соединяющиеся линии между элементами меню
     * @param g графический контекст
     */
    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (getComponentCount() > 0) {
            boolean ltr = getComponentOrientation().isLeftToRight();
            int menuHeight = getComponent(0).getHeight();
            int width = getWidth();
            Graphics2D g2d = (Graphics2D) g.create();
            FlatUIUtils.setRenderingHints(g2d);
            //создание строки в подменю
            int last = getLastLocation();
            int round = UIScale.scale(8);
            int gap = UIScale.scale((20 + (iconWidth / 2)) + (levelSpace * menuLevel));
            Path2D.Double p = new Path2D.Double();
            int x = ltr ? gap : width - gap;
            p.moveTo(x, menuHeight);
            p.lineTo(x, last - round);
            int count = getComponentCount();
            for (int i = 1; i < count; i++) {
                Component com = getComponent(i);
                int y;
                if (com instanceof SubmenuItem) {
                    y = com.getY() + ((SubmenuItem) com).getFirstItemLocation();
                } else {
                    y = com.getY() + (com.getHeight() / 2);
                }
                p.append(createCurve(round, x, y, ltr), false);
            }
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(UIScale.scale(1f)));
            g2d.draw(p);
            //создание стрелки
            paintArrow(g2d, width, menuHeight, menuLayout.getAnimate(), ltr);
            g2d.dispose();
        }
    }

    /**
     * Получает позицию последнего элемента подменю для отрисовки соединяющей линии
     * @return координата Y последнего элемента
     */
    private int getLastLocation() {
        Component com = getComponent(getComponentCount() - 1);
        if (com instanceof SubmenuItem submenuItem) {
            return com.getY() + submenuItem.getFirstItemLocation();
        } else {
            return com.getY() + com.getHeight() / 2;
        }
    }

    /**
     * Получает позицию первого элемента подменю
     * @return координата Y первого элемента
     */
    private int getFirstItemLocation() {
        if (getComponentCount() == 0) {
            return 0;
        }
        return getComponent(0).getHeight() / 2;
    }

    /**
     * Создает кривую для линии в подменю
     * @param round радиус скругления
     * @param x координата X
     * @param y координата Y
     * @param ltr направление слева-направо
     * @return форма кривой
     */
    private Shape createCurve(int round, int x, int y, boolean ltr) {
        Path2D p2 = new Path2D.Double();
        p2.moveTo(x, y - round);
        p2.curveTo(x, y - round, x, y, x + (ltr ? round : -round), y);
        return p2;
    }

    /**
     * Отрисовывает стрелку раскрытия меню
     * @param g2d графический контекст
     * @param width ширина компонента
     * @param height высота компонента
     * @param animate текущее значение анимации (от 0 до 1)
     * @param ltr направление слева-направо
     */
    private void paintArrow(Graphics2D g2d, int width, int height, float animate, boolean ltr) {
        int arrowWidth = UIScale.scale(10);
        int arrowHeight = UIScale.scale(4);
        int gap = UIScale.scale(15);
        int x = ltr ? (width - arrowWidth - gap) : gap; //горизонтальная позиция стрелка
        int y = (height - arrowHeight) / 2; //вертикальная позиция стрелки
        Path2D p = new Path2D.Double(); //создание пути
        p.moveTo(0, animate * arrowHeight); //начальная точка стрелки
        p.lineTo((double) arrowWidth / 2, (1f - animate) * arrowHeight);
        p.lineTo(arrowWidth, animate * arrowHeight);
        g2d.setColor(Color.BLACK);
        g2d.translate(x, y);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.draw(p);
    }
}

package app.gui.drawer;
import app.gui.components.header.Header;
import app.gui.components.header.HeaderInfo;
import app.gui.components.menu.*;
import app.gui.components.menu.MenuItem;
import utility.LocaleManager;
import utility.SessionHandler;
import com.formdev.flatlaf.FlatClientProperties;
import raven.swing.AvatarIcon;
import raven.utils.FlatLafStyleUtils;
import javax.swing.*;
import java.awt.*;

/**
 * Класс, который отвечает за создание боковой панели (Drawer) с заголовком и меню
 */
public class MyDrawerCreator implements DrawerCreator {
    protected Header header;
    protected JSeparator headerSeparator;
    protected JScrollPane menuScroll;
    protected MyMenu menu;
    private final LocaleManager localeManager;

    public MyDrawerCreator() {
        localeManager = LocaleManager.getInstance();
        header = new Header(getHeaderInfo(localeManager));
        headerSeparator = new JSeparator();
        menu = new MyMenu(getMenuOption());
        menuScroll = createScroll(menu);
    }

    /**
     * Метод, который создает прокручиваемую панель для заданного компонента
     */
    private JScrollPane createScroll(JComponent component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        String background = FlatLafStyleUtils.getStyleValue(component, "background", "null");
        scroll.putClientProperty(FlatClientProperties.STYLE, " " + "background:" + background);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        scroll.getHorizontalScrollBar().setUnitIncrement(10);
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, " " + "width:6;" +
                "trackArc:999;" +
                "thumbInsets:0,3,0,3;" +
                "trackInsets:0,3,0,3;" +
                "background:" + background);
        if (!background.equals("null")) {
            FlatLafStyleUtils.appendStyleIfAbsent(scroll.getVerticalScrollBar(), " " +
                    "track:" + background);
        }
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    /**
     * Метод, который создает объект HeaderInfo с иконкой пользователя и именем этого пользователя
     */
    public HeaderInfo getHeaderInfo(LocaleManager localeManager) {
        return new HeaderInfo(localeManager).setIcon(new AvatarIcon(getClass().getResource("/userForDrawer.png"), 58, 60, 999))
                .setTitle(localeManager.get("user") + " " + SessionHandler.getCurrentUser().getLogin());
    }

    /**
     * Метод, который создает опции меню с вложенными пунктами команд и выбора языка
     */
    private MenuOption getMenuOption() {
        Item.Label mainMenu = new Item.Label(localeManager.get("main"));
        Item commands = new Item(localeManager.get("commands"), "commands.svg");
        commands.addSubMenu(new Item(localeManager.get("addIfMaxCommand")));
        commands.addSubMenu(new Item(localeManager.get("addIfMinCommand")));
        commands.addSubMenu(new Item(localeManager.get("clearCommand")));
        commands.addSubMenu(new Item(localeManager.get("executeScriptCommand")));
        commands.addSubMenu(new Item(localeManager.get("exitCommand")));
        commands.addSubMenu(new Item(localeManager.get("helpCommand")));
        commands.addSubMenu(new Item(localeManager.get("historyCommand")));
        commands.addSubMenu(new Item(localeManager.get("infoCommand")));
        commands.addSubMenu(new Item(localeManager.get("maxByPostalAddressCommand")));
        commands.addSubMenu(new Item(localeManager.get("removeAllByAnnualTurnoverCommand")));
        commands.addSubMenu(new Item(localeManager.get("showCommand")));
        commands.addSubMenu(new Item(localeManager.get("sumOfAnnualTurnoverCommand")));
        Item languages = new Item(localeManager.get("languages"), "languages.svg");
        languages.addSubMenu(new Item("English (CA)"));
        languages.addSubMenu(new Item("Русский"));
        languages.addSubMenu(new Item("Norsk"));
        languages.addSubMenu(new Item("Lietuvių"));
        MenuItem[] menuItems = {
                mainMenu,
                commands,
                languages
        };
        return new MenuOption()
                .setMenuItems(menuItems)
                .setIconScale(0.35f);
    }

    /**
     * Пересоздаёт меню и заголовок, обновляет панель Drawer
     */
    public void recreateMenu() {
        header = new Header(getHeaderInfo(localeManager));
        menu = new MyMenu(getMenuOption());
        if (menuScroll != null) {
            Container parent = menuScroll.getParent();
            if (parent != null) {
                parent.remove(menuScroll);
            }
        }
        menuScroll = createScroll(menu);
        if (NavigationDrawer.getInstance().getDrawerPanel() != null) {
            NavigationDrawer.getInstance().getDrawerPanel().updateDrawer();
        }
    }

    @Override
    public Component getHeader() {
        return header;
    }

    @Override
    public Component getHeaderSeparator() {
        return headerSeparator;
    }

    @Override
    public Component getMenu() {
        return menuScroll;
    }

    @Override
    public int getDrawerWidth() {
        return 300;
    }

    public void build(DrawerPanel drawerPanel) {
    }
}

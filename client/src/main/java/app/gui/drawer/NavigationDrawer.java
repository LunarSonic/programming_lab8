package app.gui.drawer;
import raven.popup.GlassPanePopup;

/**
 * Класс для управления навигационной выдвижной панелью
 */
public class NavigationDrawer {
    private static NavigationDrawer instance = null;
    private DrawerPanel drawerPanel;
    private DrawerOption option;

    public static NavigationDrawer getInstance() {
        if (instance == null) {
            instance = new NavigationDrawer();
        }
        return instance;
    }

    private NavigationDrawer() {
    }

    /**
     * Устанавливает объект MyDrawerCreator для создания компонентов для панели
     * @param drawerCreator создатель компонентов
     */
    public void setMyDrawerCreator(MyDrawerCreator drawerCreator) {
        drawerPanel = new DrawerPanel(drawerCreator);
        option = new DrawerOption(drawerCreator.getDrawerWidth());
        drawerCreator.build(drawerPanel);
        drawerPanel.revalidate();
    }

    /**
     * Показывает выдвижную панель
     */
    public void showDrawer() {
        if (!isShowing()) {
            GlassPanePopup.showPopup(drawerPanel, option, "drawer");
        }
    }

    /**
     * Обновляет содержимое панели
     */
    public void refreshDrawer() {
        if (drawerPanel != null && isShowing()) {
            ((MyDrawerCreator) drawerPanel.getDrawerCreator()).recreateMenu();
        }
    }

    /**
     * Проверяет, отображается ли панель сейчас
     * @return true, если панель отображается
     */
    public boolean isShowing() {
        return GlassPanePopup.isShowing(drawerPanel);
    }

    public DrawerPanel getDrawerPanel() {
        return drawerPanel;
    }
}

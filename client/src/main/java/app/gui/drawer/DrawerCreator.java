package app.gui.drawer;
import java.awt.*;

/**
 * Интерфейс для создания компонентов выдвижной панели
 */
public interface DrawerCreator {
    void build(DrawerPanel drawerPanel);
    Component getHeader();
    Component getHeaderSeparator();
    Component getMenu();
    int getDrawerWidth();
}

package app.gui.components.menu;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий опции для меню (его элементы, иконки и стиль)
 */
public class MenuOption {
    protected List<Event> events = new ArrayList<>(); //список событий для меню
    protected MenuValidation menuValidation = new MenuValidation();
    protected MenuStyle menuStyle;
    protected MenuItem[] menuItems; //массив элементов меню
    protected float[] iconScale = {1f};
    protected String baseIconPath;
    protected boolean menuItemAutoSelect = true;

    /**
     * Устанавливает элементы меню для текущего меню
     * @param menuItems массив объектов MenuItem
     * @return объект MenuOption
     */
    public MenuOption setMenuItems(MenuItem[] menuItems) {
        this.menuItems = menuItems;
        return this;
    }

    /**
     * Устанавливает масштаб иконок для элементов меню, можно передавать нексколько аргументов
     * @param iconScale масштаб для иконок
     * @return объект MenuOption
     */
    public MenuOption setIconScale(float... iconScale) {
        this.iconScale = iconScale;
        return this;
    }

    /**
     * Создает и возвращает иконку для меню, основанную на масштабе
     * @param path путь к иконке
     * @param scale масштаб
     * @return объект Icon
     */
    public Icon buildMenuIcon(String path, float scale) {
        FlatSVGIcon icon = new FlatSVGIcon(path, scale);
        return icon;
    }
}

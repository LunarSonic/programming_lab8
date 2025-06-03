package app.gui.components.menu;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий элемент меню с возможностью вложенных подменю
 */
public class Item implements MenuItem {
    private String name;
    private String icon;
    private List<Item> subMenu;

    /**
     * Возвращает true, т.к item является элементом меню
     */
    @Override
    public boolean isMenu() {
        return true;
    }

    /**
     * Конструктор Item
     * @param name название элемента
     */
    public Item(String name) {
        this(name, null);
    }

    /**
     * Конструктор Item
     * @param name название элемента меню
     * @param icon иконка
     */
    public Item(String name, String icon) {
        this.name = name;
        this.icon = icon;
        this.subMenu = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    /**
     * Геттер для списка подменю этого элемента
     * @return список
     */
    public List<Item> getSubMenu() {
        return subMenu;
    }

    /**
     * Проверка, имеет ли элемент подменю
     * @return true, если подменю есть, иначе false
     */
    public boolean isSubmenuAble() {
        return !subMenu.isEmpty();
    }

    /**
     * Добавляет элемент в подменю текущего элемента
     * @param item элемент, который будет добавлен в подменю
     * @return  элемент меню с добавленным подменю
     */
    public Item addSubMenu(Item item) {
        subMenu.add(item);
        return this;
    }

    /**
     * Вложенный класс Label, представляющий обычную метку в меню
     */
    public static class Label implements MenuItem {
        private String name;

        /**
         * Констуктор Label
         * @param name метка в меню
         */
        public Label(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        /**
         * Возвращает false, т.к метка не является элементом меню с подменю
         * @return false
         */
        @Override
        public boolean isMenu() {
            return false;
        }
    }
}

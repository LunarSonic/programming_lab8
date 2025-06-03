package app.gui.components.menu;

/**
 * Интерфейс, который должен реализовывать каждый элемент меню
 */
public interface MenuItem {

    /**
     * Проверяет, является ли данный объект элементом меню
     * @return true, элемент меню, иначе false
     */
    boolean isMenu();
}

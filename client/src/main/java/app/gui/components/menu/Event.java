package app.gui.components.menu;

/**
 * Интерфейс для обработки событий, связанных с элементами меню
 */
public interface Event {

    /**
     * Метод вызывается при выборе элемента меню
     * @param action объект действия, связанного с выбором элемента
     * @param index массив индексов (представляет из себя путь к выбранному элементу в меню)
     */
    void selected(Action action, int[] index);
}

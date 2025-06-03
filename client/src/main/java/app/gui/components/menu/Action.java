package app.gui.components.menu;

/**
 * Класс Action, который помогает отслеживать состояние действий
 */
public class Action {
    private boolean selected; //указывает, было ли выбрано действие

    /**
     * Устанавливает selected в true (происходит выбор действия)
     */
    public void selected() {
        selected = true;
    }

    /**
     * Геттер для selected
     * @return значение selected
     */
    public boolean getSelected() {
        return selected;
    }
}

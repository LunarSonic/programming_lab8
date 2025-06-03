package app.gui.components.menu;

/**
 * Класс, отвечающий за валидацию состояния меню, нужен для определения правил
 * валидации в меню (сохранение индекса или удаление метки при пустом подменю)
 */
public class MenuValidation {
    protected boolean keepMenuValidationIndex;
    protected boolean removeLabelWhenEmptyMenu;

    /**
     * Конструктор по умолчанию
     */
    public MenuValidation() {
        this(true, true);
    }

    /**
     * Конструктор MenuValidation
     * @param keepMenuValidationIndex переменная, указывающая, нужно ли сохранять индекс при валидации
     * @param removeLabelWhenEmptyMenu переменная, указывающая, нужно ли удалять метку при пустом подменю
     */
    public MenuValidation(boolean keepMenuValidationIndex, boolean removeLabelWhenEmptyMenu) {
        this.keepMenuValidationIndex = keepMenuValidationIndex;
        this.removeLabelWhenEmptyMenu = removeLabelWhenEmptyMenu;
    }

    /**
     * Метод для валидации меню
     * @param index индексы
     * @return true, если прошло валидацию
     */
    public boolean menuValidation(int[] index) {
        return true;
    }

    /**
     * Метод для валидации метки
     * @param index индекс
     * @return true, если прошла валидацию
     */
    public boolean labelValidation(int index) {
        return true;
    }
}

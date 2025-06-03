package app.gui.components.header;
import utility.LocaleManager;
import javax.swing.*;

/**
 * Класс HeaderInfo содержит информацию о заголовке
 */
public class HeaderInfo {
    protected Icon icon;
    protected String title;
    protected HeaderStyle style;
    private final LocaleManager localeManager;

    /**
     * Конструктор HeaderInfo
     * @param localeManager менеджер
     */
    public HeaderInfo(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    /**
     * Устанавливает иконку для профиля
     * @param icon иконка
     * @return текущий объект HeaderInfo
     */
    public HeaderInfo setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Устанавливает название (логин)
     * @param title логин
     * @return текущий объект HeaderInfo
     */
    public HeaderInfo setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Геттер для получения объекта LocaleManager
     * @return объект LocaleManager
     */
    public LocaleManager getLocaleManager() {
        return localeManager;
    }
}

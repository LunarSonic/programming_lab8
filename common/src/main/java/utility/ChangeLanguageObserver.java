package utility;
import java.util.Locale;

/**
 * Интерфейс наблюдателя для отслеживания изменений языка приложения
 */
public interface ChangeLanguageObserver {

    /**
     * Метод вызывается при изменении языка приложения
     * @param locale новая локаль
     */
    void onLanguageChanged(Locale locale);
}

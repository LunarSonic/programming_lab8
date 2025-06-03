package utility;
import java.util.Locale;

/**
 * Интерфейс, который позволяет регистрировать наблюдателей и уведомлять их
 * о смене локали в приложении
 */
public interface ChangeLanguageObservable {

    /**
     * Регистрирует нового наблюдателя за изменениями языка
     * @param o наблюдатель, реализующий интерфейс ChangeLanguageObserver
     */
    void registerObserver(ChangeLanguageObserver o);

    /**
     * Уведомляет всех наблюдателей об изменении языка
     * @param locale локаль
     */
    void notifyObservers(Locale locale);
}

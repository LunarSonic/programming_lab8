package app.utility;
import objects.Organization;

/**
 * Интерфейс, который позволяет регистрировать наблюдателей и уведомлять их
 * о редактировании или удалении организаций
 */
public interface OrganizationObservable {

    /**
     * Регистрирует нового наблюдателя за изменениями организаций
     * @param o наблюдатель, реализующий интерфейс OrganizationObserver
     */
    void registerObserver(OrganizationObserver o);

    /**
     * Уведомляет всех наблюдателей о редактировании организации
     * @param org отредактированная организация
     */
    void notifyEditObserver(Organization org);

    /**
     * Уведомляет всех наблюдателей об удалении организации
     * @param org удаленная организация
     */
    void notifyDeleteObserver(Organization org);
}

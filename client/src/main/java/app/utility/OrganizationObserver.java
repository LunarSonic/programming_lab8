package app.utility;
import objects.Organization;

/**
 * Интерфейс наблюдателя для отслеживания изменений объектов Organization
 */
public interface OrganizationObserver {

    /**
     * Вызывается при редактировании организации.
     * @param org отредактированный объект Organization
     */
    void onOrganizationEdited(Organization org);

    /**
     * Вызывается при удалении организации из системы.
     * @param org удаленный объект Organization
     */
    void onOrganizationRemoved(Organization org);
}

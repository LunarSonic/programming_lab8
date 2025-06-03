package objects;
import utility.LocaleManager;

import java.io.Serial;
import java.io.Serializable;

/**
 * Enum из типов организации
 */
public enum OrganizationType implements Serializable {
    COMMERCIAL,
    PUBLIC,
    TRUST,
    OPEN_JOINT_STOCK_COMPANY,
    DEFAULT;
    @Serial
    private static final long serialVersionUID = 13L;

    /**
     * Метод, который возвращает элементы Enum'а
     * @return строка со всеми элементами Enum'а
     */
    public static String organizationNameList() {
        StringBuilder organizationNames = new StringBuilder();
        for (OrganizationType type: OrganizationType.values()) {
            organizationNames.append(type.name()).append(", ");
        }
        return organizationNames.substring(0, organizationNames.length() - 2);
    }

    @Override
    public String toString() {
        LocaleManager localeManager = LocaleManager.getInstance();
        return switch (this) {
            case COMMERCIAL -> localeManager.get("commercial");
            case PUBLIC -> localeManager.get("public");
            case TRUST -> localeManager.get("trust");
            case OPEN_JOINT_STOCK_COMPANY -> localeManager.get("openJointStockCompany");
            default -> localeManager.get("none");
        };
    }
}

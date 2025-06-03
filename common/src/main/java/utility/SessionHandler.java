package utility;
import java.util.Locale;

/**
 * Класс для хранения данных текущей сессии пользователя
 */
public class SessionHandler {
    public static User currentUser = null;
    public static Locale currentLocale = null;

    /**
     * Возвращает текущего авторизованного пользователя
     * @return объект User или null, если пользователь не авторизован
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Устанавливает текущего пользователя
     * @param currentUser объект User
     */
    public static void setCurrentUser(User currentUser) {
        SessionHandler.currentUser = currentUser;
    }

    /**
     * Возвращает текущую локаль приложения
     * @return объект Locale
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Устанавливает текущую локаль приложения
     * @param locale объект Locale
     */
    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }
}

package utility;
import utility.locales.*;
import java.util.*;

public class LocaleManager implements ChangeLanguageObservable {
    private static Locale currentLocale;
    private static Map<String, ListResourceBundle> bundles = new HashMap<>();
    private static LocaleManager instance = null;
    private List<ChangeLanguageObserver> observers = new ArrayList<>();

    static {
        bundles.put("en_CA", new Locale_en_CA());
        bundles.put("ru_RU", new Locale_ru_RU());
        bundles.put("no_NO", new Locale_no_NO());
        bundles.put("lt_LT", new Locale_lt_LT());
    }

    public static LocaleManager getInstance() {
        if (instance == null) {
            instance = new LocaleManager();
        }
        return instance;
    }

    public void setLocale(Locale locale) {
        currentLocale = locale;
        notifyObservers(currentLocale);
    }

    public String get(String key) {
        String localeKey = currentLocale.getLanguage() + "_" + currentLocale.getCountry();
        ListResourceBundle bundle = bundles.get(localeKey);
        return bundle.getString(key);
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }


    @Override
    public void registerObserver(ChangeLanguageObserver o) {
        observers.add(o);
    }


    @Override
    public void notifyObservers(Locale newLocale) {
        for (ChangeLanguageObserver observer : observers) {
            observer.onLanguageChanged(newLocale);
        }
    }
}
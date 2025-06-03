package app.managers;
import app.database.DatabaseUserManager;
import objects.Organization;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс для управления коллекцией
 */
public class CollectionManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 18L;
    private static CollectionManager instance = null;
    private LinkedHashSet<Organization> organizationCollection = new LinkedHashSet<>(); //коллекция, которая хранится в менеджере
    private LocalDateTime lastInitTime; //время последней инициализации менеджера
    private DatabaseUserManager databaseUserManager = new DatabaseUserManager();
    private long lastModifiedTime = System.currentTimeMillis();

    public static CollectionManager getInstance() {
        if (instance == null) {
            instance = new CollectionManager();
        }
        return instance;
    }

    /**
     * Метод, который загружает коллекцию из базы данных
     */
    public synchronized void loadCollection() {
        this.lastInitTime = LocalDateTime.now();
        this.setOrganizationCollection(databaseUserManager.loadCollection());
    }

    /**
     * Геттер для получения коллекции, которая хранится в менеджере
     * @return organizationCollection
     */
    public synchronized LinkedHashSet<Organization> getOrganizationCollection() {
        return organizationCollection;
    }

    /**
     * Геттер для получения времени последней инициализации менеджера
     * @return lastInitTime
     */
    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    /**
     * Сеттер для времени последней инициализации менеджера
     * @param lastInitTime время последней инициализации менеджера
     */
    public void setLastInitTime(LocalDateTime lastInitTime) {
        this.lastInitTime = lastInitTime;
    }

    /**
     * Сеттер для объекта коллекции
     * @param organizationCollection коллекция
     */
    public synchronized void setOrganizationCollection(LinkedHashSet<Organization> organizationCollection) {
        this.organizationCollection = organizationCollection;
    }

    /**
     * Метод, который добавляет новую организацию в коллекцию
     * @param organization коллекция
     */
    public synchronized void addOrganization(Organization organization) {
        organizationCollection.add(organization);
        setLastInitTime(LocalDateTime.now());
        sort();
        updateLastModifiedTime();
    }

    /**
     * Метод, который очищает коллекцию
     */
    public synchronized void clearCollection() {
        organizationCollection.clear();
    }

    /**
     * Метод, который удаляет элемент коллекции по id
     * @param id организации
     */
    public synchronized void removeByIdFromCollection(Long id) {
        organizationCollection.stream()
                .filter(organization -> Objects.equals(organization.getId(), id))
                .findFirst()
                .ifPresent(organizationCollection::remove);
        updateLastModifiedTime();
    }

    /**
     * Метод, который удаляет все элементы коллекций, у которых annualTurnover равен заданному
     * @param annualTurnover годовой оборот организации
     */
    public synchronized void removeAllByAnnualTurnover(long annualTurnover, List<Long> ids) {
        Iterator<Organization> iterator = organizationCollection.iterator();
        while (iterator.hasNext()) {
            Organization organization = iterator.next();
            if (organization.getAnnualTurnover() == annualTurnover && ids.contains(organization.getId())) {
                iterator.remove();
            }
        }
        updateLastModifiedTime();
    }

    /**
     * Сортировка коллекции по id
     */
    public void sort() {
        organizationCollection = organizationCollection.stream()
                .sorted(Comparator.comparing(Organization::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void updateLastModifiedTime() {
        this.lastModifiedTime = System.currentTimeMillis();
    }

    public boolean hasUpdatesSince(long clientTime) {
        return lastModifiedTime > clientTime;
    }

    /**
     * Переопределённый метод toString()
     * @return строковое представление коллекции Organization
     */
    @Override
    public String toString() {
        if (organizationCollection.isEmpty()) {
            return "Коллекция пустая";
        }
        return organizationCollection.stream()
                .map(Organization::toString)
                .collect(Collectors.joining("\n"));
    }
}

package app.database;
import network.ExecutionResponse;
import objects.Address;
import objects.Coordinates;
import objects.Organization;
import objects.OrganizationType;
import utility.AppLogger;
import utility.PasswordCreator;
import utility.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Класс для управления пользователями и элементами в базе данных
 */
public class DatabaseUserManager {
    private final DatabaseManager dbManager = new DatabaseManager();
    private final PasswordCreator passwordCreator = PasswordCreator.getInstance();
    private final AppLogger logger = new AppLogger(DatabaseUserManager.class);
    private static DatabaseUserManager instance;

    public static DatabaseUserManager getInstance() {
        if (instance == null) {
            instance = new DatabaseUserManager();
        }
        return instance;
    }

    /**
     * Метод, который регистрирует пользователя в приложении
     * @param user пользователь
     * @return ответ
     */
    public ExecutionResponse registerUser(User user) {
        try {
            if (!findUser(user.getLogin())) {
                addUser(user.getLogin(), user.getPassword());
                return new ExecutionResponse("registered_successfully");
            } else {
                return new ExecutionResponse(false, "such_login_exists");
            }
        } catch (Exception e) {
            return new ExecutionResponse(false, "server_error");
        }
    }

    /**
     * Метод, который предоставляет вход пользователю в приложении
     * @param user пользователь
     * @return ответ
     */
    public ExecutionResponse logInUser(User user) {
        try {
            if (findUser(user.getLogin())) {
                if (checkUser(user.getLogin(), user.getPassword())) {
                    return new ExecutionResponse("login_success");
                } else {
                    return new ExecutionResponse(false, "incorrect_password");
                }
            } else {
                return new ExecutionResponse(false, "login_not_found");
            }
        } catch (Exception e) {
            return new ExecutionResponse(false, "server_error");
        }
    }

    /**
     * Метод, который добавляет нового пользователя в базу данных
     * @param login логин
     * @param password пароль
     */
    public void addUser(String login, String password) {
        String salt = passwordCreator.generateSalt();
        String addQuery = "INSERT INTO users (login, password, salt) VALUES (?, ?, ?);";
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(addQuery, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, login);
            statement.setString(2, passwordCreator.encryptPassword(salt + password));
            statement.setString(3, salt);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении пользователя в базу данных");
        }
    }

    /**
     * Метод, который проверяет существование пользователя
     * @param login логин
     * @return true, если пользователь найден, иначе false
     */
    public boolean findUser(String login) {
        String findQuery = "SELECT COUNT(*) FROM users WHERE login = ?;";
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(findQuery);
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return (count > 0);
        } catch (SQLException e) {
            logger.error("Ошибка при нахождении пользователя");
            return false;
        }
    }

    /**
     * Метод, который проверяет пользователя при входе
     * @param login логин
     * @param password пароль
     * @return true, если пароль правильный, иначе false
     */
    public boolean checkUser(String login, String password) {
        boolean isValid = false;
        String storedPassword = getPassword(login);
        String salt = getSalt(login);
        String hashedPassword = passwordCreator.encryptPassword(salt + password);
        if (storedPassword.equals(hashedPassword)) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Метод, который получает значение соли, используемое для пароля, из базы данных
     * @param login логин
     * @return соль
     */
    public String getSalt(String login) {
        String saltQuery = "SELECT salt FROM users WHERE login = ?;";
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(saltQuery);
            statement.setString(1, login);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("salt");
            }
            return null;
        } catch (SQLException e) {
            logger.error("Ошибка при получении значения соли из базы данных");
        }
        return null;
    }

    /**
     * Метод, который получает значение пароля из базы данных
     * @param login логин
     * @return пароль
     */
    public String getPassword(String login) {
        String passwordQuery = "SELECT password FROM users WHERE login = ?;";
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(passwordQuery);
            statement.setString(1, login);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("password");
            }
            return null;
        } catch (SQLException e) {
            logger.error("Ошибка при получении пароля из базы данных");
        }
        return null;
    }

    /**
     * Метод, который загружает коллекцию из базы данных
     * @return коллекция
     */
    public LinkedHashSet<Organization> loadCollection() {
        LinkedHashSet<Organization> organizations = new LinkedHashSet<>();
        String loadQuery = "SELECT organizations.*, users.login AS owner FROM organizations JOIN users ON users.id = organizations.owner_id ORDER BY organizations.id";
        try (Connection connection = dbManager.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(loadQuery);
            while (result.next()) {
                Long id = result.getLong("id");
                String name = result.getString("org_name");
                Coordinates coordinates = new Coordinates(result.getDouble("coordinate_x"), result.getLong("coordinate_y"));
                LocalDateTime date = result.getTimestamp("creation_date").toLocalDateTime();
                long annualTurnover = result.getLong("annual_turnover");
                String type = result.getString("type");
                OrganizationType organizationType;
                if (type != null) {
                    organizationType = OrganizationType.valueOf(type);
                } else {
                    organizationType = null;
                }
                Address address = new Address(result.getString("postal_address"));
                String login = result.getString("owner");
                Organization organization = new Organization(id, name, coordinates, date, annualTurnover, organizationType, address, login);
                organizations.add(organization);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке коллекцции из базы данных");
        }
        return organizations;
    }

    public LinkedHashSet<Organization> search(String search) {
        LinkedHashSet<Organization> organizations = new LinkedHashSet<>();
        String searchQuery = "SELECT organizations.*, users.login AS owner FROM organizations JOIN users ON users.id = organizations.owner_id WHERE organizations.org_name LIKE ? OR users.login LIKE ? ORDER BY organizations.id;";
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            String likeQuery = "%" + search + "%";
            statement.setString(1, likeQuery);
            statement.setString(2, likeQuery);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Long id = result.getLong("id");
                String name = result.getString("org_name");
                Coordinates coordinates = new Coordinates(result.getDouble("coordinate_x"), result.getLong("coordinate_y"));
                LocalDateTime date = result.getTimestamp("creation_date").toLocalDateTime();
                long annualTurnover = result.getLong("annual_turnover");
                String type = result.getString("type");
                OrganizationType organizationType;
                if (type != null) {
                    organizationType = OrganizationType.valueOf(type);
                } else {
                    organizationType = null;
                }
                Address address = new Address(result.getString("postal_address"));
                String login = result.getString("owner");
                Organization organization = new Organization(id, name, coordinates, date, annualTurnover, organizationType, address, login);
                organizations.add(organization);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке коллекцции из базы данных");
        }
        return organizations;
    }

    /**
     * Метод, проверяющий существование организации в базе данных
     * @param id организации
     * @return true, если существует, иначе false
     */
    public boolean checkOrganizationExistence(Long id) {
        String existenceQuery = "SELECT COUNT(*) FROM organizations WHERE organizations.id = ?;";
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(existenceQuery);
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getInt(1) > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при проверке организации на существование");
            return false;
        }
    }

    /**
     * Метод, получающий id элементов, которые создал пользователь
     * @param login логин
     * @return список из id элементов
     */
    public List<Long> getIdsOfUsersElements(String login) {
        String getIdsQuery = "SELECT organizations.id FROM organizations JOIN users on organizations.owner_id = users.id WHERE users.login = ?;";
        List<Long> ids = new ArrayList<>();
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(getIdsQuery);
            statement.setString(1, login);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                ids.add(result.getLong("id"));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении id элементов, которые создал пользователь");
        }
        return ids;
    }

    /**
     * Метод, добавляющий организацию в базу данных
     * @param organization организация
     * @param login логин
     * @return id организации
     */
    public Long addOrganization(Organization organization, String login) {
        String addQuery = "INSERT INTO organizations (org_name, coordinate_x, coordinate_y, creation_date, annual_turnover, type, postal_address, owner_id) SELECT ?, ?, ?, ?, ?, ?, ?, id FROM users WHERE users.login = ?;";
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(addQuery, Statement.RETURN_GENERATED_KEYS);
            Coordinates coordinates = organization.getCoordinates();
            Timestamp date = Timestamp.valueOf(organization.getCreationDate());
            statement.setString(1, organization.getName());
            statement.setDouble(2, coordinates.getX());
            statement.setLong(3, coordinates.getY());
            statement.setTimestamp(4, date);
            statement.setLong(5, organization.getAnnualTurnover());
            if (organization.getType() != null) {
                statement.setString(6, organization.getType().toString());
            } else {
                statement.setString(6, null);
            }
            statement.setString(7, organization.getPostalAddress().getStreet());
            statement.setString(8, login);
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            if (result.next()) {
                return result.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении новой организации в базу данных");
        }
        return -1L;
    }

    /**
     * Метод, добавляющий max организацию в базу данных (сравнение по annualTurnover)
     * @param organization организация
     * @param login логин
     * @return id организации
     */
    public Long addIfMaxOrganization(Organization organization, String login) {
        String findMaxAnnualTurnoverQuery = "SELECT MAX(annual_turnover) FROM organizations;";
        try(Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(findMaxAnnualTurnoverQuery);
            ResultSet result = statement.executeQuery();
            var maxAnnualTurnover = 0L;
            if (result.next()) {
                maxAnnualTurnover = result.getLong(1);
            }
            if (organization.getAnnualTurnover() > maxAnnualTurnover) {
                return addOrganization(organization, login);
            } else {
                return -2L;
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении максимальной организации в базы данных");
        }
        return -1L;
    }

    /**
     * Метод, добавляющий min организацию в базу данных (сравнение по annualTurnover)
     * @param organization организация
     * @param login логин
     * @return id организации
     */
    public Long addIfMinOrganization(Organization organization, String login) {
        String findMaxAnnualTurnoverQuery = "SELECT MIN(annual_turnover) FROM organizations;";
        try(Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(findMaxAnnualTurnoverQuery);
            ResultSet result = statement.executeQuery();
            var minAnnualTurnover = 0L;
            if (result.next()) {
                minAnnualTurnover = result.getLong(1);
            }
            if (organization.getAnnualTurnover() < minAnnualTurnover) {
                return addOrganization(organization, login);
            } else {
                return -2L;
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении минимальной организации в базу данных");
        }
        return -1L;
    }

    /**
     * Метод, который обновляет организацию по id в базе данных
     * @param id организации
     * @param newOrganization организация
     * @param login логин
     * @return true, если обновление прошло успешно, иначе false
     */
    public boolean updateOrganizationById(Long id, Organization newOrganization, String login) {
        String getUserIdQuery = "SELECT id FROM users WHERE login = ?;";
        String updateQuery = "UPDATE organizations SET org_name = ?, coordinate_x = ?, coordinate_y = ?, creation_date = ?, annual_turnover = ?, type = ?, postal_address = ? WHERE id = ? AND owner_id = ?;";
        try (Connection connection = dbManager.getConnection()) {
            PreparedStatement getUserStatement = connection.prepareStatement(getUserIdQuery);
            getUserStatement.setString(1, login);
            ResultSet userResult = getUserStatement.executeQuery();
            if (!userResult.next()) return false;
            long userId = userResult.getLong("id");
            logger.info("Найден ID пользователя: " + userId);
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, newOrganization.getName());
            updateStatement.setDouble(2, newOrganization.getCoordinates().getX());
            updateStatement.setLong(3, newOrganization.getCoordinates().getY());
            updateStatement.setTimestamp(4, Timestamp.valueOf(newOrganization.getCreationDate()));
            updateStatement.setLong(5, newOrganization.getAnnualTurnover());
            if (newOrganization.getType() != null) {
                updateStatement.setString(6, newOrganization.getType().toString());
            } else {
                updateStatement.setString(6, null);
            }
            updateStatement.setString(7, newOrganization.getPostalAddress().getStreet());
            updateStatement.setLong(8, id);
            updateStatement.setLong(9, userId);
            int updated = updateStatement.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении организации в базе данных: " + e.getMessage());
            return false;
        }
    }

    /**
     * Метод, который удаляет все элементы, принадлежащие пользователю, из базы данных
     * @param login логин
     */
    public void clear(String login) {
        String clearQuery = "DELETE FROM organizations USING users WHERE organizations.owner_id = users.id AND users.login = ?;";
        try(Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(clearQuery);
            statement.setString(1, login);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Ошибка при удалении всех организаций из базы данных");
        }
    }

    /**
     * Метод, который удаляет все элементы, принадлежащие пользователю, по id из базы данных
     * @param id организации
     * @param login логин
     * @return true, если успешнр удалены элементы, иначе false
     */
    public boolean removeById(Long id, String login) {
        String removeQuery = "DELETE FROM organizations USING users WHERE organizations.id = ? AND organizations.owner_id = users.id AND users.login = ?;";
        try(Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(removeQuery);
            statement.setLong(1, id);
            statement.setString(2, login);
            int removedOrganizations = statement.executeUpdate();
            return removedOrganizations > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении организации по id из базы данных");
            return false;
        }
    }

    /**
     * Метод, проверяющий существование годового оборота в базе данных
     * @param annualTurnover годовой оборот
     * @return true, если существует, иначе false
     */
    public boolean checkAnnualTurnoverExistence(long annualTurnover) {
        String annualTurnoverQuery = "SELECT COUNT(*) FROM organizations WHERE annual_turnover = ?;";
        try(Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(annualTurnoverQuery);
            statement.setLong(1, annualTurnover);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getInt(1) > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при проверке годового оборота на существование");
            return false;
        }
    }

    /**
     * Метод, который удаляет все элементы, принадлежащие пользователю, по годовому обороту из базы данных
     * @param annualTurnover годовой оборот
     * @param login логин
     * @return true, если успешнр удалены элементы, иначе false
     */
    public boolean removeAllByAnnualTurnover(long annualTurnover, String login) {
        String removeQuery = "DELETE FROM organizations USING users WHERE organizations.annual_turnover = ? AND organizations.owner_id = users.id AND users.login = ?;";
        try(Connection connection = dbManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(removeQuery);
            statement.setLong(1, annualTurnover);
            statement.setString(2, login);
            int removedOrganizations = statement.executeUpdate();
            return removedOrganizations > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении организации/организаций по annual_turnover из базы данных");
            return false;
        }
    }
}

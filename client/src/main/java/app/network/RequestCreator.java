package app.network;
import commands.CommandName;
import network.Request;
import network.RequestType;
import objects.Organization;
import utility.User;

/**
 * Класс отвечает за создание запросов на основе названий команд и клиентского ввода
 */
public class RequestCreator {
    private User user;
    private Organization temporaryOrganization;

    /**
     * Конструктор класса RequestCreator
     * @param user пользователь
     */
    public RequestCreator(User user) {
        this.user = user;
    }


    public void setTemporaryOrganization(Organization organization) {
        this.temporaryOrganization = organization;
    }

    /**
     * Метод, который создаёт запрос для дальнейшей сериализации и отправки на сервер
     * @param command команда
     * @param userCommand  массив строк, представляющих из себя команду и её аргументы
     * @return объект класса Request
     */
    public Request createRequest(CommandName command, String[] userCommand) {
        switch (command) {
            case add, add_if_max, add_if_min -> {
                return createRequestWithOrg(command, user);
            }
            case remove_by_id, remove_all_by_annual_turnover -> {
                return createRequestWithArg(command, userCommand, user);
            }
            case update -> {
                return createRequestWithIdAndOrg(command, userCommand, user);
            }
            default -> {
                return new Request(command, RequestType.COMMAND, user);
            }
        }
    }

    /**
     * Метод для создания запроса пользовательского входа
     * @param user пользователь
     * @return объект класса Request
     */
    public Request createLoginRequest(User user) {
        return new Request(RequestType.LOGIN, user);
    }

    /**
     * Метод для создания запроса на регистрацию пользователя
     * @param user пользователь
     * @return объект класса Request
     */
    public Request createRegisterRequest(User user) {
        return new Request(RequestType.REGISTER, user);
    }


    /**
     * Метод для создания запроса с обычным аргументом (long id или число типа long)
     * @param command команда
     * @param userCommand массив строк, представляющих из себя команду и её аргументы
     * @return объект класса Request
     */
    private Request createRequestWithArg(CommandName command, String[] userCommand, User user) {
        Long num = parseLongFromCommand(userCommand);
        if (num == null) {
            return null;
        }
        return new Request(command, String.valueOf(num), null, RequestType.COMMAND, user);
    }

    /**
     * Метод для создания запроса с аргументом, представляющим собой объект Organization
     * @param command команда
     * @return объект класса Request
     */
    private Request createRequestWithOrg(CommandName command, User user) {
        Organization organization;
        if (temporaryOrganization != null) {
            organization = temporaryOrganization;
            temporaryOrganization = null;
        } else {
            return null;
        }
        return new Request(command, null, organization, RequestType.COMMAND, user);
    }

    /**
     * Метод для создания запроса с аргументом, представляющим собой объект Organization
     * @param command команда
     * @param userCommand массив строк, представляющих из себя команду и её аргументы
     * @return объект класса Request
     */
    private Request createRequestWithIdAndOrg(CommandName command, String[] userCommand, User user) {
        Organization organization;
        Long num = parseLongFromCommand(userCommand);
        if (num == null) {
            return null;
        }
        if (temporaryOrganization != null) {
            organization = temporaryOrganization;
            temporaryOrganization = null;
        } else {
            return null;
        }
        return new Request(command, String.valueOf(num), organization, RequestType.COMMAND, user);
    }

    /**
     * Парсинг аргумента у команды (он должен быть типа long)
     * @param userCommand массив строк, представляющих из себя команду и её аргументы
     * @return объект класса Request
     */
    private Long parseLongFromCommand(String[] userCommand) {
        if (userCommand[0].isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(userCommand[0]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

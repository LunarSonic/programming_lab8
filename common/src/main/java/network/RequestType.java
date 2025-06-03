package network;
import java.io.Serial;
import java.io.Serializable;

/**
 * Enum из типов запросов, которые отправляет пользователь
 */
public enum RequestType implements Serializable {
    COMMAND,
    LOGIN,
    REGISTER,
    GET_COLLECTION,
    SEARCH,
    CHECK_UPDATES;
    @Serial
    private static final long serialVersionUID = 31L;
}

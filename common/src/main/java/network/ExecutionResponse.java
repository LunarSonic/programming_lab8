package network;
import objects.Organization;
import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;

/**
 * Класс для хранения и вывода информации о результате выполнения метода на сервере
 */
public class ExecutionResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 15L;
    private boolean response; //переменная, которая указывает на успешность метода
    private String message; //сообщение с описанием
    private LinkedHashSet<Organization> collection;
    private boolean hasUpdates;
    private long lastUpdate;


    public ExecutionResponse(boolean response, boolean hasUpdates, long lastUpdate) {
        this.response = response;
        this.hasUpdates = hasUpdates;
        this.lastUpdate= lastUpdate;
    }

    /**
     * Конструктор класса ExecutionResponse
     * @param response ответ (true, если успешно, иначе false)
     * @param message сообщение о результате выполнения
     */
    public ExecutionResponse(boolean response, String message) {
        this.response = response;
        this.message = message;
    }

    public ExecutionResponse(boolean response, LinkedHashSet<Organization> collection) {
        this.response = response;
        this.collection = collection;
    }



    /**
     * Конструктор класса ExecutionResponse, по умолчанию команда была выполнена успешно
     * @param message сообщение о результате выполнения
     */
    public ExecutionResponse(String message) {
        this(true, message);
    }

    /**
     * Геттер для получения сообщения о выполнении
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Геттер для получения ответа о выполнении
     * @return response
     */
    public boolean getResponse() {
        return response;
    }

    public LinkedHashSet<Organization> getCollection() {
        return collection;
    }

    public boolean hasUpdates() {
        return hasUpdates;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Переопределённый метод toString
     * @return строка в определённом формате
     */
    @Override
    public String toString() {
        return getResponse() + "; " + getMessage();
    }
}


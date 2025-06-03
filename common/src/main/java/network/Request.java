package network;
import commands.CommandName;
import utility.User;
import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;

/**
 * Класс, представляющий собой запрос
 */
public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 9L;
    private CommandName commandName;
    private String commandArgs;
    private Serializable commandObjectArg;
    private RequestType requestType;
    private long lastUpdateTime;
    private User user;
    private Locale locale;

    public Request(CommandName commandName, String commandArgs, Serializable commandObjectArg, RequestType requestType, User user) {
        this.commandName = commandName;
        this.commandArgs = commandArgs;
        this.commandObjectArg = commandObjectArg;
        this.requestType = requestType;
        this.user = user;
    }

    public Request(RequestType requestType, long lastUpdateTime, User user) {
        this.requestType = requestType;
        this.lastUpdateTime = lastUpdateTime;
        this.user = user;
    }

    public Request(RequestType requestType, User user) {
        this.requestType = requestType;
        this.user = user;
    }

    public Request(CommandName commandName, RequestType requestType, User user) {
        this.commandName = commandName;
        this.requestType = requestType;
        this.user = user;
    }

    public Request(RequestType requestType, String search, User user) {
        this.requestType = requestType;
        this.commandArgs = search;
        this.user = user;
    }

    public CommandName getCommandName() {
        return commandName;
    }

    public String getCommandArgs() {
        return commandArgs;
    }

    public Serializable getCommandObjectArg() {
        return commandObjectArg;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public User getUser() {
        return user;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
}


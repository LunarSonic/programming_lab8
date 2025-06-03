package app.utility;
import network.ExecutionResponse;
import utility.AppConsole;
import utility.AppLogger;
import java.io.File;
import java.util.*;

/**
 * Класс, который управляет скриптом
 */
public class ScriptManager {
    private final AppConsole console = AppConsole.getConsoleInstance();
    private static final AppLogger logger = new AppLogger(ScriptManager.class);
    private int lengthRecursion = -1;
    private final Deque<String> scriptStack = new ArrayDeque<>(); //стек выполняемых скриптов


    /**
     * Конструктор класса ScriptManager
     */
    public ScriptManager() {
    }

    /**
     * Проверяет рекурсивность выполнения скрипта
     * @param argument название скрипта, который запускается
     * @param scriptScanner сканер для чтения из скрипта
     * @return true, если может быть рекурсия, иначе false
     */
    private boolean checkRecursion(String argument, Scanner scriptScanner) {
        var recStart = -1;
        var i = 0;
        for (String script : scriptStack) {
            i++;
            if (argument.equals(script)) {
                if (recStart < 0) recStart = i;
                if (lengthRecursion < 0) {
                    console.useConsoleScanner();
                    console.println("Была замечена рекурсия! Введите максимальную глубину рекурсии (0..300)");
                    while (lengthRecursion < 0 || lengthRecursion > 300) {
                        try {
                            console.print("> ");
                            lengthRecursion = Integer.parseInt(console.readInput().trim());
                        } catch (NumberFormatException e) {
                            logger.error("Длина не распознана");
                        }
                    }
                    console.useFileScanner(scriptScanner);
                }
                if (i > recStart + lengthRecursion || i > 300)
                    return false;
            }
        }
        return true;
    }

    /**
     * Метод для выполнения скрипта
     * @return результат выполнения скрипта
     */
    public ExecutionResponse executeScript(String filePath, Runner runner) {
        File scriptFile = new File(filePath);
        scriptStack.addLast(filePath);
        StringBuilder outputBuilder = new StringBuilder();
        try (Scanner scannerForScript = new Scanner(scriptFile)) {
            ExecutionResponse statusOfCommand;
            if (!scannerForScript.hasNext()) {
                return new ExecutionResponse(false, "Файл скрипта пуст");
            }
            console.useFileScanner(scannerForScript);
            String[] userCommand;
            do {
                userCommand = (console.readInput().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                boolean isLaunchNeeded = true;
                if (userCommand[0].equals("execute_script")) {
                    isLaunchNeeded = checkRecursion(userCommand[1], scannerForScript);
                }
                statusOfCommand = isLaunchNeeded ?
                        runner.launchCommand(userCommand) :
                        new ExecutionResponse(false, "Превышена максимальная глубина рекурсии");
                outputBuilder.append("> ").append(String.join(" ", userCommand)).append("\n");
                outputBuilder.append(statusOfCommand.getMessage()).append("\n\n");
                if (!statusOfCommand.getResponse()) {
                    return new ExecutionResponse(false, outputBuilder.toString());
                }
            } while (console.hasNextInput() && !userCommand[0].equals("exit"));
            return new ExecutionResponse(true, outputBuilder.toString());
        } catch (Exception e) {
            return new ExecutionResponse(false, "Ошибка выполнения скрипта: " + e.getMessage());
        } finally {
            console.useConsoleScanner();
            scriptStack.pollLast();
        }
    }
}

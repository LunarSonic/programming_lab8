package app.gui.mainWindow;
import network.ExecutionResponse;
import utility.LocaleManager;
import javax.swing.*;
import java.awt.*;

/**
 * Класс для управления диалоговыми окнами
 */
public class DialogManager {
    private final JFrame parentFrame;
    private final LocaleManager localeManager;

    public DialogManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.localeManager = LocaleManager.getInstance();
    }

    /**
     * Создает диалоговое окно для отображения результата выполнения скрипта
     * @param response объект ExecutionResponse, содержащий сообщение для отображения
     */
    public void createDialogForScript(ExecutionResponse response) {
        createResponseDialog(response, 700, new Font("sansserif", Font.PLAIN, 12));
    }

    /**
     * Создает стандартное диалоговое окно для отображения результата выполнения.
     * @param response объект ExecutionResponse, содержащий сообщение для отображения
     */
    public void createDialog(ExecutionResponse response) {
        createResponseDialog(response, 850, new Font("sansserif", Font.PLAIN, 13));
    }

    /**
     * Метод для создания диалогового окна с заданными параметрами
     * @param response объект ExecutionResponse, содержащий сообщение для отображения
     * @param width ширина диалогового окна
     * @param font шрифт
     */
    private void createResponseDialog(ExecutionResponse response, int width, Font font) {
        JDialog dialog = new JDialog(parentFrame, localeManager.get("response"), true);
        dialog.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(Color.WHITE);
        JTextArea textArea = new JTextArea(response.getMessage());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(font);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(width, 500));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 0, 5, 0),
                BorderFactory.createLineBorder(new Color(230, 230, 230))
        ));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }
}
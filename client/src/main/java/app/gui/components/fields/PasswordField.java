package app.gui.components.fields;
import javax.swing.*;
import java.awt.*;

/**
 * Класс PasswordField, который представляет из себя поле для ввода пароля
 */
public class PasswordField extends JPasswordField {
    private Icon prefixIcon; //иконка, расположенная слева от текста
    private String hint = ""; //подсказка для ввода

    /**
     * Конструктор PasswordField
     */
    public PasswordField() {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(0, 0, 0, 0));
        setForeground(new Color(174, 188, 188, 255));
        setFont(new Font("sansserif", Font.PLAIN, 13));
        setSelectionColor(new Color(120, 219, 208));
    }

    /**
     * Метод для отрисовки фона
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(224, 239, 235));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
        paintIcon(g);
        super.paintComponent(g);
    }

    /**
     * Метод для отрисовки подсказки
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //подсказка, если есть пустое поле
        if (getPassword().length == 0) {
            int h = getHeight();
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets(); //отступы компонента
            FontMetrics fm = g.getFontMetrics(); //параметры шрифта
            g.setColor(new Color(174, 188, 188));
            g.drawString(hint, ins.left, h / 2 + fm.getAscent() / 2 - 2); //отрисовка в центре
        }
    }

    /**
     * Метод для отрисовки иконки
     * @param g объект для рисования
     */
    private void paintIcon(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (prefixIcon != null) {
            Image prefix = ((ImageIcon) prefixIcon).getImage();
            int y = (getHeight() - prefixIcon.getIconHeight()) / 2; //выравнивание иконки
            g2.drawImage(prefix, 10, y, this); //левый угол
        }
    }

    /**
     * Метод для настройки отступов (зависит от иконки)
     */
    private void initBorder() {
        int left = 15;
        int right = 15;
        if (prefixIcon != null) {
            left = prefixIcon.getIconWidth() + 15; //увеличивает отступ слева
        }
        setBorder(BorderFactory.createEmptyBorder(10, left, 10, right));
    }

    /**
     * Сеттер для текста подсказки
     * @param hint текст подсказки
     */
    public void setHint(String hint) {
        this.hint = hint;
    }

    /**
     * Сеттер для иконки
     * @param prefixIcon иконка
     */
    public void setPrefixIcon(Icon prefixIcon) {
        this.prefixIcon = prefixIcon;
        initBorder();
    }
}

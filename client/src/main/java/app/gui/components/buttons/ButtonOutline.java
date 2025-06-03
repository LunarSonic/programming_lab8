package app.gui.components.buttons;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Класс ButtonOutline для контура кнопки без заливки
 */
public class ButtonOutline extends JButton {

    /**
     * Конструктор класса ButtonOutline
     */
    public ButtonOutline() {
        setContentAreaFilled(false); //прозрачный фон
        setBorder(new EmptyBorder(5, 0, 5, 0));
        setBackground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Метод для отрисовки компонента
     */
    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setColor(getBackground());
        g2D.drawRoundRect(0, 0, width-1, height-1, 5, 5); //скруглённый прямоугольник
        super.paintComponent(g);
    }
}

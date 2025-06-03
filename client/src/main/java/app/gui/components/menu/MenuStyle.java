package app.gui.components.menu;
import javax.swing.*;

/**
 * Абстрактный класс HeaderStyle, предоставляет базовые методы для
 * создания стилей меню и его элементов
 */
public abstract class MenuStyle {
    public void styleMenuPanel(JPanel panel, int[] index) {}

    public void styleMenu(JComponent component) {}

    public void styleLabel(JLabel label) {}

    public void styleMenuItem(JButton button, int[] index) {}
}

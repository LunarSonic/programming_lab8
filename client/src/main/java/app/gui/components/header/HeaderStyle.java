package app.gui.components.header;
import javax.swing.*;

/**
 * Абстрактный класс HeaderStyle, предоставляет базовые методы для
 * создания стилей компонентов заголовка
 */
public abstract class HeaderStyle {

    public void styleProfile(JLabel label) {}

    public void styleLogin(JLabel label) {}

    public void styleHeader(JComponent component) {}
}

package app.gui.drawer;
import raven.popup.DefaultOption;
import java.awt.*;

/**
 * Класс для настройки параметров выдвижной панели
 */
public class DrawerOption extends DefaultOption {
    private final int width;

    /**
     * Конструктор с заданием ширины панели
     * @param width ширина панели в пикселях
     */
    public DrawerOption(int width) {
        this.width = width;
    }

    /**
     * Возвращает строку с параметрами расположения панели
     * @param parent родительский компонент
     * @param animate значение анимации (от 0 до 1)
     * @return строка с параметрами расположения
     */
    @Override
    public String getLayout(Component parent, float animate) {
        String l;
        if (parent.getComponentOrientation().isLeftToRight()) {
            float x = (animate * width) - width;
            l = "pos " + x + " 0,height 100%,width " + width;
        } else {
            float x = (animate * width);
            l = "pos 100%-" + x + " 0,height 100%,width " + width;
        }
        return l;
    }

    /**
     * Определяет, нужно ли закрывать панель при клике вне её области
     * @return true, если надо закрывать, иначе false
     */
    @Override
    public boolean closeWhenClickOutside() {
        return true;
    }

    /**
     * Определяет, закрывать ли панель при нажатии ESC
     * @return true, если надо закрывать, иначе false
     */
    @Override
    public boolean closeWhenPressedEsc() {
        return true;
    }
}

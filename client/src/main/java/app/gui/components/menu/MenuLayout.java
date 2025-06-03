package app.gui.components.menu;
import java.awt.*;

/**
 * Класс MenuLayout, реализующий менеджер компоновки для элементов меню
 */
public class MenuLayout implements LayoutManager {

    /**
     * Возвращает предпочтительный размер для контейнера меню
     * @param parent контейнер, для которого рассчитывается предпочтительный размер
     * @return объект Dimension
     */
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Insets insets = parent.getInsets();
        int width = parent.getParent().getWidth();
        int height = insets.top + insets.bottom;
        int count = parent.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component com = parent.getComponent(i);
            if (com.isVisible()) {
                height += com.getPreferredSize().height;
                width = Math.max(width, com.getPreferredSize().width);
            }
        }
        return new Dimension(width, height);
    }

    /**
     * Располагает компоненты внутри контейнера по вертикали
     * @param parent the container to be laid out
     */
    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int width = parent.getWidth() - (insets.left + insets.right); //ширина контейнера
            int x = insets.left; //отступ слева
            int y = insets.top; //отступ сверху
            int count = parent.getComponentCount();
            for (int i = 0; i < count; i++) {
                Component com = parent.getComponent(i);
                if (com.isVisible()) {
                    int h = com.getPreferredSize().height;
                    com.setBounds(x, y, width, h); //раземещение компонента в контейнере
                    y += h;
                }
            }
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }
}

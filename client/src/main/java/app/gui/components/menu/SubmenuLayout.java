package app.gui.components.menu;
import java.awt.*;

public class SubmenuLayout implements LayoutManager {
    private float animate;

    public float getAnimate() {
        return animate;
    }

    public void setAnimate(float animate) {
        this.animate = animate;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Insets insets = parent.getInsets();
        int width = insets.left + insets.right;
        int height = insets.top + insets.bottom;
        int count = parent.getComponentCount();
        int first = -1;
        for (int i = 0; i < count; i++) {
            Component com = parent.getComponent(i);
            if (com.isVisible()) {
                if (first == -1) {
                    first = com.getPreferredSize().height;
                }
                height += com.getPreferredSize().height;
            }
        }
        int space = height - first;
        height = (int) (first + space * animate);
        return new Dimension(width, height);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }

    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int x = insets.left;
        int y = insets.top;
        int width = parent.getWidth() - (insets.left + insets.right);
        int count = parent.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component com = parent.getComponent(i);
            if (com.isVisible()) {
                int h = com.getPreferredSize().height;
                com.setBounds(x, y, width, h);
                y += h;
            }
        }
    }
}

package app.gui.drawer;
import net.miginfocom.swing.MigLayout;
import raven.popup.component.GlassPaneChild;

public class DrawerPanel extends GlassPaneChild {
    private final DrawerCreator drawerCreator;

    public DrawerPanel(DrawerCreator drawerBuilder) {
        this.drawerCreator = drawerBuilder;
        init();
    }

    protected void init() {
        String layoutRow = "";
        if (drawerCreator.getHeader() != null) {
            layoutRow = "[grow 0]";
            add(drawerCreator.getHeader());
        }
        if (drawerCreator.getHeaderSeparator() != null) {
            layoutRow += "[grow 0,2::]";
           add(drawerCreator.getHeaderSeparator());
        }
        if (drawerCreator.getMenu() != null) {
            layoutRow += "[fill]";
            add(drawerCreator.getMenu());
        }
        setLayout(new MigLayout("wrap,insets 0,fill", "fill", layoutRow));
    }

    public void updateDrawer() {
        removeAll();
        init();
        revalidate();
        repaint();
    }

    public DrawerCreator getDrawerCreator() {
        return drawerCreator;
    }
}
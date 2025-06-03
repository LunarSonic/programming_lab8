package app.gui.mainWindow;
import app.utility.OrganizationObservable;
import app.utility.OrganizationObserver;
import objects.Coordinates;
import objects.Organization;
import objects.OrganizationType;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.*;
import utility.LocaleManager;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс, представляющий собой панель для отображения карты с организациями
 */
public class MapView extends JPanel implements OrganizationObservable {
    private Set<ColoredWaypoint> currentWaypoints = new HashSet<>();
    private final List<OrganizationObserver> observers = new ArrayList<>();
    private final JXMapViewer mapViewer = new JXMapViewer();
    private final Map<OrganizationType, BufferedImage> iconMap;

    /**
     * Метод, загружающий иконку по указанному пути
     * @param path путь к иконке
     * @return объект BufferedImage или null, если иконка не найдена
     */
    private BufferedImage loadIcon(String path) {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream != null) {
                return ImageIO.read(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Метод, который создает Map с соответствиями типов организаций и их иконками
     * @return Map с иконками для каждого типа организации
     */
    private Map<OrganizationType, BufferedImage> getAllIcons() {
        return Map.ofEntries(
                Map.entry(OrganizationType.DEFAULT, loadIcon("/default.jpeg")),
                Map.entry(OrganizationType.PUBLIC, loadIcon("/public.jpeg")),
                Map.entry(OrganizationType.COMMERCIAL, loadIcon("/commercial.jpeg")),
                Map.entry(OrganizationType.TRUST, loadIcon("/trust.jpeg")),
                Map.entry(OrganizationType.OPEN_JOINT_STOCK_COMPANY, loadIcon("/openJoint.jpeg")));
    }

    /**
     * Конструктор MapView
     */
    public MapView() {
        setLayout(new BorderLayout());
        initMap();
        iconMap = getAllIcons();
        add(mapViewer, BorderLayout.CENTER);
    }

    /**
     * Метод для иницализации компонента карты
     */
    private void initMap() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        TileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        mapViewer.setZoom(15);
        mapViewer.setAddressLocation(new GeoPosition(59.9343, 30.3351));
        MouseInputListener mouseInputListener = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mouseInputListener);
        mapViewer.addMouseMotionListener(mouseInputListener);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point mousePoint = e.getPoint();
                for (ColoredWaypoint cwp : currentWaypoints) {
                    Point2D geoPoint = mapViewer.getTileFactory().geoToPixel(cwp.getPosition(), mapViewer.getZoom());
                    Rectangle viewportBounds = mapViewer.getViewportBounds();
                    int markerX = (int) (geoPoint.getX() - viewportBounds.getX());
                    int markerY = (int) (geoPoint.getY() - viewportBounds.getY());
                    int markerSize = 30;
                    Rectangle markerArea = new Rectangle(markerX - markerSize / 2, markerY - markerSize / 2, markerSize, markerSize);
                    if (markerArea.contains(mousePoint)) {
                        showOrganizationPopup(cwp.getOrganization());
                        break;
                    }
                }
            }
        });
    }

    /**
     * Метод, который показывает всплывающее окно с информацией об организации
     * @param org организация
     */
    private void showOrganizationPopup(Organization org) {
        SwingUtilities.invokeLater(() -> {
            String info = String.format(LocaleManager.getInstance().get("orgName") + ": %s\n" + LocaleManager.getInstance().get("x_coord") + ": %.2f\n" +
                            LocaleManager.getInstance().get("y_coord") + ": %d\n" + LocaleManager.getInstance().get("owner") + ": %s",
                    org.getName(),
                    org.getCoordinates().getX(),
                    org.getCoordinates().getY(),
                    org.getUsername());
            String cancelText = LocaleManager.getInstance().get("cancel");
            String updateText = LocaleManager.getInstance().get("updateCommand");
            String removeText = LocaleManager.getInstance().get("removeCommand");
            String[] options = {cancelText, updateText, removeText};
            ImageIcon icon = new ImageIcon(getClass().getResource("/info.jpeg"));
            int width = 32;
            int height = 32;
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon newIcon = new ImageIcon(scaledImage);
            int option = JOptionPane.showOptionDialog(
                    this,
                    info,
                    LocaleManager.getInstance().get("orgInfo"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    newIcon,
                    options,
                    cancelText);
            if (option == 1) {
                notifyEditObserver(org);
            } else if (option == 2) {
                notifyDeleteObserver(org);
            }
        });
    }

    /**
     * Метод, который обновляет список отображаемых организаций на карте
     * @param organizations множество организаций для отображения
     */
    public void setOrganizations(LinkedHashSet<Organization> organizations) {
        Set<ColoredWaypoint> waypoints = organizations.stream().filter(o -> o.getCoordinates() != null && o.getCoordinates().validate())
                .map(org -> {
                    GeoPosition pos = normalizeCoordinates(org.getCoordinates());
                    Color color = getColorByOwnerHashCode(org);
                    return new ColoredWaypoint(pos, color, org);
                })
                .collect(Collectors.toSet());
        WaypointPainter<ColoredWaypoint> painter = new WaypointPainter<>();
        painter.setWaypoints(waypoints);
        painter.setRenderer(new ColoredWaypointRenderer(iconMap));
        currentWaypoints = waypoints;
        mapViewer.setOverlayPainter(painter);
        mapViewer.repaint();
    }

    /**
     * Метод, который преобразует координаты x,y в долготу и широту для корректного отображения (jxmapviewer)
     * @param coord координаты организации
     * @return объект GeoPosition
     */
    private GeoPosition normalizeCoordinates(Coordinates coord) {
        double lat = coord.getY();
        double lon = coord.getX();
        lat = Math.max(-80, Math.min(80, lat));
        lon = lon % 360;
        if (lon > 180) lon -= 360;
        if (lon < -180) lon += 360;
        return new GeoPosition(lat, lon);
    }

    /**
     * Метод, который генерирует цвет маркера на основе хэша имени владельца
     * @param organization организация
     * @return цвет
     */
    private Color getColorByOwnerHashCode(Organization organization) {
        String owner = organization.getUsername();
        Random random = new Random(owner.hashCode());
        return new Color(
                100 + random.nextInt(156),
                100 + random.nextInt(156),
                100 + random.nextInt(156));
    }

    /**
     * Метод, который егистрирует наблюдателя для событий изменения или удаления организаций
     * @param o наблюдатель
     */
    @Override
    public void registerObserver(OrganizationObserver o) {
        observers.add(o);
    }

    /**
     * Уведомляет наблюдателей о редактировании организации
     * @param org организация, которая была изменена
     */
    @Override
    public void notifyEditObserver(Organization org) {
        for (OrganizationObserver observer: observers) {
            observer.onOrganizationEdited(org);
        }
    }

    /**
     * Уведомляет наблюдателей об удалении организации
     * @param org организация, которая была удалена
     */
    @Override
    public void notifyDeleteObserver(Organization org) {
        for (OrganizationObserver observer: observers) {
            observer.onOrganizationRemoved(org);
        }
    }

    /**
     * Вложенный класс, который представляет из себя  цветной маркер на карте (организация)
     */
    private static class ColoredWaypoint extends DefaultWaypoint {
        private final Color color;
        private final Organization organization;

        public ColoredWaypoint(GeoPosition position, Color color, Organization organization) {
            super(position);
            this.color = color;
            this.organization = organization;
        }

        public Color getColor() {
            return color;
        }

        public Organization getOrganization() {
            return organization;
        }
    }

    /**
     * Вложенный класс для отрисовки маркеров организаций на карте
     * Отрисовка иконки организации происходит с наложением цвета
     */
    private static class ColoredWaypointRenderer implements WaypointRenderer<ColoredWaypoint> {
        private final Map<OrganizationType, BufferedImage> iconMap;

        public ColoredWaypointRenderer(Map<OrganizationType, BufferedImage> iconMap) {
            this.iconMap = iconMap;
        }

        /**
         * Метод, который накладывает цвет на изображение
         * @param src исходное изображение
         * @param color цвет для наложения
         * @return объект BufferedImage
         */
        private static BufferedImage paintImage(BufferedImage src, Color color) {
            BufferedImage painted= new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TRANSLUCENT);
            Graphics2D g2d = painted.createGraphics();
            g2d.drawImage(src, 0, 0, null);
            g2d.setComposite(AlphaComposite.SrcAtop);
            g2d.setColor(color);
            g2d.fillRect(0, 0, src.getWidth(), src.getHeight());
            g2d.dispose();
            return painted;
        }

        /**
         * Метод, который отрисовывает иконку организации на карте
         * @param g графический контекст
         * @param map компонент карты
         * @param position позиция маркера
         * @param icon иконка
         * @param tintColor цвет маркера
         */
        private void drawIconWaypoint(Graphics2D g, JXMapViewer map, GeoPosition position, BufferedImage icon, Color tintColor) {
            if (icon == null) return;
            int width = 30;
            int height = 30;
            BufferedImage tintedIcon = paintImage(icon, tintColor); //наложение цвета
            Image scaledIcon = tintedIcon.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            Point2D point = map.getTileFactory().geoToPixel(position, map.getZoom());
            g.drawImage(scaledIcon, (int) point.getX() - width / 2, (int) point.getY() - height / 2, null);
        }

        /**
         * Основной метод рендеринга, который вызывается библиотекой для отображения каждого маркера
         * @param g графический контекст
         * @param map компонент карты
         * @param wp маркер, который нужно отрисовать
         */
        @Override
        public void paintWaypoint(Graphics2D g, JXMapViewer map, ColoredWaypoint wp) {
            Color color = wp.getColor();
            OrganizationType type = Optional.ofNullable(wp.getOrganization().getType()).orElse(OrganizationType.DEFAULT);
            BufferedImage icon = iconMap.getOrDefault(type, iconMap.get(OrganizationType.DEFAULT));
            drawIconWaypoint(g, map, wp.getPosition(), icon, color);
        }
    }
}

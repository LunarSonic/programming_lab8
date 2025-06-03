package app.gui.mainWindow;
import app.network.NetworkHandler;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import network.ExecutionResponse;
import network.Request;
import network.RequestType;
import objects.Organization;
import utility.LocaleManager;
import utility.SessionHandler;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

/**
 * Kласс, который управляет отображением и взаимодействием с таблицей организаций в главном окне
 */
public class OrganizationTableController {
    private final NetworkHandler networkHandler;
    private final MainWindow mainWindow;
    private final LocaleManager localeManager;
    private JPanel tablePanel;
    private JTable table;
    private JScrollPane scroll;
    private JTextField txtSearch;
    private JLabel lbTitle;
    private JSeparator jSeparator;
    private MapView mapPanel;
    private LinkedHashSet<Organization> organizations;

    public OrganizationTableController(NetworkHandler networkHandler, LocaleManager localeManager, MainWindow mainWindow) {
        this.networkHandler = networkHandler;
        this.localeManager = localeManager;
        this.mainWindow = mainWindow;
        table = new JTable();
        scroll = new JScrollPane();
        tablePanel = new JPanel();
        tablePanel.setPreferredSize(new Dimension(1000, 600));
        mapPanel = new MapView();
        mapPanel.registerObserver(mainWindow);
        txtSearch = new JTextField(20);
        jSeparator = new JSeparator();
        lbTitle = new JLabel(localeManager.get("name"));
        initTable();
        initComponents();
        loadCollection();
    }

    /**
     * Метод, который инициализирует вид таблицы и поле поиска
     */
    private void initComponents() {
        tablePanel.putClientProperty(FlatClientProperties.STYLE, " "
                + "arc:25;"
                + "background:$Table.background");

        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, " "
                + "height:30;"
                + "background:#78dbd0ff;"
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background;"
                + "font:bold;");

        table.putClientProperty(FlatClientProperties.STYLE, " "
                + "rowHeight:53;"
                + "showHorizontalLines:true;"
                + "showVerticalLines:true;"
                + "intercellSpacing:1,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");

        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, " "
                + "trackArc:999;"
                + "trackInsets:3,3,3,3;"
                + "thumbInsets:3,3,3,3;"
                + "background:$Table.background;");

        lbTitle.putClientProperty(FlatClientProperties.STYLE, " "
                + "font:bold +5;"
                + "foreground:#78dbd0ff;");

        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, localeManager.get("search"));
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon(getClass().getResource("/search.svg")));
        txtSearch.putClientProperty(FlatClientProperties.STYLE, " "
                + "arc:15;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "foreground:#aebcbcff;"
                + "background:#dbf2f0;"
                + "margin:5,25,5,25;");
        table.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(table, 0));
        table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table));
    }

    /**
     * Метод, который инициализирует структуру таблицы
     */
    private void initTable() {
        CustomTableModel model = new CustomTableModel(
                new Object[][]{},
                new String[]{
                        "",
                        localeManager.get("id"),
                        localeManager.get("org_name"),
                        localeManager.get("x_coord"),
                        localeManager.get("y_coord"),
                        localeManager.get("date"),
                        localeManager.get("an_turnover"),
                        localeManager.get("org_type"),
                        localeManager.get("org_addr"),
                        localeManager.get("owner")
                }) {
            Class[] types = new Class[]{
                    Boolean.class, Long.class, Object.class,
                    Double.class, Long.class, Object.class,
                    Long.class, Object.class, Object.class,
                    Object.class
            };
            boolean[] canEdit = new boolean[]{
                    true, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        table.setModel(model);
        table.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer leftAlignRenderer = new DefaultTableCellRenderer();
        leftAlignRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(leftAlignRenderer);
        }

        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setMaxWidth(70);
        table.getColumnModel().getColumn(2).setMaxWidth(120);
        table.getColumnModel().getColumn(3).setMaxWidth(120);
        table.getColumnModel().getColumn(4).setMaxWidth(120);
        table.getColumnModel().getColumn(5).setMaxWidth(196);
        table.getColumnModel().getColumn(6).setMaxWidth(120);
        table.getColumnModel().getColumn(7).setMaxWidth(273);
        table.getColumnModel().getColumn(8).setMaxWidth(180);
        table.getColumnModel().getColumn(9).setMaxWidth(90);
        table.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(table, 0));
        table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table));
        scroll.setViewportView(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                txtSearchKeyReleased();
            }
        });
        GroupLayout tableLayout = new GroupLayout(tablePanel);
        tablePanel.setLayout(tableLayout);
        tableLayout.setHorizontalGroup(tableLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(tableLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(lbTitle)
                        .addContainerGap(700, Short.MAX_VALUE))
                .addGroup(tableLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(txtSearch, GroupLayout.PREFERRED_SIZE, 330, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(700, Short.MAX_VALUE))
                .addComponent(jSeparator)
                .addGroup(tableLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(scroll)
                        .addGap(25, 25, 25)));
        tableLayout.setVerticalGroup(tableLayout.createSequentialGroup()
                .addGap(10)
                .addComponent(lbTitle)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSearch, GroupLayout.PREFERRED_SIZE, txtSearch.getPreferredSize().height, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addGap(10));
    }

    /**
     * Метод, который обрабатывает событие отпускания клавиши в поле поиска
     */
    private void txtSearchKeyReleased() {
        search(txtSearch.getText().trim());
    }

    /**
     * Выполняет поиск организаций по введённой строке
     * @param search строка поиска
     */
    public void search(String search) {
        CustomTableModel model = (CustomTableModel) table.getModel();
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        model.setRowCount(0);
        ExecutionResponse response = networkHandler.sendAndReceive(new Request(RequestType.SEARCH, search, SessionHandler.getCurrentUser()));
        if (response.getResponse()) {
            organizations = response.getCollection();
            updateTable();
        }
    }

    /**
     * Метод, который загружает коллекцию организаций и обновляет таблицу
     */
    public void loadCollection() {
        ExecutionResponse response = networkHandler.sendAndReceive(new Request(RequestType.GET_COLLECTION, SessionHandler.getCurrentUser()));
        if (response.getResponse()) {
            organizations = response.getCollection();
            updateTable();
        }
    }

    /**
     * Метод, который обновляет содержимое таблицы организациями из текущей коллекции
     */
    public void updateTable() {
        CustomTableModel model = (CustomTableModel) table.getModel();
        model.setRowCount(0);
        Locale locale = localeManager.getCurrentLocale();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(locale);
        for (Organization org : organizations) {
            String formattedDate = org.getCreationDate().format(formatter);
            Object[] row = {
                    false,
                    org.getId(),
                    org.getName(),
                    org.getCoordinates().getX(),
                    org.getCoordinates().getY(),
                    formattedDate,
                    org.getAnnualTurnover(),
                    org.getType(),
                    org.getPostalAddress().getStreet(),
                    org.getUsername()
            };
            model.addRow(row);
        }
        mapPanel.setOrganizations(organizations);
    }

    /**
     * Метод, возвращающий список выбранных пользователем организаций
     * @return список организаций
     */
    public List<Organization> getSelectedOrganizations() {
        List<Organization> selectedOrgs = new ArrayList<>();
        for (int i = 0; i < table.getRowCount(); i++) {
            if ((boolean) table.getValueAt(i, 0)) {
                Long orgId = (Long) table.getValueAt(i, 1);
                Organization org = getOrganizationById(orgId);
                if (org != null) {
                    selectedOrgs.add(org);
                }
            }
        }
        return selectedOrgs;
    }

    /**
     * Возвращает организацию по id
     * @param orgId id организации
     * @return объект Organization
     */
    private Organization getOrganizationById(Long orgId) {
        for (Organization org : organizations) {
            if (org.getId().equals(orgId)) {
                return org;
            }
        }
        return null;
    }

    /**
     * Обновляет текст заголовков таблицы и элементов управления при смене языка
     */
    public void updateOnLanguageChange() {
        lbTitle.setText(localeManager.get("name"));
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, localeManager.get("search"));
        updateTableHeader();
    }

    /**
     * Обновляет заголовки столбцов таблицы в соответствии с текущей локалью
     */
    public void updateTableHeader() {
        TableColumnModel columnModel = table.getColumnModel();
        String[] columns = new String[]{
                "",
                localeManager.get("id"),
                localeManager.get("org_name"),
                localeManager.get("x_coord"),
                localeManager.get("y_coord"),
                localeManager.get("date"),
                localeManager.get("an_turnover"),
                localeManager.get("org_type"),
                localeManager.get("org_addr"),
                localeManager.get("owner")
        };
        for (int i = 0; i < columns.length; i++) {
            columnModel.getColumn(i).setHeaderValue(columns[i]);
        }
        table.getTableHeader().repaint();
    }

    public JPanel getTablePanel() {
        return tablePanel;
    }

    public MapView getMapPanel() {
        return mapPanel;
    }

    public MainWindow getMainWindow() {
        return mainWindow;
    }
}

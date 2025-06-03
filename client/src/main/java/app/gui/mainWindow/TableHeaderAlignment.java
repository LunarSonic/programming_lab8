package app.gui.mainWindow;
import utility.LocaleManager;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * Класс для настройки выравнивания заголовков таблицы, сортировки и фильтрации столбцов
 */
public class TableHeaderAlignment implements TableCellRenderer {
    private final TableCellRenderer oldHeaderRenderer;
    private final JTable table;
    private int sortedColumn = -1; //номер столбца, который отсортирован
    private boolean ascending = true; //направление сортировки
    private Predicate<Object> currentFilter;


    public TableHeaderAlignment(JTable table) {
        this.table = table;
        this.oldHeaderRenderer = table.getTableHeader().getDefaultRenderer();
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                if (col > 0) {
                    showFilterPopup(col, e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Метод, который показывает всплывающее меню фильтра и сортировки при клике по заголовку столбца.
     */
    private void showFilterPopup(int column, int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem sortAscendingItem = new JMenuItem(LocaleManager.getInstance().get("sortAsc"));
        sortAscendingItem.setFont(new Font("sansserif", Font.BOLD, 12));
        sortAscendingItem.addActionListener(e -> sortColumn(column, true));
        JMenuItem sortDescendingItem = new JMenuItem(LocaleManager.getInstance().get("sortDesc"));
        sortDescendingItem.addActionListener(e -> sortColumn(column, false));
        sortDescendingItem.setFont(new Font("sansserif", Font.BOLD, 12));
        popupMenu.add(sortAscendingItem);
        popupMenu.add(sortDescendingItem);
        popupMenu.addSeparator();

        JPanel filterPanel = new JPanel(new BorderLayout(5, 5));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel filterLabel = new JLabel(LocaleManager.getInstance().get("filter"));
        filterLabel.setFont(new Font("sansserif", Font.BOLD, 12));

        JTextField filterField = new JTextField(15);
        filterField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 219, 209), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JButton applyFilterBtn = new JButton(LocaleManager.getInstance().get("apply"));
        applyFilterBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        applyFilterBtn.setBackground(new Color(120, 219, 209));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clearFilterBtn = new JButton(LocaleManager.getInstance().get("clear"));
        clearFilterBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        clearFilterBtn.setBackground(new Color(120, 219, 209));

        applyFilterBtn.addActionListener(e -> {
            String filterText = filterField.getText().trim();
            if (!filterText.isEmpty()) {
                applyFilter(column, filterText);
            }
            popupMenu.setVisible(false);
        });
        clearFilterBtn.addActionListener(e -> {
            clearFilter();
            popupMenu.setVisible(false);
        });
        buttonPanel.add(applyFilterBtn);
        buttonPanel.add(clearFilterBtn);
        filterPanel.add(filterLabel, BorderLayout.NORTH);
        filterPanel.add(filterField, BorderLayout.CENTER);
        filterPanel.add(buttonPanel, BorderLayout.SOUTH);
        popupMenu.add(filterPanel);
        popupMenu.show(table.getTableHeader(), x, y);
    }

    /**
     * Метод, выполняющий сортировку по заданному столбцу
     */
    private void sortColumn(int column, boolean ascending) {
        this.sortedColumn = column;
        this.ascending = ascending;
        Comparator<Object[]> comparator = Comparator.comparing(
                row -> (Comparable) row[column]); //приводим значение к Comparable (чтобы объект был сравниваемым)
        if (!ascending) {
            comparator = comparator.reversed();
        }
        CustomTableModel model = (CustomTableModel) table.getModel();
        model.applyFilterAndSort(
                currentFilter != null ? row -> currentFilter.test(row[column]) : null,
                comparator);
    }

    /**
     * Применяет фильтр
     * @param column колонка
     * @param filterText новые отфильтрованные данные
     */
    private void applyFilter(int column, String filterText) {
        currentFilter = createFilter(filterText);
        CustomTableModel model = (CustomTableModel) table.getModel();
        model.applyFilterAndSort(currentFilter != null ? row -> currentFilter.test(row[column]) : null,
                sortedColumn >= 0 ? getCurrentComparator() : null);
    }

    /**
     * Убирает фильтр и сортировку
     */
    private void clearFilter() {
        currentFilter = null;
        CustomTableModel model = (CustomTableModel) table.getModel();
        model.clearFilterAndSort();
    }

    /**
     * Геттер для текущего объекта Comparator
     */
    private Comparator<Object[]> getCurrentComparator() {
        Comparator<Object[]> comparator = Comparator.comparing(
                row -> (Comparable) row[sortedColumn]);
        return ascending ? comparator : comparator.reversed();
    }

    /**
     * Создает предикат-фильтр на основе введенного текста
     * @param filterText текст для фильтра
     */
    private Predicate<Object> createFilter(String filterText) {
        if (filterText.startsWith(">")) {
            return createNumericFilter(filterText.substring(1).trim(),
                    (val, filterVal) -> ((Number) val).doubleValue() > filterVal);
        } else if (filterText.startsWith(">=")) {
            return createNumericFilter(filterText.substring(2).trim(),
                    (val, filterVal) -> ((Number) val).doubleValue() >= filterVal);
        } else if (filterText.startsWith("<")) {
            return createNumericFilter(filterText.substring(1).trim(),
                    (val, filterVal) -> ((Number) val).doubleValue() < filterVal);
        } else if (filterText.startsWith("<=")) {
            return createNumericFilter(filterText.substring(2).trim(),
                    (val, filterVal) -> ((Number) val).doubleValue() <= filterVal);
        } else if (filterText.startsWith("=")) {
            String exactValue = filterText.substring(1).trim();
            return val -> val != null && val.toString().equalsIgnoreCase(exactValue);
        } else {
            String searchText = filterText.toLowerCase();
            return val -> val != null && val.toString().toLowerCase().contains(searchText);
        }
    }

    /**
     * Создает числовой фильтр с заданным условием
     * @param valueText текст для фильтра
     * @param condition условие
     */
    private Predicate<Object> createNumericFilter(String valueText, NumericFilterCondition condition) {
        try {
            double filterValue = Double.parseDouble(valueText);
            return val -> {
                if (val instanceof Number) {
                    return condition.test(((Number) val).doubleValue(), filterValue);
                }
                return false;
            };
        } catch (NumberFormatException e) {
            return val -> false;
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) oldHeaderRenderer.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        return label;
    }

    /**
     * Функциональный интерфейс для числовых условий фильтрации
     */
    @FunctionalInterface
    private interface NumericFilterCondition {
        boolean test(double value, double filterValue);
    }
}
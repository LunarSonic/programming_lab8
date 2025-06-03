package app.gui.mainWindow;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Класс, который представляет собой модель таблицы с поддержкой фильтрации и сортировки данных
 */
public class CustomTableModel extends AbstractTableModel {
    private final String[] columnNames;
    private final List<Object[]> data;
    private List<Object[]> dataToDisplay;

    public CustomTableModel(Object[][] data, String[] columnNames) {
        this.columnNames = columnNames;
        this.data = new ArrayList<>();
        if (data != null) {
            this.data.addAll(Arrays.asList(data));
        }
        this.dataToDisplay = new ArrayList<>(this.data);
    }

    /**
     * Метод, который добавляет строку в таблицу
     * @param rowData массив данных строки
     */
    public void addRow(Object[] rowData) {
        data.add(rowData);
        clearFilterAndSort();
    }

    /**
     * Метод, который устанавливает кол-во строк в таблице
     * @param rowCount кол-во строк
     */
    public void setRowCount(int rowCount) {
        int currentSize = data.size();
        if (rowCount < currentSize) {
            data.subList(rowCount, currentSize).clear();
        } else if (rowCount > currentSize) {
            for (int i = currentSize; i < rowCount; i++) {
                Object[] emptyRow = new Object[getColumnCount()];
                data.add(emptyRow);
            }
        }
        clearFilterAndSort();
    }

    /**
     * Возвращает количество отображаемых строк (с учетом фильтрации)
     */
    @Override
    public int getRowCount() {
        return dataToDisplay.size();
    }


    /**
     * Возвращает количество столбцов таблицы
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Возвращает значение ячейки по индексу строки и столбца
     * @param rowIndex индекс строки
     * @param columnIndex индекс столбца
     * @return значение ячейки
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return dataToDisplay.get(rowIndex)[columnIndex];
    }

    /**
     * Возвращает имя столбца по индексу
     * @param column индекс столбца
     * @return название столбца
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Устанавливает значение в ячейку
     * Поддерживается только редактирование первой колонки
     * @param value новое значение
     * @param rowIndex индекс строки
     * @param columnIndex индекс столбца
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == 0 && value instanceof Boolean) {
            dataToDisplay.get(rowIndex)[columnIndex] = value;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    /**
     * Применяет фильтр и сортировку к данным таблицы
     * @param filter предикат для фильтрации данных
     * @param sorter компаратор для сортировки данных
     */
    public void applyFilterAndSort(Predicate<Object[]> filter, Comparator<Object[]> sorter) {
        dataToDisplay = data.stream().filter(filter != null ? filter : row -> true)
                .sorted(sorter != null ? sorter : Comparator.comparingInt(o -> 0))
                .collect(Collectors.toList());
        fireTableDataChanged();
    }

    /**
     * Очищает все фильтры и сортировку
     */
    public void clearFilterAndSort() {
        dataToDisplay = new ArrayList<>(data);
        fireTableDataChanged();
    }
}

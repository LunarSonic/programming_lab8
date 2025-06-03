package app.gui.mainWindow;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * Рендерер для заголовка таблицы с чекбоксом, управляет выбором строк
 */
public class CheckBoxTableHeaderRenderer extends JCheckBox implements TableCellRenderer {
    private final JTable table;
    private final int column;

    public CheckBoxTableHeaderRenderer(JTable table, int column) {
        this.table = table;
        this.column = column;
        init();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, "background:$Table.background");
        setHorizontalAlignment(SwingConstants.CENTER);
        //обработчик событий для нажатий по заголовку
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int col = table.columnAtPoint(e.getPoint());
                    if (col == column) {
                        boolean newState = !isSelected();
                        if (getClientProperty(FlatClientProperties.SELECTED_STATE) == FlatClientProperties.SELECTED_STATE_INDETERMINATE) {
                            newState = true;
                        }
                        setSelected(newState);
                        selectedTableRow(newState);
                        checkRow();
                    }
                }
            }
        });
        table.getModel().addTableModelListener((me) -> {
            if (me.getColumn() == column) {
                checkRow(); //проверяем все строки на согласованность состояния
            }
        });
    }

    /**
     * Проверка всех строк в столбце и установка состояния заголовочного чекбокса
     */
    private void checkRow() {
        if (table.getRowCount() == 0) {
            setSelected(false);
            return;
        }

        boolean allSelected = true;
        boolean noneSelected = true;

        for (int i = 0; i < table.getRowCount(); i++) {
            boolean isSelected = (boolean) table.getValueAt(i, column);
            allSelected &= isSelected;
            noneSelected &= !isSelected;
        }

        if (allSelected) {
            putClientProperty(FlatClientProperties.SELECTED_STATE, null);
            setSelected(true);
        } else if (noneSelected) {
            putClientProperty(FlatClientProperties.SELECTED_STATE, null);
            setSelected(false);
        } else {
            putClientProperty(FlatClientProperties.SELECTED_STATE,
                    FlatClientProperties.SELECTED_STATE_INDETERMINATE);
        }
        table.getTableHeader().repaint();
    }

    /**
     * Устанавливает значение чекбокса (true/false) для всех строк в указанном столбце
     * @param selected true, если нужно выбрать все строки, false, если нужно снять выбор
     */
    private void selectedTableRow(boolean selected) {
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setValueAt(selected, i, column);
        }
    }

    /**
     * Отрисовка нижней границы заголовка
     * @param g графический контекст
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(UIManager.getColor("TableHeader.bottomSeparatorColor"));
        float size = UIScale.scale(1f);
        g2.fill(new Rectangle2D.Float(0, getHeight() - size, getWidth(), size));
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}

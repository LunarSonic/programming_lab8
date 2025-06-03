package app.gui.mainWindow;
import utility.LocaleManager;
import objects.Address;
import objects.Coordinates;
import objects.Organization;
import objects.OrganizationType;
import utility.SessionHandler;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

/**
 * Класс, который представляет собой форму для ввода и редактирования данных организации
 * Обеспечивает валидацию вводимых данных
 */
public class InputForm extends JPanel {
    private JComboBox<Object> comboType;
    private JTextField org_name;
    private JTextField coord_x;
    private JTextField coord_y;
    private JTextField annual_turnover;
    private JTextField postal_address;
    private JLabel label1, label2, label3, label4, label5, label6;
    private JPanel panel;
    private final LocaleManager localeManager;

    public InputForm() {
        localeManager = LocaleManager.getInstance();
        initComponents();
    }

    /**
     * Инициализирует компоненты формы и настраивает их расположение
     */
    private void initComponents() {
        String[] types = {"PUBLIC", "COMMERCIAL", "TRUST", "OPEN_JOINT_STOCK_COMPANY", "None"};
        comboType = new JComboBox<>(types);
        comboType.setSelectedIndex(4);
        org_name = new JTextField();
        coord_x = new JTextField();
        coord_y = new JTextField();
        annual_turnover = new JTextField();
        postal_address = new JTextField();
        label1 = new JLabel(localeManager.get("org_name"), SwingConstants.TRAILING);
        label1.setFont(new Font("sanserif", Font.PLAIN, 14));
        label2 = new JLabel(localeManager.get("x_coord"), SwingConstants.TRAILING);
        label2.setFont(new Font("sanserif", Font.PLAIN, 14));
        label3 = new JLabel(localeManager.get("y_coord"), SwingConstants.TRAILING);
        label3.setFont(new Font("sanserif", Font.PLAIN, 14));
        label4 = new JLabel(localeManager.get("an_turnover"), SwingConstants.TRAILING);
        label4.setFont(new Font("sanserif", Font.PLAIN, 14));
        label5 = new JLabel(localeManager.get("org_type"), SwingConstants.TRAILING);
        label5.setFont(new Font("sanserif", Font.PLAIN, 14));
        label6 = new JLabel(localeManager.get("org_addr"), SwingConstants.TRAILING);
        label6.setFont(new Font("sanserif", Font.PLAIN, 14));
        panel = new JPanel(new BorderLayout());
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(label1, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                        .addComponent(label2, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                        .addComponent(label3, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                        .addComponent(label4, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                        .addComponent(label5, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                        .addComponent(label6, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                                .addGap(10)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(org_name)
                                        .addComponent(coord_x)
                                        .addComponent(coord_y)
                                        .addComponent(annual_turnover)
                                        .addComponent(comboType)
                                        .addComponent(postal_address)
                                        .addComponent(panel, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE))
                                .addGap(10)));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(20)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label1)
                                .addComponent(org_name, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label2)
                                .addComponent(coord_x, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label3)
                                .addComponent(coord_y, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label4)
                                .addComponent(annual_turnover, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label5)
                                .addComponent(comboType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label6)
                                .addComponent(postal_address, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                        .addGap(10));
    }

    /**
     * Проверяет корректность введенных данных
     * @return true, если все данные валидны, false, если есть ошибки
     */
    private boolean validateData() {
        if (org_name.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    localeManager.get("nameIsNotEmpty"),
                    localeManager.get("error"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (coord_x.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    localeManager.get("coordXIsNotEmpty"),
                    localeManager.get("error"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            double x = Double.parseDouble(coord_x.getText().trim());
            if (x <= -947) {
                JOptionPane.showMessageDialog(this,
                        localeManager.get("coordXIsMoreThanNum"),
                        localeManager.get("error"),
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    localeManager.get("coordXIsDouble"),
                    localeManager.get("error"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (coord_y.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    localeManager.get("coordYIsNotEmpty"),
                    localeManager.get("error"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Long.parseLong(coord_y.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    localeManager.get("coordYIsLong"),
                    localeManager.get("error"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            long turnover = Long.parseLong(annual_turnover.getText().trim());
            if (turnover <= 0) {
                JOptionPane.showMessageDialog(this,
                        localeManager.get("an_turnoverIsMoreThanZero"),
                        localeManager.get("error"),
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    localeManager.get("an_turnoverIsLong"),
                    localeManager.get("error"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (postal_address.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    localeManager.get("addressIsNotEmpty"),
                    localeManager.get("error"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Создает объект Organization на основе введенных данных
     * @return объект Organization или null, если данные невалидны
     */
    public Organization getData() {
        if (!validateData()) {
            return null;
        }
        String orgName = org_name.getText().trim();
        Double coordX = Double.parseDouble(coord_x.getText().trim());
        Long coordY = Long.parseLong(coord_y.getText().trim());
        Coordinates coordinates = new Coordinates(coordX, coordY);
        long annualTurnover = Long.parseLong(annual_turnover.getText());
        OrganizationType orgType;
        Object selectedType = comboType.getSelectedItem();
        if (!selectedType.equals("None")) {
            orgType = OrganizationType.valueOf(selectedType.toString());
        } else {
            orgType = null;
        }
        Address postalAddress = new Address(postal_address.getText().trim());
        String username = SessionHandler.getCurrentUser().getLogin();
        return new Organization(
                null,
                orgName,
                coordinates,
                LocalDateTime.now(),
                annualTurnover,
                orgType,
                postalAddress,
                username
        );
    }

    /**
     * Загружает данные организации в форму для редактирования
     * @param org объект Organization
     */
    public void loadData(Organization org) {
        org_name.setText(org.getName());
        coord_x.setText(String.valueOf(org.getCoordinates().getX()));
        coord_y.setText(String.valueOf(org.getCoordinates().getY()));
        annual_turnover.setText(String.valueOf(org.getAnnualTurnover()));
        postal_address.setText(org.getPostalAddress().getStreet());
        comboType.setSelectedItem(org.getType() != null ? org.getType().toString() : "None");
    }
}

package app.gui.mainWindow;
import utility.LocaleManager;
import javax.swing.*;

public class AnnualTurnoverForm {
    private JTextField annualTurnoverField;

    public AnnualTurnoverForm() {
        this.annualTurnoverField = new JTextField(20);
    }

    public boolean showForm() {
        JPanel panel = new JPanel();
        panel.add(new JLabel(LocaleManager.getInstance().get("enterAT")));
        panel.add(annualTurnoverField);
        int option = JOptionPane.showConfirmDialog(
                null,
                panel,
                LocaleManager.getInstance().get("annualTurnover"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            return validateData();
        }
        return false;
    }

    public boolean validateData() {
        String annual_turnover = annualTurnoverField.getText().trim();
        try {
            long turnover = Long.parseLong(annual_turnover);
            if (turnover <= 0) {
                JOptionPane.showMessageDialog(null,
                        LocaleManager.getInstance().get("an_turnoverIsMoreThanZero"),
                        LocaleManager.getInstance().get("error"),
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    LocaleManager.getInstance().get("an_turnoverIsLong"),
                    LocaleManager.getInstance().get("error"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public String getAnnualTurnover() {
        return annualTurnoverField.getText().trim();
    }
}

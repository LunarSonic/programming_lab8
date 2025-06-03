package app.gui.components.buttons;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;

/**
 * Класс ButtonAction, кнопка со скруглёнными углами
 */
public class ButtonAction extends JButton {
    public ButtonAction() {
        putClientProperty(FlatClientProperties.STYLE, " "
                + "arc:15;"
                + "focusWidth:0;"
                + "borderWidth:0;"
                + "margin:5,20,5,20;"
                + "innerFocusWidth:0;"
        );
    }
}

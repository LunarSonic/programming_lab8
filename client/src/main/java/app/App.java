package app;
import app.gui.mainWindow.MainWindow;
import app.network.NetworkHandler;
import app.gui.userWindow.AuthWindow;
import app.utility.UserModule;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import javax.swing.*;
import java.awt.*;

/**
 * Класс для запуска клиентского приложения
 */
public class App {
    public static void main(String[] args) {
        UIManager.put("defaultFont", new Font("saanserif", Font.PLAIN, 13));
        FlatMacLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            var networkHandler = new NetworkHandler("localhost", 8894);
            networkHandler.connect();
            var userModule = new UserModule();
            userModule.setNetworkHandler(networkHandler);
            AuthWindow authWindow = new AuthWindow(userModule);
            authWindow.setOnLoginSuccess(()-> {
                JFrame mainWindow = new MainWindow(networkHandler);
                mainWindow.setVisible(true);
                authWindow.dispose();
            });
            authWindow.setVisible(true);
        });
    }
}


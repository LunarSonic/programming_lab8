package app.gui.components.header;
import net.miginfocom.swing.MigLayout;
import raven.utils.FlatLafStyleUtils;
import javax.swing.*;

/**
 * Класс Header, который представляет собой панель с информацией о пользователе
 */
public class Header extends JPanel {
    private JLabel login;
    private JLabel profile;
    private HeaderInfo headerInfo;

    /**
     * Констркутор Header
     * @param info объект HeaderInfo, содержащий данные о профиле и стиле
     */
    public Header(HeaderInfo info) {
        this.headerInfo = info;
        init();
    }

    /**
     * Инициализация компонента
     */
    private void init() {
        setLayout(new MigLayout("wrap, insets 10 20 5 20, fill, gap 3")); //настройка контейнера
        profile = new JLabel(headerInfo.icon);
        login = new JLabel(headerInfo.title);
        //устанавливаем стили
        if (headerInfo.style != null) {
            headerInfo.style.styleHeader(this);
            headerInfo.style.styleProfile(profile);
            headerInfo.style.styleLogin(login);
        }
        FlatLafStyleUtils.appendStyleIfAbsent(this, " "
                + "background:null");
        FlatLafStyleUtils.appendStyleIfAbsent(this, " "
                + "background:$Component.borderColor");
        FlatLafStyleUtils.appendStyleIfAbsent(this, " "
                + "font:-1;"
                + "foreground:lighten(@foreground,30%)"); //более светлый оттенок текста
        add(profile);
        add(login);
    }
}

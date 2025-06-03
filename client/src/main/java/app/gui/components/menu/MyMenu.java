package app.gui.components.menu;
import raven.utils.FlatLafStyleUtils;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Класс MyMenu, представляет собой панель, которая строит меню на основе MenuOption
 */
public class MyMenu extends JPanel {
    private MenuOption menuOption;

    public MyMenu(MenuOption menuOption) {
        this.menuOption = menuOption;
        init();
    }

    public void init() {
        setLayout(new MenuLayout());
        if (menuOption.menuStyle != null) {
            menuOption.menuStyle.styleMenu(this);
        }
        createMenu();
    }

    /**
     * Метод, который копирует массив индексов
     * @param array массив индексов
     * @return новый массив с копией переданного массива
     */
    protected int[] copyArray(int[] array) {
        return Arrays.copyOf(array, array.length);
    }

    /**
     * Метод, который получает иконку для элемента меню
     * @param icon путь к иконке
     * @param menuLevel уровень меню
     * @return иконка для элемента меню
     */
    protected Icon getIcon(String icon, int menuLevel) {
        if (icon != null) {
            String path = getBasePath();
            float iconScale;
            if (menuLevel < menuOption.iconScale.length) {
                iconScale = menuOption.iconScale[menuLevel];
            } else {
                iconScale = menuOption.iconScale[menuOption.iconScale.length - 1];
            }
            Icon iconObject = menuOption.buildMenuIcon(path + icon, iconScale);
            return iconObject;
        } else {
            return null;
        }
    }

    /**
     * Возвращает путь для иконки
     * @return строка с путём
     */
    private String getBasePath() {
        if (menuOption.baseIconPath == null) {
            return "";
        }
        if (menuOption.baseIconPath.endsWith("/")) {
            return menuOption.baseIconPath;
        } else {
            return menuOption.baseIconPath + "/";
        }
    }

    /**
     * Метод, который создаёт элемент меню (кнопку)
     * @param name название
     * @param icon иконка
     * @param ind индекс элемента
     * @param menuLevel уровень меню
     * @return кнопка
     */
    public JButton createMenuItem(String name, String icon, int[] ind, int menuLevel) {
        JButton button = new JButton(name);
        Icon iconObj = getIcon(icon, menuLevel);
        if (iconObj != null) {
            button.setIcon(iconObj);
        }
        button.setHorizontalAlignment(JButton.LEADING);
        if (menuOption.menuStyle != null) {
            menuOption.menuStyle.styleMenuItem(button, copyArray(ind));
        }
        button.setFont(new Font("sanserif", Font.BOLD, 14));
        FlatLafStyleUtils.appendStyleIfAbsent(button, " " +
                "arc:0;" +
                "margin:6,20,6,20;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "background:null;" +
                "foreground:#78dbd0ff;" +
                "iconTextGap:8");
        return button;
    }

    /**
     * Создает все элементы меню и добавляет их на панель
     */
    public void createMenu() {
        MenuItem[] menuItems = menuOption.menuItems;
        if (menuItems != null) {
            int ind = 0;
            int validationInd = -1;
            for (int i = 0; i < menuItems.length; i++) {
                MenuItem menuItem = menuItems[i];
                if (menuItem instanceof Item item) {
                    int[] arrInd = {ind};
                    if (item.isSubmenuAble()) {
                        //создание подменю
                        int[] arrValidationInd = {++validationInd};
                        boolean valid = menuOption.menuValidation.menuValidation(copyArray(arrValidationInd));
                        if (valid) {
                            add(createSubmenuItem(item, arrInd, arrValidationInd, 0, menuOption));
                        }
                        if (valid || menuOption.menuValidation.keepMenuValidationIndex) {
                            ind++;
                        }
                    } else {
                        int[] arrValidationInd = {++validationInd};
                        boolean valid = menuOption.menuValidation.menuValidation(arrValidationInd);
                        if (valid) {
                            JButton button = createMenuItem(item.getName(), item.getIcon(), arrInd, 0);
                            applyMenuEvent(button, arrInd);
                            add(button);
                        }
                        if (valid || menuOption.menuValidation.keepMenuValidationIndex) {
                            ind++;
                        }
                    }
                } else if (menuItem instanceof Item.Label label) {
                    if (checkLabelValidation(i, ind)) {
                        add(createLabel(label.getName()));
                    }
                }
            }
        }
    }

    /**
     * Метод, который применяет событие для кнопки меню
     * @param button кнопка элемента меню
     * @param ind индекс элемента меню
     */
    public void applyMenuEvent(JButton button, int[] ind) {
        button.addActionListener(e -> {
            runEvent(ind);
        });
    }

    /**
     * Запускает событие для выбранного элемента меню
     * @param ind индекс выбранного элемента
     * @return объект Action, если событие выполнено
     */
    private Action runEvent(int[] ind) {
        if (!menuOption.events.isEmpty()) {
            Action action = new Action();
            if (menuOption.menuItemAutoSelect) {
                action.selected();
            }
            for (Event event : menuOption.events) {
                event.selected(action, copyArray(ind));
            }
            return action;
        }
        return null;
    }


    public Component createSubmenuItem(Item menu, int[] ind, int[] validationInd, int menuLevel, MenuOption menuOption) {
        JPanel panelItem = new SubmenuItem(menu, ind, validationInd, menuLevel, this, menuOption);
        return panelItem;
    }

    /**
     * Проверяет, должно ли отображаться меню в случае, если это метка
     * @param labelInd индекс метки
     * @param menuInd индекс меню
     * @return true, если метка должна отображаться
     */
    private boolean checkLabelValidation(int labelInd, int menuInd) {
        if (menuOption.menuValidation.labelValidation(labelInd)) {
            if (menuOption.menuValidation.removeLabelWhenEmptyMenu) {
                boolean fondMenu = false;
                for (int i = labelInd + 1; i < menuOption.menuItems.length; i++) {
                    MenuItem menuItem = menuOption.menuItems[i];
                    if (menuItem.isMenu()) {
                        if (menuOption.menuValidation.menuValidation(new int[]{menuInd})) {
                            fondMenu = true;
                            break;
                        }
                    } else {
                        break;
                    }
                    menuInd++;
                }
                return fondMenu;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Создает метку для меню
     * @param name название метки
     * @return компонент метки
     */
    public Component createLabel(String name) {
        JLabel label = new JLabel(name);
        label.setFont(new Font("sanserif", Font.BOLD, 16));
        if (menuOption.menuStyle != null) {
            menuOption.menuStyle.styleLabel(label);
        }
        FlatLafStyleUtils.appendStyleIfAbsent(label, " " +
                "border:8,10,8,10;");
        return label;
    }
}
package app.gui.mainWindow;
import app.gui.components.buttons.ButtonAction;
import app.network.NetworkHandler;
import app.network.RequestCreator;
import app.utility.CommandHandler;
import app.utility.Runner;
import commands.CommandName;
import network.ExecutionResponse;
import network.Request;
import objects.Organization;
import raven.popup.DefaultOption;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.toast.Notifications;
import utility.LocaleManager;
import utility.SessionHandler;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Класс для обработки действий, связанных с организациями, в главном окне
 */
public class OrganizationActionHandler {
    private final NetworkHandler networkHandler;
    private final LocaleManager localeManager;
    private final OrganizationTableController tableController;
    private final RequestCreator requestCreator;
    private final Runner runner;
    private final CommandHandler commandHandler;
    private final DialogManager dialogManager;
    private ButtonAction addCommand;
    private ButtonAction removeCommand;
    private ButtonAction updateCommand;

    public OrganizationActionHandler(NetworkHandler networkHandler, LocaleManager localeManager, OrganizationTableController tableController) {
        this.networkHandler = networkHandler;
        this.localeManager = localeManager;
        this.tableController = tableController;
        this.dialogManager = new DialogManager(null);
        this.requestCreator = new RequestCreator(SessionHandler.getCurrentUser());
        this.commandHandler = new CommandHandler(networkHandler, SessionHandler.getCurrentUser());
        this.runner = new Runner(commandHandler);
        this.runner.setUser(SessionHandler.getCurrentUser());
        setupActionButtons();
    }

    /**
     * Инициализирует кнопки
     */
    public void setupActionButtons() {
        addCommand = createButton(localeManager.get("addCommand"), this::executeAddCommand);
        removeCommand = createButton(localeManager.get("removeCommand"), this::executeRemoveCommand);
        updateCommand = createButton(localeManager.get("updateCommand"), this::executeUpdateCommand);
    }

    /**
     * Создаёт кнопку с заданным текстом и обработчиком нажатия
     * @param text текст на кнопке
     * @param listener слушатель событий
     * @return объект ButtonAction
     */
    private ButtonAction createButton(String text, ActionListener listener) {
        ButtonAction button = new ButtonAction();
        button.setText(text);
        button.addActionListener(listener);
        button.setFont(new Font("sansserif", Font.PLAIN, 12));
        button.setBackground(new Color(120, 219, 208));
        return button;
    }

    /**
     * Выполняет команду добавления новой организации
     * @param e событие нажатия на кнопку
     */
    public void executeAddCommand(ActionEvent e) {
        InputForm form = new InputForm();
        SessionHandler.getCurrentUser();
        DefaultOption option = new DefaultOption() {
            @Override
            public boolean closeWhenClickOutside() {
                return true;
            }
        };
        String[] actions = {localeManager.get("cancel"), localeManager.get("save")};
        GlassPanePopup.showPopup(new SimplePopupBorder(form, localeManager.get("createOrg"), actions, (pc, i) -> {
            if (i == 1) {
                Organization org = form.getData();
                if (org != null) {
                    requestCreator.setTemporaryOrganization(org);
                    Request request = requestCreator.createRequest(CommandName.add, new String[0]);
                    request.setLocale(localeManager.getCurrentLocale());
                    ExecutionResponse response = networkHandler.sendAndReceive(request);
                    if (response.getResponse()) {
                        tableController.loadCollection();
                        pc.closePopup();
                        String message = response.getMessage();
                        Notifications.getInstance().show(Notifications.Type.SUCCESS, message);
                        tableController.updateTable();
                    } else {
                        String message = response.getMessage();
                        Notifications.getInstance().show(Notifications.Type.ERROR, message);
                    }
                }
            } else {
                pc.closePopup();
            }
        }), option);
    }

    /**
     * Выполняет команду удаления выбранной организации
     * @param e событие нажатия на кнопку
     */
    public void executeRemoveCommand(ActionEvent e) {
        List<Organization> selectedOrgs = tableController.getSelectedOrganizations();
        if (!selectedOrgs.isEmpty()) {
            if (selectedOrgs.size() == 1) {
                Organization selectedOrg = selectedOrgs.get(0);
                DefaultOption option = new DefaultOption() {
                    @Override
                    public boolean closeWhenClickOutside() {
                        return true;
                    }
                };
                String[] actions = {localeManager.get("cancel"), localeManager.get("remove")};
                JLabel label = new JLabel(localeManager.get("askForRemove"));
                label.setBorder(new EmptyBorder(0, 25, 0, 25));
                Locale locale = SessionHandler.getCurrentLocale();
                localeManager.setLocale(locale);
                GlassPanePopup.showPopup(
                        new SimplePopupBorder(label, localeManager.get("confirmDelete"), actions, (pc, i) -> {
                            if (i == 1) {
                                Request request = requestCreator.createRequest(CommandName.remove_by_id, new String[]{String.valueOf(selectedOrg.getId())});
                                request.setLocale(localeManager.getCurrentLocale());
                                ExecutionResponse response = networkHandler.sendAndReceive(request);
                                if (response.getResponse()) {
                                    tableController.loadCollection();
                                    pc.closePopup();
                                    Notifications.getInstance().show(Notifications.Type.SUCCESS, response.getMessage());
                                    tableController.updateTable();
                                } else {
                                    Notifications.getInstance().show(Notifications.Type.ERROR, response.getMessage());
                                }
                            } else {
                                pc.closePopup();
                            }
                        }), option);
            } else {
                Notifications.getInstance().show(Notifications.Type.WARNING, localeManager.get("selectOneRow"));
            }
        } else {
            Notifications.getInstance().show(Notifications.Type.WARNING, localeManager.get("selectRowToRemove"));
        }
    }

    /**
     * Выполняет команду обновления выбранной организации
     * @param e событие нажатия на кнопку
     */
    public void executeUpdateCommand(ActionEvent e) {
        InputForm form = new InputForm();
        List<Organization> selectedOrgs = tableController.getSelectedOrganizations();
        if (!selectedOrgs.isEmpty()) {
            if (selectedOrgs.size() == 1) {
                Organization selectedOrg = selectedOrgs.get(0);
                form.loadData(selectedOrg);
                DefaultOption option = new DefaultOption() {
                    @Override
                    public boolean closeWhenClickOutside() {
                        return true;
                    }
                };
                String[] actions = {localeManager.get("cancel"), localeManager.get("save")};
                GlassPanePopup.showPopup(
                        new SimplePopupBorder(form, localeManager.get("updateOrg"), actions, (pc, i) -> {
                            if (i == 1) {
                                Organization updatedOrg = form.getData();
                                if (updatedOrg != null) {
                                    updatedOrg.setId(selectedOrg.getId());
                                    requestCreator.setTemporaryOrganization(updatedOrg);
                                    Request request = requestCreator.createRequest(
                                            CommandName.update,
                                            new String[]{String.valueOf(updatedOrg.getId())});
                                    request.setLocale(localeManager.getCurrentLocale());
                                    ExecutionResponse response = networkHandler.sendAndReceive(request);
                                    if (response.getResponse()) {
                                        tableController.loadCollection();
                                        pc.closePopup();
                                        Notifications.getInstance().show(Notifications.Type.SUCCESS, response.getMessage());
                                        tableController.updateTable();
                                    } else {
                                        Notifications.getInstance().show(Notifications.Type.ERROR, response.getMessage());
                                    }
                                }
                            } else {
                                pc.closePopup();
                            }
                        }), option);
            } else {
                Notifications.getInstance().show(Notifications.Type.WARNING, localeManager.get("selectOneRow"));
            }
        } else {
            Notifications.getInstance().show(Notifications.Type.WARNING, localeManager.get("selectRowToUpdate"));
        }
    }

    /**
     * Открывает форму редактирования организации, выбранной на карте
     * @param org организация, которую нужно отредактировать
     */
    public void editOrganizationFromMap(Organization org) {
        InputForm form = new InputForm();
        form.loadData(org);
        GlassPanePopup.showPopup(
                new SimplePopupBorder(form, localeManager.get("updateOrg"),
                        new String[]{localeManager.get("cancel"), localeManager.get("save")}, (pc, i) -> {
                    if (i == 1) {
                        Organization updatedOrg = form.getData();
                        if (updatedOrg != null) {
                            updatedOrg.setId(org.getId());
                            requestCreator.setTemporaryOrganization(updatedOrg);
                            Request request = requestCreator.createRequest(CommandName.update, new String[]{String.valueOf(updatedOrg.getId())});
                            request.setLocale(localeManager.getCurrentLocale());
                            ExecutionResponse response = networkHandler.sendAndReceive(request);
                            if (response.getResponse()) {
                                tableController.loadCollection();
                                pc.closePopup();
                                Notifications.getInstance().show(
                                        Notifications.Type.SUCCESS,
                                        response.getMessage());
                                tableController.updateTable();
                            } else {
                                Notifications.getInstance().show(
                                        Notifications.Type.ERROR,
                                        response.getMessage());
                            }
                        }
                    } else {
                        pc.closePopup();
                    }
                }));
    }

    /**
     * Удаляет организацию, выбранную на карте
     * @param org организация для удаления
     */
    public void removeOrganizationFromMap(Organization org) {
        String[] actions = {localeManager.get("cancel"), localeManager.get("remove")};
        JLabel label = new JLabel(localeManager.get("askForRemove"));
        label.setBorder(new EmptyBorder(0, 25, 0, 25));
        Locale locale = SessionHandler.getCurrentLocale();
        localeManager.setLocale(locale);
        GlassPanePopup.showPopup(
                new SimplePopupBorder(label, localeManager.get("confirmDelete"), actions, (pc, i) -> {
                    if (i == 1) {
                        Request request = requestCreator.createRequest(CommandName.remove_by_id, new String[]{String.valueOf(org.getId())});
                        request.setLocale(localeManager.getCurrentLocale());
                        ExecutionResponse response = networkHandler.sendAndReceive(request);
                        if (response.getResponse()) {
                            tableController.loadCollection();
                            pc.closePopup();
                            Notifications.getInstance().show(
                                    Notifications.Type.SUCCESS,
                                    response.getMessage());
                            tableController.updateTable();
                        } else {
                            Notifications.getInstance().show(
                                    Notifications.Type.ERROR,
                                    response.getMessage());
                        }
                    } else {
                        pc.closePopup();
                    }
                }));
    }

    /**
     * Выполняет команды add if max, add if min
     * @param command команда (add if max или add if min)
     */
    public void executeAddIfMaxAndAddIfMinCommand(CommandName command) {
        InputForm form = new InputForm();
        DefaultOption option = new DefaultOption() {
            @Override
            public boolean closeWhenClickOutside() {
                return true;
            }
        };
        String[] actions = {localeManager.get("cancel"), localeManager.get("save")};
        GlassPanePopup.showPopup(new SimplePopupBorder(form, localeManager.get("createOrg"), actions, (pc, i) -> {
            if (i == 1) {
                Organization org = form.getData();
                if (org != null) {
                    requestCreator.setTemporaryOrganization(org);
                    Request request = requestCreator.createRequest(command, new String[0]);
                    request.setLocale(localeManager.getCurrentLocale());
                    ExecutionResponse response = networkHandler.sendAndReceive(request);
                    if (response.getResponse()) {
                        tableController.loadCollection();
                        tableController.updateTable();
                        pc.closePopup();
                        Notifications.getInstance().show(Notifications.Type.SUCCESS, response.getMessage());
                    } else {
                        Notifications.getInstance().show(Notifications.Type.ERROR, response.getMessage());
                    }
                }
            } else {
                pc.closePopup();
            }
        }), option);
    }

    /**
     * Выполняет команду очистки коллекции
     */
    public void executeClearCommand() {
        Request request = requestCreator.createRequest(CommandName.clear, new String[0]);
        request.setLocale(localeManager.getCurrentLocale());
        ExecutionResponse response = networkHandler.sendAndReceive(request);
        if (response.getResponse()) {
            tableController.loadCollection();
            tableController.updateTable();
            Notifications.getInstance().show(Notifications.Type.SUCCESS, response.getMessage());
        }
    }

    /**
     * Выполняет команду удаления всех организаций с заданным годовым оборотом
     */
    public void executeRemoveAllByAnnualTurnoverCommand() {
        AnnualTurnoverForm annualTurnoverForm = new AnnualTurnoverForm();
        if (annualTurnoverForm.showForm()) {
            String annualTurnoverStr = annualTurnoverForm.getAnnualTurnover();
            try {
                long annualTurnover = Long.parseLong(annualTurnoverStr);
                String[] args = {String.valueOf(annualTurnover)};
                Request request = requestCreator.createRequest(CommandName.remove_all_by_annual_turnover, args);
                request.setLocale(localeManager.getCurrentLocale());
                ExecutionResponse response = networkHandler.sendAndReceive(request);
                if (response.getResponse()) {
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, response.getMessage());
                    tableController.loadCollection();
                    tableController.updateTable();
                } else {
                    Notifications.getInstance().show(Notifications.Type.ERROR, response.getMessage());
                }
            } catch (NumberFormatException ex) {
                Notifications.getInstance().show(Notifications.Type.ERROR, localeManager.get("invalidAT"));
            }
        }
    }

    /**
     * Выполняет команду, не требующую аргументов
     * @param command команда
     */
    public void executeCommandWithoutArgs(CommandName command) {
        Request request = requestCreator.createRequest(command, new String[0]);
        request.setLocale(localeManager.getCurrentLocale());
        ExecutionResponse response = networkHandler.sendAndReceive(request);
        dialogManager.createDialog(response);
    }

    /**
     * Выполняет команду help
     */
    public void executeHelpCommand() {
        runner.reinitCommands();
        ExecutionResponse response = runner.launchCommand(new String[]{"help"});
        dialogManager.createDialog(response);
    }

    /**
     * Выполняет команду executeScript
     */
    public void performExecuteScriptCommand() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(localeManager.get("selectFile"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("(*.txt)", "txt"));
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            Request request = requestCreator.createRequest(CommandName.execute_script, new String[0]);
            request.setLocale(localeManager.getCurrentLocale());
            ExecutionResponse response = runner.launchCommand(new String[]{"execute_script", filePath});
            dialogManager.createDialogForScript(response);
        }
        tableController.loadCollection();
        tableController.updateTable();
    }

    /**
     * Обновляет надписи кнопок при смене языка
     */
    public void updateOnLanguageChange() {
        addCommand.setText(localeManager.get("addCommand"));
        removeCommand.setText(localeManager.get("removeCommand"));
        updateCommand.setText(localeManager.get("updateCommand"));
    }

    public ButtonAction getAddButton() {
        return addCommand;
    }

    public ButtonAction getUpdateButton() {
        return updateCommand;
    }

    public ButtonAction getRemoveButton() {
        return removeCommand;
    }
}
package me.machadolucas.diagnosis.ui.admin.manage;

import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import me.machadolucas.diagnosis.entity.AdminUser;
import me.machadolucas.diagnosis.entity.Person;
import me.machadolucas.diagnosis.service.PasswordHash;
import me.machadolucas.diagnosis.ui.admin.AdminPanelView;
import me.machadolucas.diagnosis.ui.admin.AdminViews;

public class ManageAdminUsersView extends AdminViews {

    Label pageTitle = new Label("Gerenciar administradores");

    Table adminUsersTable = new Table("Usuários administradores cadastrados:");

    TextField adminName = new TextField("Nome de usuário");
    PasswordField adminPassword = new PasswordField("Senha de acesso");
    Button createAdminUserButton = new Button("Criar novo usuário", this::createAdminUser);

    Button backButton = new Button("Voltar", this::goBack);

    public ManageAdminUsersView(final AdminUser adminUser, final List<AdminUser> users) {
        this.adminUser = adminUser;

        configureComponents();
        buildLayout();
        configureTable(users);
    }

    @Override
    protected void configureComponents() {
        setMargin(true);
        setSpacing(true);

        this.adminName.setRequired(true);
        this.adminName.setNullSettingAllowed(false);
        this.adminName.setRequiredError("É necessario digitar o nome de usuário");
        this.adminName.addValidator(new RegexpValidator("^[a-zA-Z0-9]+$",
                "O nome de usuário deve conter apenas letras e números, sem espaços."));
        this.adminName.setImmediate(true);
        this.adminPassword.setRequired(true);
        this.adminPassword.setMaxLength(16);
        this.adminPassword.addValidator(
                new StringLengthValidator("A senha de acesso deve ter entre 4 a 16 dígitos", 4, 16, false));
        this.adminPassword.setNullSettingAllowed(false);
        this.adminPassword.setRequiredError("É necessario digitar a senha de acesso");
        this.adminPassword.setImmediate(true);

        this.createAdminUserButton.setStyleName(ValoTheme.BUTTON_PRIMARY);

        this.backButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);

    }

    @Override
    protected void buildLayout() {
        final HorizontalLayout header = getHeaderMenu(this.pageTitle);

        final Label createLabel = new Label("Criar novo usuário administrador:");
        createLabel.addStyleName(ValoTheme.LABEL_H2);
        final VerticalLayout createUserForm = new VerticalLayout();
        createUserForm.setSpacing(true);
        createUserForm.addComponents(createLabel, this.adminName, this.adminPassword, this.createAdminUserButton);

        final Label manageLabel = new Label("Gerenciar usuários existentes:");
        manageLabel.addStyleName(ValoTheme.LABEL_H2);
        final VerticalLayout manageUsersForm = new VerticalLayout();
        manageUsersForm.setSpacing(true);
        manageUsersForm.addComponents(manageLabel, this.adminUsersTable);

        final HorizontalLayout buttonsBar = new HorizontalLayout();
        buttonsBar.setSpacing(true);
        buttonsBar.addComponents(this.backButton);

        addComponents(header, createUserForm, getGap("2em"), manageUsersForm, getGap("2em"), buttonsBar);
    }

    private void configureTable(final List<AdminUser> users) {

        this.adminUsersTable.addContainerProperty("Nome do administrador", String.class, null);
        this.adminUsersTable.addContainerProperty("Numero de pacientes", String.class, null);
        this.adminUsersTable.addContainerProperty("Data de criação", String.class, null);
        this.adminUsersTable.addContainerProperty("Remover usuário", Button.class, null);
        users.forEach(this::insertUserInTable);
        this.adminUsersTable.setPageLength(users.size());
    }

    // =================================================================================

    public void goBack(final Button.ClickEvent event) {
        getAdminRoot().getContentPanel().setContent(new AdminPanelView(this.adminUser));
    }

    public void removeAdminUser(final Button.ClickEvent event) {
        final AdminUser userToRemove = (AdminUser) event.getButton().getData();
        if (userToRemove.getName().equals(this.adminUser.getName())) {
            Notification.show("Não é possível remover o usuário atualmente logado.", Notification.Type.WARNING_MESSAGE);
        } else if (userToRemove.getName().equals("admin")) {
            Notification.show("Não é possível remover o usuário admin.", Notification.Type.WARNING_MESSAGE);
        } else {
            ConfirmDialog.show(getAdminRoot().getUI(), "Tem certeza que que deletar esse usuário?",
                    "Essa ação é irreversível. Todos os pacientes relacionados serão transferidos para o usuário 'admin'.",
                    "Sim", "Não", (ConfirmDialog.Listener) dialog -> {
                        if (dialog.isConfirmed()) {
                            final List<Person> people = getAdminRoot().getPersonRepository()
                                    .findByAdminResponsible(userToRemove);
                            // Se tiver pacientes, transfere pro administrador admin
                            if (people.size() > 0) {
                                final AdminUser sudo = getAdminRoot().getAdminUserRepository().findByName("admin");
                                people.forEach(person -> {
                                    person.setAdminResponsible(sudo);
                                    getAdminRoot().getPersonRepository().save(person);
                                });
                            }
                            this.adminUsersTable.removeItem(userToRemove.getName());
                            this.adminUsersTable.setPageLength(this.adminUsersTable.getPageLength() - 1);
                            getAdminRoot().getAdminUserRepository().delete(userToRemove);
                            Notification.show("Usuário " + userToRemove.getName() + " removido com sucesso.",
                                    Notification.Type.HUMANIZED_MESSAGE);
                        }
                    });
        }
    }

    public void createAdminUser(final Button.ClickEvent event) {
        try {
            this.adminName.validate();
            this.adminPassword.validate();

            this.adminName.setValue(this.adminName.getValue().toLowerCase());

            final AdminUser foundUser = getAdminRoot().getAdminUserRepository().findByName(this.adminName.getValue());
            if (foundUser == null) {
                final AdminUser user = new AdminUser();
                user.setName(this.adminName.getValue());
                user.setPassword(PasswordHash.createHash(this.adminPassword.getValue()));

                getAdminRoot().getAdminUserRepository().save(user);

                insertUserInTable(user);
                this.adminUsersTable.setPageLength(this.adminUsersTable.getPageLength() + 1);

                Notification.show("Usuário " + user.getName() + " criado com sucesso.",
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                throw new Validator.InvalidValueException(
                        "Já existe um usuário com esse nome. Escolha outro e tente novamente.");
            }

        } catch (final Validator.InvalidValueException e) {
            Notification.show("Atenção!", e.getMessage(), Notification.Type.WARNING_MESSAGE);
        } catch (final Exception e) {
            Notification.show("Erro no banco de dados. Contate um desenvolvedor.", e.getMessage(),
                    Notification.Type.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void insertUserInTable(final AdminUser user) {
        final Button removeAdminUserButton = new Button("Remover", this::removeAdminUser);
        removeAdminUserButton.setData(user);
        removeAdminUserButton.addStyleName(ValoTheme.BUTTON_DANGER);
        if (!this.adminUser.getName().equals("admin")) {
            removeAdminUserButton.setEnabled(false);
        }
        final String personsAmount = user.getPersonsAmount().toString();
        this.adminUsersTable.addItem(new Object[] { user.getName(), personsAmount,
                user.getCreationDate().toString(), removeAdminUserButton}, user.getName());
    }

}

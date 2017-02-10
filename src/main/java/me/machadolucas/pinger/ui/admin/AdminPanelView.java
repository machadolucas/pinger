package me.machadolucas.diagnosis.ui.admin;

import java.util.List;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import me.machadolucas.diagnosis.entity.AdminUser;
import me.machadolucas.diagnosis.entity.Person;
import me.machadolucas.diagnosis.exception.NotFoundException;
import me.machadolucas.diagnosis.ui.admin.manage.ChangePasswordView;
import me.machadolucas.diagnosis.ui.admin.manage.ManageAdminUsersView;
import me.machadolucas.diagnosis.ui.admin.results.ManagePersonsView;
import me.machadolucas.diagnosis.ui.admin.results.PersonResultView;

public class AdminPanelView extends AdminViews {

    private Label pageTitle = new Label("Painel administrativo");

    private TextField personCode = new TextField("Código do paciente");
    private Button searchResultsButton = new Button("Exibir respostas", this::searchResults);
    private Button managePersonsButton = new Button("Gerenciar pacientes", this::managePersons);
    private Button changePasswordButton = new Button("Alterar senha administrativa", this::changePassword);
    private Button manageAdminsButton = new Button("Gerenciar administradores", this::manageAdmin);

    public AdminPanelView(final AdminUser adminUser) {
        this.adminUser = adminUser;

        configureComponents();
        buildLayout();
    }

    protected void configureComponents() {
        setMargin(true);
        setSpacing(true);

        this.personCode.setRequired(true);
        this.personCode.setMaxLength(5);
        this.personCode
                .addValidator(new StringLengthValidator("O código do paciente deve ter 5 caracteres", 5, 5, false));
        this.personCode.setNullSettingAllowed(false);
        this.personCode.setRequiredError("É necessario digitar o código do paciente");
        this.personCode.setImmediate(true);

        this.managePersonsButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        this.searchResultsButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        this.manageAdminsButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
    }

    protected void buildLayout() {

        final HorizontalLayout header = getHeaderMenu(this.pageTitle);

        final HorizontalLayout hor = new HorizontalLayout();
        hor.setSpacing(true);
        final VerticalLayout ver1 = new VerticalLayout();
        final VerticalLayout ver2 = new VerticalLayout();

        final Label personSideLabel = new Label("Resultados de pacientes");
        personSideLabel.addStyleName(ValoTheme.LABEL_H2);
        ver1.setSpacing(true);
        ver1.setMargin(true);
        ver1.addComponents(personSideLabel, this.managePersonsButton, getGap("2em"), this.personCode,
                this.searchResultsButton);

        final Label manageAdminLabel = new Label("Gerenciar usuários administrativos");
        manageAdminLabel.addStyleName(ValoTheme.LABEL_H2);
        ver2.setSpacing(true);
        ver2.setMargin(true);
        ver2.addComponents(manageAdminLabel, this.manageAdminsButton, getGap("2em"), this.changePasswordButton);

        hor.addComponents(ver1, ver2);
        addComponents(header, hor);
    }

    // =================================================================================

    private void searchResults(final Button.ClickEvent event) {
        try {
            this.personCode.validate();

            final Person person = getAdminRoot().getPersonRepository()
                    .findByPersonCodeAndAdminResponsible(this.personCode.getValue(), this.adminUser);
            if (person == null) {
                throw new NotFoundException("Código de paciente não encontrado.");
            }
            person.postConstruct();
            getAdminRoot().getQuestionnaireRepository().save(person.getInexistentQuestionnaires());
            getAdminRoot().getPersonRepository().save(person);

            getAdminRoot().getContentPanel().setContent(new PersonResultView(this.adminUser, person));

        } catch (final Validator.InvalidValueException e) {
            Notification.show("Atenção!", e.getMessage(), Notification.Type.WARNING_MESSAGE);
        } catch (final NotFoundException e) {
            Notification.show("Erro", e.getMessage(), Notification.Type.ERROR_MESSAGE);
        } catch (final Exception e) {
            Notification.show("Erro no banco de dados. Contate um desenvolvedor.", e.getMessage(),
                    Notification.Type.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    private void managePersons(final Button.ClickEvent event) {
        final List<Person> personList = getAdminRoot().getPersonRepository().findByAdminResponsible(this.adminUser);
        final List<AdminUser> admins = getAdminRoot().getAdminUserRepository().findAll();
        getAdminRoot().getContentPanel().setContent(new ManagePersonsView(this.adminUser, personList, admins));
    }

    private void changePassword(final Button.ClickEvent event) {
        getAdminRoot().getContentPanel().setContent(new ChangePasswordView(this.adminUser));
    }

    private void manageAdmin(final Button.ClickEvent event) {
        final List<AdminUser> admins = getAdminRoot().getAdminUserRepository().findAll();
        admins.forEach(adminUser -> {
            adminUser.setPersonsAmount(getAdminRoot().getPersonRepository().countByAdminResponsible(adminUser));
        });
        getAdminRoot().getContentPanel().setContent(new ManageAdminUsersView(this.adminUser, admins));
    }

}

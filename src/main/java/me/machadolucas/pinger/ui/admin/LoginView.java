package me.machadolucas.diagnosis.ui.admin;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Getter;
import me.machadolucas.diagnosis.entity.AdminUser;
import me.machadolucas.diagnosis.exception.NotFoundException;
import me.machadolucas.diagnosis.service.PasswordHash;
import me.machadolucas.diagnosis.view.CookieMonster;

import javax.servlet.http.Cookie;

public class LoginView extends AdminViews {

    private static final CookieMonster cookieMonster = new CookieMonster();
    Label pageTitle = new Label("Interface administrativa");
    @Getter
    TextField adminName = new TextField("Nome de usuário");
    @Getter
    PasswordField adminPassword = new PasswordField("Senha de acesso");
    @Getter
    Button submitButton = new Button("Entrar", this::doLogin);

    public LoginView(final AdminUser adminUser) {
        this.adminUser = adminUser;

        configureComponents();
        buildLayout();
    }

    protected void configureComponents() {

        this.adminName.setRequired(true);
        this.adminName.setNullSettingAllowed(false);
        this.adminName.setRequiredError("É necessario digitar o nome de usuário");
        this.adminName.addValidator(new RegexpValidator("^[a-zA-Z0-9]+$", "O nome de usuário deve conter apenas " +
                "letras e números, sem espaços."));
        this.adminName.setImmediate(true);
        this.adminPassword.setRequired(true);
        this.adminPassword.setMaxLength(16);
        this.adminPassword.addValidator(new StringLengthValidator("A senha de acesso deve ter entre 4 a 16 dígitos",
                4, 16, false));
        this.adminPassword.setNullSettingAllowed(false);
        this.adminPassword.setRequiredError("É necessario digitar a senha de acesso");
        this.adminPassword.setImmediate(true);

        this.submitButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        this.submitButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
    }

    protected void buildLayout() {
        setMargin(true);
        setSpacing(true);

        this.pageTitle.setStyleName(ValoTheme.LABEL_H1);

        addComponents(this.pageTitle, this.adminName, this.adminPassword, this.submitButton);
    }

    /**
     * Realiza o login do usuario identificado por adminName, com o adminPassword correspondente
     *
     * @param event evento do clique
     */
    public void doLogin(final Button.ClickEvent event) {

        try {
            this.adminName.validate();
            this.adminPassword.validate();

            this.adminName.setValue(this.adminName.getValue().toLowerCase());

            // Se não existir nenhum usuário, cria o primeiro com o nome 'admin' e a senha fornecida.
            if (getAdminRoot().getAdminUserRepository().count() < 1) {
                final AdminUser adminUser = new AdminUser();
                adminUser.setName("admin");
                adminUser.setPassword(PasswordHash.createHash(this.adminPassword.getValue()));

                getAdminRoot().getAdminUserRepository().save(adminUser);
            }

            final AdminUser adminUser = getAdminRoot().getAdminUserRepository().findByName(this.adminName.getValue());
            if (adminUser == null) {
                throw new NotFoundException("Nome de usuário não encontrado.");
            }

            if (PasswordHash.validatePassword(this.adminPassword.getValue(), adminUser.getPassword())) {

                // Cria e adiciona cookie de autenticação
                final Cookie cookie = cookieMonster.createAdminCookie(adminUser.getName());
                assert cookie != null;
                cookie.setPath(VaadinService.getCurrentRequest().getContextPath());
                VaadinService.getCurrentResponse().addCookie(cookie);
                com.vaadin.ui.JavaScript.getCurrent().execute("document.cookie=\"" + cookie.getName() + "=" + cookie
                        .getValue() + "\"");

                getAdminRoot().setAdminUser(adminUser);
                this.adminUser = adminUser;

                getAdminRoot().getContentPanel().setContent(new AdminPanelView(this.adminUser));

            } else {
                throw new NotFoundException("A senha de acesso informado não está correta. Verifique.");
            }

        } catch (final Validator.InvalidValueException e) {
            Notification.show("Atenção!", e.getMessage(), Notification.Type.WARNING_MESSAGE);

        } catch (final NotFoundException e) {
            Notification.show("Erro", e.getMessage(), Notification.Type.ERROR_MESSAGE);

        } catch (final Exception e) {
            Notification.show("Erro no banco de dados. Contate um desenvolvedor.", e.getMessage(), Notification.Type
                    .ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

}

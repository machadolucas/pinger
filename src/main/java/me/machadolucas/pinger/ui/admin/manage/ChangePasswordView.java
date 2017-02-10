package me.machadolucas.diagnosis.ui.admin.manage;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import me.machadolucas.diagnosis.entity.AdminUser;
import me.machadolucas.diagnosis.service.PasswordHash;
import me.machadolucas.diagnosis.ui.admin.AdminPanelView;
import me.machadolucas.diagnosis.ui.admin.AdminViews;

public class ChangePasswordView extends AdminViews {

    Label pageTitle = new Label("Alterar senha administrativa");

    Button backButton = new Button("Voltar", this::goBack);

    PasswordField adminPassword = new PasswordField("Senha atual");

    PasswordField newPassword = new PasswordField("Nova senha");
    PasswordField newPasswordConfirmation = new PasswordField("Confirme a nova senha");

    Button actionButton = new Button("Alterar senha", this::changePassword);

    public ChangePasswordView(final AdminUser adminUser) {
        this.adminUser = adminUser;

        configureComponents();
        buildLayout();
    }

    @Override
    protected void configureComponents() {
        setMargin(true);
        setSpacing(true);

        this.adminPassword.setRequired(true);
        this.adminPassword.setMaxLength(16);
        this.adminPassword.addValidator(
                new StringLengthValidator("A senha atual deve ter entre 4 a 16 dígitos", 4, 16, false));
        this.adminPassword.setNullSettingAllowed(false);
        this.adminPassword.setRequiredError("É necessario digitar a senha atual");
        this.adminPassword.setImmediate(true);

        this.newPassword.setRequired(true);
        this.newPassword.setMaxLength(16);
        this.newPassword.addValidator(
                new StringLengthValidator("A nova senha de acesso deve ter entre 4 a 16 dígitos", 4, 16, false));
        this.newPassword.setNullSettingAllowed(false);
        this.newPassword.setRequiredError("É necessario digitar a nova senha de acesso");
        this.newPassword.setImmediate(true);

        this.newPasswordConfirmation.setRequired(true);
        this.newPasswordConfirmation.setMaxLength(16);
        this.newPasswordConfirmation.addValidator(
                new StringLengthValidator("A confirmação da nova senha de acesso deve ter entre 4 a 16 dígitos", 4, 16, false));
        this.newPasswordConfirmation.setNullSettingAllowed(false);
        this.newPasswordConfirmation.setRequiredError("É necessario confirmar a nova senha de acesso");
        this.newPasswordConfirmation.setImmediate(true);

        this.actionButton.setStyleName(ValoTheme.BUTTON_PRIMARY);

        this.backButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);

    }

    @Override
    protected void buildLayout() {
        final HorizontalLayout header = getHeaderMenu(this.pageTitle);

        final HorizontalLayout buttonsBar = new HorizontalLayout();
        buttonsBar.setSpacing(true);
        buttonsBar.addComponents(this.actionButton, this.backButton);

        addComponents(header, this.adminPassword, this.newPassword, this.newPasswordConfirmation, buttonsBar);
    }

    // =================================================================================

    public void goBack(final Button.ClickEvent event) {
        getAdminRoot().getContentPanel().setContent(new AdminPanelView(this.adminUser));
    }

    public void changePassword(final Button.ClickEvent event) {
        try {
            this.adminPassword.validate();
            this.newPassword.validate();
            this.newPasswordConfirmation.validate();
            if (!PasswordHash.validatePassword(this.adminPassword.getValue(), this.adminUser.getPassword())) {
                throw new Validator.InvalidValueException("Senha atual está incorreta. Tente novamente.");
            }

            if (this.newPassword.getValue().equals(this.newPasswordConfirmation.getValue())) {
                this.adminUser.setPassword(PasswordHash.createHash(this.newPassword.getValue()));
                getAdminRoot().getAdminUserRepository().save(this.adminUser);
                getAdminRoot().getContentPanel().setContent(new AdminPanelView(this.adminUser));
                Notification.show("Senha alterada com sucesso!", Notification.Type.HUMANIZED_MESSAGE);
            } else {
                throw new Validator.InvalidValueException("Senha nova e sua confirmação são diferentes. Tente novamente.");
            }


        } catch (final Validator.InvalidValueException e) {
            Notification.show("Atenção!", e.getMessage(), Notification.Type.WARNING_MESSAGE);
        } catch (final Exception e) {
            Notification.show("Erro no banco de dados. Contate um desenvolvedor.", e.getMessage(),
                    Notification.Type.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}

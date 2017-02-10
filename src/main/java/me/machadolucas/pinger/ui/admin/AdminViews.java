package me.machadolucas.diagnosis.ui.admin;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import me.machadolucas.diagnosis.entity.AdminUser;
import me.machadolucas.diagnosis.ui.SystemView;
import org.vaadin.dialogs.ConfirmDialog;

public abstract class AdminViews extends SystemView {

    protected AdminUser adminUser;

    protected AdminUser getAdminUser() {
        return this.adminUser;
    }

    protected AdminRoot getAdminRoot() {
        return (AdminRoot) this.getUI();
    }

    protected HorizontalLayout getHeaderMenu(final Label title) {
        final HorizontalLayout headerMenu = new HorizontalLayout();
        headerMenu.setWidth("100%");
        headerMenu.setSpacing(true);

        title.setStyleName(ValoTheme.LABEL_H1);

        final Label userLabel = new Label("Administrador logado: " + this.adminUser.getName());
        userLabel.setStyleName(ValoTheme.LABEL_LARGE);

        final Button logoutBtn = new Button("Logout", this::logout);
        logoutBtn.setStyleName(ValoTheme.BUTTON_DANGER);

        headerMenu.addComponents(title, userLabel, logoutBtn);

        headerMenu.setExpandRatio(title, 5);
        headerMenu.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

        headerMenu.setExpandRatio(userLabel, 2);
        headerMenu.setComponentAlignment(userLabel, Alignment.TOP_RIGHT);

        headerMenu.setExpandRatio(logoutBtn, 1);
        headerMenu.setComponentAlignment(logoutBtn, Alignment.TOP_RIGHT);

        return headerMenu;
    }

    protected static Label getGap(final String height) {
        final Label gap = new Label();
        gap.setHeight(height);
        gap.addStyleName("doNotPrint");
        return gap;
    }

    public void logout(final Button.ClickEvent event) {
        ConfirmDialog.show(getAdminRoot().getUI(), "Tem certeza que deseja efetuar logout?", "Você sairá do sistema e precisará se autenticar para acessá-lo novamente",
                "Sim", "Não", (ConfirmDialog.Listener) dialog -> {
                    if (dialog.isConfirmed()) {
                        getAdminRoot().logout(event);
                    }
                });
    }
}

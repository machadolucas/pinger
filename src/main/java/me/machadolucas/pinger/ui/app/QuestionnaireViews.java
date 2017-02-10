package me.machadolucas.diagnosis.ui.app;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import me.machadolucas.diagnosis.entity.Person;
import me.machadolucas.diagnosis.entity.SectionType;
import me.machadolucas.diagnosis.ui.SystemView;
import org.vaadin.dialogs.ConfirmDialog;

public abstract class QuestionnaireViews extends SystemView {

    protected Person loggedPerson;

    protected SectionType sectionType;

    protected static Label getGap(final String height) {
        final Label gap = new Label();
        gap.setHeight(height);
        return gap;
    }

    public SectionType getSectionType() {
        return this.sectionType;
    }

    protected Person getLoggedPerson() {
        return this.loggedPerson;
    }

    protected AppRoot getAppRoot() {
        return (AppRoot) this.getUI();
    }

    protected HorizontalLayout getHeaderMenu(final Label title) {
        final HorizontalLayout headerMenu = new HorizontalLayout();
        headerMenu.setWidth("100%");
        headerMenu.setSpacing(true);

        title.setStyleName(ValoTheme.LABEL_H1);

        final VerticalLayout info = new VerticalLayout();
        final Label userLabel = new Label("Usuário logado: " + getLoggedPerson().getPersonCode());
        userLabel.setStyleName(ValoTheme.LABEL_LARGE);
        final Label sectionLabel = new Label("Tipo de sessão: " + getSectionType().getName());
        info.addComponents(userLabel, sectionLabel);

        final Button logoutBtn = new Button("Logout", this::logout);
        logoutBtn.setStyleName(ValoTheme.BUTTON_DANGER);

        headerMenu.addComponents(title, info, logoutBtn);

        headerMenu.setExpandRatio(title, 5);
        headerMenu.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

        headerMenu.setExpandRatio(info, 2);
        headerMenu.setComponentAlignment(info, Alignment.TOP_RIGHT);

        headerMenu.setExpandRatio(logoutBtn, 1);
        headerMenu.setComponentAlignment(logoutBtn, Alignment.TOP_RIGHT);

        return headerMenu;
    }

    public void logout(final Button.ClickEvent event) {
        ConfirmDialog.show(getAppRoot().getUI(), "Tem certeza que deseja efetuar logout?", "Você sairá do sistema e precisará se autenticar para acessá-lo novamente",
                "Sim", "Não", (ConfirmDialog.Listener) dialog -> {
                    if (dialog.isConfirmed()) {
                        getAppRoot().logout(event);
                    }
                });
    }
}

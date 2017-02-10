package me.machadolucas.diagnosis.ui.admin;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import lombok.Getter;
import lombok.Setter;
import me.machadolucas.diagnosis.entity.AdminUser;
import me.machadolucas.diagnosis.repository.AdminUserRepository;
import me.machadolucas.diagnosis.repository.PersonRepository;
import me.machadolucas.diagnosis.repository.QuestionnaireRepository;
import me.machadolucas.diagnosis.service.CodeGenerator;
import me.machadolucas.diagnosis.view.CookieMonster;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;

@Theme("diagnosys")
@SpringUI(path = "admin")
public class AdminRoot extends UI {

    @Getter
    Panel contentPanel;
    VerticalLayout wizard;
    @Getter
    @Autowired
    private CookieMonster cookieMonster;
    @Getter
    @Setter
    private AdminUser adminUser;
    @Getter
    @Autowired
    private QuestionnaireRepository questionnaireRepository;
    @Getter
    @Autowired
    private PersonRepository personRepository;
    @Getter
    @Autowired
    private CodeGenerator codeGenerator;
    @Autowired
    @Getter
    private AdminUserRepository adminUserRepository;

    // =========================================================

    @Override
    protected void init(final VaadinRequest vaadinRequest) {
        Page.getCurrent().setTitle("Interface administrativa");
    }

    @PostConstruct
    private void checkAuthCookie() {
        final Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        if (cookies != null && cookies.length > 0) {
            for (final Cookie c : cookies) {
                if (c.getName().equals(CookieMonster.ADMIN_AUTH_COOKIE_NAME)) {
                    if (bypassLoginByCookie(c)) {
                        return;
                    }
                }
            }
        }

        final LoginView loginView = new LoginView(this.adminUser);
        configureComponents(loginView);
        buildLayout();
    }

    private void configureComponents(final AbstractOrderedLayout content) {
        this.wizard = new VerticalLayout();
        this.contentPanel = new Panel();
        this.contentPanel.setContent(content);
    }

    private void buildLayout() {
        this.contentPanel.setSizeFull();
        this.wizard.addComponents(this.contentPanel);
        this.wizard.setExpandRatio(this.contentPanel, 1);
        this.wizard.setSizeFull();
        this.wizard.setMargin(new MarginInfo(false, false, true, false));
        this.wizard.setSpacing(false);
        setContent(this.wizard);
    }

    // =================================================================================

    private boolean bypassLoginByCookie(final Cookie cookie) {
        final String username = this.cookieMonster.decryptCookie(cookie);

        if (username != null) {
            final AdminUser adminUser = this.adminUserRepository.findByName(username);
            if (adminUser != null) {
                this.adminUser = adminUser;
                final AdminPanelView adminPanelView = new AdminPanelView(this.adminUser);
                configureComponents(adminPanelView);
                buildLayout();
                return true;
            }
        }
        return false;
    }

    void logout(final Button.ClickEvent event) {
        com.vaadin.ui.JavaScript.getCurrent().execute("document.cookie=\"" + CookieMonster.ADMIN_AUTH_COOKIE_NAME +
                "=\"");

        final LoginView loginView = new LoginView(this.adminUser);
        configureComponents(loginView);
        buildLayout();

        Notification.show("Logout realizado com sucesso", Notification.Type.WARNING_MESSAGE);
    }

}

package me.machadolucas.diagnosis.ui.app;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import lombok.Getter;
import lombok.Setter;
import me.machadolucas.diagnosis.entity.Person;
import me.machadolucas.diagnosis.entity.SectionType;
import me.machadolucas.diagnosis.repository.PersonRepository;
import me.machadolucas.diagnosis.repository.QuestionnaireRepository;
import me.machadolucas.diagnosis.ui.app.sections.SectionView;
import me.machadolucas.diagnosis.view.CookieMonster;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;

@Theme("diagnosys")
@SpringUI(path = "app")
public class AppRoot extends UI {

    @Getter
    Panel contentPanel;
    // =========================================================
    VerticalLayout wizard;
    @Getter
    @Setter
    private Person loggedPerson;
    @Getter
    @Setter
    private SectionType loggedSectionType;
    @Autowired
    @Getter
    private QuestionnaireRepository questionnaireRepository;

    @Autowired
    @Getter
    private PersonRepository personRepository;

    @Autowired
    private CookieMonster cookieMonster;

    // =========================================================

    @Override
    protected void init(final VaadinRequest vaadinRequest) {
        Page.getCurrent().setTitle("Sistema de questionários");
    }

    @PostConstruct
    private void checkAuthCookie() {
        final Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        if (cookies != null && cookies.length > 0) {
            for (final Cookie c : cookies) {
                if (c.getName().equals(CookieMonster.AUTH_COOKIE_NAME)) {
                    final SectionView result = bypassLoginByCookie(c);
                    if (result != null) {
                        configureComponents(result);
                        buildLayout();
                        return;
                    }
                }
            }
        }

        final LoginView loginView = new LoginView(this.loggedPerson, this.loggedSectionType);
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

    public SectionView bypassLoginByCookie(final Cookie cookie) {

        final String usernameAndSectionString = this.cookieMonster.decryptCookie(cookie);
        if (usernameAndSectionString != null) {
            final String[] usernameAndSection = usernameAndSectionString.split("#");
            if (usernameAndSection.length == 2) {
                final Person person = this.personRepository.findByPersonCode(usernameAndSection[0]);

                if (person != null) {
                    person.postConstruct();// faz a inicialização das strings
                    this.questionnaireRepository.save(person.getInexistentQuestionnaires());
                    this.personRepository.save(person);
                    final SectionType section = SectionType.valueOf(usernameAndSection[1]);
                    this.loggedSectionType = section;
                    this.loggedPerson = person;
                    this.loggedPerson.postConstruct();

                    switch (section) {
                        case INICIAL: {
                            return new SectionView(person.getInitialSection(), this.loggedPerson, this
                                    .loggedSectionType);
                        }
                        case RASTREAMENTO_C: {
                            return new SectionView(person.getChildSection(), this.loggedPerson, this.loggedSectionType);
                        }
                        case RASTREAMENTO_P: {
                            return new SectionView(person.getParentSection(), this.loggedPerson, this
                                    .loggedSectionType);
                        }
                    }
                }
            }
        }
        return null;
    }

    public void logout(final Button.ClickEvent event) {
        com.vaadin.ui.JavaScript.getCurrent().execute("document.cookie=\"" + CookieMonster.AUTH_COOKIE_NAME + "=\"");

        final LoginView loginView = new LoginView(this.loggedPerson, this.loggedSectionType);
        configureComponents(loginView);
        buildLayout();

        Notification.show("Logout realizado com sucesso", Notification.Type.WARNING_MESSAGE);
    }

    public void showSectionView(final Button.ClickEvent event) {

        switch (this.loggedSectionType) {
            case INICIAL: {
                configureComponents(new SectionView(this.loggedPerson.getInitialSection(), this.loggedPerson, this
                        .loggedSectionType));
                break;
            }
            case RASTREAMENTO_C: {
                configureComponents(new SectionView(this.loggedPerson.getChildSection(), this.loggedPerson, this
                        .loggedSectionType));
                break;
            }
            case RASTREAMENTO_P: {
                configureComponents(new SectionView(this.loggedPerson.getParentSection(), this.loggedPerson, this
                        .loggedSectionType));
                break;
            }
        }
        buildLayout();
    }

}

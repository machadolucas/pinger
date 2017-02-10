package me.machadolucas.diagnosis.ui.app;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Getter;
import me.machadolucas.diagnosis.entity.Person;
import me.machadolucas.diagnosis.entity.SectionType;
import me.machadolucas.diagnosis.exception.NotFoundException;
import me.machadolucas.diagnosis.ui.app.sections.SectionView;
import me.machadolucas.diagnosis.view.CookieMonster;

import javax.servlet.http.Cookie;

public class LoginView extends QuestionnaireViews {

    private static final CookieMonster cookieMonster = new CookieMonster();
    Label pageTitle = new Label("Sistema de questionários diagnóstico.");
    @Getter
    TextField personCode = new TextField("Código do paciente");
    @Getter
    TextField accessCode = new TextField("Código de acesso ao questionário");
    @Getter
    NativeSelect sessionType = new NativeSelect("Selecione a sessão de questionários");
    @Getter
    Button submitButton = new Button("Entrar", this::doLogin);

    public LoginView(final Person loggedPerson, final SectionType sectionType) {
        this.loggedPerson = loggedPerson;
        this.sectionType = sectionType;

        configureComponents();
        buildLayout();
    }

    protected void configureComponents() {

        this.personCode.setRequired(true);
        this.personCode.setMaxLength(5);
        this.personCode.addValidator(new StringLengthValidator("O código do paciente deve ter 5 caracteres", 5, 5,
                false));
        this.personCode.setNullSettingAllowed(false);
        this.personCode.setRequiredError("É necessario digitar o código do paciente");
        this.personCode.setImmediate(true);

        this.accessCode.setRequired(true);
        this.accessCode.setMaxLength(4);
        this.accessCode.addValidator(new StringLengthValidator("O código de acesso deve ter 4 dígitos", 4, 4, false));
        this.accessCode.setNullSettingAllowed(false);
        this.accessCode.setRequiredError("É necessario digitar o código de acesso");
        this.accessCode.setImmediate(true);

        for (final SectionType session : SectionType.values()) {
            this.sessionType.addItem(session);
            this.sessionType.setItemCaption(session, session.getName());
        }
        this.sessionType.setNullSelectionAllowed(false);
        this.sessionType.setRequired(true);
        this.sessionType.setRequiredError("É necessario selecionar um tipo de sessão de questionários");
        this.sessionType.setImmediate(true);

        this.submitButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        this.submitButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

    }

    protected void buildLayout() {
        setMargin(true);
        setSpacing(true);

        this.pageTitle.setStyleName(ValoTheme.LABEL_H1);

        addComponents(this.pageTitle, this.personCode, this.accessCode, this.sessionType, this.submitButton);
    }

    /**
     * Realiza o login do usuario identificado por personCode, e autenticado num determinado sessionType com o
     * accessCode correspondente
     *
     * @param event evento do clique
     */
    public void doLogin(final Button.ClickEvent event) {

        try {
            this.personCode.validate();
            this.accessCode.validate();
            this.sessionType.validate();

            final Person person = getAppRoot().getPersonRepository().findByPersonCode(this.personCode.getValue()
                    .toUpperCase());
            if (person == null) {
                throw new NotFoundException("Código de paciente não encontrado.");
            }

            final String correctAccessCode = person.getCodes().get(this.sessionType.getValue());

            if (this.accessCode.getValue().equals(correctAccessCode)) {
                final SectionType section = (SectionType) this.sessionType.getValue();

                // Cria e adiciona cookie de autenticação
                final Cookie cookie = cookieMonster.createSessionCookie(person.getPersonCode() + "#" + section);
                assert cookie != null;
                cookie.setPath(VaadinService.getCurrentRequest().getContextPath());
                VaadinService.getCurrentResponse().addCookie(cookie);
                com.vaadin.ui.JavaScript.getCurrent().execute("document.cookie=\"" + cookie.getName() + "=" + cookie
                        .getValue() + "\"");

                getAppRoot().setLoggedSectionType(section);
                getAppRoot().setLoggedPerson(person);
                this.loggedPerson = person;
                this.sectionType = section;

                switch (section) {
                    case INICIAL: {
                        person.postConstructInicial();
                        getAppRoot().getQuestionnaireRepository().save(person.getInexistentQuestionnaires());
                        getAppRoot().getPersonRepository().save(person);
                        getAppRoot().getContentPanel().setContent(new SectionView(person.getInitialSection(),
                                getLoggedPerson(), getSectionType()));
                        break;
                    }
                    case RASTREAMENTO_C: {
                        person.postConstructRastreamentoC();
                        getAppRoot().getQuestionnaireRepository().save(person.getInexistentQuestionnaires());
                        getAppRoot().getPersonRepository().save(person);
                        getAppRoot().getContentPanel().setContent(new SectionView(person.getChildSection(),
                                getLoggedPerson(), getSectionType()));
                        break;
                    }
                    case RASTREAMENTO_P: {
                        person.postConstructRastreamentoP();
                        getAppRoot().getQuestionnaireRepository().save(person.getInexistentQuestionnaires());
                        getAppRoot().getPersonRepository().save(person);
                        getAppRoot().getContentPanel().setContent(new SectionView(person.getParentSection(),
                                getLoggedPerson(), getSectionType()));
                        break;
                    }
                }

            } else {
                throw new NotFoundException("O código de acesso informado não corresponde ao código para a sessão " +
                        "selecionada. Verifique.");
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

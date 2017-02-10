package me.machadolucas.diagnosis.ui.app.sections;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Getter;
import lombok.Setter;
import me.machadolucas.diagnosis.entity.Person;
import me.machadolucas.diagnosis.entity.PersonSection;
import me.machadolucas.diagnosis.entity.Questionnaire;
import me.machadolucas.diagnosis.entity.SectionType;
import me.machadolucas.diagnosis.ui.app.QuestionnaireViews;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Tela que mostra os questionários disponíveis na seção
 */
public class SectionView extends QuestionnaireViews {

    Label instructions = new Label();

    @Getter
    PersonSection personSection;

    @Getter
    @Setter
    List<Button> questionnairesButtons;

    public SectionView(final PersonSection personSection, final Person loggedPerson, final SectionType sectionType) {
        this.personSection = personSection;
        this.questionnairesButtons = new ArrayList<>();
        this.loggedPerson = loggedPerson;
        this.sectionType = sectionType;

        configureComponents();
        buildLayout();
    }

    protected void configureComponents() {

        this.instructions.setValue("Selecione um questionário para responder:");
        this.instructions.setStyleName(ValoTheme.LABEL_H1);
  
        final LinkedList<Questionnaire> questionnaires = new LinkedList<>(this.personSection.getQuestionnaires()
                .values());
        Collections.sort(questionnaires);

        questionnaires.forEach(questionnaire -> {
            final Button option = new Button(questionnaire.getTitle(), this::startQuestionnaire);
            option.setData(questionnaire);

            final String fillStatus = questionnaire.checkQuestionnaireFillStatus();

            if ("I".equals(fillStatus)) {
                // Não preenchido
                option.addStyleName(ValoTheme.BUTTON_PRIMARY);

            } else if ("P".equals(fillStatus)) {
                // Parcialmente preenchido
                option.removeStyleName(ValoTheme.BUTTON_PRIMARY);
                option.setIcon(FontAwesome.EXCLAMATION_CIRCLE, "Esse questionário não foi totalmente preenchido");

            } else if ("C".equals(fillStatus)) {
                // Totalmente preenchido
                option.addStyleName(ValoTheme.BUTTON_FRIENDLY);
                option.setIcon(FontAwesome.CHECK, "Questionário completado");
            }

            this.questionnairesButtons.add(option);
        });
    }

    protected void buildLayout() {

        final VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setMargin(true);
        final HorizontalLayout header = getHeaderMenu(this.instructions);
        content.addComponent(header);

        final VerticalLayout menu = new VerticalLayout();
        menu.setSpacing(true);
        menu.setMargin(new MarginInfo(true, true, true, true));

        this.questionnairesButtons.forEach(menu::addComponent);
        content.addComponent(menu);

        addComponents(content);
    }

    public void startQuestionnaire(final Button.ClickEvent event) {
        final Questionnaire questionnaire = (Questionnaire) event.getButton().getData();

        final QuestionnaireView view = new QuestionnaireView(questionnaire, getLoggedPerson(), getSectionType());
        getAppRoot().getContentPanel().setContent(view);
        view.scrollUp();
    }

}

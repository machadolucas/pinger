package me.machadolucas.diagnosis.ui.app.sections;

import java.time.LocalDateTime;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import lombok.Getter;
import lombok.Setter;
import me.machadolucas.diagnosis.entity.Person;
import me.machadolucas.diagnosis.entity.Question;
import me.machadolucas.diagnosis.entity.Questionnaire;
import me.machadolucas.diagnosis.entity.SectionType;
import me.machadolucas.diagnosis.service.QuestionUtils;
import me.machadolucas.diagnosis.ui.app.QuestionnaireViews;
import me.machadolucas.diagnosis.view.QuestionConstructor;
import me.machadolucas.diagnosis.view.QuestionnaireViewHelper;

/**
 * Tela que mostra o questionário em si, com as questões
 */
public class QuestionnaireView extends QuestionnaireViews {

    @Getter
    private final Questionnaire questionnaire;
    @Getter
    private VerticalLayout questionnaireForm;
    @Getter
    @Setter
    private boolean oddEvenBackground = false;
    @Getter
    private VerticalLayout questionaireAndButtonsForm;
    @Getter
    @Setter
    private boolean complementaryQuestionsVisible;
    @Getter
    @Setter
    private boolean suplementQuestionsButtonVisible;

    public QuestionnaireView(final Questionnaire questionnaire, final Person loggedPerson, final SectionType
            sectionType) {
        this.questionnaire = questionnaire;
        this.loggedPerson = loggedPerson;
        this.sectionType = sectionType;

        configureComponents();
        buildLayout();
    }

    protected void configureComponents() {
    }

    protected void buildLayout() {

        this.questionnaireForm = new VerticalLayout();
        this.questionnaireForm.setSpacing(true);

        this.questionaireAndButtonsForm = new VerticalLayout();
        this.questionaireAndButtonsForm.setSpacing(true);
        this.questionaireAndButtonsForm.setMargin(true);
        this.questionaireAndButtonsForm.addComponent(this.questionnaireForm);

        final HorizontalLayout moreInfoLayout = new HorizontalLayout();
        moreInfoLayout.setSpacing(true);
        moreInfoLayout.setMargin(false);
        moreInfoLayout.setWidth("100%");
        final Label moreInfoLabel = new Label("Outras informações:");
        final TextArea moreInfoField = new TextArea();
        moreInfoField.setWidth("100%");
        if (this.questionnaire.getMoreInfo() != null)
            moreInfoField.setValue(this.questionnaire.getMoreInfo());
        moreInfoLayout.addComponents(moreInfoLabel, moreInfoField);
        moreInfoLayout.setExpandRatio(moreInfoLabel, 1);
        moreInfoLayout.setExpandRatio(moreInfoField, 4);
        moreInfoField.addValueChangeListener(event -> this.saveQuestionAnswerOnBlur(event, moreInfoField));
        this.questionaireAndButtonsForm.addComponent(moreInfoLayout);

        final Button backBtn = new Button("Voltar", this::back);
        backBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        this.questionaireAndButtonsForm.addComponent(backBtn);
        this.questionaireAndButtonsForm.setComponentAlignment(backBtn, Alignment.BOTTOM_RIGHT);

        final HorizontalLayout header = getHeaderMenu(new Label(this.questionnaire.getTitle()));
        final Label questionnaireDescription = new Label(this.questionnaire.getDescription(), ContentMode.HTML);
        questionnaireDescription.addStyleName(ValoTheme.LABEL_COLORED);
        questionnaireDescription.addStyleName(ValoTheme.LABEL_BOLD);
        this.questionnaireForm.addComponents(header, questionnaireDescription);

        for (final Question question : this.questionnaire.getQuestions()) {
            if (question.isResumoOnly()) {
                continue;
            }

            final HorizontalLayout questionForm = QuestionnaireViewHelper.convertToHorizontalLayout(question, this,
                    this.oddEvenBackground);
            this.oddEvenBackground = !this.oddEvenBackground;
            this.questionnaireForm.addComponents(questionForm);
        }

        QuestionnaireViewHelper.addOrRemoveComplementaryQuestions(this);
        QuestionnaireViewHelper.addOrRemoveSuplementaryButton(this);
        QuestionnaireViewHelper.addOrRemoveSpecificComplementaryQuestions(this);

        addComponents(this.questionaireAndButtonsForm);
    }

    private void back(final Button.ClickEvent event) {
        getAppRoot().showSectionView(event);
    }

    public void goToSuplementar(final Button.ClickEvent event) {
        final QuestionnaireView suplementarView = new QuestionnaireView(this.questionnaire.getSuplementQuestionnaire
                (), getLoggedPerson(), getSectionType());
        getAppRoot().getContentPanel().setContent(suplementarView);
        suplementarView.scrollUp();
    }

    public void scrollUp() {
        getAppRoot().getContentPanel().setScrollTop(0);
    }

    public void saveQuestionAnswerOnBlur(final Property.ValueChangeEvent event, final AbstractField field) {
        if (field.getData() != null) {
            final QuestionConstructor.QuestionData data = (QuestionConstructor.QuestionData) field.getData();
            final Question question = data.getQuestion();
            final Question.AnswerType answerType = data.getAnswerType();

            QuestionUtils.fromFieldToQuestion(field, answerType, question);
            // salvar a questão só cria um novo registro no banco

            QuestionnaireViewHelper.addOrRemoveComplementaryQuestions(this);
            QuestionnaireViewHelper.addOrRemoveSuplementaryButton(this);
            QuestionnaireViewHelper.addOrRemoveSpecificComplementaryQuestions(this);

        } else {
            this.questionnaire.setMoreInfo((String) field.getValue());
        }
        this.questionnaire.setLastUpdated(LocalDateTime.now());

        this.getAppRoot().getQuestionnaireRepository().save(this.questionnaire);

        String notificationText = "Resposta salva com sucesso.";
        if (isSuplementQuestionsButtonVisible())
            notificationText += " O questionário suplementar está acessível.";
        Notification.show(notificationText, Notification.Type.TRAY_NOTIFICATION);
    }

}

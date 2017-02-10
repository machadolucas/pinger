package me.machadolucas.diagnosis.ui.admin.results;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Getter;
import lombok.Setter;
import me.machadolucas.diagnosis.entity.*;
import me.machadolucas.diagnosis.repository.QuestionnaireRepository;
import me.machadolucas.diagnosis.service.QuestionUtils;
import me.machadolucas.diagnosis.ui.admin.AdminViews;
import me.machadolucas.diagnosis.view.QuestionConstructor;
import me.machadolucas.diagnosis.view.QuestionnaireResultsViewHelper;

/**
 * Tela que mostra o questionário em si, com as questões
 */
public class QuestionnaireResultsView extends AdminViews {

    private final QuestionnaireResultsViewHelper viewHelper;

    @Getter
    private final String questionnaireName;

    private final SectionType sectionType;
    private final Person person;

    @Getter
    @Setter
    private boolean complementaryQuestionsVisible;

    @Getter
    @Setter
    private boolean suplementQuestionsButtonVisible;

    QuestionnaireResultsView(final AdminUser adminUser, final String questionnaireClassName, final Person person) {
        this.adminUser = adminUser;
        this.questionnaireName = questionnaireClassName;
        this.person = person;

        if (this.person.getInitialSection().getQuestionnaires().containsKey(this.questionnaireName)) {
            this.sectionType = SectionType.INICIAL;
        } else {
            this.sectionType = SectionType.RASTREAMENTO_C;
        }

        this.viewHelper = new QuestionnaireResultsViewHelper(this);

        configureComponents();
        buildLayout();
    }

    protected void configureComponents() {
        setMargin(true);
        setSpacing(true);
    }

    protected void buildLayout() {
        final HorizontalLayout header;

        if (SectionType.INICIAL.equals(this.sectionType)) {
            //Captura o objeto questionario
            final Questionnaire questionnaire = this.person.getInitialSection().getQuestionnaires().get(this
                    .questionnaireName);

            //Inicializa o header com o titulo do questionario, e a descricao
            header = getHeaderMenu(new Label(questionnaire.getTitle()));
            final Label questionnaireDescription = new Label(questionnaire.getDescription(), ContentMode.HTML);
            questionnaireDescription.addStyleName(ValoTheme.LABEL_COLORED);
            questionnaireDescription.addStyleName(ValoTheme.LABEL_BOLD);

            //Cria a grade de questoes
            final GridLayout questionnaireForm = this.viewHelper.buildInitialQuestionnaireGrid(questionnaire);

            //Titulo da grade de mais informacaoes
            final Label moreInfoTitle = new Label("Mais informações anotadas:");
            moreInfoTitle.addStyleName(ValoTheme.LABEL_H2);

            //Cria a grade de mais informacoes
            final GridLayout moreInfoGrid = this.viewHelper.buildInitialMoreInfoGrid(questionnaire);

            addComponents(header, questionnaireDescription, questionnaireForm, moreInfoTitle, moreInfoGrid);

        } else {
            //Captura os objetos questionario
            final Questionnaire questionnaireChild = this.person.getChildSection().getQuestionnaires().get(this
                    .questionnaireName);
            final Questionnaire questionnaireParent = this.person.getParentSection().getQuestionnaires().get(this
                    .questionnaireName);
            final Questionnaire questionnaireDoctor = this.person.getDoctorSection().getQuestionnaires().get(this
                    .questionnaireName);

            //Inicializa o header com o titulo do questionario, e a descricao
            header = getHeaderMenu(new Label(questionnaireChild.getTitle()));
            final Label questionnaireDescription = new Label(questionnaireChild.getDescription(), ContentMode.HTML);
            questionnaireDescription.addStyleName(ValoTheme.LABEL_COLORED);
            questionnaireDescription.addStyleName(ValoTheme.LABEL_BOLD);
            addComponents(header, questionnaireDescription);

            //Cria a grade de questoes
            final GridLayout questionnaireForm = this.viewHelper.buildTrackingQuestionnaireGrid(questionnaireChild,
                    questionnaireParent, questionnaireDoctor);

            //Titulo da grade de mais informacaoes
            final Label moreInfoTitle = new Label("Mais informações anotadas:");
            moreInfoTitle.addStyleName(ValoTheme.LABEL_H2);

            //Cria a grade de mais informacoes
            final GridLayout moreInfoGrid = this.viewHelper.buildTrackingMoreInfoGrid(questionnaireChild,
                    questionnaireParent, questionnaireDoctor);

            addComponents(questionnaireForm, moreInfoTitle, moreInfoGrid);

            //Se tiver questionario suplementar
            if (questionnaireChild.getSuplementQuestionnaire() != null) {

                //Cria titulo e descricao do questionario suplementar
                final Label supplementTitle = new Label(questionnaireChild.getSuplementQuestionnaire().getTitle());
                supplementTitle.addStyleName(ValoTheme.LABEL_H1);
                final Label supplementDescription = new Label(questionnaireChild.getSuplementQuestionnaire()
                        .getDescription(), ContentMode.HTML);
                supplementDescription.addStyleName(ValoTheme.LABEL_COLORED);
                supplementDescription.addStyleName(ValoTheme.LABEL_BOLD);

                //Cria a grade de questoes
                final GridLayout supplementForm = this.viewHelper.buildTrackingQuestionnaireGrid( //
                        questionnaireChild.getSuplementQuestionnaire(), //
                        questionnaireParent.getSuplementQuestionnaire(), //
                        questionnaireDoctor.getSuplementQuestionnaire());

                //Titulo da grade de mais informacaoes
                final Label supplementMoreInfoTitle = new Label("Mais informações anotadas:");
                supplementMoreInfoTitle.addStyleName(ValoTheme.LABEL_H2);

                //Cria a grade de mais informacoes
                final GridLayout supplementMoreInfoGrid = this.viewHelper.buildTrackingMoreInfoGrid( //
                        questionnaireChild.getSuplementQuestionnaire(), //
                        questionnaireParent.getSuplementQuestionnaire(), //
                        questionnaireDoctor.getSuplementQuestionnaire());

                addComponents(getGap("1em"), supplementTitle, supplementDescription, supplementForm,
                        supplementMoreInfoTitle, supplementMoreInfoGrid);
            }

        }

        final Button backBtn = new Button("Voltar", this::back);
        backBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);

        addComponent(backBtn);
        setComponentAlignment(backBtn, Alignment.BOTTOM_RIGHT);

    }

    private void back(final Button.ClickEvent event) {
        getAdminRoot().getContentPanel().setContent(new PersonResultView(this.adminUser, this.person));
    }

    public void saveQuestionAnswerOnBlur(final Property.ValueChangeEvent event, final AbstractField field, final
    Questionnaire questionnaire) {
        if (field.getData() != null) {
            final QuestionConstructor.QuestionData data = (QuestionConstructor.QuestionData) field.getData();
            final Question question = data.getQuestion();
            final Question.AnswerType answerType = data.getAnswerType();

            QuestionUtils.fromFieldToQuestion(field, answerType, question);
        } else {
            questionnaire.setMoreInfo((String) field.getValue());
        }

        final QuestionnaireRepository questionnaireRepository = getAdminRoot().getQuestionnaireRepository();
        questionnaireRepository.save(questionnaire);

        Notification.show("Resposta salva com sucesso.", Notification.Type.TRAY_NOTIFICATION);
    }

    public void scrollUp() {
        getAdminRoot().getContentPanel().setScrollTop(0);
    }

}

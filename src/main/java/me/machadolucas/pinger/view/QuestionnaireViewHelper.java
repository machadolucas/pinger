package me.machadolucas.diagnosis.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.machadolucas.diagnosis.entity.Question;
import me.machadolucas.diagnosis.entity.QuestionType;
import me.machadolucas.diagnosis.entity.Questionnaire;
import me.machadolucas.diagnosis.ui.admin.results.PersonResultView;
import me.machadolucas.diagnosis.ui.app.sections.QuestionnaireView;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class QuestionnaireViewHelper {

    private static final String ID_BOTAO_SUPLEMENTAR = "botao-suplementar";
    private static QuestionConstructor optionsConstructor = new QuestionConstructor();

    public static HorizontalLayout convertToHorizontalLayout(final Question question,
            final QuestionnaireView questionnaireView, final boolean oddEvenBackground) {
        final HorizontalLayout questionForm = new HorizontalLayout();
        questionForm.setSpacing(true);
        questionForm.setMargin(false);
        questionForm.setWidth("100%");
        questionForm.setData(question);
        if (oddEvenBackground) {
            questionForm.addStyleName("evenBackground");
        }

        final VerticalLayout questionText = new VerticalLayout();

        // Número da questão + texto
        final HorizontalLayout questionTitle = new HorizontalLayout();
        questionTitle.setSpacing(true);
        final Label questionNumberTitle;
        if (!StringUtils.isEmpty(question.getNumber())) {
            questionNumberTitle = new Label(question.getNumber() + ". ");
            questionNumberTitle.addStyleName(ValoTheme.LABEL_BOLD);
        } else {
            questionNumberTitle = new Label();
        }

        final Label questionMainTitle = new Label(question.getTitle(), ContentMode.HTML);
        questionMainTitle.setWidth("100%");

        if (question.getQuestionType().equals(QuestionType.CHAPTER_TITLE)) {
            questionNumberTitle.addStyleName(ValoTheme.LABEL_H2);
            questionMainTitle.addStyleName(ValoTheme.LABEL_H2);
            questionMainTitle.addStyleName(ValoTheme.LABEL_BOLD);
        } else if (question.getQuestionType().equals(QuestionType.SUBQUESTIONS_TITLE)) {
            questionNumberTitle.addStyleName(ValoTheme.LABEL_H3);
            questionMainTitle.addStyleName(ValoTheme.LABEL_H3);
            questionMainTitle.addStyleName(ValoTheme.LABEL_BOLD);
        }

        questionTitle.addComponents(questionNumberTitle, questionMainTitle);
        questionTitle.setExpandRatio(questionMainTitle, 10);
        questionText.addComponent(questionTitle);

        // + descrição da questão em nova linha, se houver
        if (!question.getDescription().isEmpty()) {
            final Label questionDescription = new Label(question.getDescription(), ContentMode.HTML);
            questionDescription.addStyleName(ValoTheme.LABEL_SMALL);
            questionText.addComponent(questionDescription);
        }

        // Cria opções de resposta da questão. Presente e passado
        final List<AbstractField> questionsPossibleAnswers = optionsConstructor.populateQuestionOptions(question, false,
                questionnaireView.getSectionType());
        for (final AbstractField questionAnswerField : questionsPossibleAnswers) {
            questionAnswerField.addValueChangeListener(
                    event -> questionnaireView.saveQuestionAnswerOnBlur(event, questionAnswerField));
        }

        questionForm.addComponents(questionText);
        questionForm.setExpandRatio(questionText, 5);
        questionForm.setComponentAlignment(questionText, Alignment.MIDDLE_LEFT);

        // componentes das respostas
        for (final AbstractField abstractField : questionsPossibleAnswers) {
            questionForm.addComponent(abstractField);
            questionForm.setComponentAlignment(abstractField, Alignment.MIDDLE_RIGHT);
        }
        return questionForm;
    }

    public static void addOrRemoveComplementaryQuestions(final QuestionnaireView questionnaireView) {
        final Questionnaire questionnaire = questionnaireView.getQuestionnaire();
        final VerticalLayout questionnaireForm = questionnaireView.getQuestionnaireForm();

        if (questionnaire.shouldAddComplementaryQuestions(questionnaireView.isComplementaryQuestionsVisible())) {
            for (final Question complementaryQuestion : questionnaire.getConditionalQuestions()) {
                final HorizontalLayout horizontalLayout = convertToHorizontalLayout(complementaryQuestion,
                        questionnaireView, questionnaireView.isOddEvenBackground());
                questionnaireView.setOddEvenBackground(!questionnaireView.isOddEvenBackground());
                questionnaireForm.addComponent(horizontalLayout);
            }
            questionnaireView.setComplementaryQuestionsVisible(true);
        }

        if (questionnaire.shouldHideComplementaryQuestions(questionnaireView.isComplementaryQuestionsVisible())) {
            removeComplementaryQuestions(questionnaire, questionnaireForm);
            questionnaireView.setComplementaryQuestionsVisible(false);
        }
    }

    private static void removeComplementaryQuestions(final Questionnaire questionnaire,
            final VerticalLayout questionnaireForm) {

        final List<Question> complementaryQuestions = questionnaire.getConditionalQuestions();
        final List<AbstractLayout> elementsToRemove = new LinkedList<>();
        for (int i = 0; i < questionnaireForm.getComponentCount(); i++) {
            if (!(questionnaireForm.getComponent(i) instanceof AbstractLayout))
                continue;

            final AbstractLayout component = (AbstractLayout) questionnaireForm.getComponent(i);
            final Question displayedQuestion = (Question) component.getData();

            if (complementaryQuestions.contains(displayedQuestion)) {
                elementsToRemove.add(component);// se eu remover aqui altero a lista que estou iterando
            }
        }

        elementsToRemove.forEach(questionnaireForm::removeComponent);// FIXME essa remoção pode bagunçar o odd even
    }

    public static void addOrRemoveSuplementaryButton(final QuestionnaireView questionnaireView) {
        final Questionnaire questionnaire = questionnaireView.getQuestionnaire();
        final VerticalLayout questionaireAndButtonsForm = questionnaireView.getQuestionaireAndButtonsForm();

        if (questionnaire.shouldAddSuplementButton(questionnaireView.isSuplementQuestionsButtonVisible())) {
            final Button suplementarButton = new Button("Questionário Suplementar", questionnaireView::goToSuplementar);
            suplementarButton.setData(ID_BOTAO_SUPLEMENTAR);
            suplementarButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
            suplementarButton.addStyleName("glowingGreen");
            questionaireAndButtonsForm.addComponent(suplementarButton);
            questionaireAndButtonsForm.setComponentAlignment(suplementarButton, Alignment.BOTTOM_CENTER);
            questionnaireView.setSuplementQuestionsButtonVisible(true);
        }

        if (questionnaire.shouldHideSuplementarButton(questionnaireView.isSuplementQuestionsButtonVisible())) {
            final Component lastComponent = questionaireAndButtonsForm
                    .getComponent(questionaireAndButtonsForm.getComponentCount() - 1);
            if (lastComponent instanceof Button && ID_BOTAO_SUPLEMENTAR.equals(((Button) lastComponent).getData())) {
                questionaireAndButtonsForm.removeComponent(lastComponent);
                questionnaireView.setSuplementQuestionsButtonVisible(false);
            } else {
                // FIXME isso nao acontece, mas e se acontecer?
            }
        }
    }

    public static void addOrRemoveSpecificComplementaryQuestions(QuestionnaireView questionnaireView) {
        Questionnaire questionnaire = questionnaireView.getQuestionnaire();
        VerticalLayout questionnaireForm = questionnaireView.getQuestionnaireForm();

        Map<Question, List<Question>> specificQuestionsMap = questionnaire.getSpecificConditionalQuestionsToShow();
        List<Question> allSpecificQuestions = questionnaire.getSpecificConditionalQuestions();

        // remover todas as especificas primeiro. É feito, mas horizonalLayout não tem retainAll e ñ vou implementar
        final List<AbstractLayout> elementsToRemove = new LinkedList<>();
        for (int i = 0; i < questionnaireForm.getComponentCount(); i++) {
            if (!(questionnaireForm.getComponent(i) instanceof AbstractLayout))
                continue;// pular outros elementos

            final AbstractLayout component = (AbstractLayout) questionnaireForm.getComponent(i);
            final Question displayedQuestion = (Question) component.getData();

            if (allSpecificQuestions.contains(displayedQuestion)) {
                elementsToRemove.add(component);
            }
        }
        elementsToRemove.forEach(questionnaireForm::removeComponent);

        // itera por todas as questões que precisam de questoes especificas
        for (Question key : specificQuestionsMap.keySet()) {
            List<Question> questionSpecificQuestions = specificQuestionsMap.get(key);

            // acha o index do horizontal layout da questao key
            Integer keyQuestionIndex = null;
            for (int i = 0; i < questionnaireForm.getComponentCount(); i++) {
                if (!(questionnaireForm.getComponent(i) instanceof AbstractLayout))
                    continue;// pular outros elementos

                final AbstractLayout component = (AbstractLayout) questionnaireForm.getComponent(i);
                final Question displayedQuestion = (Question) component.getData();

                if (key.equals(displayedQuestion)) {
                    keyQuestionIndex = i;
                    break;
                }
            }

            // adiciona todas as questões específicas logo depois da questão key
            if (keyQuestionIndex != null) {
                for (Question generatedSpecificQuestion : questionSpecificQuestions) {

                    // pega a questão corresponde salva no banco pelo QuestionnaireCreator
                    Question correctQuestionnaireSpecificQuestion = null;
                    for (Question questionnaireSpecificQuestion : allSpecificQuestions) {
                        if (questionnaireSpecificQuestion.getNumber().equals(generatedSpecificQuestion.getNumber())
                                && questionnaireSpecificQuestion.getTitle()
                                        .equals(generatedSpecificQuestion.getTitle())) {
                            correctQuestionnaireSpecificQuestion = questionnaireSpecificQuestion;
                            break;
                        }
                    }

                    // coloca a questão carregada do banco de volta na tela
                    HorizontalLayout specificQuestionLayout = QuestionnaireViewHelper.convertToHorizontalLayout(
                            correctQuestionnaireSpecificQuestion, questionnaireView,
                            questionnaireView.isOddEvenBackground());
                    questionnaireForm.addComponent(specificQuestionLayout, ++keyQuestionIndex);
                }
            }
        }
    }
}

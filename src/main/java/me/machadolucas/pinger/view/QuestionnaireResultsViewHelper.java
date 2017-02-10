package me.machadolucas.diagnosis.view;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import me.machadolucas.diagnosis.entity.Question;
import me.machadolucas.diagnosis.entity.QuestionType;
import me.machadolucas.diagnosis.entity.Questionnaire;
import me.machadolucas.diagnosis.entity.SectionType;
import me.machadolucas.diagnosis.ui.admin.results.QuestionnaireResultsView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestionnaireResultsViewHelper {

    private static final QuestionConstructor optionsConstructor = new QuestionConstructor();

    private final QuestionnaireResultsView origin;

    public QuestionnaireResultsViewHelper(final QuestionnaireResultsView origin) {
        this.origin = origin;
    }

    public GridLayout buildInitialQuestionnaireGrid(final Questionnaire questionnaire) {
        final GridLayout questionnaireForm = new GridLayout(3, 1);
        questionnaireForm.setSpacing(true);
        questionnaireForm.setSizeFull();
        questionnaireForm.setColumnExpandRatio(1, 6);

        //Cria headers da grid
        final Label titleNumber = new Label("Nº");
        titleNumber.addStyleName(ValoTheme.LABEL_H3);
        titleNumber.addStyleName(ValoTheme.LABEL_BOLD);
        titleNumber.setWidthUndefined();
        final Label titleQuestion = new Label("Questão");
        titleQuestion.addStyleName(ValoTheme.LABEL_H3);
        titleQuestion.addStyleName(ValoTheme.LABEL_BOLD);
        final Label titleAnswerChild = new Label("Resposta - Adolescente");
        titleAnswerChild.addStyleName(ValoTheme.LABEL_H3);
        titleAnswerChild.addStyleName(ValoTheme.LABEL_BOLD);

        questionnaireForm.addComponent(titleNumber);
        questionnaireForm.addComponent(titleQuestion);
        questionnaireForm.addComponent(titleAnswerChild);

        int rowsAmount = 1;
        for (final Question question : questionnaire.getQuestions()) {
            questionnaireForm.insertRow(rowsAmount);

            //Coloca o numero da questao, se nao for um CHAPTER_TITLE
            insertQuestionNumber(questionnaireForm, rowsAmount, question);

            final Label questionTitle = question.getFormattedTitleForResultsView();

            //Obtem a lista de componentes de respostas da questao
            final Component component = getInitialQuestionnaireAnswers(question, questionnaire);

            if (component != null) {
                // se for QUESTION, adiciona componentes
                questionnaireForm.addComponent(questionTitle, 1, rowsAmount);
                questionnaireForm.addComponent(component, 2, rowsAmount);

            } else if (QuestionType.CHAPTER_TITLE.equals(question.getQuestionType())) {
                // se for CHAPTER_TITLE, usa a linha inteira
                questionnaireForm.addComponent(questionTitle, 0, rowsAmount, 2, rowsAmount);
            } else {
                // se for SUBQUESTIONS_TITLE, usa a linha inteira menos o numero da questao
                questionnaireForm.addComponent(questionTitle, 1, rowsAmount, 2, rowsAmount);
            }

            rowsAmount++;

        }
        return questionnaireForm;
    }

    public GridLayout buildInitialMoreInfoGrid(final Questionnaire questionnaire) {
        final GridLayout moreInfoGrid = new GridLayout(1, 1);
        moreInfoGrid.setSizeFull();
        moreInfoGrid.setSpacing(true);

        final TextArea moreInfoField = new TextArea("Anotadas na sessão Inicial:");
        moreInfoField.setWidth("100%");
        if (questionnaire.getMoreInfo() != null) {
            moreInfoField.setValue(questionnaire.getMoreInfo());
        }
        moreInfoField.addValueChangeListener(event -> this.origin.saveQuestionAnswerOnBlur(event, moreInfoField,
                questionnaire));
        moreInfoGrid.addComponent(moreInfoField);
        return moreInfoGrid;
    }

    public GridLayout buildTrackingQuestionnaireGrid(final Questionnaire questionnaireChild, final Questionnaire
            questionnaireParent, final Questionnaire questionnaireDoctor) {
        final GridLayout questionnaireForm = new GridLayout(5, 1);
        questionnaireForm.setSpacing(true);
        questionnaireForm.setSizeFull();
        questionnaireForm.setColumnExpandRatio(1, 4);

        //Cria headers da grid
        final Label titleNumber = new Label("Nº");
        titleNumber.addStyleName(ValoTheme.LABEL_H3);
        titleNumber.addStyleName(ValoTheme.LABEL_BOLD);
        titleNumber.setWidthUndefined();
        final Label titleQuestion = new Label("Questão");
        titleQuestion.addStyleName(ValoTheme.LABEL_H3);
        titleQuestion.addStyleName(ValoTheme.LABEL_BOLD);
        final Label titleAnswerChild = new Label("Resposta - Adolescente");
        titleAnswerChild.addStyleName(ValoTheme.LABEL_H3);
        titleAnswerChild.addStyleName(ValoTheme.LABEL_BOLD);
        final Label titleAnswerParents = new Label("Resposta - Pais");
        titleAnswerParents.addStyleName(ValoTheme.LABEL_H3);
        titleAnswerParents.addStyleName(ValoTheme.LABEL_BOLD);
        final Label titleAnswerDoctor = new Label("Nota Resumo");
        titleAnswerDoctor.addStyleName(ValoTheme.LABEL_H3);
        titleAnswerDoctor.addStyleName(ValoTheme.LABEL_BOLD);

        questionnaireForm.addComponent(titleNumber);
        questionnaireForm.addComponent(titleQuestion);
        questionnaireForm.addComponent(titleAnswerChild);
        questionnaireForm.addComponent(titleAnswerParents);
        questionnaireForm.addComponent(titleAnswerDoctor);

        int rowsAmount = 1;
        // Insere questoes
        for (int i = 0; i < questionnaireChild.getQuestions().size(); i++) {
            questionnaireForm.insertRow(rowsAmount);

            insertTrackingQuestionInGrid(questionnaireForm, rowsAmount, //
                    questionnaireChild.getQuestions().get(i), //
                    questionnaireParent.getQuestions().get(i), //
                    questionnaireDoctor.getQuestions().get(i), //
                    questionnaireChild, questionnaireParent, questionnaireDoctor);

            rowsAmount++;
        }

        // Insere questoes condicionais
        for (int i = 0; i < questionnaireChild.getConditionalQuestions().size(); i++) {
            questionnaireForm.insertRow(rowsAmount);

            insertTrackingQuestionInGrid(questionnaireForm, rowsAmount, //
                    questionnaireChild.getConditionalQuestions().get(i), //
                    questionnaireParent.getConditionalQuestions().get(i), //
                    questionnaireDoctor.getConditionalQuestions().get(i), //
                    questionnaireChild, questionnaireParent, questionnaireDoctor);

            rowsAmount++;
        }

        // Processa questoes condicionais especificas
        addSpecificComplementaryQuestions(questionnaireForm, questionnaireChild, questionnaireParent,
                questionnaireDoctor);

        return questionnaireForm;
    }

    private void insertQuestionNumber(final GridLayout questionnaireForm, final int rowIndex, final Question question) {
        if (question.getNumber() != null && !QuestionType.CHAPTER_TITLE.equals(question.getQuestionType())) {
            final Label questionNumber = new Label(question.getNumber() + ".");
            questionNumber.addStyleName(ValoTheme.LABEL_BOLD);
            questionNumber.setData(question);
            questionnaireForm.addComponent(questionNumber, 0, rowIndex);
        }
    }

    private void insertTrackingQuestionInGrid(final GridLayout questionnaireForm, final int rowIndex, final Question
            childSavedQuestion, final Question parentSavedQuestion, final Question doctorSavedQuestion, final
    Questionnaire questionnaireChild, final Questionnaire questionnaireParent, final Questionnaire
            questionnaireDoctor) {

        //Coloca o numero da questao, se nao for um CHAPTER_TITLE
        insertQuestionNumber(questionnaireForm, rowIndex, childSavedQuestion);

        //Obtem a lista de componentes de respostas da questao
        final List<Component> components = getTrackingQuestionnaireAnswers(//
                childSavedQuestion, //
                parentSavedQuestion, //
                doctorSavedQuestion, //
                questionnaireChild, questionnaireParent, questionnaireDoctor);

        final Label questionTitle = childSavedQuestion.getFormattedTitleForResultsView();
        if (components != null) {
            // se for QUESTION, adiciona componentes
            questionnaireForm.addComponent(questionTitle, 1, rowIndex);

            questionnaireForm.addComponent(components.get(0), 2, rowIndex);
            questionnaireForm.addComponent(components.get(1), 3, rowIndex);
            questionnaireForm.addComponent(components.get(2), 4, rowIndex);
        } else if (QuestionType.CHAPTER_TITLE.equals(childSavedQuestion.getQuestionType())) {
            // se for CHAPTER_TITLE, usa a linha inteira
            questionTitle.setData(childSavedQuestion);
            questionnaireForm.addComponent(questionTitle, 0, rowIndex, 4, rowIndex);
        } else {
            // se for SUBQUESTIONS_TITLE, usa a linha inteira menos o numero da questao
            questionnaireForm.addComponent(questionTitle, 1, rowIndex, 4, rowIndex);
        }
    }

    private void addSpecificComplementaryQuestions(final GridLayout questionnaireForm, final Questionnaire
            questionnaireChild, final Questionnaire questionnaireParent, final Questionnaire questionnaireDoctor) {

        final Map<Question, List<Question>> specificQuestionsMap = questionnaireChild
                .getAllSpecificConditionalQuestions();
        final List<Question> childSavedQuestions = questionnaireChild.getSpecificConditionalQuestions();
        final List<Question> parentSavedQuestions = questionnaireParent.getSpecificConditionalQuestions();
        final List<Question> doctorSavedQuestions = questionnaireDoctor.getSpecificConditionalQuestions();

        // itera por todas as questões que precisam de questoes especificas
        for (final Question key : specificQuestionsMap.keySet()) {
            final List<Question> questionSpecificQuestions = specificQuestionsMap.get(key);

            // acha o index do horizontal layout da questao key
            Integer keyQuestionIndex = null;
            for (int i = 1; i < questionnaireForm.getRows(); i++) {
                if (!(questionnaireForm.getComponent(0, i) instanceof Label)) {
                    continue;// pular outros elementos
                }

                final Label component = (Label) questionnaireForm.getComponent(0, i);
                if (component.getData() != null) {
                    final Question displayedQuestion = (Question) component.getData();

                    if (key.equals(displayedQuestion)) {
                        keyQuestionIndex = i;
                        break;
                    }
                }
            }

            // adiciona todas as questões específicas logo depois da questão key
            if (keyQuestionIndex != null) {
                for (final Question generatedSpecificQuestion : questionSpecificQuestions) {

                    // pega a questão corresponde salva no banco pelo QuestionnaireCreator
                    Question childSavedQuestion = null;
                    for (final Question question : childSavedQuestions) {
                        if (question.getNumber().equals(generatedSpecificQuestion.getNumber()) && question.getTitle()
                                .equals(generatedSpecificQuestion.getTitle())) {
                            childSavedQuestion = question;
                            break;
                        }
                    }
                    Question parentSavedQuestion = null;
                    for (final Question question : parentSavedQuestions) {
                        if (question.getNumber().equals(generatedSpecificQuestion.getNumber()) && question.getTitle()
                                .equals(generatedSpecificQuestion.getTitle())) {
                            parentSavedQuestion = question;
                            break;
                        }
                    }
                    Question doctorSavedQuestion = null;
                    for (final Question question : doctorSavedQuestions) {
                        if (question.getNumber().equals(generatedSpecificQuestion.getNumber()) && question.getTitle()
                                .equals(generatedSpecificQuestion.getTitle())) {
                            doctorSavedQuestion = question;
                            break;
                        }
                    }

                    // coloca a questão carregada do banco de volta na tela
                    keyQuestionIndex++;
                    
                    questionnaireForm.insertRow(keyQuestionIndex);

                    insertTrackingQuestionInGrid(questionnaireForm, keyQuestionIndex, //
                            childSavedQuestion, parentSavedQuestion, doctorSavedQuestion, //
                            questionnaireChild, questionnaireParent, questionnaireDoctor);
                }
            }
        }
    }

    public GridLayout buildTrackingMoreInfoGrid(final Questionnaire questionnaireChild, final Questionnaire
            questionnaireParent, final Questionnaire questionnaireDoctor) {
        final GridLayout moreInfoGrid = new GridLayout(3, 1);
        moreInfoGrid.setSizeFull();
        moreInfoGrid.setSpacing(true);

        final TextArea moreInfoFieldChild = new TextArea("Anotadas na sessão Adolescente:");
        moreInfoFieldChild.setWidth("100%");
        if (questionnaireChild.getMoreInfo() != null) {
            moreInfoFieldChild.setValue(questionnaireChild.getMoreInfo());
        }
        moreInfoFieldChild.addValueChangeListener(event -> this.origin.saveQuestionAnswerOnBlur(event,
                moreInfoFieldChild, questionnaireChild));

        final TextArea moreInfoFieldParent = new TextArea("Anotadas na sessão Pais:");
        moreInfoFieldParent.setWidth("100%");
        if (questionnaireParent.getMoreInfo() != null) {
            moreInfoFieldParent.setValue(questionnaireParent.getMoreInfo());
        }
        moreInfoFieldParent.addValueChangeListener(event -> this.origin.saveQuestionAnswerOnBlur(event,
                moreInfoFieldParent, questionnaireParent));

        final TextArea moreInfoFieldDoctor = new TextArea("Anotações para resumo:");
        moreInfoFieldDoctor.setWidth("100%");
        if (questionnaireDoctor.getMoreInfo() != null) {
            moreInfoFieldDoctor.setValue(questionnaireDoctor.getMoreInfo());
        }
        moreInfoFieldDoctor.addValueChangeListener(event -> this.origin.saveQuestionAnswerOnBlur(event,
                moreInfoFieldDoctor, questionnaireDoctor));

        moreInfoGrid.addComponent(moreInfoFieldChild);
        moreInfoGrid.addComponent(moreInfoFieldParent);
        moreInfoGrid.addComponent(moreInfoFieldDoctor);
        return moreInfoGrid;
    }

    /**
     * Recebe os dados da questao e devolve um componente numa lista com as respostas
     *
     * @param question      dados da questao respondida pelo adolescente
     * @param questionnaire questionario do qual a questao pertence (para actionListener de salvamento)
     * @return um componente visual
     */
    private Component getInitialQuestionnaireAnswers(final Question question, final Questionnaire questionnaire) {
        if (QuestionType.CHAPTER_TITLE.equals(question.getQuestionType()) //
                || QuestionType.SUBQUESTIONS_TITLE.equals(question.getQuestionType())) {
            return null;
        }
        final CssLayout cell1 = new CssLayout();
        cell1.setWidth("500px");
        final List<AbstractField> fields = optionsConstructor.populateQuestionOptions(question, false, SectionType
                .INICIAL);
        fields.forEach(field -> field.addValueChangeListener(event -> this.origin.saveQuestionAnswerOnBlur(event,
                field, questionnaire)));
        fields.forEach(cell1::addComponent);
        return cell1;
    }

    /**
     * Recebe os dados das questoes e devolve tres componentes numa lista com as respostas
     *
     * @param questionChild       dados da questao respondida pelo adolescente
     * @param questionParent      dados da questao respondida pelos pais
     * @param questionDoctor      dados do resumo da questao atribuído pelo especialista
     * @param questionnaireChild  questionario do qual a questao pertence (para actionListener de salvamento)
     * @param questionnaireParent questionario do qual a questao pertence (para actionListener de salvamento)
     * @param questionnaireDoctor questionario do qual a questao pertence (para actionListener de salvamento)
     * @return tres componentes visuais numa lista
     */
    private List<Component> getTrackingQuestionnaireAnswers(final Question questionChild, final Question
            questionParent, final Question questionDoctor, final Questionnaire questionnaireChild, final
    Questionnaire questionnaireParent, final Questionnaire questionnaireDoctor) {
        if (QuestionType.CHAPTER_TITLE.equals(questionChild.getQuestionType()) //
                || QuestionType.SUBQUESTIONS_TITLE.equals(questionChild.getQuestionType())) {
            return null;
        }

        final List<Component> answers = new ArrayList<>();
        final CssLayout cell1 = new CssLayout();
        final CssLayout cell2 = new CssLayout();
        final CssLayout cell3 = new CssLayout();
        cell1.setWidth("220px");
        cell2.setWidth("220px");
        cell3.setWidth("220px");

        final List<AbstractField> fields1 = optionsConstructor.populateQuestionOptions(questionChild, false,
                SectionType.RASTREAMENTO_P);
        fields1.forEach(field -> field.addValueChangeListener(event -> this.origin.saveQuestionAnswerOnBlur(event,
                field, questionnaireChild)));
        fields1.forEach(cell1::addComponent);

        final List<AbstractField> fields2 = optionsConstructor.populateQuestionOptions(questionParent, false,
                SectionType.RASTREAMENTO_P);
        fields2.forEach(field -> field.addValueChangeListener(event -> this.origin.saveQuestionAnswerOnBlur(event,
                field, questionnaireParent)));
        fields2.forEach(cell2::addComponent);

        final List<AbstractField> fields3 = optionsConstructor.populateQuestionOptions(questionDoctor, false,
                SectionType.RASTREAMENTO_P);
        fields3.forEach(field -> field.addValueChangeListener(event -> this.origin.saveQuestionAnswerOnBlur(event,
                field, questionnaireDoctor)));
        fields3.forEach(cell3::addComponent);

        answers.add(cell1);
        answers.add(cell2);
        answers.add(cell3);
        return answers;
    }
}

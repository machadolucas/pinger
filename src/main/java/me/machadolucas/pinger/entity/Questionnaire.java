package me.machadolucas.diagnosis.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import me.machadolucas.diagnosis.questionnariesdata.ConditionalChecker;
import me.machadolucas.diagnosis.questionnariesdata.SpecificConditionalChecker;
import me.machadolucas.diagnosis.questionnariesdata.SuplementChecker;

@Data
@Document
public class Questionnaire implements Comparable<Questionnaire> {

    public Questionnaire() {
    }

    private List<Question> questions = new ArrayList<>();
    private List<Question> conditionalQuestions = new ArrayList<>();
    private List<Question> specificConditionalQuestions = new ArrayList<>();
    @Id
    private String id;

    /**
     * Título/nome do questionário
     */
    @Transient
    private String title;

    /**
     * Quando marcado como false, o questionario tem de ser salvo apos o postconstruct
     */
    @Transient
    private boolean exists = true;

    /**
     * Valor da ordem que o questionário deve ser exibido
     */
    @Transient
    private int order;

    private LocalDateTime lastUpdated;

    /**
     * Descrição ou instruções do questionário, que é mostrado antes das perguntas
     */
    @Transient
    private String description;
    private String moreInfo;
    @DBRef
    private Questionnaire suplementQuestionnaire;

    @Transient
    private ConditionalChecker conditionalChecker;
    @Transient
    private SuplementChecker suplementChecker;
    @Transient
    private SpecificConditionalChecker specificConditionalChecker;

    public boolean shouldAddComplementaryQuestions(final boolean complementaryQuestionsVisible) {
        return this.conditionalChecker != null && this.conditionalChecker.checkForConditional(this.getQuestions()) &&
                !complementaryQuestionsVisible;
    }

    public boolean shouldHideComplementaryQuestions(final boolean complementaryQuestionsVisible) {
        return this.conditionalChecker == null || !this.conditionalChecker.checkForConditional(this.getQuestions())
                && complementaryQuestionsVisible;
    }

    public boolean shouldAddSuplementButton(final boolean suplementQuestionsButtonVisible) {
        return this.suplementChecker != null && this.suplementChecker.checkForSuplements(this.getQuestions(), this
                .getConditionalQuestions()) && !suplementQuestionsButtonVisible;
    }

    public boolean shouldHideSuplementarButton(final boolean suplementQuestionsButtonVisible) {
        return this.suplementChecker == null || !this.suplementChecker.checkForSuplements(this.getQuestions(), this
                .getConditionalQuestions()) && suplementQuestionsButtonVisible;
    }

    public Map<Question, List<Question>> getSpecificConditionalQuestionsToShow() {
        if (this.specificConditionalChecker != null) {
            return this.specificConditionalChecker.getSpecificConditionalQuestionsToShow(getQuestions(),
                    getConditionalQuestions());
        }
        return new HashMap<>();
    }

    public Map<Question, List<Question>> getAllSpecificConditionalQuestions() {
        if (this.specificConditionalChecker != null) {
            return this.specificConditionalChecker.getAllSpecificConditionalQuestions(getQuestions(),
                    getConditionalQuestions());
        }
        return new HashMap<>();
    }

    /**
     * Verifica o quanto o questionário está preenchido.
     *
     * @return "I" se ele está vazio, "P" se está parcialmente preenchido, e "C" se está completo.
     */
    public String checkQuestionnaireFillStatus() {

        final int totalQuestionsAmount;
        int answeredQuestionsCount = 0;
        int titleQuestionsCount = 0;
        int resumoOnlyQuestionsCount = 0;

        //Se tiver suplementar disponível, verifica só o preenchimento dele
        if (getSuplementQuestionnaire() != null && shouldAddSuplementButton(false)) {
            totalQuestionsAmount = getSuplementQuestionnaire().getQuestions().size();
            for (final Question q : getSuplementQuestionnaire().getQuestions()) {
                if (q.getAnswer() != null || q.getAnswerMore() != null //
                        || q.getAnswerPast() != null || q.getAnswerMorePast() != null) {
                    answeredQuestionsCount++;
                } else if (QuestionType.CHAPTER_TITLE.equals(q.getQuestionType()) //
                        || QuestionType.SUBQUESTIONS_TITLE.equals(q.getQuestionType())) {
                    titleQuestionsCount++;
                } else if (q.isResumoOnly()) {
                    resumoOnlyQuestionsCount++;
                }
            }
            if (answeredQuestionsCount + titleQuestionsCount + resumoOnlyQuestionsCount < totalQuestionsAmount) {
                return "P";
            } else {
                return "C";
            }
        }

        //Se não tiver suplementar, verifica as questões do questionário
        totalQuestionsAmount = getQuestions().size();
        for (final Question q : getQuestions()) {
            if (q.getAnswer() != null || q.getAnswerMore() != null //
                    || q.getAnswerPast() != null || q.getAnswerMorePast() != null) {
                answeredQuestionsCount++;
            } else if (QuestionType.CHAPTER_TITLE.equals(q.getQuestionType()) //
                    || QuestionType.SUBQUESTIONS_TITLE.equals(q.getQuestionType())) {
                titleQuestionsCount++;
            } else if (q.isResumoOnly()) {
                resumoOnlyQuestionsCount++;
            }
        }

        if (answeredQuestionsCount == 0) {
            return "I";
        } else if (answeredQuestionsCount + titleQuestionsCount + resumoOnlyQuestionsCount < totalQuestionsAmount) {
            return "P";
        } else {
            return "C";
        }

    }

    @Override
    public int compareTo(final Questionnaire o) {
        return this.getOrder() - o.getOrder();
    }
}

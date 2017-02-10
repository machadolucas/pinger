package me.machadolucas.diagnosis.service;

import com.vaadin.ui.AbstractField;
import me.machadolucas.diagnosis.entity.Question;

public class QuestionUtils {

    public static void fromQuestionToFields(final Question question, final Question.AnswerType answerType, final AbstractField field) {
        switch (answerType) {
            case ANSWER:
                if (question.getAnswer() != null)
                    field.setValue(question.getAnswer());
                break;
            case ANSWER_MORE:
                if (question.getAnswerMore() != null)
                    field.setValue(question.getAnswerMore());
                break;
            case ANSWER_PAST:
                if (question.getAnswerPast() != null)
                    field.setValue(question.getAnswerPast());
                break;
            case ANSWER_MORE_PAST:
                if (question.getAnswerMorePast() != null)
                    field.setValue(question.getAnswerMorePast());
                break;
        }
    }

    public static void fromFieldToQuestion(final AbstractField field, final Question.AnswerType answerType, final Question question) {

        switch (answerType) {
            case ANSWER:
                question.setAnswer(field.getValue());
                break;
            case ANSWER_MORE:
                question.setAnswerMore(field.getValue());
                break;
            case ANSWER_PAST:
                question.setAnswerPast(field.getValue());
                break;
            case ANSWER_MORE_PAST:
                question.setAnswerMorePast(field.getValue());
                break;
        }

    }
}

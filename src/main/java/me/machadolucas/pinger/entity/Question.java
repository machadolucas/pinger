package me.machadolucas.diagnosis.entity;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.util.StringUtils;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Question {

    /**
     * Numero da questão/identificador
     */
    @NonNull
    private String number;
    @Transient // Só para exibir na tela
    @NonNull
    private String title;
    @Transient // Só para exibir na tela
    private String description = "";
    @NonNull
    @Transient // só para a a tela
    private QuestionType questionType;

    @Transient
    private boolean resumoOnly;

    private Object answer;
    private Object answerMore;
    private Object answerPast;
    private Object answerMorePast;

    public Label getFormattedTitleForResultsView() {
        //Adiciona a descricao da questao na linha abaixo em tamanho menor, se houver
        final Label questionTitle = new Label(getTitle(), ContentMode.HTML);
        if (!StringUtils.isEmpty(getDescription())) {
            questionTitle.setValue(questionTitle.getValue() + "</br><small>" + getDescription() +
                    "</small>");
        }

        // Se for CHAPTER_TITLE ou SUBQUESTIONS_TITLE, formata do jeito certo
        if (QuestionType.CHAPTER_TITLE.equals(getQuestionType())) {
            questionTitle.addStyleName(ValoTheme.LABEL_COLORED);
            questionTitle.addStyleName(ValoTheme.LABEL_BOLD);
            questionTitle.addStyleName(ValoTheme.LABEL_H3);
        } else if (QuestionType.SUBQUESTIONS_TITLE.equals(getQuestionType())) {
            questionTitle.addStyleName(ValoTheme.LABEL_BOLD);
        }
        return questionTitle;
    }

    public enum AnswerType {
        ANSWER("atual"), ANSWER_MORE(""), ANSWER_PAST("passado"), ANSWER_MORE_PAST("");

        @Getter
        private final String placeholder;

        AnswerType(final String placeholder) {
            this.placeholder = placeholder;
        }
    }

}

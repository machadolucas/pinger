package me.machadolucas.diagnosis.view;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.machadolucas.diagnosis.entity.Question;
import me.machadolucas.diagnosis.entity.SectionType;
import me.machadolucas.diagnosis.service.QuestionUtils;

import java.util.LinkedList;
import java.util.List;

public class QuestionConstructor {

    public List<AbstractField> populateQuestionOptions(final Question question, final boolean required,
                                                       SectionType sectionType) {
        final List<AbstractField> fields = new LinkedList<>();

        List<AbstractField> present = generateFields(question, Question.AnswerType.ANSWER,
                Question.AnswerType.ANSWER_MORE);
        if (present.size() >= 1) {
            present.get(0).setRequired(required);// TODO só o primeiro campo vai ser required sempre? : Depende do caso
        }

        boolean initialQuestion = SectionType.INICIAL == sectionType;

        List<AbstractField> past = new LinkedList<>();
        if (!initialQuestion) {
            past = generateFields(question, Question.AnswerType.ANSWER_PAST, Question.AnswerType.ANSWER_MORE_PAST);
            if (past.size() >= 1) {
                past.get(0).setRequired(required);// TODO só o primeiro campo vai ser required sempre? : Depende do caso
            }
        } else {
            for (AbstractField field : present) {
                if (field instanceof TextField) {
                    ((TextField) field).setInputPrompt(null);
                }
                if (field instanceof NativeSelect) {
                    field.setCaption(null);
                }
            }
        }

        fields.addAll(present);
        fields.addAll(past);
        return fields;
    }

    private List<AbstractField> generateFields(Question question, Question.AnswerType answer,
                                               Question.AnswerType answerMore) {
        final List<AbstractField> fields = new LinkedList<>();

        switch (question.getQuestionType()) {
            case ESCALA_ZERO_TRES: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "0", //
                        1, "1", //
                        2, "2", //
                        3, "3");
                fields.add(select);
                break;
            }
            case ESCALA_UM_CINCO: {
                final NativeSelect select = createNativeSelect(question, answer, 1, "1", //
                        2, "2", //
                        3, "3", //
                        4, "4", //
                        5, "5");
                fields.add(select);
                break;
            }
            case ESCALA_UM_SEIS: {
                final NativeSelect select = createNativeSelect(question, answer, 1, "1", //
                        2, "2", //
                        3, "3", //
                        4, "4", //
                        5, "5", //
                        6, "6");
                fields.add(select);
                break;
            }
            case NAO_SIM: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "Não", //
                        1, "Sim");
                fields.add(select);
                break;
            }
            case DESCRITIVA: {
                final TextField textField = createTextField(question, answer);
                fields.add(textField);
                break;
            }
            case NAO_SIM_NAO_SIM: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "Não", //
                        1, "Sim");
                fields.add(select);

                final NativeSelect select2 = createNativeSelect(question, answerMore, 0, "Não", //
                        1, "Sim");
                fields.add(select2);
                break;
            }
            case NAO_SIM_MAIS:
            case NAO_SIM_SE_SIM_MAIS:// TODO vai ter que ser inteligente, talvez isso devesse ser uma classe
            case NAO_SIM_SE_NAO_MAIS: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "Não", //
                        1, "Sim");
                fields.add(select);

                final TextField textField = createTextField(question, answerMore);
                fields.add(textField);
                break;
            }
            case AUSENTE_ABAIXO_LIMIAR_LIMIAR: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "Ausente", //
                        1, "Abaixo do Limiar", //
                        2, "Limiar");
                fields.add(select);
                break;
            }
            case AUSENTE_SUSPEITO_PRESENTE: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "Ausente", //
                        1, "Suspeito", //
                        2, "Presente");
                fields.add(select);
                break;
            }
            case AUSENTE_ABAIXO_LIMIAR_LIMIAR_MAIS: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "Ausente", //
                        1, "Abaixo do Limiar", //
                        2, "Limiar");
                fields.add(select);

                final TextField textField = createTextField(question, answerMore);
                fields.add(textField);
                break;
            }
            case AUSENTE_SUSPEITO_PRESENTE_MAIS: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "Ausente", //
                        1, "Suspeito", //
                        2, "Presente");
                fields.add(select);

                final TextField textField = createTextField(question, answerMore);
                fields.add(textField);
                break;
            }
            case FREQUENCIA_CADA_1SEMA_2SEMA_1MES_2MESES_MAIS: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        1, "Uma vez por semana", //
                        2, "Uma vez a cada 2 semanas", //
                        30, "Uma vez por mês", //
                        60, "Uma vez a cada dois meses",
                        61, "Outra");
                fields.add(select);

                final TextField textField = createTextField(question, answerMore);
                fields.add(textField);
                break;
            }
            case FREQUENCIA_NAO_SE_APLICA_RARAMENTE_AS_VEZES_FREQUENTEMENTE_MUITO_FREQUENTEMENTE: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        1, "não se aplica", //
                        2, "usado raramente", //
                        3, "usado às vezes", //
                        4, "usado frequentemente",//
                        5, "usado muito frequentemente");
                fields.add(select);
                break;
            }
            case TEMPO_NUNCA_4M_2M: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "Nunca", //
                        2, "2 Meses", //
                        4, "4 Meses");
                fields.add(select);
                break;
            }
            case TEMPO_NUNCA_10M_6M_4M_2M: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "Nunca", //
                        2, "2 Meses", //
                        4, "4 Meses", 6, "6 Meses", 10, "10 Meses");
                fields.add(select);
                break;
            }
            case ERRADO_CERTO: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "Errado", //
                        1, "Certo");
                fields.add(select);
                break;
            }
            case SUBQUESTIONS_TITLE:
            case CHAPTER_TITLE:
                break;
            case COM_QUEM_MORO_MAIS: {
                final NativeSelect select = createNativeSelect(question, answer, 1, "Eu vivo com meus pais biológicos", //
                        2, "Eu vivo com minha mãe somente", //
                        3, "Eu vivo com minha mãe e seu companheiro", //
                        4, "Eu vivo com meu pai somente", //
                        5, "Eu vivo com meu pai e sua companheira", //
                        6, "Eu vivo em guarda compartilhada", //
                        7, "Eu vivo em uma família de acolhimento", //
                        8, "Outro. Especifique");
                fields.add(select);

                final TextField textField = createTextField(question, answerMore);
                fields.add(textField);
                break;
            }
            case ESCOLARIDADE: {
                final NativeSelect select = createNativeSelect(question, answer, 1, "Nenhuma escolaridade", //
                        2, "Colegial Completo", //
                        3, "Colegial Profissionalizante", //
                        4, "Passou no vestibular", //
                        5, "Dois anos de faculdade", //
                        6, "Cinco anos de faculdade", //
                        7, "Não sei");
                fields.add(select);
                break;
            }
            case IDEACAO_SUICIDA_INTENCIONALIDADE: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "Nenhuma ideação suicida", 1,
                        "Ideação suicida passiva (desejo de morrer)", 2,
                        "Ideação suicida ativa (pensar em querer por fim a sua vida, se suicidar)");
                fields.add(select);

                final NativeSelect select2 = createNativeSelect(question, answer, 0, "Indeterminado", 1, "Mascarado", 2,
                        "Ambivalente", 3, "Definitivo");
                fields.add(select2);
                break;
            }
            case TIPO_TENTATIVA_INTENCIONALIDADE: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "Estrangulamento", 1, "Intoxicação", 2,
                        "Laceração", 3, "Precipitação", 4, "Eletrocussão", 5, "Colisão (carro/trem)", 6, "Arma de fogo", 7,
                        "Outro");
                fields.add(select);

                final NativeSelect select2 = createNativeSelect(question, answer, 0, "Indeterminado", 1, "Mascarado", 2,
                        "Ambivalente", 3, "Definitivo");
                fields.add(select2);
                break;
            }
            case FREQUENCIA_DROGAS: {
                final NativeSelect select = createNativeSelect(question, answer, //
                        0, "Não consumido",
                        1, "De vez em quando", //
                        2, "Em média uma vez por mês", //
                        3, "No final de semana ou 1 ou 2 vezes por semana", //
                        4, "3 vezes ou mais por semana, mas não todos os dias", //
                        5, "Todos os dias");
                fields.add(select);
                break;
            }
            case IDADE_INICIO_DROGAS: {
                final NativeSelect select = createNativeSelect(question, answer, //
                        1, "16 anos ou mais", //
                        2, "14 a 15 anos", //
                        3, "Menos de 14 anos");
                fields.add(select);
                break;
            }
            case IDADE_INICIO_ALCOOL: {
                final NativeSelect select = createNativeSelect(question, answer, //
                        1, "16 anos ou mais", //
                        2, "12 a 15 anos", //
                        3, "Menos de 12 anos");
                fields.add(select);
                break;
            }
            case FREQUENCIA_ALCOOL: {
                final NativeSelect select = createNativeSelect(question, answer, //
                        0, "Nenhuma vez", //
                        1, "1 a 2 vezes", //
                        2, "3 a 25 vezes", //
                        3, "25 vezes ou mais");
                fields.add(select);
                break;
            }
            case ESCALA_ZERO_OU_MAIOR_MAIS: {
                final NativeSelect select = createNativeSelect(question, answer, 0, "0", //
                        1, "Pelo menos uma");
                fields.add(select);

                final TextField textField = createTextField(question, answerMore);
                fields.add(textField);
                break;
            }
            case TEMPO_6_MESES_12_MESES: {
                final NativeSelect select = createNativeSelect(question, answer, 6, "6 Meses", 10, "12 Meses");
                fields.add(select);
                break;
            }
            case TEMPO_4_SEMANAS_2_SEMANAS: {
                final NativeSelect select = createNativeSelect(question, answer, 2, "2 Semanas", 4, "4 Semanas");
                fields.add(select);
                break;
            }


            case TRISTEZA: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não me sinto triste.", //
                        1, "Eu me sinto triste.", //
                        2, "Estou triste e não consigo sair disso.", //
                        3, "Estou tão triste ou infeliz que não consigo suportar.");
                fields.add(select);
                break;
            }

            case PESSIMISMO: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não estou especialmente desanimado quanto ao meu futuro.", //
                        1, "Eu me sinto desanimado quanto ao meu futuro.", //
                        2, "Acho que não tenho nada a esperar.", //
                        3, "Acho o futuro sem esperança e tenho a impressãoque as coisas não podem melhorar.");
                fields.add(select);
                break;
            }

            case FRACASSOS_NO_PASSADO: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não me sinto fracasso.", //
                        1, "Acho que fracassarei mais do que um indivíduo comum.", //
                        2, "Quando olho pra trás na minha vida , tudo o que posso ver é um monte de fracassos.", //
                        3, "Acho que, como pessoa sou um completo fracasso.");
                fields.add(select);
                break;
            }

            case PERDA_DE_PRAZER: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Tenho tanto prazer em tudo, como antes.", //
                        1, "Não sinto mais prazer nas coisas, como antes.", //
                        2, "Não encontro prazer real em mais nada.", //
                        3, "Estou insatisfeito(a) ou aborrecido com tudo.");
                fields.add(select);
                break;
            }

            case SENTIMENTO_DE_CULPA: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não me sinto especialmente culpado(a).", //
                        1, "Eu me sinto culpado(a) grande parte do tempo.", //
                        2, "Eu me sinto culpado(a) a maior parte do tempo.", //
                        3, "Eu me sinto sempre culpado(a).");
                fields.add(select);
                break;
            }

            case SENTIMENTO_DE_SER_PUNIDO: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não acho que esteja sendo punido(a).", //
                        1, "Acho que posso ser punido(a).", //
                        2, "Creio que vou ser punido(a).", //
                        3, "Acho que estou sendo punido(a).");
                fields.add(select);
                break;
            }

            case SENTIMENTOS_NEGATIVOS: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não me sinto decepcionado(a) comigo mesmo(a).", //
                        1, "Sinto-me decepcionado(a) comigo mesmo(a).", //
                        2, "Estou enjoado de mim.", //
                        3, "Eu me odeio.");
                fields.add(select);
                break;
            }

            case ATITUDE_CRITICA_CONSIGO: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não me sinto de qualquer modo pior que os outros.", //
                        1, "Sou crítico com relação à mim por minhas fraquezas ou erros.", //
                        2, "Eu me culpo sempre por minhas falhas.", //
                        3, "Eu me culpo por tudo de mal que acontece.");
                fields.add(select);
                break;
            }

            case PENSAMENTO_SUICIDA: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não tenho quaisquer ideias de me matar.", //
                        1, "Tenho ideias de me matar, mas não as executaria.", //
                        2, "Gostaria de me matar.", //
                        3, "Eu me mataria se tivesse oportunidade.");
                fields.add(select);
                break;
            }

            case CHORO: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não choro mais que o habitual.", //
                        1, "Choro mais agora do que costumava.", //
                        2, "Agora, choro o tempo todo.", //
                        3, "Costumava ser capaz de chorar, mas agora não consigo, mesmo que o queira.");
                fields.add(select);
                break;
            }

            case AGITACAO: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Eu não me sinto mais agitado ou tenso que o habitual.", //
                        1, "Eu me sinto mais agitado ou tenso que o habitual.", //
                        2, "Eu estou tão agitado ou tenso que tenho dificuldade de ficar tranquilo.", //
                        3, "Eu estou tão agitado ou tenso que tenho que me mexer o tempo todo, ou me ocupar com qualquer coisa.");
                fields.add(select);
                break;
            }

            case PERDA_DE_INTERESSE: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não perdi o interesse nas outras pessoas.", //
                        1, "Estou menos interessado pelas outras pessoas do que costumava estar .", //
                        2, "Perdi a maior parte do meu interesse pelas outras pessoas.", //
                        3, "Perdi todo o meu interesse nas outras pessoas.");
                fields.add(select);
                break;
            }

            case INDECISAO: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Tomo decisões tão bem quanto antes.", //
                        1, "Adio as tomadas de decisões mais do que costumava.", //
                        2, "Tenho mais dificuldade em tomar decisões do que antes.", //
                        3, "Absolutamente não consigo tomar decisões.");
                fields.add(select);
                break;
            }

            case DESVALORIZACAO: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não acho que de qualquer modo pareço pior do que antes.", //
                        1, "Estou preocupado em estar parecendo velho, ou sem atrativo.", //
                        2, "Acho que há mudanças permanentes na minha aparência que me fazem parecer sem atrativo.", //
                        3, "Acredito que pareço feio(a).");
                fields.add(select);
                break;
            }

            case PERDA_DE_ENERGIA: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Posso trabalhar tão bem como antes.", //
                        1, "É preciso algum esforço extra para fazer alguma coisa.", //
                        2, "Tenho que me esforçar muito para fazer alguma coisa.", //
                        3, "Não consigo mais fazer qualquer trabalho.");
                fields.add(select);
                break;
            }

            case MUDANCAS_HABITOS_SONO: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Consigo dormir tão bem como o habitual.", //
                        1, "Eu durmo um pouco mais do que costumava.", //
                        2, "Eu durmo um pouco menos oque costumava.", //
                        3, "Eu durmo muito mais do que costumava.", //
                        4, "Eu durmo muito menos do que costumava.", //
                        5, "Eu durmo quase o dia todo.", //
                        6, "Eu me acordo 1, ou 2, antes da hora e sou incapaz de voltar a dormir.");
                fields.add(select);
                break;
            }

            case IRRITABILIDADE: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não sou mais irritado agora do que já fui..", //
                        1, "Fico aborrecido(a) ou irritado(a) mais facilmente do que costumava.", //
                        2, "Agora, eu me sinto irritado(a) o tempo todo.", //
                        3, "Não me irrito mais com as coisas que costumavam me irritar.");
                fields.add(select);
                break;
            }

            case MUDANCA_APETITE: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "O meu apetite não mudou..", //
                        1, "Eu tenho um pouco menos de apetite que antes.", //
                        2, "Eu tenho um pouco mais de apetite que antes.", //
                        3, "Eu tenho muito menos apetite que antes.", //
                        4, "Eu tenho muito mais apetite que antes", //
                        5, "Eu perdi completamente o apetite.", //
                        6, "Eu tenho vontade de comer o tempo todo.");
                fields.add(select);
                break;
            }


            case DIFICULDADE_DE_CONCENTRACAO: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Eu consigo me concentrar tão bem como antes.", //
                        1, "Eu não consigo me concentrar tão bem como antes.", //
                        2, "Eu tenho dificuldade à me concentrar por muito tempo seja no que for.", //
                        3, "Eu me sinto incapaz de me concentrar em qualquer coisa.");
                fields.add(select);
                break;
            }

            case CANSACO: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não fico mais cansado(a) do que o habitual.", //
                        1, "Fico cansado(a) mais facilmente do que costumava.", //
                        2, "Fico cansado(a) em fazer qualquer coisa.", //
                        3, "Estou cansado(a) demais para fazer qualquer coisa.");
                fields.add(select);
                break;
            }

            case INTERESSE_POR_SEXO: {
                final NativeSelect select = createNativeSelect(question, answer,//
                        0, "Não notei qualquer mudança recente no meu interesse por sexo.", //
                        1, "Estou menos interessado(a) por sexo do que costumava.", //
                        2, "Estou muito menos interessado(a) por sexo agora", //
                        3, "Perdi completamente o interesse por sexo.");
                fields.add(select);
                break;
            }
        }
        return fields;
    }

    private TextField createTextField(final Question question, final Question.AnswerType answerType) {
        final TextField textField = new TextField();
        textField.setInputPrompt(answerType.getPlaceholder());

        // seta a opção escolhida carregada do banco
        QuestionUtils.fromQuestionToFields(question, answerType, textField);

        // informa qual answerType deve ser atualizado
        textField.setData(new QuestionData(question, answerType));

        return textField;
    }

    private NativeSelect createNativeSelect(final Question question, final Question.AnswerType answerType,
                                            final Object... values) {
        // inicializa as opcoes do select
        final NativeSelect select = new NativeSelect(answerType.getPlaceholder());
        for (int i = 0; i < values.length; i++) {
            select.addItem(values[i]);
            select.setItemCaption(values[i], (String) values[++i]);
        }

        // seta a opção escolhida carregada do banco
        QuestionUtils.fromQuestionToFields(question, answerType, select);

        // informa qual answerType deve ser atualizado
        select.setData(new QuestionData(question, answerType));

        return select;
    }

    @AllArgsConstructor
    @Data
    public class QuestionData {

        Question question;
        Question.AnswerType answerType;
    }
}

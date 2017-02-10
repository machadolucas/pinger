package me.machadolucas.diagnosis.entity;

/**
 * Tipos de questões. É utilizado para a interface saber quais tipos de opções renderizar para cada questão
 */
public enum QuestionType {
    ESCALA_ZERO_TRES, // Questão com escala de zero a três
    ESCALA_UM_CINCO, // Questão com escala de um a cinco
    ESCALA_UM_SEIS, // Questão com escala de um a seis
    NAO_SIM, // Questão com não(0)/sim(1)
    NAO_SIM_MAIS, // Questão com não(0)/sim(1), especifique mais
    ERRADO_CERTO, // Questão com não-errado(0)/sim-certo(1)
    AUSENTE_ABAIXO_LIMIAR_LIMIAR,//Questão com (0) ausente, abaixo do limiar (1), no limiar ou mais (2)
    AUSENTE_ABAIXO_LIMIAR_LIMIAR_MAIS,//Questão com (0) ausente, abaixo do limiar (1), no limiar ou mais (2), especifique mais
    AUSENTE_SUSPEITO_PRESENTE,//0, "Ausente", 1, "Suspeito",2, "Presente"
    AUSENTE_SUSPEITO_PRESENTE_MAIS,//0 "Ausente", 1 "Suspeito", 2 "Presente", especifique mais
    SUBQUESTIONS_TITLE,//para títulos de questões com a,b,c,d,etc
    CHAPTER_TITLE,//para títulos de capítulos dentro de um questionário
    NAO_SIM_NAO_SIM, // Questão com duas respostas não(0)/sim(1), denotadas pelo primeiro e segundo caracteres (00/01..)
    NAO_SIM_SE_SIM_MAIS, // Questão com não(0)/sim(1), se Sim, especifique mais
    NAO_SIM_SE_NAO_MAIS, // Questão com não(0)/sim(1), se Sim, especifique mais
    DESCRITIVA, // Texto livre
    FREQUENCIA_CADA_1SEMA_2SEMA_1MES_2MESES_MAIS, // Frequencia, a cada 1 semana, 2 semana, 1 mês, 2 meses
    TEMPO_NUNCA_10M_6M_4M_2M, // Tempo em meses
    TEMPO_NUNCA_4M_2M,   // Tempo em meses
    COM_QUEM_MORO_MAIS, //1. Eu vivo com meus pais biológicos 2. Eu vivo com minha mãe somente  3. Eu vivo com minha mãe e seu companheiro 4. Eu vivo com meu pai somente   5. Eu vivo com meu pai e sua companheira 6. Eu vivo em guarda compartilhada 7. Eu vivo em uma família de acolhimento 8. Outro. Especifique
    ESCOLARIDADE,//1, "Nenhuma escolaridade",// 2, "Colegial Completo",//3, "Colegial Profissionalizante",//4, "Passou no vestibular",//5, "Dois anos de faculdade",//6, "Cinco anos de faculdade",//7, "Não sei"
    IDEACAO_SUICIDA_INTENCIONALIDADE,//
    TIPO_TENTATIVA_INTENCIONALIDADE, //
    ESCALA_ZERO_OU_MAIOR_MAIS,  //(0) zero, (1) um ou mais
    TEMPO_6_MESES_12_MESES,//6, "6 Meses",10, "12 Meses"
    TEMPO_4_SEMANAS_2_SEMANAS,
    FREQUENCIA_DROGAS,   // 0, "Não consumido",1, "De vez em quando", //2, "Em média uma vez por mês", //3, "No Final de semana ou 1 ou 2 vezes por semana", //4, "3 vezes ou mais por semana, mas não todos os dias", //5, "Todos os dias"
    IDADE_INICIO_DROGAS, //1, "16 anos ou mais", //2, "14 a 15 anos", //3, "Menos de 14 anos"
    IDADE_INICIO_ALCOOL, //1, "16 anos ou mais", //2, "12 a 15 anos", //3, "Menos de 12 anos"
    FREQUENCIA_ALCOOL, //0, "Nenhuma vez", //1, "1 a 2 vezes", //2, "3 a 25 vezes", //3, "25 vezes ou mais"
    FREQUENCIA_NAO_SE_APLICA_RARAMENTE_AS_VEZES_FREQUENTEMENTE_MUITO_FREQUENTEMENTE, //1, "não se aplica", //2, "usado raramente", //3, "usado às vezes", //4, "usado frequentemente",//5, "usado muito frequentemente"
    TRISTEZA, PESSIMISMO, FRACASSOS_NO_PASSADO, PERDA_DE_PRAZER, SENTIMENTO_DE_CULPA, SENTIMENTO_DE_SER_PUNIDO, SENTIMENTOS_NEGATIVOS, ATITUDE_CRITICA_CONSIGO, PENSAMENTO_SUICIDA, CHORO, AGITACAO, INDECISAO, DESVALORIZACAO, PERDA_DE_ENERGIA, MUDANCAS_HABITOS_SONO, IRRITABILIDADE, MUDANCA_APETITE, DIFICULDADE_DE_CONCENTRACAO, CANSACO, INTERESSE_POR_SEXO, PERDA_DE_INTERESSE,
}



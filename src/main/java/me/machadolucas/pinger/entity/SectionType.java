package me.machadolucas.diagnosis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Transient;

@AllArgsConstructor
public enum SectionType {
    INICIAL("Inicial"), // Questionario inicial respondido pela criança
    RASTREAMENTO_C("Rastreamento - Adolescente"), // Questionario de rastreamento e suplemento respondido pela criança
    RASTREAMENTO_P("Rastreamento - Pais"); // Questionário de rastreamento e suplemento respondido pelos pais

    @Getter
    @Transient
    private String name;
}

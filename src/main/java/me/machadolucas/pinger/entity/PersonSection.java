package me.machadolucas.diagnosis.entity;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashMap;

public class PersonSection {

    @DBRef(lazy = true)
    @Getter
    protected HashMap<String, Questionnaire> questionnaires = new HashMap<>();
}

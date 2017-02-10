package me.machadolucas.diagnosis.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.machadolucas.diagnosis.questionnariesdata.SectionsCreator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@Document
@EqualsAndHashCode(of = "personCode")
public class Person {

    @Id
    private String id;

    /**
     * Gerado automaticamente
     */
    private String personCode;

    /**
     * Nome da pessoa. Campo opcional devido à anonimização, mas presente na interface caso necessário
     */
    private String personName = "";

    @DBRef(lazy = true)
    private AdminUser adminResponsible;

    private InitialSection initialSection;

    private ChildSection childSection;

    private ParentSection parentSection;

    private DoctorSection doctorSection;

    private final Date creationDate = new Date();

    /**
     * Codigos para logar nas sessões. Gerados automaticamente.
     */
    @Indexed
    private final Map<SectionType, String> codes = new HashMap<>();

    public void postConstruct() {
        SectionsCreator.sessaoInicial(this.initialSection);
        SectionsCreator.sessaoRastreamento_C(this.childSection);
        SectionsCreator.sessaoRastreamento_P(this.parentSection);
        SectionsCreator.sessaoDoutor(this.doctorSection);
    }

    public void postConstructInicial() {
        SectionsCreator.sessaoInicial(this.initialSection);
    }

    public void postConstructRastreamentoC() {
        SectionsCreator.sessaoRastreamento_C(this.childSection);
    }

    public void postConstructRastreamentoP() {
        SectionsCreator.sessaoRastreamento_P(this.parentSection);
    }

    public List<Questionnaire> getInexistentQuestionnaires() {
        final List<Questionnaire> inexistent = new LinkedList<>();
        this.initialSection.getQuestionnaires().forEach((name, quest) -> {
            if (!quest.isExists()) {
                inexistent.add(quest);
            }
        });
        this.childSection.getQuestionnaires().forEach((name, quest) -> {
            if (!quest.isExists()) {
                inexistent.add(quest);
            }
        });
        this.parentSection.getQuestionnaires().forEach((name, quest) -> {
            if (!quest.isExists()) {
                inexistent.add(quest);
            }
        });
        this.doctorSection.getQuestionnaires().forEach((name, quest) -> {
            if (!quest.isExists()) {
                inexistent.add(quest);
            }
        });

        return inexistent;
    }

}

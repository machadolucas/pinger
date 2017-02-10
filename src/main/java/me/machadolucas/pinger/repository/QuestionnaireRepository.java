package me.machadolucas.diagnosis.repository;

import me.machadolucas.diagnosis.entity.Questionnaire;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionnaireRepository extends MongoRepository<Questionnaire, String> {

}

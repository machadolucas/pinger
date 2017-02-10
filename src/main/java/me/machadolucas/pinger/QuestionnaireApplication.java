package me.machadolucas.diagnosis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class QuestionnaireApplication {

    public static void main(final String[] args) {
        SpringApplication.run(QuestionnaireApplication.class, args);
    }
}

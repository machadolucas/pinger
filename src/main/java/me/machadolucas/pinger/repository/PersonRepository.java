package me.machadolucas.diagnosis.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import me.machadolucas.diagnosis.entity.AdminUser;
import me.machadolucas.diagnosis.entity.Person;

public interface PersonRepository extends MongoRepository<Person, String> {

    Person findByPersonCode(String personCode);

    List<Person> findByAdminResponsible(AdminUser adminResponsible);

    Long countByAdminResponsible(AdminUser adminResponsible);

    Person findByPersonCodeAndAdminResponsible(String personCode, AdminUser adminResponsible);
}

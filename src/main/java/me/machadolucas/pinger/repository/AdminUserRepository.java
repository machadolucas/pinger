package me.machadolucas.diagnosis.repository;

import me.machadolucas.diagnosis.entity.AdminUser;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdminUserRepository extends MongoRepository<AdminUser, String> {

    AdminUser findByName(String name);

}

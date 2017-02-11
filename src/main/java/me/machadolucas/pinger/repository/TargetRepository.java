package me.machadolucas.pinger.repository;

import me.machadolucas.pinger.entity.TargetEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TargetRepository extends MongoRepository<TargetEntity, String> {

}

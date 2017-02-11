package me.machadolucas.pinger.repository;

import me.machadolucas.pinger.entity.SysUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserRepository extends MongoRepository<SysUser, String> {

    SysUser findByUsername(String name);

}

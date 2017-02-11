package me.machadolucas.pinger.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class SysUser {

    @Id
    private String id;
    private String username;
    private String password;
    private List<String> roles = new ArrayList<>();

    private boolean enabled = true;
    private boolean locked = false;
}

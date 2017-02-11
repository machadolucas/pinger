package me.machadolucas.pinger.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class TargetEntity {

    @Id
    private String id;

    @NotNull
    private String name;

    @NotNull
    private String url;

    private Date creationDate = new Date();

    private Date lastSuccess;

    private Date lastFail;

}

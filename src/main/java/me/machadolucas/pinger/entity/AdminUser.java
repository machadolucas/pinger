package me.machadolucas.diagnosis.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import lombok.Data;

@Data
public class AdminUser {

    @Id
    private String id;

    private String name;

    private String password;

    private Date creationDate = new Date();

    // Quantidade de pacientes. Apenas para exibicao
    @Transient
    private Long personsAmount;

    @Override
    public String toString() {
        return this.name.toLowerCase();
    }
}

package com.huseyincan.financeportfolio.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document("User")
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private byte[] photo;

    public User(String email, String password) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
    }

    public User(String id, String email, String password, byte[] photo) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.photo = photo;
    }

    public User(String id, String email, byte[] photo) {
        this.id = id;
        this.email = email;
        this.photo = photo;
    }
}

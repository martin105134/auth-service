package com.example.authservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "authdata")
@Getter
@Setter
public class AuthData {

    @Id
    @Column(name="username")
    private String username;
    @Column(name="password")
    private String password;
}

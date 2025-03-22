package com.example.authservice;

import org.springframework.data.repository.CrudRepository;


public interface AuthDataRepo extends CrudRepository<AuthData, String> {
}

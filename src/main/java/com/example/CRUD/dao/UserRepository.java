package com.example.CRUD.dao;

import com.example.CRUD.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    public List<User> findFirstUserByEmailAndPassword(String email, String password);
}

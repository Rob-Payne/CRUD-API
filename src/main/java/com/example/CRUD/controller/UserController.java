package com.example.CRUD.controller;

import com.example.CRUD.dao.UserRepository;
import com.example.CRUD.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    @GetMapping("/users")
    public Iterable<User> getUserList() {
        return this.userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return this.userRepository.findById(id);
    }

    @PostMapping("/users")
    public User insertUser(@RequestBody User myUser){
        return this.userRepository.save(myUser);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable Long id){
        userRepository.deleteById(id);
    }

    @PatchMapping("/users/{id}")
    public User patchUser(@PathVariable Long id, @RequestBody User myUser){

        //find record
        User oldRecord = userRepository.findById(id).get();
        //update record
        if(myUser.getEmail() == null){
            oldRecord.setEmail(oldRecord.getEmail());
        } else {oldRecord.setEmail(myUser.getEmail());}

        if(myUser.getPassword() == null){
            oldRecord.setPassword((oldRecord.getPassword()));
        } else {oldRecord.setPassword(myUser.getPassword());}
        //save record
        return userRepository.save(oldRecord);
    }

    @PostMapping("/users/authenticate")
    public Map<String, Object> authenticateUser(@RequestBody User credentials){
        boolean authenticated;

        //find record
        List<User> authUser = userRepository.findFirstUserByEmailAndPassword(credentials.getEmail(), credentials.getPassword());

        //Check to see if the user is present
        if(authUser.size() >=1){
            return Map.of("authenticated", "true","user",authUser.get(0));
        } else return Map.of("authenticated","false");

    }

}

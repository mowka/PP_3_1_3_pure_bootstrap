package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@RequestMapping("/rest")
@org.springframework.web.bind.annotation.RestController
public class RestController {

    private final UserService userService;

    @Autowired
    public RestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public User createUser(@RequestBody User user) {
        userService.save(user);
        return user;
    }

    @PutMapping("/user")
    public User editUser(@RequestBody User user) {
        userService.editUser(user.getId(), user);
        return user;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable long id) {
        try {
            userService.deleteUser(id);
            return "User with id: " + id + " was successfully  deleted";
        } catch (EmptyResultDataAccessException e) {
            return "User with id: " + id + " not found in database =)";
        }
    }

    @GetMapping()
    public List<User> showAllUsers() {
        return userService.getAllUsers();
    }

}

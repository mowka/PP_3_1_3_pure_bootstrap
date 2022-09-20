package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public AdminController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    //Отображение пользователей - только для админов
    @GetMapping("/")
    public String getAllUsers(Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        User nullUser = new User();
        model.addAttribute("userRoles", currentUser.getRolesInfo());
        model.addAttribute("userEmail", currentUser.getEmail());
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("nullUserRole", userService.getAllRoles());
        model.addAttribute("newUser", nullUser);
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);
        return "main-page";
    }


    //Добавление пользователя
//    @GetMapping("/add-user")
//    public String addUser(Model model) {
//        User user = new User();
//        user.setRoles(new HashSet<>(Arrays.asList(userService.getAllRoles().get(0))));
//        model.addAttribute("roles", userService.getAllRoles());
//        model.addAttribute("newUser", user);
//        return "user-edit-page";
//    }

//    @GetMapping("/user-info/{id}")
//    public String userInfo(@PathVariable("id") long id, Model model) {
//        User currentUser = userService.getUser(id);
//        model.addAttribute("userInfo", currentUser);
//        return "user-page";
//    }

    @GetMapping("/user-admin")
    public String userInfo(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("currentUser", user);
        return "user-admin";
    }

    //Добавление пользователя
    @PostMapping()
    public String createUser(@ModelAttribute("newUser") User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUsername(user.getEmail());
        userService.save(user);
        return "redirect:/admin/";
    }

    //Удаление пользователя
    @RequestMapping("/user-delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin/";
    }

    @RequestMapping("/update-info/{id}")
    public String userInfo(Model model, @PathVariable("id") long id) {
        User currentUser = userService.getUser(id);
        model.addAttribute("roles", userService.getAllRoles());
        model.addAttribute("newUser", currentUser);
        return "user-edit-page";
    }
}

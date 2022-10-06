package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getAllUsers(Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("allRoles", userService.getAllRoles());
        model.addAttribute("newUser", new User());
        model.addAttribute("currentUser", currentUser);
        return "main-page";
    }

    @PostMapping()
    public String createUser(@ModelAttribute("newUser") User user) {
        userService.save(user);
        return "redirect:/admin/";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") long id, Principal principal, HttpServletRequest request) throws ServletException {
        if (userService.findByUsername(principal.getName()).getId() == id) {
            userService.deleteUser(id);
            request.logout();
            return "redirect:/login";
        } else {
            userService.deleteUser(id);
            return "redirect:/admin/";
        }
    }

    @PatchMapping("/{id}")
    public String editUser(@PathVariable("id") long id, @ModelAttribute("editUser") User editUser) {
        userService.editUser(id, editUser);
        return "redirect:/admin/";
    }


    @RequestMapping("/index")
    public String index() {
        return "index";
    }
}

package com.servicebackend.controller;

import com.servicebackend.dto.AuthResponse;
import com.servicebackend.entity.User;
import com.servicebackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody Map<String,String> user) {
        return authService.login(user.get("email"), user.get("password"));
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return authService.getAllUser();
    }

    @DeleteMapping("/delete")
    public void deleteAll(){
        authService.deleteAllUser();
    }

}

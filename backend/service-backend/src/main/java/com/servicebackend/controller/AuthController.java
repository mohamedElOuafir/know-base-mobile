package com.servicebackend.controller;

import com.servicebackend.dto.AuthResponse;
import com.servicebackend.dto.UserRegisterDto;
import com.servicebackend.entity.User;
import com.servicebackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping(value="/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AuthResponse register(
            @RequestPart("firstName") String firstName,
            @RequestPart("lastName") String lastName,
            @RequestPart("email") String email,
            @RequestPart("password") String password,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {

        UserRegisterDto user = new UserRegisterDto();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setProfileImage(profileImage);

        return authService.register(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody Map<String,String> user) {
        if(authService.login(user.get("email"), user.get("password")).isPresent())
            return authService.login(user.get("email"), user.get("password")).get();

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAuthenticated(false);

        return authResponse;
    }


    @PutMapping("/users/profile")
    public ResponseEntity<AuthResponse> updateProfile(
            Authentication authentication,
            @RequestPart("firstName") String firstName,
            @RequestPart("lastName") String lastName,
            @RequestPart(value = "password", required = false) String password,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        String email = authentication.getName();
        AuthResponse updatedUser = authService.updateProfile(email, firstName, lastName, password, profileImage);
        return ResponseEntity.ok(updatedUser);
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

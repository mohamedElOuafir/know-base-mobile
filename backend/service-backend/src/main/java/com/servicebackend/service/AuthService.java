package com.servicebackend.service;


import com.servicebackend.dto.AuthResponse;
import com.servicebackend.entity.User;
import com.servicebackend.exceptions.EmailAlreadyUsedException;
import com.servicebackend.repository.UserRepository;
import com.servicebackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private JwtUtil jwtUtil;


    public User register(User user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new EmailAlreadyUsedException(user.getEmail());
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new  RuntimeException("User not found"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token, user.getEmail());
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public void deleteAllUser() {
        userRepository.deleteAll();
    }

}

package com.servicebackend.service;


import com.servicebackend.dto.AuthResponse;
import com.servicebackend.dto.UserRegisterDto;
import com.servicebackend.entity.User;
import com.servicebackend.exceptions.EmailAlreadyUsedException;
import com.servicebackend.repository.UserRepository;
import com.servicebackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private FileUploadService fileUploadService;


    public AuthResponse register(UserRegisterDto userDto) {
        if(userRepository.existsByEmail(userDto.getEmail())){
            AuthResponse authResponse = new AuthResponse();
            authResponse.setAuthenticated(false);
            return authResponse;
        }


        User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(encoder.encode(userDto.getPassword()));
        newUser.setFirstName(userDto.getFirstName());
        newUser.setLastName(userDto.getLastName());

        if(userDto.getProfileImage() != null && !userDto.getProfileImage().isEmpty()) {
            String profileImage = fileUploadService.uploadProfileImageToSupabase(userDto.getProfileImage());
            newUser.setProfileImage(profileImage);
        }

        User savedUser = userRepository.save(newUser);
        String token = jwtUtil.generateToken(savedUser.getEmail());


        return new AuthResponse(
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getProfileImage(),
                token,
                true
        );
    }

    public Optional<AuthResponse> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            User currentUser = user.get();

            if (encoder.matches(password, currentUser.getPassword())) {

                String token = jwtUtil.generateToken(currentUser.getEmail());


                return Optional.of(new AuthResponse(
                        currentUser.getEmail(),
                        currentUser.getFirstName(),
                        currentUser.getLastName(),
                        currentUser.getProfileImage(),
                        token,
                        true
                ));
            }
        }
        return Optional.empty();

    }


    public AuthResponse updateProfile(String email, String firstName, String lastName,
                                         String password, MultipartFile profileImage) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update names
        user.setFirstName(firstName);
        user.setLastName(lastName);

        // Update password only if provided
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(encoder.encode(password));
        }

        // Update profile image only if provided
        if (profileImage != null && !profileImage.isEmpty()) {
            String imageUrl = fileUploadService.uploadProfileImageToSupabase(profileImage);
            user.setProfileImage(imageUrl);
        }

        User savedUser = userRepository.save(user);

        // Build response
        AuthResponse dto = new AuthResponse();
        dto.setEmail(savedUser.getEmail());
        dto.setFirstName(savedUser.getFirstName());
        dto.setLasName(savedUser.getLastName());
        dto.setProfileImage(savedUser.getProfileImage());
        dto.setAuthenticated(true);

        // Regenerate token with updated info
        String newToken = jwtUtil.generateToken(savedUser.getEmail());
        dto.setToken(newToken);

        return dto;
    }



    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public void deleteAllUser() {
        userRepository.deleteAll();
    }


}

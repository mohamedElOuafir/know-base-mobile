package com.servicebackend.dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private MultipartFile profileImage;
}

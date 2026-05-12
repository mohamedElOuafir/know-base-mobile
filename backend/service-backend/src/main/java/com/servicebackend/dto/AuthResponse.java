package com.servicebackend.dto;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String email;
    private String firstName;
    private String lasName;
    private String profileImage;
    private String token;
    private boolean authenticated;

}
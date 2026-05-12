package com.rag.knowbase.data.dto;

public class UserResponseDto {
    private String email;
    private String firstName;
    private String lasName;
    private String profileImage;
    private String token;
    private boolean authenticated;

    public UserResponseDto(String email, String firstName, String lasName, String profileImage, String token, boolean authenticated) {
        this.email = email;
        this.firstName = firstName;
        this.lasName = lasName;
        this.profileImage = profileImage;
        this.token = token;
        this.authenticated = authenticated;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLasName() {
        return lasName;
    }

    public void setLasName(String lasName) {
        this.lasName = lasName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}

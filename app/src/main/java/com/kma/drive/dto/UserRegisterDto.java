package com.kma.drive.dto;

public class UserRegisterDto {
    private String account;
    private String password;
    private String avatar;
    private String email;
    private String username;

    public UserRegisterDto(String acconut, String password, String avatar, String email, String username) {
        this.account = acconut;
        this.password = password;
        this.avatar = avatar;
        this.email = email;
        this.username = username;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}

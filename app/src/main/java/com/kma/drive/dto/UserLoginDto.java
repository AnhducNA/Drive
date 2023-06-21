package com.kma.drive.dto;

 public class UserLoginDto {
    private String account;
    private String password;

    public UserLoginDto(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}

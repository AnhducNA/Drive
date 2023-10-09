package com.kma.drive.dto;

public class ResetPasswordDto {
    private String mail;
    private String code;
    private String password;

    public ResetPasswordDto(String mail, String code, String password) {
        this.mail = mail;
        this.code = code;
        this.password = password;
    }
}

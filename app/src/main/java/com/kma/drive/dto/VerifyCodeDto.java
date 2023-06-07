package com.kma.drive.dto;

public class VerifyCodeDto {
    private String account;
    private String verifyCode;

    public VerifyCodeDto(String account, String verifyCode) {
        this.account = account;
        this.verifyCode = verifyCode;
    }

    public String getAccount() {
        return account;
    }

    public String getVerifyCode() {
        return verifyCode;
    }
}

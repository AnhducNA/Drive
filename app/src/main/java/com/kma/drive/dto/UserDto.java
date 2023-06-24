package com.kma.drive.dto;

import java.sql.Date;

public class UserDto {
    public static final String JWT = "jwt";
    public static final String USER_NAME = "username";
    public static final String DATE_OF_BIRTH = "datOfBirth";
    public static final String EMAIL = "email";
    public static final String AVATAR = "avatar";
    public static final String ID = "id";

    private String jwt;
    private String username;
    private Date datOfBirth;
    private String email;
    private String avatar;
    private int id;

    public UserDto(String jwt, String username, Date datOfBirth, String email, String avatar, int id) {
        this.jwt = jwt;
        this.username = username;
        this.datOfBirth = datOfBirth;
        this.email = email;
        this.avatar = avatar;
        this.id = id;
    }

    public String getJwt() {
        return jwt;
    }

    public String getUsername() {
        return username;
    }

    public Date getDatOfBirth() {
        return datOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getId() {
        return id;
    }
}

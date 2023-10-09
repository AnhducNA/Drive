package com.kma.drive.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Date;

public class UserDto {
    public static final String JWT = "jwt";
    public static final String USER_NAME = "username";
    public static final String DATE_OF_BIRTH = "datOfBirth";
    public static final String EMAIL = "email";
    public static final String AVATAR = "avatar";
    public static final String ID = "id";
    public static final String USAGE = "storageUsage";

    @SerializedName(JWT)
    @Expose
    private String jwt;
    @SerializedName(USER_NAME)
    @Expose
    private String username;
    @SerializedName(DATE_OF_BIRTH)
    @Expose
    private Date datOfBirth;
    @SerializedName(EMAIL)
    @Expose
    private String email;
    @SerializedName(AVATAR)
    @Expose
    private String avatar;
    @SerializedName(ID)
    @Expose
    private int id;
    @SerializedName(USAGE)
    @Expose
    private long storageUsage;

    public UserDto(String jwt, String username, Date datOfBirth, String email, String avatar, int id, long storageUsage) {
        this.jwt = jwt;
        this.username = username;
        this.datOfBirth = datOfBirth;
        this.email = email;
        this.avatar = avatar;
        this.id = id;
        this.storageUsage = storageUsage;
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

    public long getStorageUsage() {
        return storageUsage;
    }

    public void setStorageUsage(long storageUsage) {
        this.storageUsage = storageUsage;
    }
}

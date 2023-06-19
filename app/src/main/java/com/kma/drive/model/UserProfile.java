package com.kma.drive.model;

public class UserProfile {
    private String avatar;
    private String email;
    private String username;

    public UserProfile(String avatar, String email, String username) {
        this.avatar = avatar;
        this.email = email;
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

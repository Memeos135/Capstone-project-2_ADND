package com.example.macros;

public class UserCreds {
    private String email;
    private String password;
    private String profile_picture;
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public UserCreds() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserCreds(String email, String password, String profile_picture, String name) {
        this.email = email;
        this.password = password;
        this.profile_picture = profile_picture;
        this.name = name;
    }
}

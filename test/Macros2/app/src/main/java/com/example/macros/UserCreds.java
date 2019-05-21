package com.example.macros;

public class UserCreds {
    private String email;
    private String password;
    private String profile_picture;

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

    public UserCreds(String email, String password, String profile_picture) {
        this.email = email;
        this.password = password;
        this.profile_picture = profile_picture;
    }
}

package com.example.macros;

public class UserCreds {
    private String email;
    private String password;
    private String profile_picture;
    private String name;
    private String mobileNumber;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public UserCreds() {
    }

    public UserCreds(String email, String password, String profile_picture, String name, String mobileNumber) {
        this.email = email;
        this.password = password;
        this.profile_picture = profile_picture;
        this.name = name;
        this.mobileNumber = mobileNumber;
    }
}

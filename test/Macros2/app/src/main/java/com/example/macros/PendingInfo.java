package com.example.macros;

public class PendingInfo {
    private String name;
    private String email;
    private String imageURL;
    private String uID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public PendingInfo() {
    }

    public PendingInfo(String name, String email, String imageURL, String uID) {
        this.name = name;
        this.email = email;
        this.imageURL = imageURL;
        this.uID = uID;
    }
}

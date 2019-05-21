package com.example.macros;

public class ProfileRecordSetter {
    private String protein;
    private String fats;
    private String carbs;
    private String date;

    public ProfileRecordSetter() {
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getFats() {
        return fats;
    }

    public void setFats(String fats) {
        this.fats = fats;
    }

    public String getCarbs() {
        return carbs;
    }

    public void setCarbs(String carbs) {
        this.carbs = carbs;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ProfileRecordSetter(String protein, String fats, String carbs, String date) {
        this.protein = protein;
        this.fats = fats;
        this.carbs = carbs;
        this.date = date;
    }
}

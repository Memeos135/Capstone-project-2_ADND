package com.example.macros;

public class ProfileRecordSetter {
    private String protein;
    private String fat;
    private String carbs;
    private String date;
    private String note;

    public ProfileRecordSetter() {
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ProfileRecordSetter(String protein, String fat, String carbs, String date, String note) {
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.date = date;
        this.note = note;
    }
}

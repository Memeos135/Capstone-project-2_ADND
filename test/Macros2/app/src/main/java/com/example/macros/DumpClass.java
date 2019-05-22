package com.example.macros;

public class DumpClass {
    private String protein;
    private String carbs;
    private String fat;
    private String notes;

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getCarbs() {
        return carbs;
    }

    public void setCarbs(String carbs) {
        this.carbs = carbs;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public DumpClass(String protein) {
        this.protein = protein;
    }

    public DumpClass(String protein, String carbs, String fat, String notes) {
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.notes = notes;
    }
}

package com.example.macros;

public class UserGoalMacros {
    private String protein;
    private String carbs;
    private String fats;

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

    public String getFats() {
        return fats;
    }

    public void setFats(String fats) {
        this.fats = fats;
    }

    public UserGoalMacros() {
    }

    public UserGoalMacros(String protein, String carbs, String fats) {
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }
}

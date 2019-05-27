package com.example.macros;

import android.os.Parcel;
import android.os.Parcelable;

public class ProfileRecordSetter implements Parcelable {
    private String protein;
    private String fat;
    private String carbs;
    private String date;
    private String note;

    public ProfileRecordSetter() {
    }

    protected ProfileRecordSetter(Parcel in) {
        protein = in.readString();
        fat = in.readString();
        carbs = in.readString();
        date = in.readString();
        note = in.readString();
    }

    public static final Creator<ProfileRecordSetter> CREATOR = new Creator<ProfileRecordSetter>() {
        @Override
        public ProfileRecordSetter createFromParcel(Parcel in) {
            return new ProfileRecordSetter(in);
        }

        @Override
        public ProfileRecordSetter[] newArray(int size) {
            return new ProfileRecordSetter[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(protein);
        parcel.writeString(fat);
        parcel.writeString(carbs);
        parcel.writeString(date);
        parcel.writeString(note);
    }
}

package com.example.macros;

import android.os.Parcel;
import android.os.Parcelable;

public class NotesSetter implements Parcelable {

    private String month;
    private String day;
    private String note;
    private String year;

    protected NotesSetter(Parcel in) {
        month = in.readString();
        day = in.readString();
        note = in.readString();
        year = in.readString();
    }

    public static final Creator<NotesSetter> CREATOR = new Creator<NotesSetter>() {
        @Override
        public NotesSetter createFromParcel(Parcel in) {
            return new NotesSetter(in);
        }

        @Override
        public NotesSetter[] newArray(int size) {
            return new NotesSetter[size];
        }
    };

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public NotesSetter() {
    }

    public NotesSetter(String month, String day, String note, String year) {
        this.month = month;
        this.day = day;
        this.note = note;
        this.year = year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(month);
        parcel.writeString(day);
        parcel.writeString(note);
        parcel.writeString(year);
    }
}

package com.example.macros;

public class NotesSetter {

    private String month;
    private String day;
    private String note;
    private String year;

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
}

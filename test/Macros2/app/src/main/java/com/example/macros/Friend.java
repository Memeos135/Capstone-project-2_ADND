package com.example.macros;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend implements Parcelable {
    private String name;
    private String last_seen;
    private String photo;
    private String last_message;

    protected Friend(Parcel in) {
        name = in.readString();
        last_seen = in.readString();
        photo = in.readString();
        last_message = in.readString();
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(String last_seen) {
        this.last_seen = last_seen;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public Friend() {
    }

    public Friend(String name, String last_seen, String photo, String last_message) {
        this.name = name;
        this.last_seen = last_seen;
        this.photo = photo;
        this.last_message = last_message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(last_seen);
        parcel.writeString(photo);
        parcel.writeString(last_message);
    }
}

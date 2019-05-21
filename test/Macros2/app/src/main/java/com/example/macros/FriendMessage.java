package com.example.macros;

import android.os.Parcel;
import android.os.Parcelable;

public class FriendMessage implements Parcelable {
    private String msg;
    private String time;
    private String source;

    protected FriendMessage(Parcel in) {
        msg = in.readString();
        time = in.readString();
        source = in.readString();
    }

    public static final Creator<FriendMessage> CREATOR = new Creator<FriendMessage>() {
        @Override
        public FriendMessage createFromParcel(Parcel in) {
            return new FriendMessage(in);
        }

        @Override
        public FriendMessage[] newArray(int size) {
            return new FriendMessage[size];
        }
    };

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public FriendMessage() {
    }

    public FriendMessage(String msg, String time, String source) {
        this.msg = msg;
        this.time = time;
        this.source = source;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(msg);
        parcel.writeString(time);
        parcel.writeString(source);
    }
}

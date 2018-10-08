package com.chatapp.threadripper.authenticated.models;

public class MessagesChat {

    private String mName;
    private String mLastChat;
    private String mTime;
    private String mImage;
    private boolean online;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLastChat() {
        return mLastChat;
    }

    public void setLastChat(String lastChat) {
        mLastChat = lastChat;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public boolean getOnline() {
        return online;
    }

    public void setOnline(boolean on) {
        online = on;
    }
}
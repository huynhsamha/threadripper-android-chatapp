package com.chatapp.threadripper.authenticated.models;


public class Message {
    String type, text, time, imgUrl;
    String avatarUser;

    /**
     * type: "0", "1", "2"
     * "0": Date
     * "1": You
     * "2": Me
     *
     * text == null || imgUrl == null
     */

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAvatarUser() {
        return avatarUser;
    }

    public void setAvatarUser(String avatarUser) {
        this.avatarUser = avatarUser;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}

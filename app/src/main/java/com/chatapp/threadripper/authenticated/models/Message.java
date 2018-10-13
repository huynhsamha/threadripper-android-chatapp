package com.chatapp.threadripper.authenticated.models;


import android.graphics.Bitmap;

public class Message {
    String type, text, time, imgUrl;
    String avatarUser;
    String contentType;
    Bitmap bitmap;

    /**
     *
     * contentType: // reference in Constants.java
     * "text": for text
     * "uri": for imgUrl
     * "bitmap" for bitmap
     *
     *
     * type: "0", "1", "2"
     * "0": Date
     * "1": You
     * "2": Me
     *
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

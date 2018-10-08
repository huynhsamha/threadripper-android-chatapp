package com.chatapp.threadripper.authenticated.models;

import android.support.annotation.DrawableRes;


public class Contact {
    String name;
    String image;

    public String getImage() {
        return image;
    }

    public void setImage(String img) {
        image = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

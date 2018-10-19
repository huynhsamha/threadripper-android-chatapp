package com.chatapp.threadripper.models;

import com.chatapp.threadripper.utils.Constants;
import com.google.gson.annotations.SerializedName;

public class User {

    String username;
    String email;
    String password;
    String displayName;

    @SerializedName("avatarUrl") // server use avatarUrl
    String photoUrl;

    Boolean online;

    String relationship = Constants.RELATIONSHIP_NONE;
    // relationship with current user
    // 3 type: friend | sent request | none
    // default is none


    /**
     * Constructors
     */

    public User(String username, String email, String password, String displayName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }

    public User() {

    }

    public User(String username, String email, String password, String displayName, String photoUrl) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }


    /**
     * Getters and Setters
     */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}

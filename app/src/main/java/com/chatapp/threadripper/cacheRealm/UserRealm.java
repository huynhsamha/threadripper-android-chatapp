package com.chatapp.threadripper.cacheRealm;

import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserRealm extends RealmObject {

    @PrimaryKey
    String username;
    String email;
    String password;
    String displayName;
    String photoUrl;
    Boolean online;
    String relationship = Constants.RELATIONSHIP_NONE;

    public UserRealm() {

    }

    public UserRealm(User user) {
        username = user.getUsername();
        email = user.getEmail();
        password = user.getPassword();
        displayName = user.getDisplayName();
        photoUrl = user.getPhotoUrl();
        online = user.getOnline();
        relationship = user.getRelationship();
    }


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

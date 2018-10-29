package com.chatapp.threadripper.models;

import com.chatapp.threadripper.utils.Constants;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject implements Serializable {

    @PrimaryKey
    private String username;
    private String email;
    private String password;
    private String displayName;

    // server use avatarUrl
    @SerializedName("avatarUrl")
    private String photoUrl;

    private boolean online;

    private String relationship = Constants.RELATIONSHIP_NONE;
    // relationship with current user
    // 3 type: friend | sent request | none
    // default is none

    public boolean isFriend() {
        return relationship.equals(Constants.RELATIONSHIP_FRIEND);
    }

    private String privateConversationId; // store conversation ID of 2 people

    private boolean isSelectedMember = false;
    private boolean isMatched = false; // default everyone is matched
    // used for selected member for creating conversation


    /**
     * Keep fields server no use before update it to cache
     * @param user: from server response
     */
    public void safetyUserBeforeToCache(User user) {
        if (!username.equals(user.getUsername())) return;

        user.setRelationship(relationship);
        user.setPrivateConversationId(privateConversationId);
        user.setSelectedMember(isSelectedMember);
    }

    /**
     * Constructors
     */

    public User() {

    }

    public User(String username, String email, String password, String displayName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public boolean isSelectedMember() {
        return isSelectedMember;
    }

    public void setSelectedMember(boolean selectedMember) {
        isSelectedMember = selectedMember;
    }

    public String getPrivateConversationId() {
        return privateConversationId;
    }

    public void setPrivateConversationId(String privateConversationId) {
        this.privateConversationId = privateConversationId;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }
}

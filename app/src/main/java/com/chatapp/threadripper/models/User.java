package com.chatapp.threadripper.models;

import com.chatapp.threadripper.api.CacheService;
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

    /**
     * Relationship with current user
     * friend | sent request | none
     */
    private String relationship = Constants.RELATIONSHIP_NONE;

    // store conversation ID of 2 people
    private String privateConversationId;

    // used for selected member for creating conversation
    private boolean isSelectedMember = false;
    private boolean isMatched = false;

    /**
     * Keep fields server no use before updateFromServer it to cache
     */
    public void safetyUserBeforeToCache() {
        User cache = CacheService.getInstance().retrieveCacheUser(username);

        if (cache != null) {
            setPrivateConversationId(cache.getPrivateConversationId());
            setRelationship(cache.getRelationship());
            setSelectedMember(cache.isSelectedMember());
            setMatched(cache.isMatched());
        }
    }

    public boolean isFriend() {
        return relationship.equals(Constants.RELATIONSHIP_FRIEND);
    }


    /**
     * Constructors
     */

    public User() {

    }

    /**
     * Clone the user from cache to new User
     * This is clone a new user which is not a proxy of Realm
     *
     * @param cache: User from cache
     */
    public User(User cache) {
        username = cache.getUsername();
        email = cache.getEmail();
        password = cache.getPassword();
        displayName = cache.getDisplayName();
        photoUrl = cache.getPhotoUrl();
        online = cache.isOnline();
        relationship = cache.getRelationship();
        privateConversationId = cache.getPrivateConversationId();
        isSelectedMember = cache.isSelectedMember();
        isMatched = cache.isMatched();
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

package com.chatapp.threadripper.cacheRealm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PreferencesRealm extends RealmObject {

    @PrimaryKey
    private String id = "ID_ONLY_ONCE_OBJECT";

    private UserRealm currentUser = new UserRealm();
    private String chatAuthToken = "";


    public UserRealm getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserRealm currentUser) {
        this.currentUser = currentUser;
    }

    public String getChatAuthToken() {
        return chatAuthToken;
    }

    public void setChatAuthToken(String chatAuthToken) {
        this.chatAuthToken = chatAuthToken;
    }
}

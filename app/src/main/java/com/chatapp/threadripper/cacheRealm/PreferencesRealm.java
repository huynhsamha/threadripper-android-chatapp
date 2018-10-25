package com.chatapp.threadripper.cacheRealm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PreferencesRealm extends RealmObject {

    /**
     * Preferences that is the state of the user
     * It is sync with Preferences running on RAM
     */

    @PrimaryKey
    private String id = "ID_ONLY_ONCE_OBJECT";

    private UserRealm currentUser = new UserRealm();
    private String chatAuthToken = "";

    private boolean firstUseApp = true;
    private boolean firstUseProfileSettings = true;
    private boolean firstUseChatting = true;
    private boolean firstUseVideoCall = true;


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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFirstUseApp() {
        return firstUseApp;
    }

    public void setFirstUseApp(boolean firstUseApp) {
        this.firstUseApp = firstUseApp;
    }

    public boolean isFirstUseProfileSettings() {
        return firstUseProfileSettings;
    }

    public void setFirstUseProfileSettings(boolean firstUseProfileSettings) {
        this.firstUseProfileSettings = firstUseProfileSettings;
    }

    public boolean isFirstUseChatting() {
        return firstUseChatting;
    }

    public void setFirstUseChatting(boolean firstUseChatting) {
        this.firstUseChatting = firstUseChatting;
    }

    public boolean isFirstUseVideoCall() {
        return firstUseVideoCall;
    }

    public void setFirstUseVideoCall(boolean firstUseVideoCall) {
        this.firstUseVideoCall = firstUseVideoCall;
    }
}

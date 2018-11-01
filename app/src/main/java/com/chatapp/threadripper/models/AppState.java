package com.chatapp.threadripper.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AppState extends RealmObject {

    /**
     * AppState that is the state of the user
     * It is sync with AppState running on RAM
     */

    @PrimaryKey
    private String id = "ID_ONLY_ONCE_OBJECT";

    // private User currentUser = new User(); // don't store as Realm Object
    private String chatAuthToken = "";

    private boolean firstUseApp = true;
    private boolean firstUseProfileSettings = true;
    private boolean firstUseChatting = true;
    private boolean firstUseVideoCall = true;

    /**
     * Current User
     * Store as Strings, not use Realm Object
     */
    private String username;
    private String email;
    private String password;
    private String displayName;
    private String photoUrl;


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
}

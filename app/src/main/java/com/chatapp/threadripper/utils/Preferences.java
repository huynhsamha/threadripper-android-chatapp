package com.chatapp.threadripper.utils;


import com.chatapp.threadripper.models.User;

public class Preferences {

    /**
     * AppState that is the state of the user
     * This is used on runtime, that is on RAM
     * It is sync with Cache via PreferencesRealm
     */

    private static User currentUser = new User(); // this user is not managed by Realm Cache
    private static String chatAuthToken = "";

    private static boolean firstUseApp = true;
    private static boolean firstUseProfileSettings = true;
    private static boolean firstUseChatting = true;
    private static boolean firstUseVideoCall = true;

    public static void resetAll() {
        currentUser = new User();
        chatAuthToken = "";
        firstUseApp = true;
        firstUseProfileSettings = true;
        firstUseChatting = true;
        firstUseVideoCall = true;
    }

    public static String getChatAuthToken() {
        return chatAuthToken;
    }

    public static void setChatAuthToken(String chatAuthToken) {
        Preferences.chatAuthToken = chatAuthToken;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        if (user != null) Preferences.currentUser = user;
    }

    public static boolean isFirstUseApp() {
        return firstUseApp;
    }

    public static void setFirstUseApp(boolean firstUseApp) {
        Preferences.firstUseApp = firstUseApp;
    }

    public static boolean isFirstUseProfileSettings() {
        return firstUseProfileSettings;
    }

    public static void setFirstUseProfileSettings(boolean firstUseProfileSettings) {
        Preferences.firstUseProfileSettings = firstUseProfileSettings;
    }

    public static boolean isFirstUseChatting() {
        return firstUseChatting;
    }

    public static void setFirstUseChatting(boolean firstUseChatting) {
        Preferences.firstUseChatting = firstUseChatting;
    }

    public static boolean isFirstUseVideoCall() {
        return firstUseVideoCall;
    }

    public static void setFirstUseVideoCall(boolean firstUseVideoCall) {
        Preferences.firstUseVideoCall = firstUseVideoCall;
    }
}

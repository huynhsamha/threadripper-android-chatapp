package com.chatapp.threadripper.utils;


import com.chatapp.threadripper.models.User;

public class Preferences {

    private static User currentUser = new User();
    private static String chatAuthToken = "";

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

}

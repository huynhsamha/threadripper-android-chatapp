package com.chatapp.threadripper.utils;


public class Preferences {
    private static String email = "default-email@gmail.com";
    private static String username = "Default Username";
    private static String displayName = "Default Name";
    private static String userAvatar = "http://abc.com/xyz.jpg";
    private static boolean isConnected = false;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        if (username == null || username.length() == 0) return;
        Preferences.username = username;
    }

    public static String getUserAvatar() {
        return userAvatar;
    }

    public static void setUserAvatar(String userAvatar) {
        if (userAvatar == null || userAvatar.length() == 0) return;
        Preferences.userAvatar = userAvatar;
    }

    public static boolean isIsConnected() {
        return isConnected;
    }

    public static void setIsConnected(boolean isConnected) {
        Preferences.isConnected = isConnected;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Preferences.email = email;
    }

    public static String getDisplayName() {
        return displayName;
    }

    public static void setDisplayName(String displayName) {
        Preferences.displayName = displayName;
    }

}

package com.chatapp.threadripper.utils;

public class Preferences {
    private static String username;
    private static String userAvatar;
    private static boolean isConnected;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Preferences.username = username;
    }

    public static String getUserAvatar() {
        return userAvatar;
    }

    public static void setUserAvatar(String userAvatar) {
        Preferences.userAvatar = userAvatar;
    }

    public static boolean isIsConnected() {
        return isConnected;
    }

    public static void setIsConnected(boolean isConnected) {
        Preferences.isConnected = isConnected;
    }
}

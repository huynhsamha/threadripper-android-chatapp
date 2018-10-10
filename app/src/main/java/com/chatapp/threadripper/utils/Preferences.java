package com.chatapp.threadripper.utils;

public class Preferences {
    private static String username = "Default Username";
    private static String userAvatar = "http://nguoi-noi-tieng.com/photo/tieu-su-dien-vien-xa-thi-man-6850.jpg";
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
}

package com.chatapp.threadripper.utils;


import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.User;

import java.util.List;

public class Preferences {

    private static User currentUser = new User();
    private static String chatAuthToken = "";
    private static List<User> users, friends;
    private static List<Conversation> conversations;


    public static List<User> getUsers() {
        return users;
    }

    public static void setUsers(List<User> users) {
        Preferences.users = users;
    }

    public static List<User> getFriends() {
        return friends;
    }

    public static void setFriends(List<User> friends) {
        Preferences.friends = friends;
    }

    public static List<Conversation> getConversations() {
        return conversations;
    }

    public static void setConversations(List<Conversation> conversations) {
        Preferences.conversations = conversations;
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

}

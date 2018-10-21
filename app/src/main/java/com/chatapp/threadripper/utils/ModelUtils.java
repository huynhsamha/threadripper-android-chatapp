package com.chatapp.threadripper.utils;

import android.text.TextUtils;

import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.User;

import java.util.ArrayList;
import java.util.List;

public class ModelUtils {

    public static String getConversationName(Conversation conversation) {
        if (conversation.getConversationName() != null) {
            return conversation.getConversationName();
        }

        List<String> users = new ArrayList<>();
        for (User user : conversation.getListUser()) {
            if (user == null) continue;
            if (user.getUsername().equals(Preferences.getCurrentUser().getUsername())) continue;
            users.add(user.getDisplayName());
        }

        return TextUtils.join(", ", users);
    }

    public static String getConversationAvatar(Conversation conversation) {
        return "";
    }

    public static boolean isOnlineGroup(Conversation conversation) {
        for (User user : conversation.getListUser()) {
            if (user == null) continue;
            if (user.isOnline()) return true;
        }
        return false;
    }

}

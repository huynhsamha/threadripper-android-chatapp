package com.chatapp.threadripper.utils;

import android.text.TextUtils;

import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ModelUtils {

    public static String getConversationName(Conversation conversation) {
        if (conversation.getConversationName() != null
                && !conversation.getConversationName().isEmpty()) {
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
        if (conversation.getPhotoUrl() != null
                && !conversation.getPhotoUrl().isEmpty()) {
            return conversation.getPhotoUrl();
        }

        if (conversation.getListUser().size() < 2) {
            // fucking conversation !!!
            if (conversation.getListUser().size() == 1) {
                return conversation.getListUser().get(0).getPhotoUrl();
            }
            return "";
        }

        if (conversation.getListUser().size() == 2) {
            User user = conversation.getListUser().get(0);
            if (user.getUsername().equals(Preferences.getCurrentUser().getUsername())) {
                // this is me, so return the other
                return conversation.getListUser().get(1).getPhotoUrl();
            }
            return user.getPhotoUrl();
        }


        // TODO: handle for group with multiple users
        return Constants.PLACEHOLDER_GROUP_AVATAR;
    }

    public static boolean isOnlineGroup(Conversation conversation) {
        for (User user : conversation.getListUser()) {
            if (user == null) continue;
            if (user.getUsername().equals(Preferences.getCurrentUser().getUsername())) continue;
            if (user.isOnline()) return true;
        }
        return false;
    }

}

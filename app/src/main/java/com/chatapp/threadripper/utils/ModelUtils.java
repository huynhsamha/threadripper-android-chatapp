package com.chatapp.threadripper.utils;

import android.text.TextUtils;

import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.User;

import java.util.ArrayList;
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
            if (Preferences.getCurrentUser().getUsername().equals(user.getUsername())) continue;
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
                if (conversation.getListUser().get(0) != null) {
                    return conversation.getListUser().get(0).getPhotoUrl();
                }
            }
            return "";
        }

        if (conversation.getListUser().size() == 2) {
            User user = conversation.getListUser().get(0);
            if (Preferences.getCurrentUser().getUsername().equals(user != null ? user.getUsername() : null)) {
                // this is me, so return the other
                if (conversation.getListUser().get(1) != null) {
                    return conversation.getListUser().get(1).getPhotoUrl();
                }
            }
            return user != null ? user.getPhotoUrl() : null;
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

    public static void parseConversationToFriend(Conversation conversation) {
        if (conversation == null) return;
        if (conversation.getListUser() == null) return;
        if (conversation.getListUser().size() != 2) return;
        User user = null;
        if (conversation.getListUser().get(0) != null) {
            if (Preferences.getCurrentUser().getUsername().equals(conversation.getListUser().get(0) != null ? conversation.getListUser().get(0).getUsername() : null)) {
                user = conversation.getListUser().get(1);
            } else {
                user = conversation.getListUser().get(0);
            }
        }

        if (user != null) {
            user.setRelationship(Constants.RELATIONSHIP_FRIEND);
            user.setPrivateConversationId(conversation.getConversationId());
        }
        CacheService.getInstance().addOrUpdateCacheUser(user);
    }

}

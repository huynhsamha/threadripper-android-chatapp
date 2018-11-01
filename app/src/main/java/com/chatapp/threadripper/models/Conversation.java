package com.chatapp.threadripper.models;

import com.chatapp.threadripper.utils.ModelUtils;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Conversation extends RealmObject {

    @PrimaryKey
    private String conversationId;
    private String conversationName;
    private Message lastMessage;
    private String photoUrl;
    private RealmList<User> listUser = new RealmList<>();
    private int notiCount;

    public void updateFromServer() {
        setConversationName(ModelUtils.getConversationName(this));
        setPhotoUrl(ModelUtils.getConversationAvatar(this));
        if (getLastMessage() != null) {
            getLastMessage().updateDateTime();
        }

        for (User user : listUser) {
            // user is not proxy user realm
            user.safetyUserBeforeToCache();
        }
    }

    public void increaseNotificationCount() {
        this.notiCount++;
    }

    /**
     * Getters and Setters
     */

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public RealmList<User> getListUser() {
        return listUser;
    }

    public void setListUser(RealmList<User> listUser) {
        this.listUser = listUser;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getNotiCount() {
        return notiCount;
    }

    public void setNotiCount(int notiCount) {
        this.notiCount = notiCount;
    }
}

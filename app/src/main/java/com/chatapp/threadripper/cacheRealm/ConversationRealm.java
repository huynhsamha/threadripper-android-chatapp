package com.chatapp.threadripper.cacheRealm;

import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.User;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ConversationRealm extends RealmObject {

    @PrimaryKey
    String conversationId;
    String conversationName;
    MessageRealm lastMessage;
    RealmList<UserRealm> listUser = new RealmList<>();

    public ConversationRealm() {

    }

    public ConversationRealm(Conversation o) {
        conversationId = o.getConversationId();
        conversationName = o.getConversationName();
        if (o.getLastMessage() != null)
            lastMessage = new MessageRealm(o.getLastMessage());
        for (User u : o.getListUser()) {
            if (u != null)
                listUser.add(new UserRealm(u));
        }
    }


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

    public MessageRealm getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageRealm lastMessage) {
        this.lastMessage = lastMessage;
    }

    public RealmList<UserRealm> getListUser() {
        return listUser;
    }

    public void setListUser(RealmList<UserRealm> listUser) {
        this.listUser = listUser;
    }
}

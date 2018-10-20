package com.chatapp.threadripper.models;

import com.chatapp.threadripper.cacheRealm.ConversationRealm;
import com.chatapp.threadripper.cacheRealm.MessageRealm;
import com.chatapp.threadripper.cacheRealm.UserRealm;

import java.util.ArrayList;
import java.util.List;

public class Conversation {

    String conversationId;
    String conversationName;
    Message lastMessage;
    List<User> listUser = new ArrayList<>();

    public Conversation() {

    }

    public Conversation(ConversationRealm o) {
        conversationId = o.getConversationId();
        conversationName = o.getConversationName();
        if (o.getLastMessage() != null)
            lastMessage = new Message(o.getLastMessage());
        for (UserRealm u : o.getListUser()) {
            if (u != null)
                listUser.add(new User(u));
        }
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

    public List<User> getListUser() {
        return listUser;
    }

    public void setListUser(List<User> listUser) {
        this.listUser = listUser;
    }
}

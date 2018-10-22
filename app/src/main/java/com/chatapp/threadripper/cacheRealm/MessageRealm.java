package com.chatapp.threadripper.cacheRealm;

import com.chatapp.threadripper.models.Message;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MessageRealm extends RealmObject {

    @PrimaryKey
    String messageId;
    String type;
    String content;
    Date datetime;
    String conversationId;
    String username;
    boolean read;


    public MessageRealm() {

    }

    public MessageRealm(Message o) {
        messageId = o.getMessageId();
        type = o.getType();
        content = o.getContent();
        datetime = o.getDateTime();
        conversationId = o.getConversationId();
        username = o.getUsername();
        read = o.isRead();
    }


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}

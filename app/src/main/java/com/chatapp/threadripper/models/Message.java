package com.chatapp.threadripper.models;

import android.graphics.Bitmap;

import com.chatapp.threadripper.cacheRealm.MessageRealm;
import com.chatapp.threadripper.utils.DateTimeUtils;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    String messageId;
    String type;
    String content;
    Date dateTime;
    String conversationId;
    String username;
    String token;
    boolean read;

    String datetime; // server send string format, parse to "dateTime" (Date())

    // Used for render image
    // bitmap for camera capture | url for server | uri for image in device
    Bitmap bitmap;
    boolean isBitmap = false;

    // Used for render View
    boolean isYou = false;  // for you or me message
    boolean isDate = false; // for a Date inline
    String conversationAvatar; // Used for render Avatar message


    public static class MessageType {
        public static final String JOIN = "JOIN";
        public static final String LEAVE = "LEAVE";
        public static final String TEXT = "TEXT";
        public static final String IMAGE = "IMAGE";
        public static final String FILE = "FILE";
        public static final String CALL = "CALL";
        public static final String READ = "READ";
        public static final String TYPING = "TYPING";
    }

    public void updateDateTime() {
        // server send date in format string (store in strDateTime)
        // convert it to Date() which is dateTime field
        if (datetime != null) {
            String format = "yyyy-MM-dd HH:mm:ss";
            dateTime = DateTimeUtils.parseDateTime(format, datetime);
        }
    }

    public Message(MessageRealm o) {
        messageId = o.getMessageId();
        type = o.getType();
        content = o.getContent();
        dateTime = o.getDatetime();
        conversationId = o.getConversationId();
        username = o.getUsername();
        read = o.isRead();
    }

    public Message() {

    }


    /**
     * Getters and Setters
     */


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isBitmap() {
        return isBitmap;
    }

    public void setBitmap(boolean bitmap) {
        isBitmap = bitmap;
    }

    public boolean isYou() {
        return isYou;
    }

    public void setYou(boolean you) {
        isYou = you;
    }

    public boolean isDate() {
        return isDate;
    }

    public void setDate(boolean date) {
        isDate = date;
    }

    public String getConversationAvatar() {
        return conversationAvatar;
    }

    public void setConversationAvatar(String conversationAvatar) {
        this.conversationAvatar = conversationAvatar;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}

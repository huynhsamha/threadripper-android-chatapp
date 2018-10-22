package com.chatapp.threadripper.models;


import com.chatapp.threadripper.utils.DateTimeUtils;

import java.util.Date;

public class MessageResponse {

    public String messageId;
    public String type;
    public String content;
    public String datetime;
    public String conversationId;
    public String username;
    public boolean read;

    public Message toMessage() {
        Message message = new Message();
        message.setMessageId(messageId);
        message.setType(type);
        message.setContent(content);
        message.setConversationId(conversationId);
        message.setUsername(username);
        message.setRead(read);
        if (datetime != null) {
            String format = "yyyy-MM-dd HH:mm:ss";
            message.setDatetime(DateTimeUtils.parseDateTime(format, datetime));
        }
        return message;
    }
}

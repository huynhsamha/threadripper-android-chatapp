package com.chatapp.threadripper.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.services.SocketService;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.Preferences;

public class SocketManager {

    /**
     * Socket Manager for Application, used for all activities
     */

    ServiceConnection mSocketServiceConnection;
    SocketService mSocketService;
    boolean mBound = false; // is bound service


    private static SocketManager instance;

    public static SocketManager getInstance() {
        if (instance == null) instance = new SocketManager();
        return instance;
    }

    public SocketManager() {

        mSocketServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                SocketService.SocketBinder binder = (SocketService.SocketBinder) iBinder;
                mSocketService = binder.getService(); // instance of service
                mBound = true; // mark currently is bound

                // start connecting to socket
                mSocketService.connectSocket();

                join();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mSocketService.disconnectSocket();

                mBound = false; // mark currently is unbound
                mSocketService = null; // delete instance
            }
        };

    }

    public void connectSocketService(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        context.bindService(intent, mSocketServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void disconnectSocketService(Context context) {
        if (mSocketServiceConnection != null) {
            context.unbindService(mSocketServiceConnection);
        }
    }

    public boolean isConnected() {
        return mBound && mSocketService != null;
    }

    private boolean pushMessage(Message message) {
        // Add token to message for authentication
        message.setToken(Preferences.getChatAuthToken());

        if (isConnected()) {
            mSocketService.sendMessage(message);
            return true;
        }

        return false; // cannot send to socket
    }

    public boolean join() { // join to socket
        Message message = new Message();
        message.setType(Message.MessageType.JOIN);

        return pushMessage(message);
    }

    public boolean leave() { // leave the socket
        Message message = new Message();
        message.setType(Message.MessageType.LEAVE);

        return pushMessage(message);
    }

    public boolean readMessage(String conversationId, String lastMessageId) {
        Message message = new Message();
        message.setType(Message.MessageType.READ);
        message.setConversationId(conversationId);
        message.setContent(lastMessageId);

        return pushMessage(message);
    }

    public boolean isTyping(String conversationId, boolean typing) {
        Message message = new Message();
        message.setType(Message.MessageType.TYPING);
        message.setConversationId(conversationId);
        message.setContent(typing ? "true" : "false");

        return pushMessage(message);
    }

    public boolean sendText(String conversationId, String content) {
        Message message = new Message();
        message.setType(Message.MessageType.TEXT);
        message.setConversationId(conversationId);
        message.setContent(content);

        return pushMessage(message);
    }

    public boolean sendImage(String conversationId, String url) {
        Message message = new Message();
        message.setType(Message.MessageType.IMAGE);
        message.setConversationId(conversationId);
        message.setContent(url);

        return pushMessage(message);
    }

    public boolean sendReadMessages(String conversationId, long messageId) {
        Message message = new Message();
        message.setType(Message.MessageType.READ);
        message.setConversationId(conversationId);
        message.setContent(String.valueOf(messageId));

        return pushMessage(message);
    }

    public boolean sendCalling(User targetUser, String typeCalling, String channelId) {
        // targetUser is partner in the calling
        Message message = new Message();
        message.setType(Message.MessageType.CALL);
        message.setConversationId(targetUser.getPrivateConversationId());
        message.setContent(typeCalling);
        message.setUsername(channelId);

        return pushMessage(message);
    }
}

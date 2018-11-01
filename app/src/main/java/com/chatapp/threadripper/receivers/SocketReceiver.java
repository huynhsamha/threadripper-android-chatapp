package com.chatapp.threadripper.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.authenticated.fragments.FragmentMessagesChat;
import com.chatapp.threadripper.authenticated.fragments.FragmentVideoCallList;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;

public class SocketReceiver extends BroadcastReceiver {

    OnCallbackListener listener;

    public interface OnCallbackListener {

        void onNewMessage(Message message);

        void onTyping(String conversationId, String username, boolean typing);

        void onRead(String conversationId, String username);

        void onCall(User targetUser, String typeCalling, String channelId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case Constants.ACTION_STRING_RECEIVER_NEW_MESSAGE:
                handleNewMessage(intent);
                break;
            case Constants.ACTION_STRING_RECEIVER_JOIN:
                handleJoin(intent);
                break;
            case Constants.ACTION_STRING_RECEIVER_LEAVE:
                handleLeave(intent);
                break;
            case Constants.ACTION_STRING_RECEIVER_TYPING:
                handleTyping(intent);
                break;
            case Constants.ACTION_STRING_RECEIVER_READ:
                // handleRead(intent);
            case Constants.ACTION_STRING_RECEIVER_CALL:
                handleCall(intent);
                break;
            default:
                break;
        }

    }

    public void setListener(Context context) {
        this.listener = (OnCallbackListener) context;
    }

    public void setListener(FragmentMessagesChat fragment) {
        this.listener = fragment;
    }

    public void setListener(FragmentVideoCallList fragment) {
        this.listener = fragment;
    }

    void handleNewMessage(Intent intent) {
        Message message = (Message) intent.getSerializableExtra(Constants.MESSAGE_CHAT);
        if (listener != null) {
            listener.onNewMessage(message);
        }
    }

    void handleJoin(Intent intent) {
        String username = intent.getStringExtra(Constants.USER_USERNAME);
        CacheService.getInstance().setUserOnlineOrOffline(username, true);
    }

    void handleLeave(Intent intent) {
        String username = intent.getStringExtra(Constants.USER_USERNAME);
        CacheService.getInstance().setUserOnlineOrOffline(username, false);
    }

    void handleTyping(Intent intent) {
        String conversationId = intent.getStringExtra(Constants.CONVERSATION_ID);
        String username = intent.getStringExtra(Constants.USER_USERNAME);
        boolean typing = intent.getBooleanExtra(Constants.CHAT_IS_TYPING_BOOLEAN, false);
        if (listener != null) {
            listener.onTyping(conversationId, username, typing);
        }
    }

    void handleRead(Intent intent) {
        String conversationId = intent.getStringExtra(Constants.CONVERSATION_ID);
        String username = intent.getStringExtra(Constants.USER_USERNAME);
        if (listener != null) {
            listener.onRead(conversationId, username);
        }
    }

    void handleCall(Intent intent) {
        String username = intent.getStringExtra(Constants.USER_USERNAME);
        String typeCalling = intent.getStringExtra(Constants.TYPE_CALLING);
        String channelId = intent.getStringExtra(Constants.EXTRA_VIDEO_CHANNEL_TOKEN);
        User targetUser = CacheService.getInstance().retrieveCacheUser(username);
        if (listener != null) {
            listener.onCall(targetUser, typeCalling, channelId);
        }

        // ApiService.getInstance().getUser(username).enqueue(new Callback<User>() {
        //     @Override
        //     public void onResponse(Call<User> call, Response<User> response) {
        //         if (listener != null) {
        //             if (response.isSuccessful()) {
        //                 User targetUser = response.body();
        //             }
        //         }
        //     }
        //
        //     @Override
        //     public void onFailure(Call<User> call, Throwable t) {
        //         t.printStackTrace();
        //     }
        // });
    }
}

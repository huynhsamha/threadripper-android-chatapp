package com.chatapp.threadripper.receivers;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chatapp.threadripper.BaseActivity;
import com.chatapp.threadripper.authenticated.BaseMainActivity;
import com.chatapp.threadripper.authenticated.fragments.FragmentMessagesChat;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.utils.Constants;

public class SocketReceiver extends BroadcastReceiver {

    OnCallbackListener listener;

    public interface OnCallbackListener {
        void onNewMessage(Message message);
        void onJoin(String username);
        void onLeave(String username);
        void onTyping(String conversationId, String username, boolean typing);
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

    void handleNewMessage(Intent intent) {
        Message message = (Message) intent.getSerializableExtra(Constants.MESSAGE_CHAT);
        if (listener != null) {
            listener.onNewMessage(message);
        }
    }

    void handleJoin(Intent intent) {
        String username = intent.getStringExtra(Constants.USER_USERNAME);
        if (listener != null) {
            listener.onJoin(username);
        }
    }

    void handleLeave(Intent intent) {
        String username = intent.getStringExtra(Constants.USER_USERNAME);
        if (listener != null) {
            listener.onLeave(username);
        }
    }

    void handleTyping(Intent intent) {
        String conversationId = intent.getStringExtra(Constants.CONVERSATION_ID);
        String username = intent.getStringExtra(Constants.USER_USERNAME);
        boolean typing = intent.getBooleanExtra(Constants.CHAT_IS_TYPING_BOOLEAN, false);
        if (listener != null) {
            listener.onTyping(conversationId, username, typing);
        }
    }
}

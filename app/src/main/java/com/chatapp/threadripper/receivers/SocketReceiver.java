package com.chatapp.threadripper.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.utils.Constants;

public class SocketReceiver extends BroadcastReceiver {

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
            default:
                break;
        }
    }

    void handleNewMessage(Intent intent) {
        Message message = (Message) intent.getSerializableExtra(Constants.MESSAGE_CHAT);

    }

    void handleJoin(Intent intent) {
        String username = intent.getStringExtra(Constants.USER_USERNAME);
    }

    void handleLeave(Intent intent) {
        String username = intent.getStringExtra(Constants.USER_USERNAME);
    }
}

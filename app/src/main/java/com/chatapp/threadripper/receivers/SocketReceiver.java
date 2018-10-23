package com.chatapp.threadripper.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chatapp.threadripper.BaseActivity;
import com.chatapp.threadripper.authenticated.BaseMainActivity;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.utils.Constants;

public class SocketReceiver extends BroadcastReceiver {

    BaseMainActivity context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = (BaseMainActivity) context;

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
        this.context.handleNewMessage(message);
    }

    void handleJoin(Intent intent) {
        String username = intent.getStringExtra(Constants.USER_USERNAME);
    }

    void handleLeave(Intent intent) {
        String username = intent.getStringExtra(Constants.USER_USERNAME);
    }
}

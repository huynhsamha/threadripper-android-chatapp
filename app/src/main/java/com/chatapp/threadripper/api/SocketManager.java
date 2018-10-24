package com.chatapp.threadripper.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.services.SocketService;
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

    public boolean sendMessage(Message message) {
        if (isConnected()) {
            mSocketService.sendMessage(message);
            return true;
        }
        return false;
    }

    public boolean join() {
        Message message = new Message();
        message.setToken(Preferences.getChatAuthToken());
        message.setType(Message.MessageType.JOIN);

        return sendMessage(message);
    }

    public boolean leave() {
        Message message = new Message();
        message.setToken(Preferences.getChatAuthToken());
        message.setType(Message.MessageType.LEAVE);

        return sendMessage(message);
    }
}

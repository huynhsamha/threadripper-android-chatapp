package com.chatapp.threadripper.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.services.SocketService;

public class SocketManager {

    /**
     * Socket Manager for Application, used for all activities
     */

    ServiceConnection mSocketServiceConnection;
    SocketService mSocketService;
    boolean mBound = false;


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
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mBound = false; // mark currently is unbound
            }
        };

    }

    public void connectSocketService(Context context) {
        context.bindService(new Intent(context, SocketService.class), mSocketServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void disconnectSocketService(Context context) {
        context.unbindService(mSocketServiceConnection);
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
}

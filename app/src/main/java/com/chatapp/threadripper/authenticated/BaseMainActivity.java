package com.chatapp.threadripper.authenticated;


import android.util.Log;

import com.chatapp.threadripper.BaseActivity;
import com.chatapp.threadripper.api.SocketManager;
import com.chatapp.threadripper.models.Message;

public class BaseMainActivity extends BaseActivity  {

    // This is main activity wrapping all activities after authenticated.


    @Override
    protected void onResume() {
        super.onResume();

        try {
            // TODO: error without explanation

            // On Resume, try to bind socket service
            SocketManager.getInstance().connectSocketService(this);

            // On Resume, if socket service is running, join to socket
            SocketManager.getInstance().join();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {

        try {

            // On Destroy (closing app), leave socket => status online = false
            SocketManager.getInstance().leave();

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }
}

package com.chatapp.threadripper.authenticated;


import com.chatapp.threadripper.BaseActivity;
import com.chatapp.threadripper.api.SocketManager;

public class BaseMainActivity extends BaseActivity {

    // This is main activity wrapping all activities after authenticated.


    @Override
    protected void onResume() {
        super.onResume();

        try {
            // TODO: error without explanation

            // On Resume, try to bind socket service
            SocketManager.getInstance().connectSocketService(this);

            // On Resume, if socket service is running, join to socket
            SocketManager.getInstance().onlyConnectSocket();
            SocketManager.getInstance().join();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

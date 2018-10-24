package com.chatapp.threadripper.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.chatapp.threadripper.api.Config;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.Preferences;
import com.google.gson.Gson;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class SocketService extends Service {

    String TAG = "SOCKET_LOG";

    public class SocketBinder extends Binder {
        // return instance of service for client use public methods
        public SocketService getService() {
            return SocketService.this;
        }
    }

    private StompClient client;
    private SocketBinder binder = new SocketBinder();

    public SocketService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // run when service is created
        initSocket(); // init the StompClient socket for connective
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Don't use startService() -> don't use the onStartCommand()
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // use onBind() <- activity use bindService()
        // return interface for client use the public methods of the service
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    void sendBroadcastNewMessage(Message message) {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_STRING_RECEIVER_NEW_MESSAGE);
        intent.putExtra(Constants.MESSAGE_CHAT, message);
        sendBroadcast(intent);
    }

    void sendBroadcastJoin(String username) {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_STRING_RECEIVER_JOIN);
        intent.putExtra(Constants.USER_USERNAME, username);
        sendBroadcast(intent);
    }

    void sendBroadcastLeave(String username) {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_STRING_RECEIVER_LEAVE);
        intent.putExtra(Constants.USER_USERNAME, username);
        sendBroadcast(intent);
    }

    @SuppressLint("CheckResult")
    void initSocket() {
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Config.WEB_SOCKET_FULL_PATH);

        String username = Preferences.getCurrentUser().getUsername();
        String channel = "/topic/" + username;

        client.topic(channel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    String jsonString = response.getPayload();
                    Gson gson = new Gson();
                    Message message = gson.fromJson(jsonString, Message.class);

                    if (message.getType() == null) {
                        return; // fucking message !!!
                        // used for handle crash bug from internal libraries
                    }

                    message.updateDateTime();

                    switch (message.getType()) {
                        case Message.MessageType.TEXT:
                        case Message.MessageType.FILE:
                        case Message.MessageType.IMAGE:
                            sendBroadcastNewMessage(message);
                            break;

                        case Message.MessageType.JOIN:
                            sendBroadcastJoin(message.getUsername());
                            break;

                        case Message.MessageType.LEAVE:
                            sendBroadcastLeave(message.getUsername());
                            break;

                        default:
                            break;
                    }
                });
    }

    public void connectSocket() {
        try {
            // TODO: Error without explanation
            if (!client.isConnected()) {
                client.connect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectSocket() {
        try {
            client.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("CheckResult")
    public void sendMessage(Message message) {
        message.setToken(Preferences.getChatAuthToken());

        client.send("/queue/sendMessage", new Gson().toJson(message))
                .compose(applySchedulers())
                .subscribe(
                        () -> Log.d(TAG, "Sent data!"),
                        error -> Log.e(TAG, "Error", error)
                );
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}

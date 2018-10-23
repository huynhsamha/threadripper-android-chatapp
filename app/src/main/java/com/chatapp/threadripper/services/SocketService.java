package com.chatapp.threadripper.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.chatapp.threadripper.api.Config;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.Preferences;
import com.google.gson.Gson;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class SocketService extends Service {

    String TAG = "SOCKET_LOG";

    private StompClient client;

    public SocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Config.WEB_SOCKET_FULL_PATH);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // new Thread(() -> runSocketService()).start();
        runSocketService();

        return START_REDELIVER_INTENT;
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

    void runSocketService() {
        String username = Preferences.getCurrentUser().getUsername();
        String channel = "/topic/" + username;

        client.topic(channel).subscribe(response -> {
            String jsonString = response.getPayload();
            Gson gson = new Gson();
            Message message = gson.fromJson(jsonString, Message.class);
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

        try {
            // TODO: Error without explanation
            client.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(Message message) {
        message.setToken(Preferences.getChatAuthToken());

        client.send("/queue/sendMessage", new Gson().toJson(message)).subscribe(
                () -> Log.d(TAG, "Sent data!"),
                error -> Log.e(TAG, "Error", error)
        );
    }

    @Override
    public void onDestroy() {


        super.onDestroy();
    }
}

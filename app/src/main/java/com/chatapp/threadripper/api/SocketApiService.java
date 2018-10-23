package com.chatapp.threadripper.api;

import android.util.Log;

import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.utils.Preferences;
import com.google.gson.Gson;


import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class SocketApiService {

    String TAG = "SOCKET_LOG";

    private static SocketApiService instance;

    public interface SocketListener {
        void onMessage(Message message);
        void onJoin(String username);
        void onLeave(String username);
    }


    private StompClient client;
    private SocketListener listener;

    public static void init() {
        instance = new SocketApiService();
    }

    public static SocketApiService getInstance() {
        if (instance == null) {
            instance = new SocketApiService();
        }
        return instance;
    }

    public SocketApiService() {
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Config.WEB_SOCKET_FULL_PATH);
    }

    public SocketApiService subscribe() {

        String username = Preferences.getCurrentUser().getUsername();
        String channel = "/topic/" + username;

        client.topic(channel).subscribe(response -> {
            String jsonString = response.getPayload();
            Gson gson = new Gson();
            Message message = gson.fromJson(jsonString, Message.class);
            message.updateDateTime();

            if (listener == null) return;

            switch (message.getType()) {
                case Message.MessageType.TEXT:
                case Message.MessageType.FILE:
                case Message.MessageType.IMAGE:
                    listener.onMessage(message);
                    break;

                case Message.MessageType.JOIN:
                    listener.onJoin(message.getUsername());
                    break;

                case Message.MessageType.LEAVE:
                    listener.onLeave(message.getUsername());
                    break;

                default:
                    break;
            }
        });

        return this;
    }

    public void sendMessage(Message message) {
        message.setToken(Preferences.getChatAuthToken());

        client.send("/queue/sendMessage", new Gson().toJson(message)).subscribe(
                () -> Log.d(TAG, "Sent data!"),
                error -> Log.e(TAG, "Error", error)
        );
    }

    public void connect() {
        if (!client.isConnected()) client.connect();
    }

    public void disconnect() {
        if (client.isConnected()) client.disconnect();
    }

    public void destroy() {
        instance = null;
    }

    public SocketApiService addSocketListener(SocketListener listener) {
        this.listener = listener;
        return this;
    }

}

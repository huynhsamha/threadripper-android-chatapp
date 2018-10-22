package com.chatapp.threadripper.api;

import android.util.Log;

import com.chatapp.threadripper.cacheRealm.MessageRealm;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.MessageResponse;
import com.chatapp.threadripper.utils.Preferences;
import com.google.gson.Gson;


import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class SocketService {

    private static SocketService instance;

    public interface SocketListener {
        void onMessage(Message message);
        void onJoin(String username);
        void onLeave(String username);
    }


    private StompClient client;
    private SocketListener listener;

    public static void init() {
        instance = new SocketService();
    }

    public static SocketService getInstance() {
        if (instance == null) {
            instance = new SocketService();
        }
        return instance;
    }

    public SocketService() {
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Config.WEB_SOCKET_FULL_PATH);
    }

    public SocketService subscribe() {

        String username = Preferences.getCurrentUser().getUsername();
        String channel = "/topic/" + username;

        client.topic(channel).subscribe(response -> {
            String jsonString = response.getPayload();
            Gson gson = new Gson();
            MessageResponse messageResponse = gson.fromJson(jsonString, MessageResponse.class);
            Message message = messageResponse.toMessage();

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

    public void connect() {
        if (!client.isConnected()) client.connect();
    }

    public void disconnect() {
        if (client.isConnected()) client.disconnect();
    }

    public void destroy() {
        instance = null;
    }

    public SocketService addSocketListener(SocketListener listener) {
        this.listener = listener;
        return this;
    }

}

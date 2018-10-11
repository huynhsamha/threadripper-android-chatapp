package com.chatapp.threadripper.authenticated;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.chatapp.threadripper.BaseActivity;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.api.ChatSocketListener;
import com.chatapp.threadripper.api.Config;
import com.chatapp.threadripper.authenticated.models.Conversation;
import com.chatapp.threadripper.authenticated.models.Message;
import com.chatapp.threadripper.authenticated.adapters.ConversationAdapter;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.ShowToast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class ConversationActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private ConversationAdapter mAdapter;
    private EditText edtMessage;
    private ImageButton imgBtnSend;
    private CircleImageView cirImgUserAvatar;
    private View onlineIndicator;

    String username, userAvatarImage;

    OkHttpClient client;
    Request request;
    ChatSocketListener chatSocketListener;
    WebSocket ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        userAvatarImage = intent.getStringExtra("Image");
        boolean isOnline = intent.getBooleanExtra("IsOnline", false);

        setupToolbarWithBackButton(R.id.toolbar, username);

        edtMessage = (EditText) findViewById(R.id.edtMessage);
        imgBtnSend = (ImageButton) findViewById(R.id.imgBtnSend);


        // Load User Avatar & Online ?
        cirImgUserAvatar = (CircleImageView) findViewById(R.id.cirImgUserAvatar);
        onlineIndicator = findViewById(R.id.onlineIndicator);

        findViewById(R.id.rlImgUserAvatar).setVisibility(View.VISIBLE);
        ImageLoader.loadUserAvatar(cirImgUserAvatar, userAvatarImage);
        if (isOnline) onlineIndicator.setVisibility(View.VISIBLE);
        else onlineIndicator.setVisibility(View.GONE);


        // Messages
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        setListeners();

        fetchMessages();

        setupWebSocket();
    }

    void setupWebSocket() {
        client = new OkHttpClient();

        request = new Request.Builder().url(Config.WEB_SOCKET_URL).build();
        // chatSocketListener = new ChatSocketListener(this);
        ws = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, final String text) {
                super.onMessage(webSocket, text);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleOnReceiveMessage(text);
                    }
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);

                // ShowToast.lengthShort(ConversationActivity.this, bytes.toString());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);

                // ShowToast.lengthShort(ConversationActivity.this, "Code: " + code + " Reason: " + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);

                // ShowToast.lengthShort(ConversationActivity.this, "Code: " + code + " Reason: " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);

                // ShowToast.lengthShort(ConversationActivity.this, t.getMessage());
            }
        });

        client.dispatcher().executorService();
    }

    void setListeners() {
        // edtMessage.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         mRecyclerView.postDelayed(new Runnable() {
        //             @Override
        //             public void run() {
        //                 scrollToBottom();
        //             }
        //         }, 500);
        //     }
        // });

        edtMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // For the system keyboard toggle before scroll to last message
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollToBottom();
                    }
                }, 500);
                return false;
            }
        });

        imgBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSendMessage();
            }
        });
    }

    void handleSendMessage() {
        String msg = edtMessage.getText().toString();
        if (msg.isEmpty()) return;

        Message item = new Message();
        item.setTime("6:00pm");
        item.setType("2");
        item.setText(msg);

        mAdapter.addItem(item);
        scrollToBottom();
        edtMessage.setText("");

        ws.send(msg);
    }

    void handleOnReceiveMessage(String msg) {
        if (msg.isEmpty()) return;

        Message item = new Message();
        item.setTime("6:00pm");
        item.setType("1");
        item.setText(msg);
        item.setAvatarUser(userAvatarImage);

        mAdapter.addItem(item);
        scrollToBottom();
    }

    void fetchMessages() {
        ApiService.getInstance().getMessages(new ApiService.OnCompleteListener() {
            @Override
            public void onSuccess(ArrayList list) {
                ArrayList<Message> messages = new ArrayList<>();
                for (Object i : list) {
                    Message m = (Message) i;
                    if (m.getType().equals("1")) { // YOU
                        m.setAvatarUser(userAvatarImage);
                    }
                    messages.add(m);
                }
                mAdapter.setItemsList(messages);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    void scrollToBottom() {
        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    //     MenuInflater inflater = getMenuInflater();
    //     inflater.inflate(R.menu.menu_userphoto, menu);
    //     return true;
    // }

}

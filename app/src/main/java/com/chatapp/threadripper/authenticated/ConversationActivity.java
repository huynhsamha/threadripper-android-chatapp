package com.chatapp.threadripper.authenticated;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.chatapp.threadripper.BaseActivity;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.authenticated.models.Message;
import com.chatapp.threadripper.authenticated.adapters.ConversationAdapter;
import com.chatapp.threadripper.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ConversationActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private ConversationAdapter mAdapter;
    private EditText text;
    private Button send;
    private CircleImageView cirImgUserAvatar;
    View onlineIndicator;

    String username, userAvatarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        userAvatarImage = intent.getStringExtra("Image");
        boolean isOnline = intent.getBooleanExtra("IsOnline", false);

        setupToolbarWithBackButton(R.id.toolbar, username);

        text = (EditText) findViewById(R.id.et_message);
        send = (Button) findViewById(R.id.bt_send);


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
    }

    void setListeners() {
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                    }
                }, 500);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!text.getText().equals("")) {
                    ArrayList<Message> data = new ArrayList<Message>();
                    Message item = new Message();
                    item.setTime("6:00pm");
                    item.setType("2");
                    item.setText(text.getText().toString());
                    data.add(item);
                    mAdapter.addItemsList(data);
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                    text.setText("");
                }
            }
        });
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
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            }
        }, 1000);
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    //     MenuInflater inflater = getMenuInflater();
    //     inflater.inflate(R.menu.menu_userphoto, menu);
    //     return true;
    // }
}

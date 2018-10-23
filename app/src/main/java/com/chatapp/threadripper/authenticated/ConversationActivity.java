package com.chatapp.threadripper.authenticated;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.authenticated.adapters.ConversationAdapter;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.receivers.SocketReceiver;
import com.chatapp.threadripper.services.SocketService;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.Preferences;
import com.chatapp.threadripper.utils.ShowToast;
import com.makeramen.roundedimageview.RoundedImageView;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConversationActivity extends BaseMainActivity implements SocketReceiver.OnCallbackListener {

    String TAG = "ConversationActivity";

    private RecyclerView mRecyclerView;
    private ConversationAdapter mAdapter;
    private EditText edtMessage;
    private ImageButton imgBtnSend, btnAttachChatImage, btnCaptureImage, btnAttachFile, btnShowButtons;
    private CircleImageView cirImgUserAvatar;
    private View onlineIndicator;
    private RoundedImageView rivImageIsPickedOrCaptured;

    boolean isOnline;
    String displayName, avatar, conversationId;
    String uriAttachImage;
    Bitmap bitmapCaptureImage;

    IntentFilter mIntentFilter;
    SocketReceiver mSocketReceiver;
    ServiceConnection mSocketServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        getIntentData();

        setupToolbarWithBackButton(R.id.toolbar, displayName);

        initViews();

        initRecyclerView();

        setListeners();

        fetchMessages();

        // setupWebSocket();
        initSocketReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mSocketReceiver, mIntentFilter);
    }

    void initSocketReceiver() {
        mSocketReceiver = new SocketReceiver();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_NEW_MESSAGE);
        mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_JOIN);
        mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_LEAVE);

        mSocketReceiver.setListener(this);

        mSocketServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
    }

    void initRecyclerView() {
        // Messages
        mRecyclerView = (RecyclerView) findViewById(R.id.rcvGroups);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
    }

    void getIntentData() {
        Intent intent = getIntent();
        conversationId = intent.getStringExtra(Constants.CONVERSATION_ID);
        displayName = intent.getStringExtra(Constants.CONVERSATION_NAME);
        avatar = intent.getStringExtra(Constants.CONVERSATION_PHOTO);
        isOnline = intent.getBooleanExtra(Constants.CONVERSATION_IS_ONLINE, false);
    }

    void initViews() {
        edtMessage = (EditText) findViewById(R.id.edtMessage);
        imgBtnSend = (ImageButton) findViewById(R.id.imgBtnSend);
        btnAttachChatImage = (ImageButton) findViewById(R.id.btnAttachChatImage);
        btnCaptureImage = (ImageButton) findViewById(R.id.btnCaptureImage);
        btnAttachFile = (ImageButton) findViewById(R.id.btnAttachFile);
        btnShowButtons = (ImageButton) findViewById(R.id.btnShowButtons);

        rivImageIsPickedOrCaptured = (RoundedImageView) findViewById(R.id.rivImageIsPickedOrCaptured);

        // Load User Avatar & Online ?
        cirImgUserAvatar = (CircleImageView) findViewById(R.id.cirImgUserAvatar);
        onlineIndicator = findViewById(R.id.onlineIndicator);

        findViewById(R.id.rlImgUserAvatar).setVisibility(View.VISIBLE);
        ImageLoader.loadUserAvatar(cirImgUserAvatar, avatar);
        if (isOnline) onlineIndicator.setVisibility(View.VISIBLE);
        else onlineIndicator.setVisibility(View.GONE);

        btnShowButtons.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            btnCaptureImage.setVisibility(View.GONE); // not support capture image with API < 23
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    void setListeners() {

        edtMessage.setOnTouchListener((view, event) -> {
            mRecyclerView.postDelayed(() -> {
                hideButtonsBar();
                scrollToBottom();
            }, 300);
            return false;
        });

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hideButtonsBar();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgBtnSend.setOnClickListener(view -> handleClickButtonSend());
        btnAttachChatImage.setOnClickListener(view -> handleAttachImage());
        btnCaptureImage.setOnClickListener(view -> handleCaptureCamera());
        btnAttachFile.setOnClickListener(view -> handleAttachFile());

        btnShowButtons.setOnClickListener(view -> showButtonsBar());
    }

    void hideButtonsBar() {
        btnShowButtons.setVisibility(View.VISIBLE);
        btnAttachChatImage.setVisibility(View.GONE);
        btnCaptureImage.setVisibility(View.GONE);
        btnAttachFile.setVisibility(View.GONE);
    }

    void showButtonsBar() {
        btnShowButtons.setVisibility(View.GONE);
        btnAttachChatImage.setVisibility(View.VISIBLE);
        btnCaptureImage.setVisibility(View.VISIBLE);
        btnAttachFile.setVisibility(View.VISIBLE);
    }

    void handleAttachFile() {
        btnAttachFile.setImageResource(R.drawable.ic_action_attach_file_accent);
    }

    void handleClickButtonSend() {
        if (edtMessage.getVisibility() == View.VISIBLE) { // send a message text
            handleSendMessage();
        } else if (rivImageIsPickedOrCaptured.getVisibility() == View.VISIBLE) { // send a message image
            if (uriAttachImage != null) { // image is picked - use uri
                handleSendAttachImage();
            } else if (bitmapCaptureImage != null) { // image is captured - use bitmap
                handleSendCaptureImage();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
        // >= 23
    void handleCaptureCamera() {
        btnCaptureImage.setImageResource(R.drawable.ic_action_linked_camera_accent);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CODE_PERMISSION_IMAGE_CAPTURE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, Constants.REQUEST_CODE_CAPTURE_IMAGE);
        }
    }

    void handleAttachImage() {
        btnAttachChatImage.setImageResource(R.drawable.ic_action_add_photo_alternate_accent);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), Constants.REQUEST_CODE_PICK_IMAGE);
    }

    void handleSendCaptureImage() {
        Message item = new Message();
        item.setDateTime(new Date());
        item.setType(Message.MessageType.IMAGE);
        item.setBitmap(bitmapCaptureImage);
        item.setBitmap(true);

        mAdapter.addItem(item);
        scrollToBottom();

        edtMessage.setVisibility(View.VISIBLE);
        rivImageIsPickedOrCaptured.setImageResource(R.drawable.placeholder_image_chat);
        rivImageIsPickedOrCaptured.setVisibility(View.GONE);
    }

    void handleSendAttachImage() {
        Message item = new Message();
        item.setDateTime(new Date());
        item.setType(Message.MessageType.IMAGE);
        item.setContent(uriAttachImage);

        mAdapter.addItem(item);
        scrollToBottom();

        edtMessage.setVisibility(View.VISIBLE);
        rivImageIsPickedOrCaptured.setImageResource(R.drawable.placeholder_image_chat);
        rivImageIsPickedOrCaptured.setVisibility(View.GONE);
    }

    void handleSendMessage() {
        String msg = edtMessage.getText().toString().trim();
        if (msg.isEmpty()) return;

        Message item = new Message();
        item.setDateTime(new Date());
        item.setType(Message.MessageType.TEXT);
        item.setConversationId(msg);
        item.setContent(msg);

        edtMessage.setText("");

        mAdapter.addItem(item);
        scrollToBottom();

        // Intent intent = new Intent(this, SocketService.class);
        // bindService(intent, mSocketServiceConnection, BIND_AUTO_CREATE);
    }

    void handleMessagesList(ArrayList<Message> messages) {
        for (Message message : messages) {
            message.updateDateTime();
            if (!message.getUsername().equals(Preferences.getCurrentUser().getUsername())) {
                message.setYou(true);
            }
        }

        mAdapter.setItemsList(messages);

        mRecyclerView.postDelayed(() -> scrollToBottom(), 300);
    }

    void fetchMessages() {
        ApiService.getInstance().getMessagesInConversation(conversationId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful()) {

                    // parse to Message
                    ArrayList<Message> messages = (ArrayList<Message>) response.body();
                    handleMessagesList(messages);

                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

            }
        });
    }

    void scrollToBottom() {
        if (mRecyclerView.getAdapter().getItemCount() > 0)
            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                // ShowToast.lengthShort(this, "OK");
                handlePickImageSuccess(data);
            } else {
                // Not do anything
                // ShowToast.lengthShort(this, "An error occurred, please try again later.");
            }

            // reset button attach image
            btnAttachChatImage.setImageResource(R.drawable.ic_action_add_photo_alternate);
        } else if (requestCode == Constants.REQUEST_CODE_CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                handleCaptureImageSuccess(data);
            }

            // reset button capture image
            btnCaptureImage.setImageResource(R.drawable.ic_action_linked_camera);
        }
    }

    void handlePickImageSuccess(Intent data) {
        edtMessage.setVisibility(View.GONE);
        rivImageIsPickedOrCaptured.setVisibility(View.VISIBLE);

        Uri uri = data.getData();
        uriAttachImage = uri.toString();
        ImageLoader.loadImageChatMessage(rivImageIsPickedOrCaptured, uriAttachImage);
        bitmapCaptureImage = null; // reset method capture image, current image is picked

        // Log.d("LogImage", "handlePickImageFromMedia: " + uriAttachImage);
        // example: content://com.android.providers.media.documents/document/image%3A14109
    }

    void handleCaptureImageSuccess(Intent data) {
        edtMessage.setVisibility(View.GONE);
        rivImageIsPickedOrCaptured.setVisibility(View.VISIBLE);

        bitmapCaptureImage = (Bitmap) data.getExtras().get("data");
        // ImageLoader.loadImageChatMessage(rivImageIsPickedOrCaptured, photo.toString());
        rivImageIsPickedOrCaptured.setImageBitmap(bitmapCaptureImage);
        uriAttachImage = null; // reset method pick image, current image is captured

        // Log.d("LogImage", "handleCaptureImageSuccess: " + photo.toString());
        // example: android.graphics.Bitmap@312c4eb
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE_PERMISSION_IMAGE_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleCaptureCamera();
            } else {
                // the fucking user!!!
                ShowToast.lengthLong(this, "Camera permission is denied");

                // reset UI
                // edtMessage.setVisibility(View.VISIBLE);
                // rivImageIsPickedOrCaptured.setVisibility(View.GONE);
                btnCaptureImage.setImageResource(R.drawable.ic_action_linked_camera);
            }
        }
    }

    @Override
    public void onNewMessage(Message message) {
        if (!message.getConversationId().equals(conversationId)) return;

        message.setConversationAvatar(avatar);
        message.setYou(true);

        mAdapter.addItem(message);
        scrollToBottom();
    }

    @Override
    public void onJoin(String username) {
        Log.d(TAG, "onJoin: " + username);
    }

    @Override
    public void onLeave(String username) {
        Log.d(TAG, "onLeave: " + username);
    }
}

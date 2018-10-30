package com.chatapp.threadripper.authenticated;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiResponseData;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.api.SocketManager;
import com.chatapp.threadripper.authenticated.adapters.ConversationAdapter;
import com.chatapp.threadripper.models.ErrorResponse;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.receivers.SocketReceiver;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.DateTimeUtils;
import com.chatapp.threadripper.utils.FileUtils;
import com.chatapp.threadripper.utils.ImageFilePath;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.PathUtil;
import com.chatapp.threadripper.utils.Preferences;
import com.chatapp.threadripper.utils.ShowToast;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConversationActivity extends BaseMainActivity implements SocketReceiver.OnCallbackListener {

    String TAG = "ConversationActivity";

    private RecyclerView rcvMessages;
    private ConversationAdapter mAdapter;
    private EditText edtMessage;
    private ImageButton imgBtnSend, btnAttachChatImage, btnCaptureImage, btnAttachFile, btnShowButtons;
    private RoundedImageView rivImageIsPickedOrCaptured;
    private RoundedImageView filePicked;
    TextView tvUserTyping;

    Set<String> typingUsername = new HashSet<>(); // use Set for unique username

    boolean isOnline;
    String displayName, avatar, conversationId;
    String uriAttachImageString;
    Uri uriAttachImage;
    Uri uriAttachFile;
    Bitmap bitmapCaptureImage;

    IntentFilter mIntentFilter;
    SocketReceiver mSocketReceiver;

    RealmResults<Message> messages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        getIntentData();

        setupToolbarWithBackButton(R.id.toolbar, displayName);

        initViews();

        setListeners();

        fetchMessages();

        initSocketReceiver();

        initDetectNetworkStateChange();

        markReadAllMessages();
    }

    void markReadAllMessages() {
        if (messages.isEmpty()) return;

        long lastMessageId = Objects.requireNonNull(messages.get(messages.size() - 1)).getMessageId();
        SocketManager.getInstance().sendReadMessages(conversationId, lastMessageId);

        CacheService.getInstance().setReadAllMessagesConversation(conversationId);
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
        // mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_JOIN);
        // mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_LEAVE);
        mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_TYPING);
        mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_READ);

        mSocketReceiver.setListener(this);
    }

    void getIntentData() {
        Intent intent = getIntent();
        conversationId = intent.getStringExtra(Constants.CONVERSATION_ID);
        displayName = intent.getStringExtra(Constants.CONVERSATION_NAME);
        avatar = intent.getStringExtra(Constants.CONVERSATION_PHOTO);
        isOnline = intent.getBooleanExtra(Constants.CONVERSATION_IS_ONLINE, false);
    }

    void initViews() {
        tvUserTyping = (TextView) findViewById(R.id.tvUserTyping);
        edtMessage = (EditText) findViewById(R.id.edtMessage);
        imgBtnSend = (ImageButton) findViewById(R.id.imgBtnSend);
        btnAttachChatImage = (ImageButton) findViewById(R.id.btnAttachChatImage);
        btnCaptureImage = (ImageButton) findViewById(R.id.btnCaptureImage);
        btnAttachFile = (ImageButton) findViewById(R.id.btnAttachFile);
        btnShowButtons = (ImageButton) findViewById(R.id.btnShowButtons);

        rivImageIsPickedOrCaptured = (RoundedImageView) findViewById(R.id.rivImageIsPickedOrCaptured);
        filePicked = (RoundedImageView) findViewById(R.id.filePicked);

        tvUserTyping.setText("");
        btnShowButtons.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            btnCaptureImage.setVisibility(View.GONE); // not support capture image with API < 23
        }

        // Messages Recycler View
        rcvMessages = (RecyclerView) findViewById(R.id.rcvMessages);

        rcvMessages.setHasFixedSize(true);
        rcvMessages.setLayoutManager(new LinearLayoutManager(this));
        messages = CacheService.getInstance().retrieveCacheMessages(conversationId);
        mAdapter = new ConversationAdapter(this, messages);
        rcvMessages.setAdapter(mAdapter);
    }


    @SuppressLint("ClickableViewAccessibility")
    void setListeners() {

        edtMessage.setOnTouchListener((view, event) -> {
            rcvMessages.postDelayed(() -> {
                hideButtonsBar();
                scrollToBottom();

                markReadAllMessages();
            }, 300);
            return false;
        });

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hideButtonsBar();
                SocketManager.getInstance().isTyping(conversationId, true);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                SocketManager.getInstance().isTyping(conversationId, false);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            imgBtnSend.setOnClickListener(view -> handleClickButtonSend());
        }
        btnCaptureImage.setOnClickListener(view -> handleCaptureCamera());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnAttachFile.setOnClickListener(view -> handleAttachFile());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnAttachChatImage.setOnClickListener(view -> handleAttachImage());
        }

        btnShowButtons.setOnClickListener(view -> showButtonsBar());
    }

    void hideButtonsBar() {
        btnAttachChatImage.setVisibility(View.GONE);
        btnCaptureImage.setVisibility(View.GONE);
        btnAttachFile.setVisibility(View.GONE);
        btnShowButtons.setVisibility(View.VISIBLE);
    }

    void showButtonsBar() {
        btnShowButtons.setVisibility(View.GONE);
        btnAttachChatImage.setVisibility(View.VISIBLE);
        btnCaptureImage.setVisibility(View.VISIBLE);
        btnAttachFile.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void handleAttachFile() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_PERMISSION_READ_EXTERNAL);
            return;
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_PERMISSION_WRITE_EXTERNAL);
            return;
        }

        btnAttachFile.setImageResource(R.drawable.ic_action_attach_file_accent);

        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select file"), Constants.REQUEST_CODE_PICK_FILE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void handleClickButtonSend() {
        if (edtMessage.getVisibility() == View.VISIBLE) { // send a message text
            handleSendMessage();
        } else if (rivImageIsPickedOrCaptured.getVisibility() == View.VISIBLE) { // send a message image
            if (uriAttachImageString != null) { // image is picked - use uri
                handleSendAttachImage();
            } else if (bitmapCaptureImage != null) { // image is captured - use bitmap
                handleSendCaptureImage();
            }
        } else if (filePicked.getVisibility() == View.VISIBLE) { //send a file
            handleSendAttachFile();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    void handleCaptureCamera() {
        btnCaptureImage.setImageResource(R.drawable.ic_action_linked_camera_accent);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CODE_PERMISSION_IMAGE_CAPTURE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, Constants.REQUEST_CODE_CAPTURE_IMAGE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void handleAttachImage() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_PERMISSION_READ_EXTERNAL);
            return;
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_PERMISSION_WRITE_EXTERNAL);
            return;
        }

        btnAttachChatImage.setImageResource(R.drawable.ic_action_add_photo_alternate_accent);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), Constants.REQUEST_CODE_PICK_IMAGE);
    }

    Message makeNewMessage(String type) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setType(type);
        return message;
    }

    void updateCache(Message message) {
        CacheService.getInstance().addOrUpdateCacheMessage(message);
        markReadAllMessages();
    }

    void handleSendMessage() {
        String content = edtMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        Message message = makeNewMessage(Message.MessageType.TEXT);
        message.setContent(content);

        SocketManager.getInstance().sendText(conversationId, content);

        edtMessage.setText("");
    }

    void handleSendCaptureImage() {
        Message message = makeNewMessage(Message.MessageType.IMAGE);

        edtMessage.setVisibility(View.VISIBLE);
        rivImageIsPickedOrCaptured.setImageResource(R.drawable.placeholder_image_chat);
        rivImageIsPickedOrCaptured.setVisibility(View.GONE);

        try {
            File file = FileUtils.bitmap2File(this, bitmapCaptureImage);
            postImageToServerWithFile(file, new OnCompletePostImage() {
                @Override
                public void onSuccess(String url) {
                    message.setContent(url);
                    SocketManager.getInstance().sendImage(conversationId, url);
                }

                @Override
                public void onFailure(String errMessage) {
                    ConversationActivity.this.ShowErrorDialog(errMessage);
                }
            });

        } catch (Exception err) {
            ConversationActivity.this.ShowErrorDialog(err.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void handleSendAttachImage() {
        Message message = makeNewMessage(Message.MessageType.IMAGE);

        edtMessage.setVisibility(View.VISIBLE);
        rivImageIsPickedOrCaptured.setImageResource(R.drawable.placeholder_image_chat);
        rivImageIsPickedOrCaptured.setVisibility(View.GONE);

        try {
            String realFilePath = ImageFilePath.getPath(ConversationActivity.this, uriAttachImage);
            File file = new File(realFilePath);

            postImageToServerWithFile(file, new OnCompletePostImage() {
                @Override
                public void onSuccess(String url) {
                    message.setContent(url);
                    SocketManager.getInstance().sendImage(conversationId, url);
                }

                @Override
                public void onFailure(String errMessage) {
                    ConversationActivity.this.ShowErrorDialog(errMessage);
                }
            });

        } catch (Exception err) {
            ConversationActivity.this.ShowErrorDialog(err.getMessage());
        }
    }

    void handleSendAttachFile() {
        Message message = makeNewMessage(Message.MessageType.IMAGE);

        edtMessage.setVisibility(View.VISIBLE);
        filePicked.setImageResource(R.drawable.placeholder_attached_file);
        filePicked.setVisibility(View.GONE);

        try {
            String realFilePath = PathUtil.getPath(ConversationActivity.this, uriAttachFile);
            File file = new File(realFilePath);

            postFileToServer(file, new OnCompletePostFile() {
                @Override
                public void onSuccess(String url) {
                    message.setContent(url);
                    SocketManager.getInstance().sendFile(conversationId, url);
                }

                @Override
                public void onFailure(String errMessage) {
                    ConversationActivity.this.ShowErrorDialog(errMessage);
                }
            });

        } catch (Exception err) {
            ConversationActivity.this.ShowErrorDialog(err.getMessage());
        }
    }

    interface OnCompletePostImage {
        void onSuccess(String url);

        void onFailure(String errMessage);
    }

    interface OnCompletePostFile {
        void onSuccess(String url);

        void onFailure(String errMessage);
    }

    void postImageToServerWithFile(File file, OnCompletePostImage listener) {
        ApiService.getInstance().uploadImageInChat(file).enqueue(new Callback<ApiResponseData>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseData> call, @NonNull Response<ApiResponseData> response) {
                if (response.isSuccessful()) {
                    ApiResponseData data = response.body();
                    String url = null;
                    if (data != null) {
                        url = data.getImageUrl();
                    }
                    listener.onSuccess(url);
                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        listener.onFailure(err.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onFailure(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseData> call, @NonNull Throwable t) {
                listener.onFailure(t.getMessage());
            }
        });
    }

    void postFileToServer(File file, OnCompletePostFile listener) {
        ApiService.getInstance().uploadFileInChat(file).enqueue(new Callback<ApiResponseData>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseData> call, @NonNull Response<ApiResponseData> response) {
                if (response.isSuccessful()) {
                    ApiResponseData data = response.body();
                    String url = null;
                    if (data != null) {
                        url = data.getFileUrl();
                    }
                    listener.onSuccess(url);
                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        listener.onFailure(err.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onFailure(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseData> call, @NonNull Throwable t) {
                listener.onFailure(t.getMessage());
            }
        });
    }

    void compareDifferentTimeMessages(Message mOld, Message mNew) {
        // determine if message should be leading block (show time)
        if (mOld == null || mNew == null) return;
        int diffInMinutes = DateTimeUtils.differentInMinutes(mOld.getDateTime(), mNew.getDateTime());
        if (diffInMinutes > 30) { // later 30 minutes
            mNew.setLeadingBlock(true);
        }
    }

    void handleReceivedMessagesList(ArrayList<Message> messages) {
        // add messages received from server to cache "messages"
        for (Message message : messages) {
            handleReceivedMessage(message);
        }

        rcvMessages.postDelayed(this::scrollToBottom, 300);
    }

    void handleReceivedMessage(Message message) {
        message.updateDateTime();
        message.setConversationAvatar(avatar);
        if (!message.getUsername().equals(Preferences.getCurrentUser().getUsername())) {
            message.setYou(true);
        }

        // determine if message should be leading block (show time)
        if (messages.isEmpty()) {
            message.setLeadingBlock(true); // first message, it should be leading block
        } else {
            Message lastMessage = messages.get(messages.size()-1); // get last message
            compareDifferentTimeMessages(lastMessage, message);
        }

        updateCache(message); // push it to "messages"
    }

    void fetchMessages() {
        ApiService.getInstance().getMessagesInConversation(conversationId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                if (response.isSuccessful()) {

                    ArrayList<Message> messages = (ArrayList<Message>) response.body();
                    handleReceivedMessagesList(messages);

                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        ConversationActivity.this.ShowErrorDialog(err.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        ConversationActivity.this.ShowErrorDialog(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable t) {
                ConversationActivity.this.ShowErrorDialog(t.getMessage());
            }
        });
    }

    void scrollToBottom() {
        if (rcvMessages.getAdapter().getItemCount() > 0) {
            rcvMessages.smoothScrollToPosition(rcvMessages.getAdapter().getItemCount() - 1);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                handlePickImageSuccess(data);
            }
            btnAttachChatImage.setImageResource(R.drawable.ic_action_add_photo_alternate);

        } else if (requestCode == Constants.REQUEST_CODE_CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                handleCaptureImageSuccess(data);
            }
            btnCaptureImage.setImageResource(R.drawable.ic_action_linked_camera);
        } else if (requestCode == Constants.REQUEST_CODE_PICK_FILE) {
            if (resultCode == RESULT_OK && data != null) {
                handlePickFileSuccess(data);
            }
            btnCaptureImage.setImageResource(R.drawable.ic_action_linked_camera);
        }
    }

    void handlePickImageSuccess(Intent data) {
        edtMessage.setVisibility(View.GONE);
        rivImageIsPickedOrCaptured.setVisibility(View.VISIBLE);

        Uri uri = data.getData();
        uriAttachImage = uri;
        if (uri != null) {
            uriAttachImageString = uri.toString();
        }
        ImageLoader.loadImageChatMessage(rivImageIsPickedOrCaptured, uriAttachImageString);
        bitmapCaptureImage = null; // reset method capture image, current image is picked

        // Log.d("LogImage", "handlePickImageFromMedia: " + uriAttachImageString);
        // example: content://com.android.providers.media.documents/document/image%3A14109
    }

    void handlePickFileSuccess(Intent data) {
        edtMessage.setVisibility(View.GONE);
        filePicked.setVisibility(View.VISIBLE);

        Uri uri = data.getData();
        uriAttachFile = uri;
        // Log.d("LogImage", "handlePickImageFromMedia: " + uriAttachImageString);
        // example: content://com.android.providers.media.documents/document/image%3A14109
    }

    void handleCaptureImageSuccess(Intent data) {
        edtMessage.setVisibility(View.GONE);
        rivImageIsPickedOrCaptured.setVisibility(View.VISIBLE);

        bitmapCaptureImage = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
        rivImageIsPickedOrCaptured.setImageBitmap(bitmapCaptureImage);
        uriAttachImageString = null; // reset method pick image, current image is captured
        uriAttachImage = null;

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

                btnCaptureImage.setImageResource(R.drawable.ic_action_linked_camera);
            }
        }

        if (requestCode == Constants.REQUEST_CODE_PERMISSION_READ_EXTERNAL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    handleAttachImage();
                }
            } else {
                // the fucking user!!!
                ShowToast.lengthLong(this, "Read external storage permission is denied");
            }
        }

        if (requestCode == Constants.REQUEST_CODE_PERMISSION_WRITE_EXTERNAL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    handleAttachImage();
                }
            } else {
                // the fucking user!!!
                ShowToast.lengthLong(this, "Write external storage permission is denied");
            }
        }
    }

    @Override
    public void onNewMessage(Message message) {
        // Message not in this conversation
        if (!message.getConversationId().equals(conversationId)) return;

        handleReceivedMessage(message);
        scrollToBottom();
    }

    @Override
    public void onJoin(String username) {

    }

    @Override
    public void onLeave(String username) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTyping(String _conversationId, String username, boolean typing) {
        if (!_conversationId.equals(conversationId)) return;
        if (username.equals(Preferences.getCurrentUser().getUsername())) return;

        if (typing) typingUsername.add(username);
        else typingUsername.remove(username);

        if (typingUsername.isEmpty()) {
            tvUserTyping.setText("");
        } else {
            tvUserTyping.setText(TextUtils.join(", ", typingUsername) + " is typing...");
        }
    }

    public static String getPathFromFile(Uri uri) {
        File file = new File(uri.getPath());
        final String[] split = file.getPath().split(":");
        final String filepath = split[1];
        return filepath;
    }
}

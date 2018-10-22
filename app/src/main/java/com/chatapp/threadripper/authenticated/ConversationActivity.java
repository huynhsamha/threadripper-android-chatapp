package com.chatapp.threadripper.authenticated;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TabWidget;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.TestApiService;
import com.chatapp.threadripper.api.Config;
import com.chatapp.threadripper.authenticated.models.Message;
import com.chatapp.threadripper.authenticated.adapters.ConversationAdapter;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.Preferences;
import com.chatapp.threadripper.utils.ShowToast;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;


public class ConversationActivity extends BaseMainActivity {

    private RecyclerView mRecyclerView;
    private ConversationAdapter mAdapter;
    private EditText edtMessage;
    private ImageButton imgBtnSend, btnAttachChatImage, btnCaptureImage, btnAttachFile, btnShowButtons;
    private CircleImageView cirImgUserAvatar;
    private View onlineIndicator;
    private RoundedImageView rivImageIsPickedOrCaptured;

    boolean isOnline;
    String displayName, avatar;
    String uriAttachImage;
    Bitmap bitmapCaptureImage;

    StompClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Intent intent = getIntent();
        displayName = intent.getStringExtra(Constants.CONVERSATION_NAME);
        avatar = intent.getStringExtra(Constants.CONVERSATION_PHOTO);
        isOnline = intent.getBooleanExtra(Constants.CONVERSATION_IS_ONLINE, false);


        setupToolbarWithBackButton(R.id.toolbar, displayName);

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

        // Messages
        mRecyclerView = (RecyclerView) findViewById(R.id.rcvGroups);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        setListeners();

        fetchMessages();

        // setupWebSocket();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            btnCaptureImage.setVisibility(View.GONE); // not support capture image with API < 23
        }
    }

    void setupWebSocket() {
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Config.WEB_SOCKET_FULL_PATH);

        client.connect();

        client.topic("/topic/public").subscribe(message -> {
            String str = message.getPayload();
            Log.d("LogConversation:", "setupWebSocket: " + str);
            JSONObject json = null;
            try {
                json = new JSONObject(str);
                String type = json.getString("type");
                if (type.equals("CHAT")) {
                    // Chat message
                    String sender = json.getString("sender");
                    String content = json.getString("content");
                    if (!sender.equals(Preferences.getCurrentUser().getUsername())) { // other user
                        runOnUiThread(new Runnable() { // main thread
                            @Override
                            public void run() {
                                handleOnReceiveMessage(content);
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    void setListeners() {

        // edtMessage.setOnFocusChangeListener((view, hasFocus) -> {
        //     if (hasFocus) {
        //         mRecyclerView.postDelayed(() -> {
        //             hideButtonsBar();
        //             scrollToBottom();
        //         }, 300);
        //     } else {
        //         showButtonsBar();
        //     }
        // });

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

    @TargetApi(Build.VERSION_CODES.M) // >= 23
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
        item.setTime("6:00pm");
        item.setType("2");
        item.setContentType(Constants.CHAT_CONTENT_TYPE_BITMAP);
        item.setBitmap(bitmapCaptureImage);

        mAdapter.addItem(item);
        scrollToBottom();

        edtMessage.setVisibility(View.VISIBLE);
        rivImageIsPickedOrCaptured.setImageResource(R.drawable.placeholder_image_chat);
        rivImageIsPickedOrCaptured.setVisibility(View.GONE);
    }

    void handleSendAttachImage() {
        Message item = new Message();
        item.setTime("6:00pm");
        item.setType("2");
        item.setContentType(Constants.CHAT_CONTENT_TYPE_URI);
        item.setImgUrl(uriAttachImage);

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
        item.setTime("6:00pm");
        item.setType("2");
        item.setContentType(Constants.CHAT_CONTENT_TYPE_TEXT);
        item.setText(msg);

        mAdapter.addItem(item);
        scrollToBottom();
        edtMessage.setText("");

        JSONObject json = new JSONObject();

        try {
            json.put("sender", Preferences.getCurrentUser().getUsername());
            json.put("content", msg);
            json.put("type", "CHAT");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.send("/app/chat.sendMessage", json.toString()).subscribe(
                () -> Log.d("LogConversation", "Sent data!"),
                error -> Log.e("LogConversation", "Encountered error while sending data!", error)
        );
    }

    void handleOnReceiveMessage(String msg) {
        if (msg.isEmpty()) return;

        Message item = new Message();
        item.setTime("6:00pm");
        item.setType("1");
        item.setText(msg);
        item.setAvatarUser(avatar);

        mAdapter.addItem(item);
        scrollToBottom();
    }

    void fetchMessages() {
        TestApiService.getInstance().getMessages(new TestApiService.OnCompleteListener() {
            @Override
            public void onSuccess(ArrayList list) {
                ArrayList<Message> messages = new ArrayList<>();
                for (Object i : list) {
                    Message m = (Message) i;
                    if (m.getType().equals("1")) { // YOU
                        m.setAvatarUser(avatar);
                    }
                    messages.add(m);
                }
                mAdapter.setItemsList(messages);

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollToBottom();
                    }
                }, 500);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    void scrollToBottom() {
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
        }
        else if (requestCode == Constants.REQUEST_CODE_CAPTURE_IMAGE) {
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
}

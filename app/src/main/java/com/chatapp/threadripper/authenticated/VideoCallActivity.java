package com.chatapp.threadripper.authenticated;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.SocketManager;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.receivers.SocketReceiver;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.ModelUtils;
import com.chatapp.threadripper.utils.Preferences;
import com.chatapp.threadripper.utils.ShowToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoCallActivity extends BaseMainActivity implements SocketReceiver.OnCallbackListener {

    CircleImageView cirImgUserAvatar;
    RippleView rvCall, rvCallEnd;
    TextView tvUsername, tvStatus;
    LinearLayout linLayoutCall;

    User targetUser;
    boolean callerSide, callingAudioOrVideo; // me, caller or callee
    String channelId; // not me, the caller or callee

    IntentFilter mIntentFilter;
    SocketReceiver mSocketReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();

        getIntentData();

        initViews();

        setListener();

        initSocketReceiver();

        if (callerSide) {
            SocketManager.getInstance().sendCalling(targetUser, Constants.CALLER_REQUEST_CALLING, channelId);
        } else {
            // waiting the callee accept or not by send socket
        }
    }

    void getIntentData() {
        Intent intent = getIntent();
        callerSide = intent.getBooleanExtra(Constants.IS_CALLER_SIDE, false);
        targetUser = (User) intent.getSerializableExtra(Constants.USER_MODEL);
        channelId = intent.getStringExtra(Constants.EXTRA_VIDEO_CHANNEL_TOKEN);
        callingAudioOrVideo = intent.getBooleanExtra(Constants.CALLING_VIDEO_OR_AUDIO, false); // default is call audio
    }

    void initSocketReceiver() {
        mSocketReceiver = new SocketReceiver();

        mIntentFilter = new IntentFilter();
        // mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_NEW_MESSAGE);
        // mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_JOIN);
        // mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_LEAVE);
        // mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_TYPING);
        // mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_READ);
        mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_CALL);

        mSocketReceiver.setListener(this);
    }

    void initViews() {
        cirImgUserAvatar = (CircleImageView) findViewById(R.id.cirImgUserAvatar);
        rvCall = (RippleView) findViewById(R.id.rvCall);
        rvCallEnd = (RippleView) findViewById(R.id.rvCallEnd);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        linLayoutCall = (LinearLayout) findViewById(R.id.linLayoutCall);

        // Hide icon call (green) when is caller
        if (callerSide) {
            linLayoutCall.setVisibility(View.GONE);
        }

        // Change user info
        tvUsername.setText(targetUser.getDisplayName());
        ImageLoader.loadUserAvatar(cirImgUserAvatar, targetUser.getPhotoUrl());
    }

    void setListener() {
        rvCallEnd.setOnRippleCompleteListener(rippleView -> handleEndCalling());
        rvCall.setOnRippleCompleteListener(rippleView -> handleAcceptCalling());
    }

    void handleEndCalling() {
        if (callerSide) {
            // is waiting calling, but cancel the call
            SocketManager.getInstance().sendCalling(targetUser, Constants.CALLER_CANCEL_REQUEST, channelId);
            setResult(RESULT_CANCELED);
            finish();
        } else {
            // the callee cancel the calling
            SocketManager.getInstance().sendCalling(targetUser, Constants.CALLEE_REJECT_REQUEST_CALL, channelId);
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    void handleAcceptCalling() {
        if (callerSide) {
            // don't have this case
        } else {
            // the callee accept the calling
            SocketManager.getInstance().sendCalling(targetUser, Constants.CALLEE_ACCEPT_REQUEST_CALL, channelId);
            callingSuccessful();
            finish();
        }

    }

    void callingSuccessful() {
        String channelId;
        if (callerSide) {
            channelId = ModelUtils.generateVideoChannelId(Preferences.getCurrentUser(), targetUser);
        } else {
            channelId = ModelUtils.generateVideoChannelId(targetUser, Preferences.getCurrentUser());
        }

        Intent intent = new Intent(this, VideoChatViewActivity.class);
        intent.putExtra(Constants.USER_MODEL, targetUser);
        intent.putExtra(Constants.EXTRA_VIDEO_CHANNEL_TOKEN, channelId);
        intent.putExtra(Constants.CALLING_VIDEO_OR_AUDIO, callingAudioOrVideo);
        startActivity(intent);
        finish();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onBackPressed() {
        ShowToast.lengthShort(this, "Please click RED button to exit");
    }

    @Override
    public void onNewMessage(Message message) {

    }

    @Override
    public void onTyping(String conversationId, String username, boolean typing) {

    }

    @Override
    public void onRead(String conversationId, String username) {

    }

    @Override
    public void onCall(User targetUser, String typeCalling, String channelId) {

        switch (typeCalling) {
            case Constants.CALLEE_ACCEPT_REQUEST_CALL:
                if (targetUser.getUsername().equals(Preferences.getCurrentUser().getUsername())) {
                    callingSuccessful();
                }

                break;
            case Constants.CALLEE_REJECT_REQUEST_CALL:
                if (targetUser.getUsername().equals(Preferences.getCurrentUser().getUsername())) {
                    setResult(RESULT_CANCELED);
                    finish();
                }

                break;
            case Constants.CALLER_REQUEST_CALLING:
                break;
            case Constants.CALLER_CANCEL_REQUEST:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }

    }

}

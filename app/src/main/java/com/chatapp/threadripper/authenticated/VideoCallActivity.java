package com.chatapp.threadripper.authenticated;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.BaseActivity;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.ShowToast;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionStateCallback;
import com.quickblox.videochat.webrtc.view.QBRTCSurfaceView;

import org.webrtc.EglBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoCallActivity extends BaseMainActivity {

    String TAG = "VIDEO_CALL_LOGCAT";

    CircleImageView cirImgUserAvatar;
    RippleView rvCall, rvCallEnd;
    TextView tvUsername, tvStatus;
    LinearLayout linLayoutCall;


    boolean callerSide; // me, caller or callee
    int qbUserId;
    String username, userAvatar, displayName; // not me, the caller or callee


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();

        initViews();

        setListener();


        // if this is caller, activity start call using QB
        // Call the method start calling from the BaseMainActivity which it extend
        if (callerSide) {
            ArrayList<User> opponents = new ArrayList<>();
            opponents.add(new User(username, displayName, userAvatar, qbUserId));
            this.QB_startCalling(opponents);
        }
    }

    void initViews() {
        cirImgUserAvatar = (CircleImageView) findViewById(R.id.cirImgUserAvatar);
        rvCall = (RippleView) findViewById(R.id.rvCall);
        rvCallEnd = (RippleView) findViewById(R.id.rvCallEnd);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        linLayoutCall = (LinearLayout) findViewById(R.id.linLayoutCall);

        Intent intent = getIntent();
        callerSide = intent.getBooleanExtra(Constants.IS_CALLER_SIDE, false);
        username = intent.getStringExtra(Constants.USER_USERNAME);
        displayName = intent.getStringExtra(Constants.USER_DISPLAY_NAME);
        userAvatar = intent.getStringExtra(Constants.USER_PHOTO_URL);
        qbUserId = intent.getIntExtra(Constants.USER_QB_USERID, 0);

        // Hide icon call (green) when is caller
        if (callerSide) {
            linLayoutCall.setVisibility(View.GONE);
        }

        // Change user info
        tvUsername.setText(displayName);
        ImageLoader.loadUserAvatar(cirImgUserAvatar, userAvatar);
    }

    void setListener() {
        rvCallEnd.setOnRippleCompleteListener(rippleView -> handleEndCalling());
        rvCall.setOnRippleCompleteListener(rippleView -> handleAcceptCalling());
    }

    void handleEndCalling() {
        // TODO
        if (callerSide) {
            // TODO
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    void handleAcceptCalling() {
        setResult(RESULT_OK);
        // TODO
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



    /**
     * -------------------------     QBRTCClientSessionCallbacks     ----------------------------
     */

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer noAnserUserId) {
        Log.d(TAG, "onUserNotAnswer: ");

        finish();
    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer rejectUserId, Map<String, String> rejectUserInfo) {
        Log.d(TAG, "onCallRejectByUser: ");

        String rejectUsername = rejectUserInfo.get(Constants.USER_USERNAME);
        String rejectDisplayName = rejectUserInfo.get(Constants.USER_DISPLAY_NAME);
        String rejectPhotoUrl = rejectUserInfo.get(Constants.USER_PHOTO_URL);

        ShowToast.lengthShort(this, rejectDisplayName + " rejected");
        finish();
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer acceptUserId, Map<String, String> acceptUserInfo) {
        Log.d(TAG, "onCallAcceptByUser: " + acceptUserId);

        String acceptUsername = acceptUserInfo.get(Constants.USER_USERNAME);
        String acceptDisplayName = acceptUserInfo.get(Constants.USER_DISPLAY_NAME);
        String acceptPhotoUrl = acceptUserInfo.get(Constants.USER_PHOTO_URL);

        ShowToast.lengthShort(this, acceptDisplayName + " accepted");
    }
}

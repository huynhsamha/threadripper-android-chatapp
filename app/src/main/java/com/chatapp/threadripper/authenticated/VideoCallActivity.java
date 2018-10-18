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

public class VideoCallActivity extends BaseMainActivity implements
        QBRTCClientSessionCallbacks {

    String TAG = "VIDEO_CALL_LOGCAT";

    CircleImageView cirImgUserAvatar;
    RippleView rvCall, rvCallEnd;
    TextView tvUsername, tvStatus;
    LinearLayout linLayoutCall;


    boolean callerSide; // me, caller or callee
    int qbUserId;
    String username, userAvatar, displayName; // not me, the caller or callee

    /**
     * QB
     */
    QBRTCClient rtcClient;
    QBRTCTypes.QBConferenceType qbConferenceType;


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

        // QB_config();
    }

    void QB_config() {
        QB_addSignalManager();
        QB_initQBRTCClient();

        QB_startSession();
    }

    void QB_addSignalManager() {
        QBChatService.getInstance().getVideoChatWebRTCSignalingManager()
                .addSignalingManagerListener((qbSignaling, createdLocally) -> {
                    if (!createdLocally) {
                        QBRTCClient.getInstance(this).addSignaling(qbSignaling);
                    }
                });
    }

    void QB_initQBRTCClient() {
        // Init RTC Client for this context
        rtcClient = QBRTCClient.getInstance(this);

        // Prepare your activity class to audio/video calls
        rtcClient.addSessionCallbacksListener(this);

        // Notify RTCClient that you are ready to receive calls
        // As soon as your app is ready for calls processing and activity exists
        // Pay attention if you forgot to add signalling manager you will not be able to process calls.
        rtcClient.prepareToProcessCalls();
    }

    void QB_setupViews() {
        QBRTCSurfaceView surfaceView = new QBRTCSurfaceView(this);
        EglBase eglContext = QBRTCClient.getInstance(this).getEglContext();
        surfaceView.init(eglContext.getEglBaseContext(), null);
    }

    void QB_startSession() {
        // Set conference type
        // There are two types of calls:
        // - QB_CONFERENCE_TYPE_VIDEO - for video call;
        // - QB_CONFERENCE_TYPE_AUDIO - for audio call;
        qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

        //Initiate opponents list
        List<Integer> opponents = new ArrayList<Integer>();
        opponents.add(qbUserId);

        // Set user information
        // User can set any string key and value in user info
        // Then retrieve this data from sessions which is returned in callbacks
        // and parse them as he wish
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("key", "value");

        // Init session
        QBRTCSession session =
                QBRTCClient.getInstance(this).createNewSessionWithOpponents(opponents, qbConferenceType);

        // Start call
        session.startCall(userInfo);
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
        rvCallEnd.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                handleEndCalling();
            }
        });

        rvCall.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                handleAcceptCalling();
            }
        });
    }

    void handleEndCalling() {
        // TODO

        finish();
    }

    void handleAcceptCalling() {
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
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {
        // obtain received user info
        Map<String,String> callingUserInfo = qbrtcSession.getUserInfo();

        // Set userInfo
        // User can set any string key and value in user info
        Map<String,String> userInfo = new HashMap<String,String>();
        userInfo.put("Key", "Value");

        // Accept incoming call
        qbrtcSession.acceptCall(userInfo);
    }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {

    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer acceptUserId, Map<String, String> acceptUserInfo) {
        Log.d(TAG, "onCallAcceptByUser: " + acceptUserId);
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {

    }
}

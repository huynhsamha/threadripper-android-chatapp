package com.chatapp.threadripper.authenticated;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chatapp.threadripper.BaseActivity;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.Preferences;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.view.QBRTCSurfaceView;

import org.webrtc.EglBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseMainActivity extends BaseActivity implements
        QBRTCClientSessionCallbacks {

    // This is main activity wrapping all activities after authenticated.
    // use for config QB + Socket...

    String TAG = "BASE_MAIN_ACTIVITY_LOGCAT";


    QBUser qbUser;
    QBChatService qbChatService;
    QBRTCClient qbRTCClient;
    QBRTCSession qbCurrentSession;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QB_config();
    }

    void QB_config() {
        qbUser = Preferences.getCurrentQBUser();
        qbChatService = QBChatService.getInstance();
        qbRTCClient = QBRTCClient.getInstance(this);

        QB_loginChatService();
        QB_initQBRTCClient();
    }

    void QB_loginChatService() {
        if (!qbChatService.isLoggedIn()) {
            qbChatService.login(qbUser, new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {
                    QB_addSignalManager();
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        } else {
            QB_addSignalManager();
        }
    }

    void QB_addSignalManager() {
        qbChatService.getVideoChatWebRTCSignalingManager()
                .addSignalingManagerListener((qbSignaling, createdLocally) -> {
                    if (!createdLocally) {
                        qbRTCClient.addSignaling(qbSignaling);
                    }
                });
    }

    void QB_initQBRTCClient() {
        // Prepare your activity class to audio/video calls
        qbRTCClient.addSessionCallbacksListener(this);

        // Notify RTCClient that you are ready to receive calls
        // As soon as your app is ready for calls processing and activity exists
        // Pay attention if you forgot to add signalling manager you will not be able to process calls.
        qbRTCClient.prepareToProcessCalls();
    }

    void QB_setupViews() {
        QBRTCSurfaceView surfaceView = new QBRTCSurfaceView(this);
        EglBase eglContext = QBRTCClient.getInstance(this).getEglContext();
        surfaceView.init(eglContext.getEglBaseContext(), null);
    }

    // Start Call
    // To call the users you should create a session and start call
    void QB_startCalling(ArrayList<User> usersList) {
        // Set conference type
        // There are two types of calls:
        // - QB_CONFERENCE_TYPE_VIDEO - for video call;
        // - QB_CONFERENCE_TYPE_AUDIO - for audio call;
        QBRTCTypes.QBConferenceType qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

        //Initiate opponents list
        List<Integer> opponents = new ArrayList<Integer>();
        for (User user : usersList) {
            opponents.add(user.getQbUserId());
        }

        // Set user information
        // User can set any string key and value in user info
        // Then retrieve this data from sessions which is returned in callbacks
        // and parse them as he wish
        Map<String, String> userInfo = makeUserInfoMap();

        // Init session
        qbCurrentSession = qbRTCClient.createNewSessionWithOpponents(opponents, qbConferenceType);

        // Start call
        qbCurrentSession.startCall(userInfo);
    }


    /**
     * Parse current user info into Map, used for activities
     * @return Map
     */
    Map<String, String> makeUserInfoMap() {
        Map<String, String> userInfo = new HashMap<>();

        userInfo.put(Constants.USER_USERNAME, Preferences.getUsername());
        userInfo.put(Constants.USER_DISPLAY_NAME, Preferences.getDisplayName());
        userInfo.put(Constants.USER_PHOTO_URL, Preferences.getUserAvatar());

        return userInfo;
    }






    /**
     * ------------------------      onActivityResult     -------------------------
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Usecase: A call B, used for B
         * A->B, B receive on BaseMain -> start VideoCall -> result code to BaseMain
         */
        if (requestCode == Constants.REQUEST_CODE_RECEIVE_CALLING) {
            Map<String, String> userInfo = makeUserInfoMap();
            if (resultCode == RESULT_OK) {
                qbCurrentSession.acceptCall(userInfo);
            } else {
                qbCurrentSession.rejectCall(userInfo);
                qbCurrentSession = null;
            }
        }
    }






    /**
     * -------------------------     QBRTCClientSessionCallbacks     ----------------------------
     */
    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {
        Log.d(TAG, "onReceiveNewSession: ");

        // Set userInfo
        Map<String, String> userInfo = makeUserInfoMap();

        // current user is busy with other session
        if (qbCurrentSession != null) {
            qbrtcSession.rejectCall(userInfo);
            return;
        }

        // obtain received user info
        Map<String, String> callingUserInfo = qbrtcSession.getUserInfo();

        String callingUsername = callingUserInfo.get(Constants.USER_USERNAME);
        String callingDisplayName = callingUserInfo.get(Constants.USER_DISPLAY_NAME);
        String callingPhotoUrl = callingUserInfo.get(Constants.USER_PHOTO_URL);
        int callingQBUserId = qbrtcSession.getCallerID();

        Intent intent = new Intent(this, VideoCallActivity.class);
        intent.putExtra(Constants.IS_CALLER_SIDE, false); // user who receive a calling
        intent.putExtra(Constants.USER_USERNAME, callingUsername);
        intent.putExtra(Constants.USER_DISPLAY_NAME, callingDisplayName);
        intent.putExtra(Constants.USER_PHOTO_URL, callingPhotoUrl);
        intent.putExtra(Constants.USER_QB_USERID, callingQBUserId);

        qbCurrentSession = qbrtcSession;

        startActivityForResult(intent, Constants.REQUEST_CODE_RECEIVE_CALLING);
    }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {
        Log.d(TAG, "onUserNoActions: ");
    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {
        Log.d(TAG, "onSessionStartClose: ");
    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
        Log.d(TAG, "onUserNotAnswer: ");
    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        Log.d(TAG, "onCallRejectByUser: ");
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer acceptUserId, Map<String, String> acceptUserInfo) {
        Log.d(TAG, "onCallAcceptByUser: " + acceptUserId);
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        Log.d(TAG, "onReceiveHangUpFromUser: ");
    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {
        Log.d(TAG, "onSessionClosed: ");
    }
}

package com.chatapp.threadripper.authenticated;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.chatapp.threadripper.R;
import com.chatapp.threadripper.utils.Constants;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

import io.agora.rtc.video.VideoEncoderConfiguration; // 2.3.0 and later

public class VideoChatViewActivity extends AppCompatActivity {

    private static final String LOG_TAG = "VideoChatViewActivity";

    private static final int PERMISSION_REQ_ID = 22;

    // permission WRITE_EXTERNAL_STORAGE is not mandatory for Agora RTC SDK, just incase if you wanna save logs to external sdcard
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private RtcEngine mRtcEngine;

    private boolean videoMode;
    private String channel;

    private enum LocalViewSize {NORMAL, BIG, SMALL};
    private LocalViewSize localViewSize = LocalViewSize.NORMAL;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(() -> onRemoteUserLeft());
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            runOnUiThread(() -> onRemoteUserVideoMuted(uid, muted));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat_view);

        if (!(checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)))
            return;


        setupAgoraEngine();
    }

    private void getIntentData(){
        Intent intent = getIntent();
        this.channel = intent.getStringExtra(Constants.EXTRA_VIDEO_CHANNEL_TOKEN);

        if (this.channel == null || this.channel.isEmpty()) {
            finish();
            return;
        }

        this.videoMode = decodeVideoMode(this.channel);
        this.channel = decodeChannel(this.channel);
    }

    private boolean decodeVideoMode(String encodedText) {
        return encodedText.substring(encodedText.length() - 1).equals("1");
    }

    private String decodeChannel(String text) {
        return text.substring(0, text.length() - 1);
    }

    private void enableVideoMode(boolean enable) {
        ImageView audioVideoImg = (ImageView) findViewById(R.id.audioVideoImg);
        View changeCameraImg = findViewById(R.id.changeCameraImg);
        View muteLocalVideoImg = findViewById(R.id.muteLocalVideoImg);
        FrameLayout localFrame = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView localSurfaceView = (SurfaceView) localFrame.getChildAt(0);
        FrameLayout remoteFrame = (FrameLayout) findViewById(R.id.remote_video_view_container);
        SurfaceView remoteSurfaceView = (SurfaceView) remoteFrame.getChildAt(0);

        int visibility = enable ? View.VISIBLE : View.GONE;
        changeCameraImg.setVisibility(visibility);
        muteLocalVideoImg.setVisibility(visibility);
        localFrame.setVisibility(visibility);

        if (localSurfaceView != null)
            localSurfaceView.setVisibility(visibility);

        if (remoteSurfaceView != null)
            remoteSurfaceView.setVisibility(visibility);

        if (enable) {
            mRtcEngine.enableVideo();
            audioVideoImg.setImageResource(R.drawable.videocall);
        }
        else {
            mRtcEngine.disableVideo();
            audioVideoImg.setImageResource(R.drawable.audiocall);
        }
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    REQUESTED_PERMISSIONS,
                    requestCode);
            return false;
        }
        return true;
    }

    private void setupAgoraEngine() {
        getIntentData();
        initializeAgoraEngine();
        setupVideoProfile();
        setupLocalVideo();

        enableVideoMode(this.videoMode);
        joinChannel(this.channel);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO + "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    finish();
                    break;
                }

                setupAgoraEngine();
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }

    public void onLocalVideoMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalVideoStream(iv.isSelected());

        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        surfaceView.setZOrderMediaOverlay(!iv.isSelected());
        surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);
    }

    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    public void onEncCallClicked(View view) {
        finish();
    }

    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();

        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x360, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        final FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        final SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }

    private void joinChannel(String channel) {
        mRtcEngine.joinChannel(null, channel, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));

        surfaceView.setTag(uid); // for mark purpose
    }

    private void leaveChannel() {
        if (mRtcEngine != null)
            mRtcEngine.leaveChannel();
    }

    private void onRemoteUserLeft() {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        container.removeAllViews();
    }

    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);

        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }

    public void onAudioVideoChangeClick(View view) {
        this.videoMode = !this.videoMode;
        this.enableVideoMode(this.videoMode);
    }

    public void onLocalVideoClick(View view) {
//        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.local_video_view_container);
        FrameLayout frameLayout = (FrameLayout) view;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();

        if (this.localViewSize == LocalViewSize.NORMAL) { // normal view to smaller view
            this.localViewSize = LocalViewSize.SMALL;
            params.height /= 2;
            params.width /= 2;
        }
        else if (this.localViewSize == LocalViewSize.SMALL) { // smaller view to larger view
            this.localViewSize = LocalViewSize.BIG;
            params.height *= 3;
            params.width *= 3;
        }
        else {
            this.localViewSize = LocalViewSize.NORMAL;
            params.height /= 1.5;
            params.width /= 1.5;
        }
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_END, R.id.remote_video_view_container);
        params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.remote_video_view_container);


        frameLayout.setLayoutParams(params);
    }
}

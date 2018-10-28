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
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.ShowToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoCallActivity extends BaseMainActivity {

    CircleImageView cirImgUserAvatar;
    RippleView rvCall, rvCallEnd;
    TextView tvUsername, tvStatus;
    LinearLayout linLayoutCall;


    boolean callerSide, callingAudioOrVideo; // me, caller or callee
    String username, userAvatar, displayName, channelId; // not me, the caller or callee


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
        channelId = intent.getStringExtra(Constants.EXTRA_VIDEO_CHANNEL_TOKEN);
        callingAudioOrVideo = intent.getBooleanExtra(Constants.CALLING_VIDEO_OR_AUDIO, false); // default is call audio

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
            setResult(RESULT_CANCELED);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    void handleAcceptCalling() {
        // TODO
        // Generate token to both size
        // start VideoChatViewActivity with putExtra EXTRA_CHANNEL_TOKEN
        Intent intent = new Intent(this, VideoChatViewActivity.class);
        intent.putExtra(Constants.EXTRA_VIDEO_CHANNEL_TOKEN, channelId);
        intent.putExtra(Constants.CALLING_VIDEO_OR_AUDIO, callingAudioOrVideo);
        startActivity(intent);
        finish();

        if (callerSide) {

        } else {

        }

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
}

package com.chatapp.threadripper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.chatapp.threadripper.authenticated.LayoutFragmentActivity;
import com.chatapp.threadripper.authentication.LoginActivity;
import com.chatapp.threadripper.utils.Preferences;
import com.quickblox.auth.session.QBSettings;

import static com.chatapp.threadripper.api.QBConfig.ACCOUNT_KEY;
import static com.chatapp.threadripper.api.QBConfig.APP_ID;
import static com.chatapp.threadripper.api.QBConfig.AUTH_KEY;
import static com.chatapp.threadripper.api.QBConfig.AUTH_SECRET;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();

        configQBAuthentication();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Preferences.isIsConnected()) {
                    startActivity(new Intent(Splash.this, LayoutFragmentActivity.class));
                } else {
                    startActivity(new Intent(Splash.this, LoginActivity.class));
                }
                finish();
            }
        }, 1 * 1000);
    }

    void configQBAuthentication() {
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
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
    //    do nothing
    }
}

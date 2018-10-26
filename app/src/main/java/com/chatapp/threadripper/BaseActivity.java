package com.chatapp.threadripper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.authentication.SignUpActivity;
import com.chatapp.threadripper.receivers.NetworkChangeReceiver;
import com.chatapp.threadripper.utils.KeyboardUtils;
import com.chatapp.threadripper.utils.ShowToast;
import com.chatapp.threadripper.utils.SweetDialog;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class BaseActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    TextView title;
    ImageView btnImgBack;

    boolean doubleBackToExitPressedOnce = false;
    private BroadcastReceiver mNetworkReceiver;

    private static TextView tvCheckConnection;





    // Utils for Network detect
    protected void initDetectNetworkStateChange() {
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();
        tvCheckConnection = (TextView) findViewById(R.id.tvCheckConnection);
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            // Update: Don't need send leave message
            // On Destroy (closing app), leave socket => status online = false
            // SocketManager.getInstance().leave();

            // CacheService.getInstance().close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        unregisterNetworkChanges();
        super.onDestroy();
    }

    public static void dialogStateConnectionChanged(boolean value){
        if (value) {
            tvCheckConnection.setText("Connection is established");
            tvCheckConnection.setBackgroundColor(Color.rgb(83, 185, 15));
            tvCheckConnection.setTextColor(Color.WHITE);

            Handler handler = new Handler();
            Runnable delayRunnable = () -> tvCheckConnection.setVisibility(View.GONE);
            handler.postDelayed(delayRunnable, 2500);

        } else {
            tvCheckConnection.setVisibility(View.VISIBLE);
            tvCheckConnection.setText("Could not connect to internet");
            tvCheckConnection.setBackgroundColor(Color.RED);
            tvCheckConnection.setTextColor(Color.WHITE);
        }
    }




    // Utils for Toolbars
    public final void changeTitle(int toolbarId, String titlePage) {
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);

        title = (TextView) toolbar.findViewById(R.id.tv_title);
        title.setText(titlePage);

        getSupportActionBar().setTitle("");
    }

    public final void setupToolbar(int toolbarId, String titlePage) {
        changeTitle(toolbarId, titlePage);
    }

    public void setupToolbarWithBackButton(int toolbarId, String titlePage) {
        setupToolbar(toolbarId, titlePage);

        btnImgBack = (ImageView) findViewById(R.id.btnImgBack);
        btnImgBack.setVisibility(View.VISIBLE);
        btnImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }





    // Utils for Fonts
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



    // Utils for back press
    public void setupDoubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        ShowToast.lengthShort(this, "Please click BACK again to exit");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }




    // Utils for Keyboards
    public void hideSoftKeyboard() {
        KeyboardUtils.hideSoftKeyboard(this);
    }

    public void configHideKeyboardOnTouchOutsideEditText(View wrapperView) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(wrapperView instanceof EditText)) {
            wrapperView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (wrapperView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) wrapperView).getChildCount(); i++) {
                View innerView = ((ViewGroup) wrapperView).getChildAt(i);
                configHideKeyboardOnTouchOutsideEditText(innerView);
            }
        }
    }



    // Utils for error action
    public void ShowErrorDialog(String message) {
        SweetDialog.showErrorMessage(this, "Error", message);
    }


}

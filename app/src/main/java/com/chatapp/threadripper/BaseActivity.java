package com.chatapp.threadripper;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatapp.threadripper.authentication.SignUpActivity;
import com.chatapp.threadripper.utils.KeyboardUtils;
import com.chatapp.threadripper.utils.ShowToast;
import com.chatapp.threadripper.utils.SweetDialog;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class BaseActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    TextView title;
    ImageView btnImgBack;

    boolean doubleBackToExitPressedOnce = false;


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


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

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

    public void ShowErrorDialog(String message) {
        SweetDialog.showErrorMessage(this, "Error", message);
    }

}

package com.chatapp.threadripper.authenticated;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.BaseActivity;
import com.chatapp.threadripper.R;

public class SettingsActivity extends BaseActivity {

    RippleView rvToggleEditUsername, rvChangeUserAvatar, rvAcceptChangedUsername, rvCancelChangedUsername, rvBtnBack;
    EditText edtUsername, edtOldPassword, edtPassword, edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        setListeners();
    }

    void initViews() {
        rvToggleEditUsername = (RippleView) findViewById(R.id.rvToggleEditUsername);
        rvChangeUserAvatar = (RippleView) findViewById(R.id.rvChangeUserAvatar);
        rvAcceptChangedUsername = (RippleView) findViewById(R.id.rvAcceptChangedUsername);
        rvCancelChangedUsername = (RippleView) findViewById(R.id.rvCancelChangedUsername);
        rvBtnBack = (RippleView) findViewById(R.id.rvBtnBack);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtOldPassword = (EditText) findViewById(R.id.edtOldPassword);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);

        edtUsername.setInputType(InputType.TYPE_NULL);
    }

    void setListeners() {
        rvBtnBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                onBackPressed();
            }
        });

        rvToggleEditUsername.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                edtUsername.setInputType(InputType.TYPE_CLASS_TEXT);
                rvAcceptChangedUsername.setVisibility(View.VISIBLE);
                rvCancelChangedUsername.setVisibility(View.VISIBLE);
                rvToggleEditUsername.setVisibility(View.GONE);

                edtUsername.requestFocus();
            }
        });

        rvAcceptChangedUsername.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                edtUsername.setInputType(InputType.TYPE_NULL);
                rvAcceptChangedUsername.setVisibility(View.GONE);
                rvCancelChangedUsername.setVisibility(View.GONE);
                rvToggleEditUsername.setVisibility(View.VISIBLE);
            }
        });

        rvCancelChangedUsername.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                edtUsername.setInputType(InputType.TYPE_NULL);
                rvAcceptChangedUsername.setVisibility(View.GONE);
                rvCancelChangedUsername.setVisibility(View.GONE);
                rvToggleEditUsername.setVisibility(View.VISIBLE);
            }
        });
    }
}

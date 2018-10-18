package com.chatapp.threadripper.authenticated;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiResponseData;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.authentication.SignUpActivity;
import com.chatapp.threadripper.utils.Preferences;
import com.chatapp.threadripper.utils.ShowToast;
import com.chatapp.threadripper.utils.SweetDialog;

public class SettingsActivity extends BaseMainActivity {

    RippleView rvToggleEditUsername, rvChangeUserAvatar, rvAcceptChangedUsername, rvCancelChangedUsername, rvBtnBack;
    EditText edtDisplayName, edtOldPassword, edtPassword, edtConfirmPassword;
    TextView tvUsername, tvEmail;
    Button btnChangePassword;

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
        edtDisplayName = (EditText) findViewById(R.id.edtDisplayName);
        edtOldPassword = (EditText) findViewById(R.id.edtOldPassword);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);

        edtDisplayName.setInputType(InputType.TYPE_NULL);

        try {
            tvUsername.setText(Preferences.getCurrentUser().getUsername());
            tvEmail.setText(Preferences.getCurrentUser().getEmail());
            edtDisplayName.setText(Preferences.getCurrentUser().getDisplayName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setListeners() {
        rvBtnBack.setOnRippleCompleteListener(rippleView -> onBackPressed());

        rvToggleEditUsername.setOnRippleCompleteListener(rippleView -> {
            edtDisplayName.setInputType(InputType.TYPE_CLASS_TEXT);
            rvAcceptChangedUsername.setVisibility(View.VISIBLE);
            rvCancelChangedUsername.setVisibility(View.VISIBLE);
            rvToggleEditUsername.setVisibility(View.GONE);

            edtDisplayName.requestFocus();
        });

        rvAcceptChangedUsername.setOnRippleCompleteListener(rippleView -> {
            edtDisplayName.setInputType(InputType.TYPE_NULL);
            rvAcceptChangedUsername.setVisibility(View.GONE);
            rvCancelChangedUsername.setVisibility(View.GONE);
            rvToggleEditUsername.setVisibility(View.VISIBLE);
        });

        rvCancelChangedUsername.setOnRippleCompleteListener(rippleView -> {
            edtDisplayName.setInputType(InputType.TYPE_NULL);
            rvAcceptChangedUsername.setVisibility(View.GONE);
            rvCancelChangedUsername.setVisibility(View.GONE);
            rvToggleEditUsername.setVisibility(View.VISIBLE);
        });

        btnChangePassword.setOnClickListener(view -> handleChangePassword());
    }

    void validateForm(String oldPassword, String password, String confirmPassword) throws Exception {
        if (oldPassword.isEmpty()) throw new Exception("Old password can't be empty");
        if (password.isEmpty()) throw new Exception("New password can't be empty");
        if (confirmPassword.equals(password) == false)
            throw new Exception("Confirm password isn't match");
    }


    void handleChangePassword() {
        String oldPassword = edtOldPassword.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        try {
            validateForm(oldPassword, password, confirmPassword);
        } catch (Exception e) {
            ShowToast.lengthShort(this, e.getMessage());
            return;
        }

        SweetDialog.showLoading(this);

        ApiService.getInstance().changePassword(oldPassword, password).addCallbackListener(new ApiService.CallbackApiListener() {
            @Override
            public void onSuccess(ApiResponseData data) {
                SweetDialog.hideLoading();

                if (data.getError() != null) {
                    String errorMessage = data.getError().getMessage();
                    SweetDialog.showErrorMessage(SettingsActivity.this, "Error", errorMessage);
                } else {
                    SweetDialog.showSuccessMessage(SettingsActivity.this, "Successful",
                            "Password has been changed successfully.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                SweetDialog.hideLoading();
                SweetDialog.showErrorMessage(SettingsActivity.this, "Error", t.getMessage());
            }
        });
    }
}

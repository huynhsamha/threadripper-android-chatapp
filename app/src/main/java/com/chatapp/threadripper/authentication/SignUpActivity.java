package com.chatapp.threadripper.authentication;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.BaseActivity;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiResponseData;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.models.ErrorResponse;
import com.chatapp.threadripper.utils.ShowToast;
import com.chatapp.threadripper.utils.SweetDialog;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends BaseActivity {

    Button btnSignUp;
    RippleView btnBack;
    EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword, edtDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();

        initViews();

        configHideKeyboardOnTouchOutsideEditText(findViewById(R.id.wrapperView));

        initDetectNetworkStateChange();
    }

    private void initViews() {
        edtUsername = (EditText) findViewById(R.id.edtDisplayName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        edtDisplayName = (EditText) findViewById(R.id.edtDisplayName);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(view -> handleSignUp());

        btnBack = (RippleView) findViewById(R.id.btnBack);
        btnBack.setOnRippleCompleteListener(rippleView -> onBackPressed());
    }

    void validateForm(String username, String email, String displayName, String password, String confirmPassword) throws Exception {
        if (username.isEmpty()) throw new Exception("Username can't be empty");
        if (email.isEmpty()) throw new Exception("Email can't be empty");
        if (displayName.isEmpty()) throw new Exception("Display name can't be empty");
        if (password.isEmpty()) throw new Exception("Password can't be empty");
        if (!confirmPassword.equals(password))
            throw new Exception("Confirm password isn't match");
    }

    void handleSignUp() {
        String username = edtUsername.getText().toString();
        String email = edtEmail.getText().toString();
        String displayName = edtDisplayName.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        try {
            validateForm(username, email, displayName, password, confirmPassword);
        } catch (Exception e) {
            ShowToast.lengthShort(this, e.getMessage());
            return;
        }

        SweetDialog.showLoading(this);

        ApiService.getInstance().signUp(username, email, displayName, password).enqueue(new Callback<ApiResponseData>() {
            @Override
            public void onResponse(Call<ApiResponseData> call, Response<ApiResponseData> response) {
                SweetDialog.hideLoading();

                if (response.isSuccessful()) {
                    ApiResponseData data = response.body();
                    SweetDialog.showSuccessMessage(SignUpActivity.this, "Successful",
                            "Please check your email to verify and active account",
                            new SweetDialog.OnCallbackListener() {
                                @Override
                                public void onConfirm() {
                                    finish();
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        SignUpActivity.this.ShowErrorDialog(err.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseData> call, Throwable t) {
                SweetDialog.hideLoading();
                try {
                    SignUpActivity.this.ShowErrorDialog(t.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}

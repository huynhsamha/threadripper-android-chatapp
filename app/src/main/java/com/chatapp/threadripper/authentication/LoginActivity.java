package com.chatapp.threadripper.authentication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chatapp.threadripper.BaseActivity;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiResponseData;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.authenticated.LayoutFragmentActivity;
import com.chatapp.threadripper.models.ErrorResponse;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Preferences;
import com.chatapp.threadripper.utils.ShowToast;
import com.chatapp.threadripper.utils.SweetDialog;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    Button btnLogin;
    TextView tvSignUp, tvForgot;
    EditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();

        initViews();

        configHideKeyboardOnTouchOutsideEditText(findViewById(R.id.wrapperView));

        initDetectNetworkStateChange();
    }


    void validateForm(String username, String password) throws Exception {
        if (username.isEmpty()) throw new Exception("Username can't be empty");
        if (password.isEmpty()) throw new Exception("Password can't be empty");
    }

    void handleLogin() {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        try {
            validateForm(username, password);
        } catch (Exception e) {
            ShowToast.lengthShort(this, e.getMessage());
            return;
        }

        SweetDialog.showLoading(this);

        ApiService.getInstance().login(username, password).enqueue(new Callback<ApiResponseData>() {
            @Override
            public void onResponse(Call<ApiResponseData> call, Response<ApiResponseData> response) {
                SweetDialog.hideLoading();

                if (response.isSuccessful()) {
                    ApiResponseData data = response.body();
                    Preferences.setCurrentUser(data.getUser());
                    safetyUserInformation();

                    // Store Authorization Token
                    String chatAuthToken = response.headers().get("Authorization");
                    if (chatAuthToken != null && chatAuthToken.contains("CHAT")) {
                        Preferences.setChatAuthToken(chatAuthToken);

                        // Update cache
                        CacheService.getInstance().syncPreferencesInCache();
                    }

                    startActivity(new Intent(LoginActivity.this, LayoutFragmentActivity.class));
                    finish();

                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        LoginActivity.this.ShowErrorDialog(err.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();

                        // For Test
                        // startActivity(new Intent(LoginActivity.this, LayoutFragmentActivity.class));
                        // finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseData> call, Throwable t) {
                SweetDialog.hideLoading();

                try {
                    LoginActivity.this.ShowErrorDialog(t.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // For Test
                // startActivity(new Intent(LoginActivity.this, LayoutFragmentActivity.class));
                // finish();
            }
        });
    }

    void safetyUserInformation() {
        User user = Preferences.getCurrentUser();

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            user.setUsername("default"); // Fucking error !!!
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            user.setEmail("default@threadripper.com"); // Fucking error !!!
        }
        if (user.getDisplayName() == null || user.getDisplayName().isEmpty()) {
            user.setDisplayName("Default Display Name");
        }
        if (user.getPhotoUrl() == null || user.getPhotoUrl().isEmpty()) {
            user.setPhotoUrl("http://abc.com/xyz.png");
        }
    }

    private void initViews() {
        edtUsername = (EditText) findViewById(R.id.edtDisplayName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> handleLogin());

        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

        tvForgot = (TextView) findViewById(R.id.tvForgot);
        tvForgot.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
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
        // Double back to exit
        this.setupDoubleBackToExit();
    }
}

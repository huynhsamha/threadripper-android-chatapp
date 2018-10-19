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
import com.chatapp.threadripper.api.Config;
import com.chatapp.threadripper.authenticated.LayoutFragmentActivity;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Preferences;
import com.chatapp.threadripper.utils.ShowToast;
import com.chatapp.threadripper.utils.SweetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class LoginActivity extends BaseActivity {

    Button btnLogin;
    TextView tvSignUp, tvForgot;
    EditText edtUsername, edtPassword;

    StompClient client;

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

        // setupWebSocket();
    }


    void setupWebSocket() {
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Config.WEB_SOCKET_FULL_PATH);

        client.topic("/topic/public").subscribe(message -> {
            String str = message.getPayload();
            JSONObject json = null;
            try {
                json = new JSONObject(str);
                String type = json.getString("type");
                if (type.equals("JOIN")) {
                    client.disconnect();
                    startActivity(new Intent(LoginActivity.this, LayoutFragmentActivity.class));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                btnLogin.setEnabled(true);
            }
        });

        client.connect();
    }

    void validateForm(String username, String password) throws Exception {
        if (username.isEmpty()) throw new Exception("Username can't be empty");
        if (password.isEmpty()) throw new Exception("Password can't be empty");
    }

    void handleLogin() {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        try {
            validateForm(username,  password);
        } catch (Exception e) {
            ShowToast.lengthShort(this, e.getMessage());
            return;
        }

        SweetDialog.showLoading(this);

        ApiService.getInstance().login(username, password).addCallbackListener(new ApiService.CallbackApiListener() {
            @Override
            public void onSuccess(ApiResponseData data) {
                SweetDialog.hideLoading();

                if (data.getError() != null) {
                    String errorMessage = data.getError().getMessage();
                    SweetDialog.showErrorMessage(LoginActivity.this, "Error", errorMessage);
                } else {
                    Preferences.setCurrentUser(data.getUser());
                    safetyUserInformation();

                    startActivity(new Intent(LoginActivity.this, LayoutFragmentActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                SweetDialog.hideLoading();
                SweetDialog.showErrorMessage(LoginActivity.this, "Error", t.getMessage());
            }
        });

        // JSONObject json = new JSONObject();
        //
        // try {
        //     json.put("sender", username);
        //     json.put("type", "JOIN");
        // } catch (JSONException e) {
        //     e.printStackTrace();
        // }
        //
        // client.send("/app/chat.addUser", json.toString()).subscribe(
        //         () -> Log.d("Login", "Sent data!"),
        //         error -> Log.e("Login", "Encountered error while sending data!", error)
        // );
        //
        // btnLogin.setEnabled(false);
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

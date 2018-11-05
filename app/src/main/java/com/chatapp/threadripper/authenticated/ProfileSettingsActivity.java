package com.chatapp.threadripper.authenticated;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiResponseData;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.models.ErrorResponse;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.FileUtils;
import com.chatapp.threadripper.utils.ImageFilePath;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.Preferences;
import com.chatapp.threadripper.utils.ShowToast;
import com.chatapp.threadripper.utils.SweetDialog;
import com.chatapp.threadripper.utils.TargetPrompt;
import com.google.gson.Gson;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSettingsActivity extends BaseMainActivity {

    RippleView rvToggleEditUsername, rvChangeUserAvatar, rvAcceptChangedUsername, rvCancelChangedUsername, rvBtnBack;
    EditText edtDisplayName, edtOldPassword, edtPassword, edtConfirmPassword;
    TextView tvUsername, tvEmail;
    Button btnChangePassword;
    CircleImageView cirImgUserAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        initViews();
        setListeners();

        configHideKeyboardOnTouchOutsideEditText(findViewById(R.id.wrapperView));

        initDetectNetworkStateChange();

        checkRunWalkThrough();
    }

    void checkRunWalkThrough() {
        if (Preferences.isFirstUseProfileSettings()) {
            showWalkThroughChangeAvatar(() -> showWalkThroughChangePassword(() -> {
                Preferences.setFirstUseProfileSettings(false);
                CacheService.getInstance().syncPreferencesInCache();
            }));
        }
    }

    interface SimpleCallback {
        void onComplete();
    }

    void showWalkThroughChangeAvatar(SimpleCallback cb) {
        TargetPrompt.prompt(this, R.id.wrapperAvatar,
                "Change your avatar",
                "Tap to select or capture image for change your avatar",
                new TargetPrompt.OnCallbackListener() {
                    @Override
                    public void onAccepted() {
                        cb.onComplete();
                    }

                    @Override
                    public void onDenied() {
                        cb.onComplete();
                    }
                });
    }

    void showWalkThroughChangePassword(SimpleCallback cb) {
        TargetPrompt.prompt(this, R.id.btnChangePassword,
                "Change password",
                "You can change your current password with a new password",
                new TargetPrompt.OnCallbackListener() {
                    @Override
                    public void onAccepted() {
                        cb.onComplete();
                    }

                    @Override
                    public void onDenied() {
                        cb.onComplete();
                    }
                });

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
        cirImgUserAvatar = (CircleImageView) findViewById(R.id.cirImgUserAvatar);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvUsername = (TextView) findViewById(R.id.tvUsername);


        edtDisplayName.setInputType(InputType.TYPE_NULL);
        edtDisplayName.setCursorVisible(false);

        tvUsername.setText(Preferences.getCurrentUser().getUsername());
        tvEmail.setText(Preferences.getCurrentUser().getEmail());
        edtDisplayName.setText(Preferences.getCurrentUser().getDisplayName());

        ImageLoader.loadUserAvatar(cirImgUserAvatar, Preferences.getCurrentUser().getPhotoUrl());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            rvChangeUserAvatar.setVisibility(View.GONE); // not support capture image with API < 23
        }
    }

    void setListeners() {
        rvBtnBack.setOnRippleCompleteListener(rippleView -> onBackPressed());

        rvToggleEditUsername.setOnRippleCompleteListener(rippleView -> {
            edtDisplayName.setInputType(InputType.TYPE_CLASS_TEXT);
            edtDisplayName.setCursorVisible(true);
            rvAcceptChangedUsername.setVisibility(View.VISIBLE);
            rvCancelChangedUsername.setVisibility(View.VISIBLE);
            rvToggleEditUsername.setVisibility(View.GONE);

            edtDisplayName.requestFocus();
        });

        rvAcceptChangedUsername.setOnRippleCompleteListener(rippleView -> {
            edtDisplayName.setInputType(InputType.TYPE_NULL);
            edtDisplayName.setCursorVisible(false);
            rvAcceptChangedUsername.setVisibility(View.GONE);
            rvCancelChangedUsername.setVisibility(View.GONE);
            rvToggleEditUsername.setVisibility(View.VISIBLE);

            handleAcceptChangeDisplayName();
        });

        rvCancelChangedUsername.setOnRippleCompleteListener(rippleView -> {
            edtDisplayName.setInputType(InputType.TYPE_NULL);
            edtDisplayName.setCursorVisible(false);
            rvAcceptChangedUsername.setVisibility(View.GONE);
            rvCancelChangedUsername.setVisibility(View.GONE);
            rvToggleEditUsername.setVisibility(View.VISIBLE);

            handleCancelChangeDisplayName();
        });

        rvChangeUserAvatar.setOnRippleCompleteListener(view -> handleChangeAvatar());

        btnChangePassword.setOnClickListener(view -> handleChangePassword());
    }

    void validateForm(String oldPassword, String password, String confirmPassword) throws Exception {
        if (oldPassword.isEmpty()) throw new Exception("Old password can't be empty");
        if (password.isEmpty()) throw new Exception("New password can't be empty");
        if (!confirmPassword.equals(password))
            throw new Exception("Confirm password isn't match");
    }

    void handleAcceptChangeDisplayName() {
        String displayName = edtDisplayName.getText().toString();
        if (displayName.isEmpty()) return;
        Preferences.getCurrentUser().setDisplayName(displayName);
        // TODO: Call API to updateFromServer user profile
    }

    void handleCancelChangeDisplayName() {
        try {
            edtDisplayName.setText(Preferences.getCurrentUser().getDisplayName());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        ApiService.getInstance().changePassword(oldPassword, password).enqueue(new Callback<ApiResponseData>() {
            @Override
            public void onResponse(Call<ApiResponseData> call, Response<ApiResponseData> response) {
                SweetDialog.hideLoading();

                if (response.isSuccessful()) {
                    ApiResponseData data = response.body();
                    SweetDialog.showSuccessMessage(ProfileSettingsActivity.this, "Successful",
                            "Password has been changed successfully.");

                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        ProfileSettingsActivity.this.ShowErrorDialog(err.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseData> call, Throwable t) {
                SweetDialog.hideLoading();
                ProfileSettingsActivity.this.ShowErrorDialog(t.getMessage());
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
        // >= 23
    void handleCaptureCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CODE_PERMISSION_IMAGE_CAPTURE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, Constants.REQUEST_CODE_CAPTURE_IMAGE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
        // >= 23
    void handleSelectImage() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_PERMISSION_READ_EXTERNAL);
            return;
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_PERMISSION_WRITE_EXTERNAL);
            return;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), Constants.REQUEST_CODE_PICK_IMAGE);
    }

    void handleChangeAvatar() {
        SweetDialog.showOption(this, "Choose avatar",
                "Would you like to get a photo from camera or gallery?", "Camera", "Gallery",
                new SweetDialog.OnCallbackOptionsListener() {
                    @Override
                    public void onSelectOption1() {
                        handleCaptureCamera();
                    }

                    @Override
                    public void onSelectOption2() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            handleSelectImage();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE_PERMISSION_IMAGE_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleCaptureCamera();
            } else {
                // the fucking user!!!
                ShowToast.lengthLong(this, "Camera permission is denied");
            }
        }

        if (requestCode == Constants.REQUEST_CODE_PERMISSION_READ_EXTERNAL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    handleSelectImage();
                }
            } else {
                // the fucking user!!!
                ShowToast.lengthLong(this, "Read external storage permission is denied");
            }
        }

        if (requestCode == Constants.REQUEST_CODE_PERMISSION_WRITE_EXTERNAL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    handleSelectImage();
                }
            } else {
                // the fucking user!!!
                ShowToast.lengthLong(this, "Write external storage permission is denied");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                // ShowToast.lengthShort(this, "OK");
                handlePickImageSuccess(data);
            }
        } else if (requestCode == Constants.REQUEST_CODE_CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                handleCaptureImageSuccess(data);
            }
        }
    }


    void handlePickImageSuccess(Intent data) {
        Uri uri = data.getData();
        ImageLoader.loadImageChatMessage(cirImgUserAvatar, uri.toString());

        // Call API to updateFromServer avatar
        try {
            String realFilePath = ImageFilePath.getPath(ProfileSettingsActivity.this, data.getData());
            File file = new File(realFilePath);
            postAvatarToServerWithFile(file);

        } catch (Exception err) {
            err.printStackTrace();
            ProfileSettingsActivity.this.ShowErrorDialog(err.getMessage());
        }
    }

    void handleCaptureImageSuccess(Intent data) {
        Bitmap bitmapCaptureImage = (Bitmap) data.getExtras().get("data");
        cirImgUserAvatar.setImageBitmap(bitmapCaptureImage);

        // Call API to updateFromServer avatar
        try {
            File file = FileUtils.bitmap2File(this, bitmapCaptureImage);
            postAvatarToServerWithFile(file);

        } catch (Exception err) {
            err.printStackTrace();
            ProfileSettingsActivity.this.ShowErrorDialog(err.getMessage());
        }
    }

    void postAvatarToServerWithFile(File file) {
        ApiService.getInstance().changeUserAvatar(file).enqueue(new Callback<ApiResponseData>() {
            @Override
            public void onResponse(Call<ApiResponseData> call, Response<ApiResponseData> response) {
                if (response.isSuccessful()) {
                    ApiResponseData data = response.body();
                    String newAvatarUrl = data.getAvatarUrl();
                    Preferences.getCurrentUser().setPhotoUrl(newAvatarUrl);

                    // updateFromServer cache
                    CacheService.getInstance().syncPreferencesInCache();
                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        ProfileSettingsActivity.this.ShowErrorDialog(err.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseData> call, Throwable t) {
                try {
                    ProfileSettingsActivity.this.ShowErrorDialog(t.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

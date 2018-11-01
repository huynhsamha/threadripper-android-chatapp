package com.chatapp.threadripper.authenticated;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoViewActivity extends BaseMainActivity {

    PhotoView photoView;
    ImageButton btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();

        photoView = (PhotoView) findViewById(R.id.photoView);
        btnClose = (ImageButton) findViewById(R.id.btnClose);

        btnClose.setOnClickListener(view -> onBackPressed());

        String url = getIntent().getStringExtra(Constants.CHAT_IMAGE_URL);
        if (url != null) {
            ImageLoader.loadPhotoView(photoView, url);
        } else {
            Bitmap bitmap = getIntent().getParcelableExtra(Constants.CHAT_IMAGE_BITMAP);
            photoView.setImageBitmap(bitmap);
        }
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}

package com.chatapp.threadripper.authenticated;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.ShowToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class PhotoViewActivity extends BaseMainActivity {

    ImageView imgView;
    ImageButton btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();

        imgView = (ImageView) findViewById(R.id.imgView);
        btnClose = (ImageButton) findViewById(R.id.btnClose);

        btnClose.setOnClickListener(view -> onBackPressed());

        String url = getIntent().getStringExtra(Constants.CHAT_IMAGE_URL);
        if (url != null) {
            ImageLoader.loadImageChatMessage(imgView, url);
        } else {
            Bitmap bitmap = getIntent().getParcelableExtra(Constants.CHAT_IMAGE_BITMAP);
            imgView.setImageBitmap(bitmap);
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

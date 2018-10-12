package com.chatapp.threadripper.utils;


import android.view.View;
import android.widget.ImageView;

import com.chatapp.threadripper.R;
import com.squareup.picasso.Picasso;

public class ImageLoader {

    public static void loadUserAvatar(View view, String url) {
        Picasso.get().load(url)
                .placeholder(R.drawable.placeholder_user_avatar)
                .error(R.drawable.placeholder_user_avatar)
                .into((ImageView) view);
    }

    public static void loadImageChatMessage(View view, String url) {
        Picasso.get().load(url)
                .placeholder(R.drawable.placeholder_image_chat)
                .error(R.drawable.placeholder_image_chat)
                .fit().centerCrop()
                .into((ImageView) view);
    }

}

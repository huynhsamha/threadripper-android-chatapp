package com.chatapp.threadripper.utils;

import android.view.View;

public class ViewUtils {
    public static void toggleView(View v) {
        if (v.getVisibility() == View.VISIBLE) {
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
    }
}

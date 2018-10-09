package com.chatapp.threadripper.utils;

import android.content.Context;
import android.widget.Toast;

public class ShowToast {

    public static void lengthShort(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static void lengthLong(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}

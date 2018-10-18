package com.chatapp.threadripper.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}

package com.chatapp.threadripper.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import com.chatapp.threadripper.R;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class TargetPrompt {

    public interface OnCallbackListener {
        void onAccepted();

        void onDenied();
    }

    private static void promptTemplate(int targetColor, Context context, int targetId, String title, String content, OnCallbackListener listener) {
        new MaterialTapTargetPrompt.Builder((Activity) context)
                .setTarget(targetId)
                .setBackgroundColour(context.getResources().getColor(R.color.colorTargetPrompt))
                .setFocalColour(targetColor)
                .setPrimaryTextTypeface(Typeface.createFromAsset(context.getAssets(), "font/Quicksand-Medium.ttf"))
                .setSecondaryTextTypeface(Typeface.createFromAsset(context.getAssets(), "font/Nunito-Regular.ttf"))
                .setPrimaryTextColour(Color.WHITE)
                .setSecondaryTextColour(Color.WHITE)
                .setPrimaryText(title)
                .setSecondaryText(content)
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        // User has pressed the prompt target
                        listener.onAccepted();
                    } else if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                        listener.onDenied();
                    }
                })
                .show();
    }

    public static void prompt(Context context, int targetId, String title, String content, OnCallbackListener listener) {
        promptTemplate(context.getResources().getColor(R.color.colorTextWhite),
                context, targetId, title, content, listener);
    }

    public static void promptTargetWhite(Context context, int targetId, String title, String content, OnCallbackListener listener) {
        promptTemplate(context.getResources().getColor(R.color.colorTargetPromptFocal),
                context, targetId, title, content, listener);
    }
}

package com.chatapp.threadripper.utils;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SweetDialog {

    public interface OnCallbackListener {
        void onConfirm();

        void onCancel();
    }

    public interface OnCallbackOptionsListener {
        void onSelectOption1();

        void onSelectOption2();
    }

    public static SweetAlertDialog globalLoadingDialog;

    public static void showLoading(Context context) {
        globalLoadingDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        globalLoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        globalLoadingDialog.setTitleText("Please wait a moment");
        globalLoadingDialog.setCancelable(false);
        globalLoadingDialog.show();
    }

    public static void hideLoading() {
        if (globalLoadingDialog == null) return;
        globalLoadingDialog.dismissWithAnimation();
    }

    public static void showErrorMessage(Context context, String title, String content) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("OK")
                .show();
    }

    public static void showErrorMessage(Context context, String title, String content, OnCallbackListener listener) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("OK")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    listener.onConfirm();
                })
                .show();
    }

    public static void showSuccessMessage(Context context, String title, String content) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("OK")
                .show();
    }

    public static void showSuccessMessage(Context context, String title, String content, OnCallbackListener listener) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("OK")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    listener.onConfirm();
                })
                .show();
    }

    public static void showSuccessMessageWithCancel(Context context, String title, String content, OnCallbackListener listener) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("OK")
                .setCancelText("CANCEL")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    listener.onConfirm();
                })
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    listener.onCancel();
                })
                .show();
    }

    public static void showWarningMessageWithCancel(Context context, String title, String content, OnCallbackListener listener) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("OK")
                .setCancelText("CANCEL")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    listener.onConfirm();
                })
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    listener.onCancel();
                })
                .show();
    }


    public static void showOption(Context context, String title, String content,
                                  String option1, String option2, OnCallbackOptionsListener listener) {
        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText(option1)
                .setCancelText(option2)
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    listener.onSelectOption1();
                })
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    listener.onSelectOption2();
                })
                .show();
    }
}

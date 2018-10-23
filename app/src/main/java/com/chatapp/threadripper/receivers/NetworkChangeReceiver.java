package com.chatapp.threadripper.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.chatapp.threadripper.BaseActivity;

public class NetworkChangeReceiver extends BroadcastReceiver {

    String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (isOnline(context)) {
                BaseActivity.dialogStateConnectionChanged(true);
                Log.e(TAG, "Online Connect Internet");
            } else {
                BaseActivity.dialogStateConnectionChanged(false);
                Log.e(TAG, "Connectivity Failure");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
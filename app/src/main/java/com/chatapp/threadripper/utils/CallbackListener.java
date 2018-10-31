package com.chatapp.threadripper.utils;

public class CallbackListener {

    public interface SimpleCallback {
        void onComplete();
    }

    public interface Callback {
        void onSuccess(String message);

        void onFailure(String errorMessage);
    }

}

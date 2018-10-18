package com.chatapp.threadripper.api;

import android.appwidget.AppWidgetProviderInfo;
import android.content.SharedPreferences;

import com.chatapp.threadripper.models.ErrorResponse;
import com.chatapp.threadripper.utils.Preferences;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService implements Callback<ApiResponseData> {

    CallbackApiListener listener;

    public static ApiService getInstance() {
        return new ApiService();
    }

    public interface CallbackApiListener {
        void onSuccess(ApiResponseData data);

        void onFailure(Throwable t);
    }

    public void addCallbackListener(CallbackApiListener listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<ApiResponseData> call, Response<ApiResponseData> response) {
        if (response.isSuccessful()) {
            ApiResponseData data = response.body();
            String chatAuthToken = response.headers().get("Authorization");

            // store token when login to app
            if (chatAuthToken != null && chatAuthToken.contains("CHAT")) {
                Preferences.setChatAuthToken(chatAuthToken);
            }

            listener.onSuccess(data);
            return;
        }

        ApiResponseData data = new ApiResponseData();
        Gson gson = new Gson();
        try {
            ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
            data.setError(err);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            listener.onSuccess(data);
        }
    }

    @Override
    public void onFailure(Call<ApiResponseData> call, Throwable t) {
        listener.onFailure(t);
    }

    Retrofit getRetrofitInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.API_ROUTE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public ApiService signUp(String username, String email, String displayName, String password) {
        ApiRoutes api = getRetrofitInstance().create(ApiRoutes.class);

        api.signUp(username, email, displayName, password).enqueue(this);
        return this;
    }

    public ApiService login(String username, String password) {
        ApiRoutes api = getRetrofitInstance().create(ApiRoutes.class);

        api.login(username, password).enqueue(this);
        return this;
    }
}
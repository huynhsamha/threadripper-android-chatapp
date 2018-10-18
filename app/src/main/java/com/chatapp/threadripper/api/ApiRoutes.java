package com.chatapp.threadripper.api;

import com.chatapp.threadripper.utils.Preferences;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiRoutes {

    @FormUrlEncoded
    @POST("signup")
    Call<ApiResponseData> signUp(
            @Field("username") String username,
            @Field("email") String email,
            @Field("displayName") String displayName,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login")
    Call<ApiResponseData> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("changePassword")
    Call<ApiResponseData> changePassword(
            @Header("Authorization") String authToken,
            @Field("oldPassword") String oldPassword,
            @Field("newPassword") String newPassword
    );
}

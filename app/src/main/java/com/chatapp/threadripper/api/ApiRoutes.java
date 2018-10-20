package com.chatapp.threadripper.api;

import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Preferences;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

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
    @PUT("password")
    Call<ApiResponseData> changePassword(
            @Header("Authorization") String authToken,
            @Field("oldPassword") String oldPassword,
            @Field("newPassword") String newPassword
    );


    @GET("user")
    Call<List<User>> searchUsers(
            @Query("search") String keywords
    );


    @GET("conversation")
    Call<List<Conversation>> getConversations(
            @Header("Authorization") String authToken
    );


    @POST("conversation")
    Call<ApiResponseData> createConversation(
            @Header("Authorization") String authToken,
            @Body String body
    );

    @Multipart
    @POST("avatar")
    Call<ApiResponseData> changeUserAvatar(
            @Header("Authorization") String authToken,
            @Part MultipartBody.Part file,
            @Part("ext") RequestBody extension
    );
}

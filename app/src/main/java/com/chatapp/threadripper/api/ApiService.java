package com.chatapp.threadripper.api;


import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.FileUtils;
import com.chatapp.threadripper.utils.Preferences;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiService {

    public static ApiService getInstance() {
        return new ApiService();
    }

    private Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(Config.API_ROUTE)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private ApiRoutes getApiInstance() {
        return getRetrofitInstance().create(ApiRoutes.class);
    }


    /**
     * Functions
     */

    public Call<ApiResponseData> signUp(String username, String email, String displayName, String password) {
        return getApiInstance().signUp(username, email, displayName, password);
    }

    public Call<ApiResponseData> login(String username, String password) {
        return getApiInstance().login(username, password);
    }

    public Call<ApiResponseData> changePassword(String oldPassword, String newPassword) {
        return getApiInstance().changePassword(Preferences.getChatAuthToken(), oldPassword, newPassword);
    }

    public Call<List<User>> searchUsers(String keywords) {
        return getApiInstance().searchUsers(keywords);
    }

    public Call<List<Conversation>> getConversations() {
        return getApiInstance().getConversations(Preferences.getChatAuthToken());
    }

    public Call<List<Conversation>> getFriends() {
        return getApiInstance().getFriends(Preferences.getChatAuthToken());
    }

    public Call<Conversation> getConversation(String conversationId) {
        return getApiInstance().getConversation(Preferences.getChatAuthToken(), conversationId);
    }

    public Call<User> getUser(String username) {
        return getApiInstance().getUser(Preferences.getChatAuthToken(), username);
    }

    public Call<List<Message>> getMessagesInConversation(String conversationId) {
        return getApiInstance().getMessagesInConversation(Preferences.getChatAuthToken(), conversationId);
    }

    public Call<ApiResponseData> createConversation(List<String> listUsername) {
        JsonObject json = new JsonObject();
        /* Create body format json as following:
         {
             "listUsername": [ "username_01", "username_02", ... ]
         }
         */

        JsonArray jsonListUser = new JsonArray();
        for (String username : listUsername) {
            jsonListUser.add(username);
        }
        json.add("listUsername", jsonListUser);

        String body = json.toString();
        return getApiInstance().createConversation(Preferences.getChatAuthToken(), body);
    }

    public Call<ApiResponseData> changeUserAvatar(File file) {
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                file.getName(),
                RequestBody.create(MediaType.parse("image/*"), file)
        );
        RequestBody extension = RequestBody.create(MediaType.parse("text/plain"), FileUtils.getExtension(file));
        return getApiInstance().changeUserAvatar(Preferences.getChatAuthToken(), filePart, extension);
    }

    public Call<ApiResponseData> uploadImageInChat(File file) {
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                file.getName(),
                RequestBody.create(MediaType.parse("image/*"), file)
        );
        RequestBody extension = RequestBody.create(MediaType.parse("text/plain"), FileUtils.getExtension(file));
        return getApiInstance().uploadImageInChat(Preferences.getChatAuthToken(), filePart, extension);
    }

    public Call<ApiResponseData> uploadFileInChat(File file) {
        String mimeType = FileUtils.getMimeType(file.getAbsolutePath());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                file.getName(),
                RequestBody.create(MediaType.parse(mimeType), file)
        );
        RequestBody extension = RequestBody.create(MediaType.parse("text/plain"), FileUtils.getExtension(file));
        return getApiInstance().uploadFileInChat(Preferences.getChatAuthToken(), filePart, extension);
    }

}
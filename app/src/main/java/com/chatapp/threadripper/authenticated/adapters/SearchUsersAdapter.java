package com.chatapp.threadripper.authenticated.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiResponseData;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.authenticated.SearchUsersActivity;
import com.chatapp.threadripper.authentication.LoginActivity;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.ErrorResponse;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.Preferences;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.ViewHolder> {

    private List<User> mItems;
    private Context mContext;

    public SearchUsersAdapter(Context context, List<User> data) {
        this.mContext = context;
        if (data == null) data = new ArrayList<>();
        this.mItems = data;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private User getItem(int position) {
        return this.mItems.get(position);
    }

    public void addAllItems(List<User> items) {
        this.mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(User item) {
        this.mItems.add(item);
        notifyDataSetChanged();
    }

    public void clearAllItems() {
        this.mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public SearchUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_search_user, null);

        return new ViewHolder(itemLayoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(SearchUsersAdapter.ViewHolder holder, int position) {
        User user = getItem(position);

        holder.tvUsername.setText("@" + user.getUsername());
        holder.tvDisplayName.setText(user.getDisplayName());
        ImageLoader.loadUserAvatar(holder.cirImgUserAvatar, user.getPhotoUrl());

        holder.btnAddFriend.setOnClickListener(view -> handleAddFriend(position));
    }

    private void handleAddFriend(int position) {
        User user = getItem(position);
        user.setRelationship(Constants.RELATIONSHIP_FRIEND);
        updateServer(user);
        updateCache(user);
        this.mItems.remove(user);
        notifyDataSetChanged();
    }

    private void updateCache(User user) {
        CacheService.getInstance().addOrUpdateCacheUser(user);
    }

    private void updateServer(User user) {
        List<String> listUsername = new ArrayList<>();
        listUsername.add(Preferences.getCurrentUser().getUsername());
        listUsername.add(user.getUsername());

        ApiService.getInstance().createConversation(listUsername).enqueue(new Callback<ApiResponseData>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseData> call, @NonNull Response<ApiResponseData> response) {
                if (response.isSuccessful()) {
                    ApiResponseData data = response.body();
                    if (data != null) {
                        retrieveConversation(data.getConversationId());
                    }

                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        showError(err.getMessage());

                    } catch (Exception e) {
                        e.printStackTrace();
                        showError(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseData> call, @NonNull Throwable t) {
                showError(t.getMessage());
            }
        });
    }

    private void retrieveConversation(String conversationId) {
        ApiService.getInstance().getConversation(conversationId).enqueue(new Callback<Conversation>() {
            @Override
            public void onResponse(@NonNull Call<Conversation> call, @NonNull Response<Conversation> response) {
                if (response.isSuccessful()) {

                    Conversation c = response.body();
                    if (c != null) {
                        c.update();
                        CacheService.getInstance().addOrUpdateCacheConversation(c);
                    }

                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        showError(err.getMessage());

                    } catch (Exception e) {
                        e.printStackTrace();
                        showError(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Conversation> call, @NonNull Throwable t) {
                showError(t.getMessage());
            }
        });
    }

    private void showError(String msg) {
        ((SearchUsersActivity) mContext).ShowErrorDialog(msg);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvUsername, tvDisplayName;
        public CircleImageView cirImgUserAvatar;
        public Button btnAddFriend;

        ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);

            tvUsername = (TextView) itemLayoutView.findViewById(R.id.tvUsername);
            tvDisplayName = (TextView) itemLayoutView.findViewById(R.id.tvDisplayName);
            cirImgUserAvatar = (CircleImageView) itemLayoutView.findViewById(R.id.cirImgUserAvatar);
            btnAddFriend = (Button) itemLayoutView.findViewById(R.id.btnAddFriend);
        }
    }
}

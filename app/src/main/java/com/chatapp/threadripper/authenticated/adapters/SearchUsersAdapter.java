package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiResponseData;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.authenticated.CallingActivity;
import com.chatapp.threadripper.authenticated.VideoCallActivity;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.ViewHolder> {

    private List<User> mArrayList;
    private Context mContext;

    public SearchUsersAdapter(Context context, List<User> arrayList) {
        this.mContext = context;

        if (arrayList != null) this.mArrayList = arrayList;
        else this.mArrayList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public void setArrayList(ArrayList<User> users) {
        this.mArrayList.clear();
        this.mArrayList.addAll(users);
        this.notifyDataSetChanged();
    }

    public void addItem(User item) {
        this.mArrayList.add(item);
        this.notifyItemChanged(this.mArrayList.size()-1);
    }

    public User getItem(int position) {
        return this.mArrayList.get(position);
    }


    @Override
    public SearchUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_search_user, null);

        SearchUsersAdapter.ViewHolder viewHolder = new SearchUsersAdapter.ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchUsersAdapter.ViewHolder holder, int position) {
        User user = getItem(position);

        holder.tvUsername.setText("@" + user.getUsername());
        holder.tvDisplayName.setText(user.getDisplayName());
        ImageLoader.loadUserAvatar(holder.cirImgUserAvatar, user.getPhotoUrl());

        if (user.getRelationship().equals(Constants.RELATIONSHIP_FRIEND)) {
            holder.btnAddFriend.setVisibility(View.GONE);
            holder.btnSent.setVisibility(View.GONE);
            holder.btnChat.setVisibility(View.VISIBLE);
            holder.btnChat.setOnClickListener(view -> {
                // TODO
                handleChat(position);
            });
        }
        else if (user.getRelationship().equals(Constants.RELATIONSHIP_NONE)) {
            holder.btnSent.setVisibility(View.GONE);
            holder.btnChat.setVisibility(View.GONE);
            holder.btnAddFriend.setVisibility(View.VISIBLE);
            holder.btnAddFriend.setOnClickListener(view -> {
                // TODO
                handleAddFriend(position);
            });
        }
        else if (user.getRelationship().equals(Constants.RELATIONSHIP_SENT)) {
            holder.btnAddFriend.setVisibility(View.GONE);
            holder.btnChat.setVisibility(View.GONE);
            holder.btnSent.setVisibility(View.VISIBLE);
            holder.btnSent.setOnClickListener(view -> {
                // TODO
            });
        }
    }

    void handleAddFriend(int position) {
        User user = getItem(position);
        user.setRelationship(Constants.RELATIONSHIP_FRIEND);
        updateServer(user);
        updateCache(user);
        notifyItemChanged(position);
    }

    void updateCache(User user) {
        CacheService.getInstance().addOrUpdateCacheUser(user);
    }

    void updateServer(User user) {
        List<String> listUsername = new ArrayList<>();
        listUsername.add(Preferences.getCurrentUser().getUsername());
        listUsername.add(user.getUsername());
        ApiService.getInstance().createConversation(listUsername).enqueue(new Callback<ApiResponseData>() {
            @Override
            public void onResponse(Call<ApiResponseData> call, Response<ApiResponseData> response) {
                if (response.isSuccessful()) {
                    ApiResponseData data = response.body();
                } else {

                }
            }

            @Override
            public void onFailure(Call<ApiResponseData> call, Throwable t) {

            }
        });
    }

    void handleChat(int position) {
        User user = getItem(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvUsername, tvDisplayName;
        public CircleImageView cirImgUserAvatar;
        public Button btnAddFriend, btnSent, btnChat;

        public ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);

            tvUsername = (TextView) itemLayoutView.findViewById(R.id.tvUsername);
            tvDisplayName = (TextView) itemLayoutView.findViewById(R.id.tvDisplayName);
            cirImgUserAvatar = (CircleImageView) itemLayoutView.findViewById(R.id.cirImgUserAvatar);
            btnAddFriend = (Button) itemLayoutView.findViewById(R.id.btnAddFriend);
            btnChat = (Button) itemLayoutView.findViewById(R.id.btnChat);
            btnSent = (Button) itemLayoutView.findViewById(R.id.btnSent);
        }
    }
}

package com.chatapp.threadripper.authenticated.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    public interface OnSelectListener {
        void onSelect(int position, boolean isSelected);
    }

    private List<User> mItems;
    private Context mContext;
    private OnSelectListener listener;

    public SearchUsersAdapter(Context context, List<User> data, OnSelectListener listener) {
        this.mContext = context;
        if (data == null) data = new ArrayList<>();
        this.mItems = data;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public User getItem(int position) {
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

    public void unSelectItem(User user) {
        int position = mItems.indexOf(user);
        mItems.get(position).setSelectedMember(false);
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

        holder.cbSelect.setChecked(user.isSelectedMember());

        holder.cbSelect.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (listener != null) {
                listener.onSelect(position, isChecked);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView tvUsername, tvDisplayName;
        public CircleImageView cirImgUserAvatar;
        public CheckBox cbSelect;

        ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);

            view = itemLayoutView;

            tvUsername = (TextView) itemLayoutView.findViewById(R.id.tvUsername);
            tvDisplayName = (TextView) itemLayoutView.findViewById(R.id.tvDisplayName);
            cirImgUserAvatar = (CircleImageView) itemLayoutView.findViewById(R.id.cirImgUserAvatar);
            cbSelect = (CheckBox) itemLayoutView.findViewById(R.id.cbSelect);
        }
    }
}

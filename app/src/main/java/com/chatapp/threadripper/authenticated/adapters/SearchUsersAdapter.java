package com.chatapp.threadripper.authenticated.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


public class SearchUsersAdapter extends RealmRecyclerViewAdapter<User, SearchUsersAdapter.ViewHolder> {

    public interface OnSelectListener {
        void onSelectUser(User user, boolean isSelected);
    }

    private OnSelectListener listener;

    public SearchUsersAdapter(Context context, OrderedRealmCollection<User> data, OnSelectListener listener) {
        super(data, true);
        this.listener = listener;
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

        holder.addListener(() -> {
            listener.onSelectUser(user, !user.isSelectedMember());
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View view;
        public TextView tvUsername, tvDisplayName;
        public CircleImageView cirImgUserAvatar;
        public CheckBox cbSelect;

        OnClickListener listener;

        interface OnClickListener {
            void onClick();
        }

        void addListener(OnClickListener listener) {
            this.listener = listener;
        }

        ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);

            view = itemLayoutView;

            tvUsername = (TextView) itemLayoutView.findViewById(R.id.tvUsername);
            tvDisplayName = (TextView) itemLayoutView.findViewById(R.id.tvDisplayName);
            cirImgUserAvatar = (CircleImageView) itemLayoutView.findViewById(R.id.cirImgUserAvatar);
            cbSelect = (CheckBox) itemLayoutView.findViewById(R.id.cbSelect);

            view.setOnClickListener(this);
            cbSelect.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick();
        }
    }
}

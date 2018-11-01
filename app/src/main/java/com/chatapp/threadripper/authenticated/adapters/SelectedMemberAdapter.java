package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class SelectedMemberAdapter extends RealmRecyclerViewAdapter<User, SelectedMemberAdapter.ViewHolder> {

    private OrderedRealmCollection<User> mItems;
    private Context mContext;
    private OnClickRemoveListener listener;

    public interface OnClickRemoveListener {
        void onClickRemove(User user);
    }

    public SelectedMemberAdapter(Context context, OrderedRealmCollection<User> data, OnClickRemoveListener listener) {
        super(data, true);
        mContext = context;
        mItems = data;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_selected_member, null);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = getItem(position);

        ImageLoader.loadUserAvatar(holder.cirImgUserAvatar, user.getPhotoUrl());

        holder.view.setOnClickListener(view -> {
            if (listener != null) listener.onClickRemove(user);
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        CircleImageView cirImgUserAvatar;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            cirImgUserAvatar = (CircleImageView) itemView.findViewById(R.id.cirImgUserAvatar);
        }
    }

}

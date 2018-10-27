package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.models.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class HorizontalAvatarAdapter extends RealmRecyclerViewAdapter<User, HorizontalAvatarAdapter.ViewHolder> {

    private Context mContext;
    private OrderedRealmCollection<User> mItems;

    public HorizontalAvatarAdapter(Context context, OrderedRealmCollection<User> items) {
        super(items, true);
        mContext = context;
        mItems = items;
    }

    public User getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_horizontal_avatar, null);
        return new ViewHolder(itemLayoutView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = getItem(position);
        holder.vOnline.setVisibility(user.isOnline() ? View.VISIBLE : View.GONE);

        holder.view.setOnClickListener(view -> {
            // TODO: start Conversation Chat
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        CircleImageView cirImgUserAvatar;
        View vOnline;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            cirImgUserAvatar = (CircleImageView) itemView.findViewById(R.id.cirImgUserAvatar);
            vOnline = itemView.findViewById(R.id.vOnline);
        }
    }

}

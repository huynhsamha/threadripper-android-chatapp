package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.authenticated.ConversationActivity;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.ModelUtils;

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

        holder.tvName.setText(user.getDisplayName());

        ImageLoader.loadUserAvatar(holder.cirImgUserAvatar, user.getPhotoUrl());

        holder.view.setOnClickListener(view -> {
            Conversation item = CacheService.getInstance().retrieveCacheConversation(user.getPrivateConversationId());

            Intent intent = new Intent(mContext, ConversationActivity.class);
            intent.putExtra(Constants.CONVERSATION_ID, item.getConversationId());
            intent.putExtra(Constants.CONVERSATION_NAME, ModelUtils.getConversationName(item));
            intent.putExtra(Constants.CONVERSATION_PHOTO, ModelUtils.getConversationAvatar(item));
            intent.putExtra(Constants.CONVERSATION_IS_ONLINE, ModelUtils.isOnlineGroup(item));
            mContext.startActivity(intent);
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        CircleImageView cirImgUserAvatar;
        View vOnline;
        TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            cirImgUserAvatar = (CircleImageView) itemView.findViewById(R.id.cirImgUserAvatar);
            vOnline = itemView.findViewById(R.id.vOnline);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }

}

package com.chatapp.threadripper.authenticated.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.ConversationActivity;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.DateTimeUtils;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.ModelUtils;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.OrderedRealmCollection;


public class MessagesChatAdapter extends RecyclerView.Adapter<MessagesChatAdapter.ViewHolder> {

    private Context mContext;
    private OrderedRealmCollection<Conversation> mItems;

    public MessagesChatAdapter(Context context, OrderedRealmCollection<Conversation> data) {
        this.mContext = context;
        this.mItems = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, null);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public Conversation getItem(int position) {
        return mItems.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {

        Conversation item = getItem(position);

        vh.tvName.setText(ModelUtils.getConversationName(item));
        vh.onlineView.setVisibility(ModelUtils.isOnlineGroup(item) ? View.VISIBLE : View.INVISIBLE);
        ImageLoader.loadUserAvatar(vh.cirImgUserAvatar, item.getPhotoUrl());

        Message lastMessage = item.getLastMessage();

        if (lastMessage == null) {
            vh.tvTime.setText(DateTimeUtils.formatBestDateTime(new Date()));
            vh.tvLastChat.setText("No any message");

        } else {
            if (lastMessage.getDateTime() != null) {
                vh.tvTime.setText(DateTimeUtils.formatBestDateTime(lastMessage.getDateTime()));
            }

            switch (lastMessage.getType()) {
                case Message.MessageType.TEXT:
                    vh.tvLastChat.setText(lastMessage.getContent());
                    break;
                case Message.MessageType.IMAGE:
                    vh.tvLastChat.setText(lastMessage.getUsername() + " sent an image");
                    break;
                case Message.MessageType.FILE:
                    vh.tvLastChat.setText(lastMessage.getUsername() + " sent a file");
                    break;
                case Message.MessageType.CALL:
                    vh.tvLastChat.setText("A call end");
                    break;
                default:
                    // handle error case !!!
                    vh.tvLastChat.setText("");
                    break;
            }
        }

        vh.view.setOnClickListener(view -> {
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
        public TextView tvName;
        public TextView tvTime;
        public TextView tvLastChat;
        public CircleImageView cirImgUserAvatar;
        public View onlineView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            view = itemLayoutView;
            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
            tvTime = (TextView) itemLayoutView.findViewById(R.id.tv_time);
            tvLastChat = (TextView) itemLayoutView.findViewById(R.id.tv_last_chat);
            cirImgUserAvatar = (CircleImageView) itemLayoutView.findViewById(R.id.cirImgUserAvatar);
            onlineView = itemLayoutView.findViewById(R.id.online_indicator);
        }
    }
}

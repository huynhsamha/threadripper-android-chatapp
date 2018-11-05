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
import com.chatapp.threadripper.utils.Preferences;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


public class MessagesChatAdapter extends RealmRecyclerViewAdapter<Conversation, MessagesChatAdapter.ViewHolder> {

    private Context mContext;

    public MessagesChatAdapter(Context context, OrderedRealmCollection<Conversation> data) {
        super(data, true);
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, null);

        return new ViewHolder(itemLayoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {

        Conversation item = getItem(position);

        vh.tvName.setText(ModelUtils.getConversationName(item));
        vh.onlineView.setVisibility(ModelUtils.isOnlineGroup(item) ? View.VISIBLE : View.INVISIBLE);
        ImageLoader.loadUserAvatar(vh.cirImgUserAvatar, item.getPhotoUrl());

        if (item.getNotiCount() > 0) {
            vh.tvNotiCount.setVisibility(View.VISIBLE);
            vh.tvNotiCount.setText(String.valueOf(item.getNotiCount()));
        } else {
            vh.tvNotiCount.setVisibility(View.GONE);
        }

        Message lastMessage = item.getLastMessage();

        if (lastMessage == null) {
            vh.tvTime.setVisibility(View.GONE);
            vh.tvLastChat.setText("Please wave to everyone");

        } else {
            vh.tvTime.setVisibility(View.VISIBLE);
            if (lastMessage.getDateTime() != null) {
                vh.tvTime.setText(DateTimeUtils.formatBestShortDateTime(lastMessage.getDateTime()));
            }

            String username = lastMessage.getUsername();
            if (username == null) username = "";
            else {
                if (username.equals(Preferences.getCurrentUser().getUsername())) username = "You";
            }

            switch (lastMessage.getType()) {
                case Message.MessageType.TEXT:
                    vh.tvLastChat.setText(username + ": " + lastMessage.getContent());
                    break;
                case Message.MessageType.IMAGE:
                    vh.tvLastChat.setText(username + " sent an image");
                    break;
                case Message.MessageType.FILE:
                    vh.tvLastChat.setText(username + " sent a file");
                    break;
                case Message.MessageType.CALL:
                    vh.tvLastChat.setText("A call ended");
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

        View view, onlineView;
        TextView tvName, tvTime, tvLastChat, tvNotiCount;
        public CircleImageView cirImgUserAvatar;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            view = itemLayoutView;
            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
            tvTime = (TextView) itemLayoutView.findViewById(R.id.tv_time);
            tvLastChat = (TextView) itemLayoutView.findViewById(R.id.tv_last_chat);
            tvNotiCount = (TextView) itemLayoutView.findViewById(R.id.tvNotiCount);
            cirImgUserAvatar = (CircleImageView) itemLayoutView.findViewById(R.id.cirImgUserAvatar);
            onlineView = itemLayoutView.findViewById(R.id.online_indicator);
        }
    }
}

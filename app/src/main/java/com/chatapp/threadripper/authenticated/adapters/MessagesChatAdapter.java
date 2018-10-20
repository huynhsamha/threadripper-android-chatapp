package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.models.Contact;
import com.chatapp.threadripper.authenticated.models.MessagesChat;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.utils.DateTimeUtils;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.ModelUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessagesChatAdapter extends RecyclerView.Adapter<MessagesChatAdapter.ViewHolder> {

    private ArrayList<Conversation> mArrayList;
    private Context mContext;
    private ViewHolder.ClickListener clickListener;

    public MessagesChatAdapter(Context context, ArrayList<Conversation> arrayList, ViewHolder.ClickListener clickListener) {
        this.mContext = context;
        this.clickListener = clickListener;

        if (arrayList != null) this.mArrayList = arrayList;
        else this.mArrayList = new ArrayList<>();
    }

    public void setArrayList(ArrayList<Conversation> arrayList) {
        this.mArrayList.clear();
        this.mArrayList.addAll(arrayList);
        this.notifyDataSetChanged();
    }

    public void addAll(ArrayList<Conversation> arrayList) {
        this.mArrayList.addAll(arrayList);
        this.notifyDataSetChanged();
    }

    public void addItem(Conversation item) {
        this.mArrayList.add(item);
        this.notifyItemChanged(this.mArrayList.size() - 1);
    }

    public void clean() {
        this.mArrayList.clear();
        this.notifyDataSetChanged();
    }

    public Conversation getItem(int position) {
        return this.mArrayList.get(position);
    }


    @Override
    public MessagesChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, null);

        ViewHolder viewHolder = new MessagesChatAdapter.ViewHolder(itemLayoutView, clickListener);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MessagesChatAdapter.ViewHolder vh, int position) {

        Conversation item = getItem(position);

        vh.tvName.setText(ModelUtils.getConversationName(item));
        vh.onlineView.setVisibility(ModelUtils.isOnlineGroup(item) ? View.VISIBLE : View.INVISIBLE);
        // ImageLoader.loadUserAvatar(vh.cirImgUserAvatar, );

        Message lastMessage = item.getLastMessage();
        if (lastMessage == null) {
            vh.tvTime.setText(DateTimeUtils.formatBestDateTime(new Date()));
            vh.tvLastChat.setText("<No any message>");
        } else {
            vh.tvTime.setText(DateTimeUtils.formatBestDateTime(item.getLastMessage().getDatetime()));

            if (lastMessage.getType().equals(Message.MessageType.TEXT)) {
                vh.tvLastChat.setText(lastMessage.getContent());
            } else if (lastMessage.getType().equals(Message.MessageType.IMAGE)) {
                vh.tvLastChat.setText(lastMessage.getUsername() + " sent an image");
            } else if (lastMessage.getType().equals(Message.MessageType.FILE)) {
                vh.tvLastChat.setText(lastMessage.getUsername() + " sent a file");
            } else if (lastMessage.getType().equals(Message.MessageType.CALL)) {
                vh.tvLastChat.setText("A call end");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvName;
        public TextView tvTime;
        public TextView tvLastChat;
        public CircleImageView cirImgUserAvatar;
        public View onlineView;
        ClickListener listener;

        public interface ClickListener {
            public void onItemClicked(int position);
        }


        public ViewHolder(View itemLayoutView, ClickListener listener) {
            super(itemLayoutView);

            this.listener = listener;

            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
            tvTime = (TextView) itemLayoutView.findViewById(R.id.tv_time);
            tvLastChat = (TextView) itemLayoutView.findViewById(R.id.tv_last_chat);
            cirImgUserAvatar = (CircleImageView) itemLayoutView.findViewById(R.id.cirImgUserAvatar);
            onlineView = itemLayoutView.findViewById(R.id.online_indicator);

            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition());
            }
        }
    }
}

package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.PhotoViewActivity;
import com.chatapp.threadripper.authenticated.adapters.viewholders.ViewHolderYouOrMe;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.DateTimeUtils;
import com.chatapp.threadripper.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Message> mItems;
    private ContactAdapter.ViewHolder.ClickListener clickListener;

    private final int YOU = 1, ME = 2;

    public ConversationAdapter(Context context, List<Message> data) {
        this.mContext = context;
        if (data == null) data = new ArrayList<>();
        this.mItems = data;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private Message getItem(int position) {
        return this.mItems.get(position);
    }

    public void addAllItems(List<Message> items) {
        this.mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(Message item) {
        this.mItems.add(item);
        notifyDataSetChanged();
    }

    public void clearAllItems() {
        this.mItems.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        Message msg = mItems.get(position);
        if (msg.isYou()) return YOU;
        return ME;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case YOU:
                View v1 = inflater.inflate(R.layout.layout_holder_you, viewGroup, false);
                viewHolder = new ViewHolderYouOrMe(v1, true);
                break;
            default: // is ME
                View v2 = inflater.inflate(R.layout.layout_holder_me, viewGroup, false);
                viewHolder = new ViewHolderYouOrMe(v2, false);
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case YOU:
                configureViewHolderYouOrMe((ViewHolderYouOrMe) viewHolder, position, true);
                break;
            default: // is ME
                configureViewHolderYouOrMe((ViewHolderYouOrMe) viewHolder, position, false);
                break;
        }
    }

    private void configureViewHolderYouOrMe(ViewHolderYouOrMe vh, int position, boolean isYou) {
        Message msg = mItems.get(position);

        vh.getTime().setText(DateTimeUtils.formatBestDateTime(msg.getDateTime()));

        switch (msg.getType()) {
            case Message.MessageType.TEXT:
                vh.getChatText().setText(msg.getContent());
                vh.getRivChatImage().setVisibility(View.GONE);
                vh.getChatText().setVisibility(View.VISIBLE);
                break;

            case Message.MessageType.IMAGE:
                vh.getRivChatImage().setVisibility(View.VISIBLE);
                vh.getChatText().setVisibility(View.GONE);

                if (msg.isBitmap()) {
                    // bitmap when use camera capture
                    vh.getRivChatImage().setImageBitmap(msg.getBitmap());
                    vh.getRivChatImage().setOnClickListener(view -> {
                        Intent intent = new Intent(this.mContext, PhotoViewActivity.class);
                        intent.putExtra(Constants.CHAT_IMAGE_BITMAP, msg.getBitmap());
                        this.mContext.startActivity(intent);
                    });

                } else {
                    // this is url (server) or uri (in device)
                    ImageLoader.loadImageChatMessage(vh.getRivChatImage(), msg.getContent());
                    vh.getRivChatImage().setOnClickListener(view -> {
                        Intent intent = new Intent(this.mContext, PhotoViewActivity.class);
                        intent.putExtra(Constants.CHAT_IMAGE_URL, msg.getContent());
                        this.mContext.startActivity(intent);
                    });
                }
                break;

            default:
                // oh, no man!, what the fucking message!!!
                vh.getRivChatImage().setVisibility(View.GONE);
                vh.getChatText().setVisibility(View.GONE);
                break;
        }

        if (isYou) {
            // TODO - implement load image for group > 2 people

            if (msg.getConversationAvatar() == null) {
                ImageLoader.loadUserAvatar(vh.getCirImgUserAvatar(), msg.getConversationAvatar());
                return;
            }

            if (msg.getConversationAvatar().equals(Constants.PLACEHOLDER_GROUP_AVATAR)) {
                ImageLoader.loadGroupAvatar(vh.getCirImgUserAvatar(), "");
            } else {
                ImageLoader.loadUserAvatar(vh.getCirImgUserAvatar(), msg.getConversationAvatar());
            }
        }
    }

}

package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.PhotoViewActivity;
import com.chatapp.threadripper.authenticated.adapters.viewholders.ViewHolderDate;
import com.chatapp.threadripper.authenticated.adapters.viewholders.ViewHolderYouOrMe;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.DateTimeUtils;
import com.chatapp.threadripper.utils.ImageLoader;

import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private ArrayList<Message> items;
    private Context mContext;
    private ContactAdapter.ViewHolder.ClickListener clickListener;

    private final int DATE = 0, YOU = 1, ME = 2;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ConversationAdapter(Context context, ArrayList<Message> items) {
        this.mContext = context;
        this.items = items;

        if (items != null) this.items = items;
        else this.items = new ArrayList<>();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }


    public void addItem(Message item) {
        this.items.add(item);
        // notifyItemChanged(items.size()-1); // not working
        notifyDataSetChanged();
    }

    public void setItemsList(ArrayList<Message> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        // More to come
        Message msg = items.get(position);
        if (msg.isDate()) return DATE;
        if (msg.isYou()) return YOU;
        return ME;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case DATE:
                View v1 = inflater.inflate(R.layout.layout_holder_date, viewGroup, false);
                viewHolder = new ViewHolderDate(v1);
                break;
            case YOU:
                View v2 = inflater.inflate(R.layout.layout_holder_you, viewGroup, false);
                viewHolder = new ViewHolderYouOrMe(v2, true);
                break;
            default: // is ME
                View v3 = inflater.inflate(R.layout.layout_holder_me, viewGroup, false);
                viewHolder = new ViewHolderYouOrMe(v3, false);
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case DATE:
                configureViewHolderDate((ViewHolderDate) viewHolder, position);
                break;
            case YOU:
                configureViewHolderYouOrMe((ViewHolderYouOrMe) viewHolder, position, true);
                break;
            default: // is ME
                configureViewHolderYouOrMe((ViewHolderYouOrMe) viewHolder, position, false);
                break;
        }
    }

    private void configureViewHolderYouOrMe(ViewHolderYouOrMe vh, int position, boolean isYou) {
        Message msg = items.get(position);

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
            // TODO
            ImageLoader.loadUserAvatar(vh.getCirImgUserAvatar(), msg.getConversationAvatar());
        }
    }

    private void configureViewHolderDate(ViewHolderDate vh, int position) {
        Message msg = items.get(position);
        vh.getDate().setText(DateTimeUtils.formatDate(msg.getDateTime()));
    }

}

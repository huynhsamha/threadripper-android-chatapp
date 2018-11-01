package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.chatapp.threadripper.utils.ViewUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ConversationAdapter extends RealmRecyclerViewAdapter<Message, RecyclerView.ViewHolder> {

    private Context mContext;
    private OrderedRealmCollection<Message> mItems;

    private final int YOU = 1, ME = 2;

    public ConversationAdapter(Context context, OrderedRealmCollection<Message> data) {
        super(data, true);
        this.mContext = context;
        this.mItems = data;
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
        if (!msg.isLeadingBlock()) {
            vh.getTime().setVisibility(View.GONE);
        } else {
            vh.getTime().setVisibility(View.VISIBLE);
        }

        switch (msg.getType()) {
            case Message.MessageType.TEXT:
                vh.getChatText().setText(msg.getContent());
                vh.getRivChatImage().setVisibility(View.GONE);
                vh.getFileContent().setVisibility(View.GONE);
                vh.getChatText().setVisibility(View.VISIBLE);
                if (!msg.isLeadingBlock()) {
                    vh.getChatText().setOnClickListener(view -> {
                        ViewUtils.toggleView(vh.getTime());
                    });
                }
                break;

            case Message.MessageType.IMAGE:
                vh.getRivChatImage().setVisibility(View.VISIBLE);
                vh.getChatText().setVisibility(View.GONE);
                vh.getFileContent().setVisibility(View.GONE);
                // if (msg.isBitmap()) {
                //     // bitmap when use camera capture
                //     vh.getRivChatImage().setImageBitmap(msg.getBitmap());
                //     vh.getRivChatImage().setOnClickListener(view -> {
                //         Intent intent = new Intent(this.mContext, PhotoViewActivity.class);
                //         intent.putExtra(Constants.CHAT_IMAGE_BITMAP, msg.getBitmap());
                //         this.mContext.startActivity(intent);
                //     });
                //
                // } else {
                // this is url (server) or uri (in device)
                ImageLoader.loadImageChatMessage(vh.getRivChatImage(), msg.getContent());
                vh.getRivChatImage().setOnClickListener(view -> {
                    Intent intent = new Intent(this.mContext, PhotoViewActivity.class);
                    intent.putExtra(Constants.CHAT_IMAGE_URL, msg.getContent());
                    this.mContext.startActivity(intent);
                });
                // }
                break;

            case Message.MessageType.FILE:
            case Message.MessageType.CALL:
                vh.getFileContent().setVisibility(View.VISIBLE);
                vh.getChatText().setVisibility(View.GONE);
                vh.getRivChatImage().setVisibility(View.GONE);

                // Case CALL
                if (msg.getType().equals(Message.MessageType.CALL)) {
                    vh.getFileContent().setText("A call ended");
                    vh.getFileContent().setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_action_missed_video_call_accent), null, null, null);
                    break;
                }

                vh.getFileContent().setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_action_attachment_horizontal), null, null, null);

                // Case FILE
                if (msg.getContent() == null) {
                    vh.getFileContent().setText("[Error file]");
                } else {
                    try {
                        JsonObject json = (new JsonParser()).parse(msg.getContent()).getAsJsonObject();
                        final String filename = json.has("filename") ? json.get("filename").getAsString() : "";
                        final String url = json.has("url") ? json.get("url").getAsString() : "";

                        vh.getFileContent().setText(filename);

                        vh.getFileContent().setOnClickListener(view -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            this.mContext.startActivity(intent);
                        });
                    } catch (Exception e) {
                        vh.getFileContent().setText("[Error format file]");
                    }
                }

                break;

            default:
                // oh, no man!, what the fucking message!!!
                vh.getRivChatImage().setVisibility(View.GONE);
                vh.getChatText().setVisibility(View.GONE);
                vh.getFileContent().setVisibility(View.GONE);
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

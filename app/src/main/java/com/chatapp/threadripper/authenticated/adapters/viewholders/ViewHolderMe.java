package com.chatapp.threadripper.authenticated.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.makeramen.roundedimageview.RoundedImageView;


public class ViewHolderMe extends RecyclerView.ViewHolder {

    private TextView time, chatText;
    private RoundedImageView rivChatImage;

    public ViewHolderMe(View v) {
        super(v);
        time = (TextView) v.findViewById(R.id.tv_time);
        chatText = (TextView) v.findViewById(R.id.tv_chat_text);
        rivChatImage = (RoundedImageView) v.findViewById(R.id.rivChatImage);
    }

    public TextView getTime() {
        return time;
    }

    public void setTime(TextView time) {
        this.time = time;
    }

    public TextView getChatText() {
        return chatText;
    }

    public void setChatText(TextView chatText) {
        this.chatText = chatText;
    }

    public RoundedImageView getRivChatImage() {
        return rivChatImage;
    }

    public void setRivChatImage(RoundedImageView rivChatImage) {
        this.rivChatImage = rivChatImage;
    }
}

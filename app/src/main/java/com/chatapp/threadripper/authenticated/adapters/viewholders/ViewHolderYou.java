package com.chatapp.threadripper.authenticated.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.makeramen.roundedimageview.RoundedImageView;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewHolderYou extends RecyclerView.ViewHolder {

    private TextView time, chatText;
    private CircleImageView cirImgUserAvatar;
    private RoundedImageView imgChatImage;

    public ViewHolderYou(View v) {
        super(v);
        time = (TextView) v.findViewById(R.id.tv_time);
        chatText = (TextView) v.findViewById(R.id.tv_chat_text);
        cirImgUserAvatar = (CircleImageView) v.findViewById(R.id.cirImgUserAvatar);
        imgChatImage = (RoundedImageView) v.findViewById(R.id.rivChatImage);
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

    public CircleImageView getCirImgUserAvatar() {
        return cirImgUserAvatar;
    }

    public void setCirImgUserAvatar(CircleImageView cirImgUserAvatar) {
        this.cirImgUserAvatar = cirImgUserAvatar;
    }

    public RoundedImageView getImgChatImage() {
        return imgChatImage;
    }

    public void setImgChatImage(RoundedImageView imgChatImage) {
        this.imgChatImage = imgChatImage;
    }
}

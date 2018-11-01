package com.chatapp.threadripper.authenticated.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.makeramen.roundedimageview.RoundedImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolderYouOrMe extends RecyclerView.ViewHolder {


    private TextView time, chatText;
    private CircleImageView cirImgUserAvatar;
    private RoundedImageView rivChatImage;
    private TextView fileContent;

    public ViewHolderYouOrMe(View v, boolean isYou) {
        super(v);
        time = (TextView) v.findViewById(R.id.tv_time);
        chatText = (TextView) v.findViewById(R.id.tv_chat_text);
        rivChatImage = (RoundedImageView) v.findViewById(R.id.rivChatImage);
        fileContent = (TextView) v.findViewById(R.id.fileContent);
        if (isYou) {
            cirImgUserAvatar = (CircleImageView) v.findViewById(R.id.cirImgUserAvatar);
        }
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

    public RoundedImageView getRivChatImage() {
        return rivChatImage;
    }

    public void setRivChatImage(RoundedImageView rivChatImage) {
        this.rivChatImage = rivChatImage;
    }

    public TextView getFileContent() {
        return fileContent;
    }

    public void setFileContent(TextView fileContent) {
        this.fileContent = fileContent;
    }
}

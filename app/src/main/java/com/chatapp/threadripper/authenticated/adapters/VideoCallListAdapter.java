package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.CallingActivity;
import com.chatapp.threadripper.authenticated.VideoCallActivity;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class VideoCallListAdapter extends SelectableAdapter<VideoCallListAdapter.ViewHolder> {

    private List<User> mArrayList;
    private Context mContext;


    public VideoCallListAdapter(Context context, List<User> arrayList) {
        this.mContext = context;

        if (arrayList != null) this.mArrayList = arrayList;
        else this.mArrayList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public void setArrayList(ArrayList<User> users) {
        this.mArrayList.clear();
        this.mArrayList.addAll(users);
        this.notifyDataSetChanged();
    }

    public void addItem(User item) {
        this.mArrayList.add(item);
        this.notifyItemChanged(this.mArrayList.size()-1);
    }

    public User getItem(int position) {
        return this.mArrayList.get(position);
    }


    // Create new views
    @Override
    public VideoCallListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_video_call, null);

        VideoCallListAdapter.ViewHolder viewHolder = new VideoCallListAdapter.ViewHolder(itemLayoutView);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(VideoCallListAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.tvName.setText(mArrayList.get(position).getDisplayName());

        // load avatar
        ImageLoader.loadUserAvatar(viewHolder.cirImgUserAvatar, mArrayList.get(position).getPhotoUrl());

        viewHolder.rvCall.setOnRippleCompleteListener(rippleView -> handleStartCalling(position));

        viewHolder.rvCallVideo.setOnRippleCompleteListener(rippleView -> handleStartCallingVideo(position));
    }

    void handleStartCalling(int position) {
        // ShowToast.lengthShort(this.mContext, "Calling...");

        Intent intent = new Intent(this.mContext, CallingActivity.class);
        intent.putExtra(Constants.IS_CALLER_SIDE, true); // user who start a calling is a caller
        intent.putExtra(Constants.USER_USERNAME, this.mArrayList.get(position).getUsername());
        intent.putExtra(Constants.USER_DISPLAY_NAME, this.mArrayList.get(position).getDisplayName());
        intent.putExtra(Constants.USER_PHOTO_URL, this.mArrayList.get(position).getPhotoUrl());

        this.mContext.startActivity(intent);
    }

    void handleStartCallingVideo(int position) {
        // ShowToast.lengthShort(this.mContext, "Calling Video...");

        Intent intent = new Intent(this.mContext, VideoCallActivity.class);
        intent.putExtra(Constants.IS_CALLER_SIDE, true); // user who start a calling is a caller
        intent.putExtra(Constants.USER_USERNAME, this.mArrayList.get(position).getUsername());
        intent.putExtra(Constants.USER_DISPLAY_NAME, this.mArrayList.get(position).getDisplayName());
        intent.putExtra(Constants.USER_PHOTO_URL, this.mArrayList.get(position).getPhotoUrl());

        this.mContext.startActivity(intent);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public CircleImageView cirImgUserAvatar;
        public RippleView rvCall, rvCallVideo;

        public ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);

            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
            cirImgUserAvatar = (CircleImageView) itemLayoutView.findViewById(R.id.cirImgUserAvatar);
            rvCall = (RippleView) itemLayoutView.findViewById(R.id.rvCall);
            rvCallVideo = (RippleView) itemLayoutView.findViewById(R.id.rvCallVideo);
        }
    }
}

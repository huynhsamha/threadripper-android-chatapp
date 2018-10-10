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
import com.chatapp.threadripper.authenticated.models.Contact;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.ShowToast;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class VideoCallListAdapter extends SelectableAdapter<VideoCallListAdapter.ViewHolder> {

    private List<Contact> mArrayList;
    private Context mContext;


    public VideoCallListAdapter(Context context, List<Contact> arrayList) {
        this.mArrayList = arrayList;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
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

        viewHolder.tvName.setText(mArrayList.get(position).getName());

        // load avatar
        ImageLoader.loadUserAvatar(viewHolder.cirImgUserAvatar, mArrayList.get(position).getImage());

        viewHolder.rvCall.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                handleStartCalling(position);
            }
        });

        viewHolder.rvCallVideo.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                handleStartCallingVideo(position);
            }
        });
    }

    void handleStartCalling(int position) {
        // ShowToast.lengthShort(this.mContext, "Calling...");

        Intent intent = new Intent(this.mContext, CallingActivity.class);
        intent.putExtra(Constants.IS_CALLER_SIDE, true); // user who start a calling is a caller
        intent.putExtra(Constants.USERNAME, this.mArrayList.get(position).getName());
        intent.putExtra(Constants.USER_AVATAR, this.mArrayList.get(position).getImage());

        this.mContext.startActivity(intent);
    }

    void handleStartCallingVideo(int position) {
        ShowToast.lengthShort(this.mContext, "Calling Video...");
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

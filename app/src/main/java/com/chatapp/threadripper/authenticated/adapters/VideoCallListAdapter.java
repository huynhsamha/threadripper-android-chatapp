package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.models.Contact;
import com.chatapp.threadripper.utils.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class VideoCallListAdapter extends SelectableAdapter<VideoCallListAdapter.ViewHolder> {

    private List<Contact> mArrayList;
    private Context mContext;


    public VideoCallListAdapter(Context context, List<Contact> arrayList) {
        this.mArrayList = arrayList;
        this.mContext = context;
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
    public void onBindViewHolder(VideoCallListAdapter.ViewHolder viewHolder, int position) {

        viewHolder.tvName.setText(mArrayList.get(position).getName());

        // load avatar
        ImageLoader.loadUserAvatar(viewHolder.cirImgUserAvatar, mArrayList.get(position).getImage());
    }


    @Override
    public int getItemCount() {
        return mArrayList.size();
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

            rvCall.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    Toast.makeText(itemLayoutView.getContext(), "Calling...", Toast.LENGTH_SHORT).show();
                }
            });

            rvCallVideo.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    Toast.makeText(itemLayoutView.getContext(), "Calling video...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

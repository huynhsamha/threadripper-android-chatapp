package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.VideoCallActivity;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.Preferences;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


public class VideoCallListAdapter extends RealmRecyclerViewAdapter<User, VideoCallListAdapter.ViewHolder> {

    private Context mContext;


    public VideoCallListAdapter(Context context, OrderedRealmCollection<User> arrayList) {
        super(arrayList, true);
        this.mContext = context;
    }

    @Override
    public VideoCallListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_video_call, null);

        return new ViewHolder(itemLayoutView);
    }


    @Override
    public void onBindViewHolder(VideoCallListAdapter.ViewHolder viewHolder, final int position) {

        User user = getItem(position);

        viewHolder.tvName.setText(user.getDisplayName());

        viewHolder.online_indicator.setVisibility(user.isOnline() ? View.VISIBLE : View.GONE);

        // load avatar
        ImageLoader.loadUserAvatar(viewHolder.cirImgUserAvatar, user.getPhotoUrl());

        viewHolder.rvCall.setOnRippleCompleteListener(rippleView -> handleStartCalling(position, false));

        viewHolder.rvCallVideo.setOnRippleCompleteListener(rippleView -> handleStartCalling(position, true));
    }

    private void handleStartCalling(int position, boolean callVideoOrAudio) {
        Intent intent = new Intent(mContext, VideoCallActivity.class);

        User userRealm = getItem(position);

        User user = new User();
        user.setUsername(userRealm.getUsername());
        user.setPhotoUrl(userRealm.getPhotoUrl());
        user.setDisplayName(userRealm.getDisplayName());
        user.setPrivateConversationId(userRealm.getPrivateConversationId());

        String channelId = "THREADRIPPER_CALL_"
                + Preferences.getCurrentUser().getUsername() + "_"
                + user.getUsername();

        // CALLING_VIDEO_OR_AUDIO is not transfer correctly but EXTRA_VIDEO_CHANNEL_TOKEN does
        // so I encode videocall and audio mode into the channelId
        // VideoCallActivity will decode it

        intent.putExtra(Constants.IS_CALLER_SIDE, true); // user who start a calling is a caller
        intent.putExtra(Constants.USER_MODEL, user);
        intent.putExtra(Constants.EXTRA_VIDEO_CHANNEL_TOKEN, encode(channelId, callVideoOrAudio));

        mContext.startActivity(intent);
    }

    private String encode(String channelId, boolean isVideoMode) {
        return channelId.concat(isVideoMode ? "1" : "0");
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public CircleImageView cirImgUserAvatar;
        public RippleView rvCall, rvCallVideo;
        public View online_indicator;

        public ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);

            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
            cirImgUserAvatar = (CircleImageView) itemLayoutView.findViewById(R.id.cirImgUserAvatar);
            rvCall = (RippleView) itemLayoutView.findViewById(R.id.rvCall);
            rvCallVideo = (RippleView) itemLayoutView.findViewById(R.id.rvCallVideo);
            online_indicator = itemLayoutView.findViewById(R.id.online_indicator);
        }
    }
}

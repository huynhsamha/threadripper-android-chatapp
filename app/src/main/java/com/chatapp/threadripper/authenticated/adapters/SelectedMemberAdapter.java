package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.models.Contact;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.ImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedMemberAdapter extends RecyclerView.Adapter<SelectedMemberAdapter.ViewHolder> {

    private ArrayList<User> mItems;
    private Context mContext;
    private OnClickRemoveListener listener;

    public interface OnClickRemoveListener {
        void onClickRemove(int position);
    }

    public SelectedMemberAdapter(Context context, ArrayList<User> data, OnClickRemoveListener listener) {
        mContext = context;
        if (data == null) data = new ArrayList<>();
        mItems = data;
        this.listener = listener;
    }

    public void addItem(User item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(User item) {
        mItems.remove(item);
        notifyDataSetChanged();
    }

    public ArrayList<User> getAll() {
        return mItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_contact, null);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = getItem(position);

        ImageLoader.loadUserAvatar(holder.cirImgUserAvatar, user.getPhotoUrl());

        holder.view.setOnClickListener(view -> {
            if (listener != null) listener.onClickRemove(position);
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public User getItem(int position) {
        return mItems.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        CircleImageView cirImgUserAvatar;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            cirImgUserAvatar = (CircleImageView) itemView.findViewById(R.id.cirImgUserAvatar);
        }
    }

}

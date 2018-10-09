package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.models.Contact;
import com.chatapp.threadripper.authenticated.models.MessagesChat;
import com.chatapp.threadripper.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessagesChatAdapter extends SelectableAdapter<MessagesChatAdapter.ViewHolder> {

    private ArrayList<MessagesChat> mArrayList;
    private Context mContext;
    private ViewHolder.ClickListener clickListener;


    public MessagesChatAdapter(Context context, ArrayList<MessagesChat> arrayList, ViewHolder.ClickListener clickListener) {
        this.mContext = context;
        this.clickListener = clickListener;

        if (arrayList != null) this.mArrayList = arrayList;
        else this.mArrayList = new ArrayList<>();
    }

    public void setArrayList(ArrayList<MessagesChat> arrayList) {
        this.mArrayList.clear();
        this.mArrayList.addAll(arrayList);
        this.notifyDataSetChanged();
    }

    public void addItem(MessagesChat item) {
        this.mArrayList.add(item);
        this.notifyItemChanged(this.mArrayList.size()-1);
    }

    public MessagesChat getItem(int position) {
        return this.mArrayList.get(position);
    }

    // Create new views
    @Override
    public MessagesChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView, clickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.tvName.setText(mArrayList.get(position).getName());

        if (isSelected(position)) {
            viewHolder.checked.setChecked(true);
            viewHolder.checked.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checked.setChecked(false);
            viewHolder.checked.setVisibility(View.GONE);
        }

        viewHolder.tvTime.setText(mArrayList.get(position).getTime());

        ImageLoader.loadUserAvatar(viewHolder.cirImgUserAvatar, mArrayList.get(position).getImage());

        if (mArrayList.get(position).getOnline()) {
            viewHolder.onlineView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.onlineView.setVisibility(View.INVISIBLE);
        }

        viewHolder.tvLastChat.setText(mArrayList.get(position).getLastChat());
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView tvName;
        public TextView tvTime;
        public TextView tvLastChat;
        public CircleImageView cirImgUserAvatar;
        private final View onlineView;
        public CheckBox checked;
        private ClickListener listener;


        public ViewHolder(View itemLayoutView, ClickListener listener) {
            super(itemLayoutView);

            this.listener = listener;

            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
            tvTime = (TextView) itemLayoutView.findViewById(R.id.tv_time);
            tvLastChat = (TextView) itemLayoutView.findViewById(R.id.tv_last_chat);
            cirImgUserAvatar = (CircleImageView) itemLayoutView.findViewById(R.id.cirImgUserAvatar);
            onlineView = itemLayoutView.findViewById(R.id.online_indicator);
            checked = (CheckBox) itemLayoutView.findViewById(R.id.chk_list);

            itemLayoutView.setOnClickListener(this);

            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition());
            }
            return false;
        }

        public interface ClickListener {
            void onItemClicked(int position);

            boolean onItemLongClicked(int position);

            boolean onCreateOptionsMenu(Menu menu);
        }
    }
}

package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.models.Message;
import com.chatapp.threadripper.authenticated.adapters.viewholders.ViewHolderDate;
import com.chatapp.threadripper.authenticated.adapters.viewholders.ViewHolderMe;
import com.chatapp.threadripper.authenticated.adapters.viewholders.ViewHolderYou;
import com.chatapp.threadripper.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private ArrayList<Message> items;
    private Context mContext;

    private final int DATE = 0, YOU = 1, ME = 2;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ConversationAdapter(Context context, ArrayList<Message> items) {
        this.mContext = context;
        this.items = items;

        if (items != null) this.items = items;
        else this.items = new ArrayList<>();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }


    public void addItem(Message item) {
        this.items.add(item);
        // notifyItemChanged(items.size()-1);
        this.notifyDataSetChanged();
    }

    public void setItemsList(ArrayList<Message> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        //More to come
        if (items.get(position).getType().equals("0")) {
            return DATE;
        } else if (items.get(position).getType().equals("1")) {
            return YOU;
        } else if (items.get(position).getType().equals("2")) {
            return ME;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case DATE:
                View v1 = inflater.inflate(R.layout.layout_holder_date, viewGroup, false);
                viewHolder = new ViewHolderDate(v1);
                break;
            case YOU:
                View v2 = inflater.inflate(R.layout.layout_holder_you, viewGroup, false);
                viewHolder = new ViewHolderYou(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.layout_holder_me, viewGroup, false);
                viewHolder = new ViewHolderMe(v);
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case DATE:
                configureViewHolderDate((ViewHolderDate) viewHolder, position);
                break;
            case YOU:
                configureViewHolderYou((ViewHolderYou) viewHolder, position);
                break;
            default:
                configureViewHolderMe((ViewHolderMe) viewHolder, position);
                break;
        }
    }

    private void configureViewHolderMe(ViewHolderMe vh, int position) {
        vh.getTime().setText(items.get(position).getTime());
        vh.getChatText().setText(items.get(position).getText());
    }

    private void configureViewHolderYou(ViewHolderYou vh, int position) {
        vh.getTime().setText(items.get(position).getTime());
        vh.getChatText().setText(items.get(position).getText());
        ImageLoader.loadUserAvatar(vh.getCirImgUserAvatar(), items.get(position).getAvatarUser());
    }

    private void configureViewHolderDate(ViewHolderDate vh, int position) {
        vh.getDate().setText(items.get(position).getText());
    }

}

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

import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<Message> items;
    private Context mContext;

    private final int DATE = 0, YOU = 1, ME = 2;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ConversationAdapter(Context context, List<Message> items) {
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


    public void addItemsList(ArrayList<Message> items) {
        items.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(Message item) {
        items.add(item);
        notifyItemChanged(items.size()-1);
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
                ViewHolderDate vh1 = (ViewHolderDate) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case YOU:
                ViewHolderYou vh2 = (ViewHolderYou) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            default:
                ViewHolderMe vh = (ViewHolderMe) viewHolder;
                configureViewHolder3(vh, position);
                break;
        }
    }

    private void configureViewHolder3(ViewHolderMe vh1, int position) {
        vh1.getTime().setText(items.get(position).getTime());
        vh1.getChatText().setText(items.get(position).getText());
    }

    private void configureViewHolder2(ViewHolderYou vh1, int position) {
        vh1.getTime().setText(items.get(position).getTime());
        vh1.getChatText().setText(items.get(position).getText());
    }

    private void configureViewHolder1(ViewHolderDate vh1, int position) {
        vh1.getDate().setText(items.get(position).getText());
    }

}

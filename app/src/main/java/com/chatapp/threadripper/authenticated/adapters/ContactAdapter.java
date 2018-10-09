package com.chatapp.threadripper.authenticated.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.models.Contact;
import com.chatapp.threadripper.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactAdapter extends SelectableAdapter<ContactAdapter.ViewHolder> {

    private ArrayList<Contact> mArrayList;
    private Context mContext;
    private ContactAdapter.ViewHolder.ClickListener clickListener;


    public ContactAdapter(Context context, ArrayList<Contact> arrayList, ContactAdapter.ViewHolder.ClickListener clickListener) {
        this.mContext = context;
        this.clickListener = clickListener;

        if (arrayList != null) this.mArrayList = arrayList;
        else this.mArrayList = new ArrayList<>();
    }

    public void setArrayList(ArrayList<Contact> arrayList) {
        this.mArrayList.clear();
        this.mArrayList.addAll(arrayList);
        this.notifyDataSetChanged();
    }

    public void addItem(Contact item) {
        this.mArrayList.add(item);
        this.notifyItemChanged(this.mArrayList.size()-1);
    }

    // Create new views
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_contact, null);

        ContactAdapter.ViewHolder viewHolder = new ContactAdapter.ViewHolder(itemLayoutView, clickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder viewHolder, int position) {

        viewHolder.tvName.setText(mArrayList.get(position).getName());

        // load avatar
        ImageLoader.loadUserAvatar(viewHolder.cirImgUserAvatar, mArrayList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView tvName;
        public CircleImageView cirImgUserAvatar;
        private ContactAdapter.ViewHolder.ClickListener listener;
        //private final View selectedOverlay;


        public ViewHolder(View itemLayoutView, ContactAdapter.ViewHolder.ClickListener listener) {
            super(itemLayoutView);

            this.listener = listener;

            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_user_name);
            cirImgUserAvatar = (CircleImageView) itemLayoutView.findViewById(R.id.cirImgUserAvatar);

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
            public void onItemClicked(int position);

            public boolean onItemLongClicked(int position);

            boolean onCreateOptionsMenu(Menu menu);
        }
    }
}

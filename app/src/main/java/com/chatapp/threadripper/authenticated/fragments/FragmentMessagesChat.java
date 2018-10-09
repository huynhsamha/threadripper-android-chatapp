package com.chatapp.threadripper.authenticated.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.ConversationActivity;
import com.chatapp.threadripper.authenticated.MainActivity;
import com.chatapp.threadripper.authenticated.models.MessagesChat;
import com.chatapp.threadripper.authenticated.adapters.MessagesChatAdapter;

import java.util.ArrayList;
import java.util.List;


public class FragmentMessagesChat extends Fragment implements MessagesChatAdapter.ViewHolder.ClickListener {
    private RecyclerView mRecyclerView;
    private MessagesChatAdapter mAdapter;
    private TextView tv_selection;

    public FragmentMessagesChat() {
        setHasOptionsMenu(true);
    }

    public void onCreate(Bundle a) {
        super.onCreate(a);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_chat, null, false);

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity) getActivity()).changeTitle(R.id.toolbar, "Messages");

        tv_selection = (TextView) view.findViewById(R.id.tv_selection);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MessagesChatAdapter(getContext(), setData(), this);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public List<MessagesChat> setData() {
        List<MessagesChat> data = new ArrayList<>();
        String name[] = {"Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris", "Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris"};
        String lastchat[] = {"Hi Laura Owens", "Hi there how are you", "Can we meet?", "Ow this awesome", "How are you?", "Ow this awesome", "How are you?", "Ow this awesome", "How are you?", "How are you?"};
        boolean online[] = {true, false, true, false, true, true, true, false, false, true};
        String img[] = {
                "http://2sao.vietnamnetjsc.vn/2016/07/01/23/15/xtm1a.jpg",
                "https://znews-photo-td.zadn.vn/w660/Uploaded/bpivpjbp/2018_08_20/mpen180803MB002__1.jpg",
                "http://nguoi-noi-tieng.com/photo/tieu-su-dien-vien-xa-thi-man-6850.jpg",
                "https://2sao.vietnamnetjsc.vn/images/2018/07/17/11/43/xa-thi-man-4.jpg",
                "https://cdn.iconscout.com/icon/free/png-256/avatar-369-456321.png",
                "https://cdn1.iconfinder.com/data/icons/business-charts/512/customer-512.png",
                "http://abc.com/abc.jpg",
                "http://abc.com/abc.jpg",
                "http://www.asiaone.com/sites/default/files/original_images/Sep2014/170914_charmaine_lollipop.jpgg",
                "http://abc.com/abc.jpg"
        };

        for (int i = 0; i < 10; i++) {
            MessagesChat messagesChat = new MessagesChat();
            messagesChat.setTime("5:04pm");
            messagesChat.setName(name[i]);
            messagesChat.setImage(img[i]);
            messagesChat.setOnline(online[i]);
            messagesChat.setLastChat(lastchat[i]);
            data.add(messagesChat);
        }
        return data;
    }

    @Override
    public void onItemClicked(int position) {
        startActivity(new Intent(getActivity(), ConversationActivity.class));
    }

    @Override
    public boolean onItemLongClicked(int position) {
        toggleSelection(position);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        if (mAdapter.getSelectedItemCount() > 0) {
            tv_selection.setVisibility(View.VISIBLE);
        } else
            tv_selection.setVisibility(View.GONE);


        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                tv_selection.setText("Delete (" + mAdapter.getSelectedItemCount() + ")");
            }
        });

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit, menu);
    }
}

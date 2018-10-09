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
import com.chatapp.threadripper.authenticated.adapters.MessagesChatAdapter;
import com.chatapp.threadripper.authenticated.adapters.VideoCallListAdapter;
import com.chatapp.threadripper.authenticated.models.Contact;
import com.chatapp.threadripper.authenticated.models.MessagesChat;

import java.util.ArrayList;
import java.util.List;


public class FragmentVideoCallList extends Fragment {
    private RecyclerView mRecyclerView;
    private VideoCallListAdapter mAdapter;

    public FragmentVideoCallList() {
        setHasOptionsMenu(true);
    }

    public void onCreate(Bundle a) {
        super.onCreate(a);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_call_list, null, false);

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity) getActivity()).changeTitle(R.id.toolbar, "Video Call");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new VideoCallListAdapter(getContext(), setData());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public List<Contact> setData() {
        List<Contact> data = new ArrayList<>();
        String name[] = {"Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris", "Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris"};
        String img[] = {
                "http://2sao.vietnamnetjsc.vn/2016/07/01/23/15/xtm1a.jpg",
                "https://znews-photo-td.zadn.vn/w660/Uploaded/bpivpjbp/2018_08_20/mpen180803MB002__1.jpg",
                "http://nguoi-noi-tieng.com/photo/tieu-su-dien-vien-xa-thi-man-6850.jpg",
                "https://2sao.vietnamnetjsc.vn/images/2018/07/17/11/43/xa-thi-man-4.jpg",
                "https://cdn.iconscout.com/icon/free/png-256/avatar-369-456321.png",
                "https://cdn1.iconfinder.com/data/icons/business-charts/512/customer-512.png",
                "http://abc.com/abc.jpg",
                "http://abc.com/abc.jpg",
                "http://abc.com/abc.jpg",
                "http://abc.com/abc.jpg"
        };

        for (int i = 0; i < 10; i++) {
            Contact contact = new Contact();
            contact.setName(name[i]);
            contact.setImage(img[i]);
            data.add(contact);
        }
        return data;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add, menu);
    }
}

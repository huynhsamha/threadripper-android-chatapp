package com.chatapp.threadripper.authenticated.fragments;

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

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.TestApiService;
import com.chatapp.threadripper.authenticated.LayoutFragmentActivity;
import com.chatapp.threadripper.authenticated.adapters.VideoCallListAdapter;

import java.util.ArrayList;


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
        ((LayoutFragmentActivity) getActivity()).changeTitle(R.id.toolbar, "Video Call");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rcvConversations);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new VideoCallListAdapter(getContext(), null);
        mRecyclerView.setAdapter(mAdapter);

        TestApiService.getInstance().getUsersList(new TestApiService.OnCompleteListener() {
            @Override
            public void onSuccess(ArrayList list) {
                mAdapter.setArrayList(list);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add, menu);
    }
}

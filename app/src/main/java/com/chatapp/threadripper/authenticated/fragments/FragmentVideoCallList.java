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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.authenticated.LayoutFragmentActivity;
import com.chatapp.threadripper.authenticated.SearchUsersActivity;
import com.chatapp.threadripper.authenticated.adapters.VideoCallListAdapter;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.ErrorResponse;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.ModelUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentVideoCallList extends Fragment {
    private RecyclerView mRecyclerView;
    private VideoCallListAdapter mAdapter;
    TextView tvNoAnyFriends;

    private RealmResults<User> friends;

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

        tvNoAnyFriends = (TextView) view.findViewById(R.id.tvNoAnyFriends);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rcvFriends);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        friends = CacheService.getInstance().retrieveCacheFriends();
        mAdapter = new VideoCallListAdapter(getContext(), friends);
        mRecyclerView.setAdapter(mAdapter);

        friends.addChangeListener(users -> {
            if (users.isEmpty()) {
                tvNoAnyFriends.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                tvNoAnyFriends.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        ApiService.getInstance().getFriends().enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(Call<List<Conversation>> call, Response<List<Conversation>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Conversation> items = (ArrayList<Conversation>) response.body();
                    if (items == null || items.isEmpty()) {
                        // no do anything

                    } else {

                        for (Conversation c : items) {
                            ModelUtils.parseConversationToFriend(c);
                        }
                    }

                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        showError(err.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError(e.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {
                showError(t.getMessage());
            }
        });

        return view;
    }

    void showError(String msg) {
        ((LayoutFragmentActivity) getActivity()).ShowErrorDialog(msg);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuIconAdd) {
            startActivity(new Intent(getContext(), SearchUsersActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {

        friends.removeAllChangeListeners();

        super.onDestroy();
    }
}

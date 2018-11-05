package com.chatapp.threadripper.authenticated.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.chatapp.threadripper.authenticated.VideoCallActivity;
import com.chatapp.threadripper.authenticated.adapters.VideoCallListAdapter;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.ErrorResponse;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.receivers.SocketReceiver;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ModelUtils;
import com.chatapp.threadripper.utils.Preferences;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentVideoCallList extends Fragment implements SocketReceiver.OnCallbackListener {

    Context mContext;

    private RecyclerView mRecyclerView;
    private VideoCallListAdapter mAdapter;
    TextView tvNoAnyFriends;
    private SwipeRefreshLayout swipeContainer;

    private RealmResults<User> friends;

    IntentFilter mIntentFilter;
    SocketReceiver mSocketReceiver;

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

        friends = CacheService.getInstance().retrieveCacheFriendsOnline();
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

        tvNoAnyFriends.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        // Pull to refresh
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright
        );

        swipeContainer.setOnRefreshListener(() -> {
            fetchFriends();
        });

        fetchFriends();

        initSocketReceiver();

        return view;
    }

    void updateStateFriends() {
        if (friends.isEmpty()) {
            tvNoAnyFriends.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            tvNoAnyFriends.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    void fetchFriends() {
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
                    }
                }

                updateStateFriends();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {
                try {
                    showError(t.getMessage());
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    updateStateFriends();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(mSocketReceiver, mIntentFilter);
    }

    void initSocketReceiver() {
        mSocketReceiver = new SocketReceiver();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_NEW_MESSAGE);
        mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_JOIN);
        mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_LEAVE);
        // mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_TYPING);
        // mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_READ);
        mIntentFilter.addAction(Constants.ACTION_STRING_RECEIVER_CALL);

        mSocketReceiver.setListener(this);
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

    @Override
    public void onNewMessage(Message message) {
        // no handle
    }

    @Override
    public void onTyping(String conversationId, String username, boolean typing) {
        // no handle
    }

    @Override
    public void onRead(String conversationId, String username) {
        // no handle
    }

    @Override
    public void onCall(User targetUser, String typeCalling, String channelId) {

        if (targetUser.getUsername().equals(Preferences.getCurrentUser().getUsername())) {
            // targetUser cannot be the current user
            return;
        }

        switch (typeCalling) {
            case Constants.CALLEE_ACCEPT_REQUEST_CALL:
                // don't have this case
                break;

            case Constants.CALLEE_REJECT_REQUEST_CALL:
                // don't have this case
                break;

            case Constants.CALLER_REQUEST_CALLING:
                // callee receive a calling request

                onCallComing(targetUser, channelId);

                break;

            case Constants.CALLER_CANCEL_REQUEST:
                // callee receive a cancel for calling request
                break;
        }
    }

    void onCallComing(User targetUser, String channelId) {
        if (!VideoCallActivity.isAvailable()) return;

        Intent intent = new Intent(mContext, VideoCallActivity.class);

        User user = new User();
        user.setUsername(targetUser.getUsername());
        user.setPhotoUrl(targetUser.getPhotoUrl());
        user.setDisplayName(targetUser.getDisplayName());
        user.setPrivateConversationId(targetUser.getPrivateConversationId());

        intent.putExtra(Constants.IS_CALLER_SIDE, false); // user who start a calling is a caller
        intent.putExtra(Constants.USER_MODEL, user);
        intent.putExtra(Constants.EXTRA_VIDEO_CHANNEL_TOKEN, channelId);

        mContext.startActivity(intent);
    }

}

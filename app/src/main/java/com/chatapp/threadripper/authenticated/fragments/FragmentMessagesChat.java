package com.chatapp.threadripper.authenticated.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.chatapp.threadripper.authenticated.adapters.HorizontalAvatarAdapter;
import com.chatapp.threadripper.authenticated.adapters.MessagesChatAdapter;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.receivers.SocketReceiver;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentMessagesChat extends Fragment implements SocketReceiver.OnCallbackListener {

    String TAG = "FragmentMessagesChat";

    private RecyclerView mRcvConversations, mRcvHorizontalAvatar;
    private MessagesChatAdapter mAdapterConversations;
    private HorizontalAvatarAdapter mAdapterHorizontalAvatar;
    private TextView tvNoAnyConversations, tvNoAnyFriends, tvLoading;
    private SwipeRefreshLayout swipeContainer;

    IntentFilter mIntentFilter;
    SocketReceiver mSocketReceiver;

    RealmResults<Conversation> conversations;
    RealmResults<User> onlineFriends;

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
        ((LayoutFragmentActivity) getActivity()).changeTitle(R.id.toolbar, "Messages");

        initViews(view);

        fetchConversations();

        initSocketReceiver();

        return view;
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

        mSocketReceiver.setListener(this);
    }

    void initViews(View view) {
        tvNoAnyConversations = (TextView) view.findViewById(R.id.tvNoAnyConversations);
        tvNoAnyFriends = (TextView) view.findViewById(R.id.tvNoAnyFriends);
        // tvLoading = (TextView) view.findViewById(R.id.tvLoading);

        // Friends Recycler View
        mRcvConversations = (RecyclerView) view.findViewById(R.id.rcvMessages);
        mRcvConversations.setHasFixedSize(true);
        mRcvConversations.setLayoutManager(new LinearLayoutManager(getContext()));

        conversations = CacheService.getInstance().retrieveCacheConversationsByLastActiveTime();

        mAdapterConversations = new MessagesChatAdapter(getContext(), conversations);
        mRcvConversations.setAdapter(mAdapterConversations);

        conversations.addChangeListener(conversations -> {
            if (conversations.isEmpty()) {
                tvNoAnyConversations.setVisibility(View.VISIBLE);
            } else {
                tvNoAnyConversations.setVisibility(View.GONE);
            }
        });

        // Horizontal Avatar Recycler View
        mRcvHorizontalAvatar = (RecyclerView) view.findViewById(R.id.rcvHorizontalAvatar);
        mRcvHorizontalAvatar.setHasFixedSize(true);
        mRcvHorizontalAvatar.setLayoutManager(new LinearLayoutManager(getContext()));

        onlineFriends = CacheService.getInstance().retrieveCacheFriendsOnline();

        mAdapterHorizontalAvatar = new HorizontalAvatarAdapter(getContext(), onlineFriends);
        mRcvHorizontalAvatar.setAdapter(mAdapterHorizontalAvatar);

        onlineFriends.addChangeListener(users -> {
            if (users.isEmpty()) {
                tvNoAnyFriends.setVisibility(View.VISIBLE);
            } else {
                tvNoAnyFriends.setVisibility(View.GONE);
            }
        });

        // Pull to refresh
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright
        );

        swipeContainer.setOnRefreshListener(this::fetchConversations);
    }

    void fetchConversations() {
        ApiService.getInstance().getConversations().enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(@NonNull Call<List<Conversation>> call, @NonNull Response<List<Conversation>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Conversation> items = (ArrayList<Conversation>) response.body();
                    if (items == null || items.isEmpty()) {
                        // no do anything
                    } else {
                        for (Conversation c : items) {
                            c.update();
                            CacheService.getInstance().addOrUpdateCacheConversation(c);
                        }
                    }

                } else {
                    // no do anything
                }

                if (conversations.isEmpty()) {
                    tvNoAnyConversations.setVisibility(View.VISIBLE);
                } else {
                    tvNoAnyConversations.setVisibility(View.GONE);
                }

                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Conversation>> call, @NonNull Throwable t) {
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuIconSeach) {
            startActivity(new Intent(getContext(), SearchUsersActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewMessage(Message message) {
        // handle and add message to Realm
        message.updateDateTime();
        if (!message.getUsername().equals(Preferences.getCurrentUser().getUsername())) {
            message.setYou(true);
        }
        CacheService.getInstance().addOrUpdateCacheMessage(message);

        // handle and add conversation to Realm
        String conversationId = message.getConversationId();
        CacheService.getInstance().updateLastMessageConversation(conversationId, message.getMessageId());
    }

    @Override
    public void onJoin(String username) {
        CacheService.getInstance().setUserOnlineOrOffline(username, true);
    }

    @Override
    public void onLeave(String username) {
        CacheService.getInstance().setUserOnlineOrOffline(username, false);
    }

    @Override
    public void onTyping(String conversationId, String username, boolean typing) {
        // no receive broadcast
    }
}

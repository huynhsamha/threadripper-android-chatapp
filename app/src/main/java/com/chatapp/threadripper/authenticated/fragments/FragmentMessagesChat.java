package com.chatapp.threadripper.authenticated.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.chatapp.threadripper.authenticated.ConversationActivity;
import com.chatapp.threadripper.authenticated.LayoutFragmentActivity;
import com.chatapp.threadripper.authenticated.SearchUsersActivity;
import com.chatapp.threadripper.authenticated.adapters.MessagesChatAdapter;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.receivers.SocketReceiver;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ModelUtils;
import com.chatapp.threadripper.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentMessagesChat extends Fragment implements
        MessagesChatAdapter.ViewHolder.ClickListener,
        SocketReceiver.OnCallbackListener {

    String TAG = "FragmentMessagesChat";

    private RecyclerView mRcvConversations;
    private MessagesChatAdapter mAdapterConversations;
    private TextView tvNoAnyConversations, tvNoAnyFriends, tvLoading;
    private SwipeRefreshLayout swipeContainer;
    boolean isLoadingConversations;

    IntentFilter mIntentFilter;
    SocketReceiver mSocketReceiver;

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

        setListener();

        isLoading();
        userCacheConversation();
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

    void userCacheConversation() {
        ArrayList<Conversation> cache = CacheService.getInstance().retrieveCacheConversations();
        if (cache.isEmpty()) {
            tvNoAnyConversations.setVisibility(View.VISIBLE);
        } else {
            tvNoAnyConversations.setVisibility(View.GONE);
            mAdapterConversations.setArrayList(cache);
        }
    }

    void initViews(View view) {
        tvNoAnyConversations = (TextView) view.findViewById(R.id.tvNoAnyConversations);
        tvNoAnyFriends = (TextView) view.findViewById(R.id.tvNoAnyFriends);
        tvLoading = (TextView) view.findViewById(R.id.tvLoading);

        // Groups and Friends
        mRcvConversations = (RecyclerView) view.findViewById(R.id.rcvConversations);
        mRcvConversations.setHasFixedSize(true);
        mRcvConversations.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapterConversations = new MessagesChatAdapter(getContext(), null, this);
        mRcvConversations.setAdapter(mAdapterConversations);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);


        tvLoading.setVisibility(View.VISIBLE);

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright
        );
    }

    void setListener() {
        swipeContainer.setOnRefreshListener(() -> {
            isLoading();
            fetchConversations();
        });
    }


    void isLoading() {
        // tvNoAnyConversations.setVisibility(View.GONE);
        isLoadingConversations = true;
    }

    void endLoadingCallbackOne(int id) {
        if (id == 1) isLoadingConversations = false;
        if (isLoadingConversations) return;
        // current is completely loading
        swipeContainer.setRefreshing(false);
        tvLoading.setVisibility(View.GONE);
    }

    void endFailLoading(int id) {
        if (id == 1) {
            if (mAdapterConversations.getItemCount() == 0) {
                // cache is empty
                tvNoAnyConversations.setVisibility(View.VISIBLE);
            }
        }
        endLoadingCallbackOne(id);
    }

    void endSuccessLoading(int id) {
        if (id == 1) {
            if (mAdapterConversations.getItemCount() == 0) {
                // cache is empty
                tvNoAnyConversations.setVisibility(View.GONE);
            }
        }
        endLoadingCallbackOne(id);
    }

    void updateFriendsList(ArrayList<Conversation> conversations) {
        ArrayList<User> friends = new ArrayList<>();
        for (Conversation conversation : conversations) {
            if (conversation.getListUser().size() == 2) { // Friend
                User user = conversation.getListUser().get(0);
                if (user.getUsername().equals(Preferences.getCurrentUser().getUsername())) {
                    user = conversation.getListUser().get(1); // 0th is me, 1st is friend
                }
                user.setRelationship(Constants.RELATIONSHIP_FRIEND);
                friends.add(user);
            }
        }
        updateCacheUser(friends);
    }

    void fetchConversations() {
        ApiService.getInstance().getConversations().enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(Call<List<Conversation>> call, Response<List<Conversation>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Conversation> conversations = (ArrayList<Conversation>) response.body();
                    if (conversations.isEmpty()) {
                        endFailLoading(1);
                    } else {
                        for (Conversation c : conversations) {
                            c.setConversationName(ModelUtils.getConversationName(c));
                            c.setPhotoUrl(ModelUtils.getConversationAvatar(c));
                            if (c.getLastMessage() != null) {
                                c.getLastMessage().updateDateTime();
                            }
                        }
                        endSuccessLoading(1);
                        mAdapterConversations.setArrayList(conversations);
                        updateCacheConversation(conversations);
                        updateFriendsList(conversations);
                    }
                } else {
                    endFailLoading(1);
                }
            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {
                endFailLoading(1);
            }
        });
    }

    void updateCacheConversation(ArrayList<Conversation> conversations) {
        new Thread(() -> {
            for (Conversation c : conversations) {
                CacheService.getInstance().addOrUpdateCacheConversation(c);
            }
        }).start();
    }

    void updateCacheUser(ArrayList<User> users) {
        new Thread(() -> {
            for (User user : users) {
                if (user.getUsername().equals(Preferences.getCurrentUser().getUsername())) continue;
                CacheService.getInstance().addOrUpdateCacheUser(user);
            }
        }).start();
    }

    @Override
    public void onItemClicked(int position) {
        Conversation item = mAdapterConversations.getItem(position);
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra(Constants.CONVERSATION_ID, item.getConversationId());
        intent.putExtra(Constants.CONVERSATION_NAME, ModelUtils.getConversationName(item));
        intent.putExtra(Constants.CONVERSATION_PHOTO, ModelUtils.getConversationAvatar(item));
        intent.putExtra(Constants.CONVERSATION_IS_ONLINE, ModelUtils.isOnlineGroup(item));
        startActivity(intent);
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
        Log.d(TAG, "onNewMessage: " + message.toString());
    }

    @Override
    public void onJoin(String username) {
        Log.d(TAG, "onJoin: " + username);
    }

    @Override
    public void onLeave(String username) {
        Log.d(TAG, "onLeave: " + username);
    }

    @Override
    public void onTyping(String conversationId, String username, boolean typing) {

    }
}

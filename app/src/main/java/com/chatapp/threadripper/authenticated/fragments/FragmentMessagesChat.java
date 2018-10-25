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

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


public class FragmentMessagesChat extends Fragment implements SocketReceiver.OnCallbackListener {

    String TAG = "FragmentMessagesChat";

    private RecyclerView mRcvConversations;
    private MessagesChatAdapter mAdapterConversations;
    private TextView tvNoAnyConversations, tvNoAnyFriends, tvLoading;
    private SwipeRefreshLayout swipeContainer;

    IntentFilter mIntentFilter;
    SocketReceiver mSocketReceiver;

    RealmResults<Conversation> conversations;

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

        mAdapterConversations.notifyDataSetChanged();
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
        tvLoading = (TextView) view.findViewById(R.id.tvLoading);

        // Friends Recycler View
        mRcvConversations = (RecyclerView) view.findViewById(R.id.rcvConversations);
        mRcvConversations.setHasFixedSize(true);
        mRcvConversations.setLayoutManager(new LinearLayoutManager(getContext()));

        conversations = CacheService.getInstance().retrieveCacheConversations();

        mAdapterConversations = new MessagesChatAdapter(getContext(), conversations);
        mRcvConversations.setAdapter(mAdapterConversations);

        conversations.addChangeListener((conversations, changeSet) -> {
            if (conversations.isEmpty()) {
                tvNoAnyConversations.setVisibility(View.VISIBLE);
            } else {
                tvNoAnyConversations.setVisibility(View.GONE);
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

    void endFailLoading() {
        swipeContainer.setRefreshing(false);
        tvLoading.setVisibility(View.GONE);
    }

    void endSuccessLoading() {
        swipeContainer.setRefreshing(false);
        tvLoading.setVisibility(View.GONE);

        mAdapterConversations.notifyDataSetChanged();
    }

    void fetchConversations() {
        ApiService.getInstance().getConversations().enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(@NonNull Call<List<Conversation>> call, @NonNull Response<List<Conversation>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Conversation> items = (ArrayList<Conversation>) response.body();
                    if (items == null || items.isEmpty()) {
                        endFailLoading();
                    } else {
                        for (Conversation c : items) {
                            c.update();
                            CacheService.getInstance().addOrUpdateCacheConversation(c);
                        }
                        endSuccessLoading();
                    }
                } else {
                    endFailLoading();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Conversation>> call, @NonNull Throwable t) {
                endFailLoading();
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

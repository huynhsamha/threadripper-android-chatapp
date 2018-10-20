package com.chatapp.threadripper.authenticated.fragments;

import android.content.Intent;
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
import com.chatapp.threadripper.authenticated.ConversationActivity;
import com.chatapp.threadripper.authenticated.LayoutFragmentActivity;
import com.chatapp.threadripper.authenticated.SearchUsersActivity;
import com.chatapp.threadripper.authenticated.adapters.MessagesChatAdapter;
import com.chatapp.threadripper.authenticated.adapters.SearchUsersAdapter;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ModelUtils;
import com.chatapp.threadripper.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentMessagesChat extends Fragment implements MessagesChatAdapter.ViewHolder.ClickListener {
    private RecyclerView mRcvGroups, mRcvPeople;
    private MessagesChatAdapter mAdapterGroups;
    private SearchUsersAdapter mAdapterPeople;
    private TextView tv_selection, tvNoAnyFriends, tvLoading;
    private SwipeRefreshLayout swipeContainer;
    boolean isLoadingFriends, isLoadingPeople;


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
        useCacheUser();
        userCacheConversation();
        fetchConversations();
        fetchPeople();

        return view;
    }

    void useCacheUser() {
        ArrayList<User> cacheUsers = CacheService.getInstance().retrieveCacheNotFriends();
        if (cacheUsers.isEmpty()) {

        } else {
            mAdapterPeople.setArrayList(cacheUsers);
        }
    }

    void userCacheConversation() {
        ArrayList<Conversation> cache = CacheService.getInstance().retrieveCacheConversations();
        if (cache.isEmpty()) {
            tvNoAnyFriends.setVisibility(View.VISIBLE);
        } else {
            tvNoAnyFriends.setVisibility(View.GONE);
            mAdapterGroups.setArrayList(cache);
        }
    }

    void initViews(View view) {
        // tv_selection = (TextView) view.findViewById(R.id.tv_selection);
        tvNoAnyFriends = (TextView) view.findViewById(R.id.tvNoAnyFriends);
        tvLoading = (TextView) view.findViewById(R.id.tvLoading);

        // Groups and Friends
        mRcvGroups = (RecyclerView) view.findViewById(R.id.rcvGroups);
        mRcvGroups.setHasFixedSize(true);
        mRcvGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapterGroups = new MessagesChatAdapter(getContext(), null, this);
        mRcvGroups.setAdapter(mAdapterGroups);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // People
        mRcvPeople = (RecyclerView) view.findViewById(R.id.rcvPeople);
        mRcvPeople.setHasFixedSize(true);
        mRcvPeople.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapterPeople = new SearchUsersAdapter(getContext(), null);
        mRcvPeople.setAdapter(mAdapterPeople);



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
            fetchPeople();
        });
    }


    void isLoading() {
        // tvNoAnyFriends.setVisibility(View.GONE);
        isLoadingFriends = true;
        isLoadingPeople = true;
    }

    void endLoadingCallbackOne(int id) {
        if (id == 1) isLoadingFriends = false;
        if (id == 2) isLoadingPeople = false;
        if (isLoadingPeople || isLoadingFriends) return;
        // current is complete
        swipeContainer.setRefreshing(false);
        tvLoading.setVisibility(View.GONE);
    }

    void endFailLoading(int id) {
        if (id == 1) tvNoAnyFriends.setVisibility(View.VISIBLE);
        endLoadingCallbackOne(id);
    }

    void endSuccessLoading(int id) {
        if (id == 1) tvNoAnyFriends.setVisibility(View.GONE);
        endLoadingCallbackOne(id);
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
                        endSuccessLoading(1);
                        mAdapterGroups.setArrayList(conversations);
                        updateCacheConversation(conversations);
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

    void fetchPeople() {
        ApiService.getInstance().searchUsers("").enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    ArrayList<User> users = (ArrayList<User>) response.body();
                    if (users.isEmpty()) {
                        endFailLoading(2);
                    } else {
                        endSuccessLoading(2);
                        ArrayList<User> notFriends = new ArrayList<>();
                        for (User user: users) {
                            handleUserResponse(user);
                            if (user.getRelationship().equals(Constants.RELATIONSHIP_FRIEND)) continue;
                            notFriends.add(user);
                        }
                        // Show not friends
                        mAdapterPeople.setArrayList(notFriends);
                        // update new data
                        updateCacheUser(users);
                    }
                } else {
                    endFailLoading(2);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                endFailLoading(2);
            }
        });
    }

    void updateCacheConversation(ArrayList<Conversation> conversations) {
        new Thread(() -> {
            for (Conversation c: conversations) {
                CacheService.getInstance().addOrUpdateCacheConversation(c);
            }
        }).start();
    }

    void updateCacheUser(ArrayList<User> users) {
        new Thread(() -> {
            for (User user: users) {
                if (user.getUsername().equals(Preferences.getCurrentUser().getUsername())) continue;
                CacheService.getInstance().addOrUpdateCacheUser(user);
            }
        }).start();
    }

    void handleUserResponse(User user) {
        if (CacheService.getInstance().checkRelationFriend(user)) {
            user.setRelationship(Constants.RELATIONSHIP_FRIEND);
        };
    }

    @Override
    public void onItemClicked(int position) {
        Conversation item = mAdapterGroups.getItem(position);
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
}

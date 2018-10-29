package com.chatapp.threadripper.authenticated.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.chatapp.threadripper.authenticated.adapters.HorizontalAvatarAdapter;
import com.chatapp.threadripper.authenticated.adapters.MessagesChatAdapter;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.ErrorResponse;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.receivers.SocketReceiver;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ModelUtils;
import com.chatapp.threadripper.utils.Preferences;
import com.chatapp.threadripper.utils.TargetPrompt;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentMessagesChat extends Fragment implements SocketReceiver.OnCallbackListener {

    String TAG = "FragmentMessagesChat";

    Context mContext;

    private RecyclerView mRcvConversations, mRcvHorizontalAvatar;
    private MessagesChatAdapter mAdapterConversations;
    private HorizontalAvatarAdapter mAdapterHorizontalAvatar;
    private TextView tvNoAnyConversations, tvNoAnyFriends;
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

        fetchFriends();

        initSocketReceiver();

        return view;
    }

    void checkRunWalkThrough() {
        // showWalkThroughSearch(() -> {
        //     showWalkThroughMenu(() -> {
        //
        //     });
        // });
        if (Preferences.isFirstUseApp()) {
            showWalkThroughSearch(() -> {
                Preferences.setFirstUseApp(false);
                CacheService.getInstance().syncPreferencesInCache();
            });
        }
    }

    interface SimpleCallback {
        void onComplete();
    }

    void showWalkThroughSearch(SimpleCallback cb) {
        TargetPrompt.promptTargetWhite(mContext, R.id.menuIconAdd,
                "New Conversation",
                "Tap to search your friends and create new conversations for a funny chat",
                new TargetPrompt.OnCallbackListener() {
                    @Override
                    public void onAccepted() {
                        cb.onComplete();
                    }

                    @Override
                    public void onDenied() {
                        cb.onComplete();
                    }
                });
    }
    // void showWalkThroughMenu(SimpleCallback cb) {
    //     TargetPrompt.prompt(mContext, android.R.id.home,
    //             "Change password",
    //             "You can change your current password with a new password",
    //             new TargetPrompt.OnCallbackListener() {
    //                 @Override
    //                 public void onAccepted() {
    //                     cb.onComplete();
    //                 }
    //
    //                 @Override
    //                 public void onDenied() {
    //                     cb.onComplete();
    //                 }
    //             });
    //
    // }

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

    void initViews(View view) {

        tvNoAnyConversations = (TextView) view.findViewById(R.id.tvNoAnyConversations);
        tvNoAnyFriends = (TextView) view.findViewById(R.id.tvNoAnyFriends);

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
                mRcvConversations.setVisibility(View.GONE);
            } else {
                tvNoAnyConversations.setVisibility(View.GONE);
                mRcvConversations.setVisibility(View.VISIBLE);
            }
        });

        tvNoAnyConversations.setVisibility(View.VISIBLE);
        mRcvConversations.setVisibility(View.GONE);

        // Horizontal Avatar Recycler View
        mRcvHorizontalAvatar = (RecyclerView) view.findViewById(R.id.rcvHorizontalAvatar);
        mRcvHorizontalAvatar.setHasFixedSize(true);
        mRcvHorizontalAvatar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        onlineFriends = CacheService.getInstance().retrieveCacheFriendsOnline();

        mAdapterHorizontalAvatar = new HorizontalAvatarAdapter(getContext(), onlineFriends);
        mRcvHorizontalAvatar.setAdapter(mAdapterHorizontalAvatar);

        tvNoAnyFriends.setVisibility(View.VISIBLE);
        mRcvHorizontalAvatar.setVisibility(View.GONE);

        onlineFriends.addChangeListener(users -> {
            if (users.isEmpty()) {
                tvNoAnyFriends.setVisibility(View.VISIBLE);
                mRcvHorizontalAvatar.setVisibility(View.GONE);
            } else {
                tvNoAnyFriends.setVisibility(View.GONE);
                mRcvHorizontalAvatar.setVisibility(View.VISIBLE);
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

        swipeContainer.setOnRefreshListener(() -> {
            fetchFriends();
            fetchConversations();
        });
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
                        showError(e.getMessage());
                    }
                }

                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {
                showError(t.getMessage());
                swipeContainer.setRefreshing(false);
            }
        });

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
                            c.updateFromServer();

                            if (c.getListUser().size() == 2) {
                                ModelUtils.parseConversationToFriend(c);
                            }

                            CacheService.getInstance().addOrUpdateCacheConversation(c);
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

                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Conversation>> call, @NonNull Throwable t) {
                showError(t.getMessage());
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add, menu);

        new Handler().post(() -> {
            checkRunWalkThrough();
        });
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
    public void onTyping(String conversationId, String username, boolean typing) {
        // no receive broadcast
    }

    @Override
    public void onRead(String conversationId, String username) {
        // no receive broadcast
    }

    @Override
    public void onCall(User targetUser, String typeCalling, String channelId) {

        switch (typeCalling) {
            case Constants.CALLEE_ACCEPT_REQUEST_CALL:

                break;

            case Constants.CALLEE_REJECT_REQUEST_CALL:

                break;

            case Constants.CALLER_REQUEST_CALLING:

                if (targetUser.getUsername().equals(Preferences.getCurrentUser().getUsername()))
                    break;

                onCallComing(targetUser, channelId);

                break;

            case Constants.CALLER_CANCEL_REQUEST:

                break;
        }
    }

    void onCallComing(User targetUser, String channelId) {
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

    void showError(String msg) {
        ((LayoutFragmentActivity) getActivity()).ShowErrorDialog(msg);
    }

    @Override
    public void onDestroy() {

        conversations.removeAllChangeListeners();
        onlineFriends.removeAllChangeListeners();

        super.onDestroy();
    }
}

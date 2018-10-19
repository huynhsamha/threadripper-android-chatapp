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
import com.chatapp.threadripper.api.ApiResponseData;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.api.TestApiService;
import com.chatapp.threadripper.authenticated.ConversationActivity;
import com.chatapp.threadripper.authenticated.LayoutFragmentActivity;
import com.chatapp.threadripper.authenticated.SearchUsersActivity;
import com.chatapp.threadripper.authenticated.models.MessagesChat;
import com.chatapp.threadripper.authenticated.adapters.MessagesChatAdapter;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ModelUtils;
import com.chatapp.threadripper.utils.ShowToast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentMessagesChat extends Fragment implements MessagesChatAdapter.ViewHolder.ClickListener {
    private RecyclerView mRecyclerView;
    private MessagesChatAdapter mAdapter;
    private TextView tv_selection, tvNoAny, tvLoading;

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

        tv_selection = (TextView) view.findViewById(R.id.tv_selection);
        tvNoAny = (TextView) view.findViewById(R.id.tvNoAny);
        tvLoading = (TextView) view.findViewById(R.id.tvLoading);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MessagesChatAdapter(getContext(), null, this);
        mRecyclerView.setAdapter(mAdapter);

        fetchConversations();

        // List<String> usernames = new ArrayList<>();
        // usernames.add("hope68");
        // usernames.add("george.fay2760");
        // ApiService.getInstance().createConversation(usernames).enqueue(new Callback<ApiResponseData>() {
        //     @Override
        //     public void onResponse(Call<ApiResponseData> call, Response<ApiResponseData> response) {
        //         if (response.isSuccessful()) {
        //             ApiResponseData data = response.body();
        //         }
        //     }
        //
        //     @Override
        //     public void onFailure(Call<ApiResponseData> call, Throwable t) {
        //         t.printStackTrace();
        //     }
        // });

        return view;
    }

    void isLoading() {
        tvNoAny.setVisibility(View.GONE);
        tvLoading.setVisibility(View.VISIBLE);
    }

    void endFailLoading() {
        tvLoading.setVisibility(View.GONE);
        tvNoAny.setVisibility(View.VISIBLE);
    }

    void endSuccessLoading() {
        tvLoading.setVisibility(View.GONE);
        tvNoAny.setVisibility(View.GONE);
    }

    void fetchConversations() {
        isLoading();

        ApiService.getInstance().getConversations().enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(Call<List<Conversation>> call, Response<List<Conversation>> response) {
                if (response.isSuccessful()) {
                    endSuccessLoading();
                    List<Conversation> conversations = response.body();
                    mAdapter.setArrayList((ArrayList<Conversation>) conversations);
                } else {
                    endFailLoading();
                }
            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {
                endFailLoading();
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        Conversation item = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra(Constants.CONVERSATION_ID, item.getConversationId());
        intent.putExtra(Constants.CONVERSATION_NAME, ModelUtils.getConversationName(item));
        // intent.putExtra(Constants.CONVERSATION_PHOTO, );
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

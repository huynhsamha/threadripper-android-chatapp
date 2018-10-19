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
import com.chatapp.threadripper.api.TestApiService;
import com.chatapp.threadripper.authenticated.ConversationActivity;
import com.chatapp.threadripper.authenticated.LayoutFragmentActivity;
import com.chatapp.threadripper.authenticated.models.MessagesChat;
import com.chatapp.threadripper.authenticated.adapters.MessagesChatAdapter;
import com.chatapp.threadripper.utils.ShowToast;

import java.util.ArrayList;


public class FragmentMessagesChat extends Fragment implements MessagesChatAdapter.ViewHolder.ClickListener {
    private RecyclerView mRecyclerView;
    private MessagesChatAdapter mAdapter;
    private TextView tv_selection;

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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MessagesChatAdapter(getContext(), null, this);
        mRecyclerView.setAdapter(mAdapter);

        TestApiService.getInstance().getMessagesChatList(new TestApiService.OnCompleteListener() {
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

    @Override
    public void onItemClicked(int position) {
        MessagesChat item = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra("Username", item.getName());
        intent.putExtra("Image", item.getImage());
        intent.putExtra("IsOnline", item.getOnline());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClicked(int position) {
        toggleSelection(position);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        if (mAdapter.getSelectedItemCount() > 0) {
            tv_selection.setVisibility(View.VISIBLE);
        } else
            tv_selection.setVisibility(View.GONE);


        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                tv_selection.setText("Delete (" + mAdapter.getSelectedItemCount() + ")");
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
            ShowToast.lengthShort(getContext(), "OK");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.chatapp.threadripper.authenticated.fragments;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.authenticated.MainActivity;
import com.chatapp.threadripper.authenticated.models.Contact;
import com.chatapp.threadripper.authenticated.adapters.ContactAdapter;

import java.util.ArrayList;
import java.util.List;


public class FragmentContacts extends Fragment implements ContactAdapter.ViewHolder.ClickListener {
    private RecyclerView mRecyclerView;
    private ContactAdapter mAdapter;

    public FragmentContacts() {
        setHasOptionsMenu(true);
    }

    public void onCreate(Bundle a) {
        super.onCreate(a);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, null, false);

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity) getActivity()).changeTitle(R.id.toolbar, "Contacts");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ContactAdapter(getContext(), null, this);
        mRecyclerView.setAdapter(mAdapter);

        ApiService.getInstance().getContactsList(new ApiService.OnCompleteListener() {
            @Override
            public void onSuccess(ArrayList<Contact> contactsList) {
                mAdapter.setArrayList(contactsList);
            }

            @Override
            public void onFailure(String message) {

            }
        });

        return view;
    }

    @Override
    public void onItemClicked(int position) {

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
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add, menu);
    }
}

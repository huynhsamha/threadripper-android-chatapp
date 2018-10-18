package com.chatapp.threadripper.authenticated.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.chatapp.threadripper.authenticated.models.Contact;
//import com.chatapp.threadripper.authenticated.adapters.ContactAdapter;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.authenticated.LayoutFragmentActivity;


public class FragmentGroups extends Fragment {
    private RecyclerView mRecyclerView;
//    private ContactAdapter mAdapter;

    public FragmentGroups() {
        setHasOptionsMenu(true);
    }

    public void onCreate(Bundle a) {
        super.onCreate(a);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, null, false);

        getActivity().supportInvalidateOptionsMenu();
        ((LayoutFragmentActivity) getActivity()).changeTitle(R.id.toolbar, "Groups");

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add, menu);
    }
}

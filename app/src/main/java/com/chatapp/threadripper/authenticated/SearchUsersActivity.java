package com.chatapp.threadripper.authenticated;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.api.TestApiService;
import com.chatapp.threadripper.authenticated.adapters.SearchUsersAdapter;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.SweetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUsersActivity extends BaseMainActivity {

    RippleView rvSearch, rvBtnBack;
    EditText edtSearch;
    RecyclerView mRecyclerView;
    SearchUsersAdapter mAdapter;
    TextView tvNoAnyone, tvLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        initViews();
        setListeners();

        configHideKeyboardOnTouchOutsideEditText(findViewById(R.id.wrapperView));

        requestSearchUsers();
    }

    void isLoading() {
        rvBtnBack.setEnabled(false);
        rvSearch.setEnabled(false);
        edtSearch.setEnabled(false);
        tvLoading.setVisibility(View.VISIBLE);
    }

    void endLoading() {
        rvBtnBack.setEnabled(true);
        rvSearch.setEnabled(true);
        edtSearch.setEnabled(true);
        tvLoading.setVisibility(View.GONE);
    }

    void requestSearchUsers() {
        String keywords = edtSearch.getText().toString();

        isLoading();

        ApiService.getInstance().searchUsers(keywords).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                endLoading();
                if (response.isSuccessful()) {
                    List<User> users = response.body();

                    if (users.isEmpty()) {
                        tvNoAnyone.setVisibility(View.VISIBLE);
                    } else {
                        tvNoAnyone.setVisibility(View.GONE);
                        mAdapter.setArrayList((ArrayList<User>) users);
                    }

                } else {
                    endLoading();
                    tvNoAnyone.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                endLoading();
            }
        });
    }

    void initViews() {
        tvNoAnyone = (TextView) findViewById(R.id.tvNoAnyone);
        tvLoading = (TextView) findViewById(R.id.tvLoading);
        rvSearch = (RippleView) findViewById(R.id.rvSearch);
        rvBtnBack = (RippleView) findViewById(R.id.rvBtnBack);
        edtSearch = (EditText) findViewById(R.id.edtSearch);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchUsersAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
    }

    void setListeners() {
        rvBtnBack.setOnRippleCompleteListener(rippleView -> onBackPressed());

        rvSearch.setOnRippleCompleteListener(view -> requestSearchUsers());
    }
}

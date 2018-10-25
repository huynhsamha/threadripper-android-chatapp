package com.chatapp.threadripper.authenticated;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.authenticated.adapters.SearchUsersAdapter;
import com.chatapp.threadripper.models.User;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUsersActivity extends BaseMainActivity {

    RippleView rvSearch, rvBtnBack;
    EditText edtSearch;
    RecyclerView mRecyclerView;
    SearchUsersAdapter mAdapter;
    TextView tvNoAnyone, tvLoading;

    RealmResults<User> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        initViews();

        configHideKeyboardOnTouchOutsideEditText(findViewById(R.id.wrapperView));

        requestSearchUsers();

        initDetectNetworkStateChange();

        friends = CacheService.getInstance().retrieveCacheFriends();
    }

    void handleUserResponse(User user) {
        if (!friends.contains(user)) {
            CacheService.getInstance().addOrUpdateCacheUser(user);
            mAdapter.addItem(user);
        }
    }

    void requestSearchUsers() {
        String keywords = edtSearch.getText().toString();

        ApiService.getInstance().searchUsers(keywords).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {

                if (response.isSuccessful()) {
                    ArrayList<User> users = (ArrayList<User>) response.body();

                    if (users != null && users.isEmpty()) {
                        tvNoAnyone.setVisibility(View.VISIBLE);
                    } else {
                        tvNoAnyone.setVisibility(View.GONE);
                        mAdapter.clearAllItems();
                        if (users != null) {
                            for (User user: users) handleUserResponse(user);
                        }
                        if (mAdapter.getItemCount() == 0) {
                            tvNoAnyone.setVisibility(View.VISIBLE);
                        }
                    }

                } else {
                    tvNoAnyone.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
            }
        });
    }

    void initViews() {
        tvNoAnyone = (TextView) findViewById(R.id.tvNoAnyone);
        tvLoading = (TextView) findViewById(R.id.tvLoading);
        rvSearch = (RippleView) findViewById(R.id.rvSearch);
        rvBtnBack = (RippleView) findViewById(R.id.rvBtnBack);
        edtSearch = (EditText) findViewById(R.id.edtSearch);

        // Not Friends Recycler View
        mRecyclerView = (RecyclerView) findViewById(R.id.rcvMessages);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new SearchUsersAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        rvBtnBack.setOnRippleCompleteListener(rippleView -> onBackPressed());
        rvSearch.setOnRippleCompleteListener(view -> requestSearchUsers());

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                requestSearchUsers();
            }
        });
    }

}

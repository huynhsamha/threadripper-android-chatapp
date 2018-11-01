package com.chatapp.threadripper.authenticated;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.ApiResponseData;
import com.chatapp.threadripper.api.ApiService;
import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.authenticated.adapters.SearchUsersAdapter;
import com.chatapp.threadripper.authenticated.adapters.SelectedMemberAdapter;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.ErrorResponse;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Preferences;
import com.chatapp.threadripper.utils.SweetDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUsersActivity extends BaseMainActivity implements
        SearchUsersAdapter.OnSelectListener,
        SelectedMemberAdapter.OnClickRemoveListener {

    RippleView rvSearch, rvBtnBack;
    EditText edtSearch;

    RecyclerView mRcvSearchUser, mRcvSelectedMember;
    SelectedMemberAdapter mAdapterSelectedMembers;
    SearchUsersAdapter mAdapterSearchUser;
    RealmResults<User> selectedMembers;
    RealmResults<User> matchedUsers;

    TextView tvNoAnyone, tvLoading;

    RelativeLayout vMembersSelected;
    Button btnCreateConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        initViews();

        configHideKeyboardOnTouchOutsideEditText(findViewById(R.id.wrapperView));

        requestSearchUsers();

        initDetectNetworkStateChange();
    }

    void handleUserResponse(User user) {
        String username = user.getUsername();
        User cacheUser = CacheService.getInstance().retrieveCacheUser(username);
        if (cacheUser == null) {
            user.setMatched(true);
            CacheService.getInstance().addOrUpdateCacheUser(user);
        } else {
            CacheService.getInstance().setUserMatchedInSearching(username, true);
        }
    }

    void requestSearchUsers() {
        String keyword = edtSearch.getText().toString();

        ApiService.getInstance().searchUsers(keyword).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {

                for (User user : matchedUsers) {
                    CacheService.getInstance().setUserMatchedInSearching(user.getUsername(), false);
                }

                if (response.isSuccessful()) {
                    ArrayList<User> users = (ArrayList<User>) response.body();

                    if (users == null || users.isEmpty()) {

                    } else {
                        for (User user : users) handleUserResponse(user);
                    }
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

        vMembersSelected = (RelativeLayout) findViewById(R.id.vMembersSelected);
        btnCreateConversation = (Button) findViewById(R.id.btnCreateConversation);

        // All People searched Recycler View
        mRcvSearchUser = (RecyclerView) findViewById(R.id.rcvMessages);
        mRcvSearchUser.setHasFixedSize(true);
        mRcvSearchUser.setLayoutManager(new LinearLayoutManager(this));

        matchedUsers = CacheService.getInstance().retrieveCacheMatchedUsers();
        mAdapterSearchUser = new SearchUsersAdapter(this, matchedUsers, this);
        mRcvSearchUser.setAdapter(mAdapterSearchUser);

        matchedUsers.addChangeListener(users -> {
            if (users.isEmpty()) {
                tvNoAnyone.setVisibility(View.VISIBLE);
            } else {
                tvNoAnyone.setVisibility(View.GONE);
            }
        });

        // Selected Member Recycler View
        vMembersSelected.setVisibility(View.GONE);
        mRcvSelectedMember = (RecyclerView) findViewById(R.id.rcvSelectedMember);
        mRcvSelectedMember.setHasFixedSize(true);
        mRcvSelectedMember.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        selectedMembers = CacheService.getInstance().retrieveCacheSelectedMember();
        mAdapterSelectedMembers = new SelectedMemberAdapter(this, selectedMembers, this);
        mRcvSelectedMember.setAdapter(mAdapterSelectedMembers);

        selectedMembers.addChangeListener(users -> {
            if (users.isEmpty()) {
                vMembersSelected.setVisibility(View.GONE);
            } else {
                vMembersSelected.setVisibility(View.VISIBLE);
            }
        });

        rvBtnBack.setOnRippleCompleteListener(rippleView -> onBackPressed());
        rvSearch.setOnRippleCompleteListener(view -> requestSearchUsers());
        btnCreateConversation.setOnClickListener(view -> handleCreateConversation());

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

    void handleCreateConversation() {

        List<String> listUsername = new ArrayList<>();
        listUsername.add(Preferences.getCurrentUser().getUsername());
        for (User user : selectedMembers) {
            listUsername.add(user.getUsername());
        }

        SweetDialog.showLoading(this);

        ApiService.getInstance().createConversation(listUsername).enqueue(new Callback<ApiResponseData>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseData> call, @NonNull Response<ApiResponseData> response) {
                if (response.isSuccessful()) {
                    ApiResponseData data = response.body();
                    if (data != null) {

                        updateCacheData(data.getConversationId());

                    } else {
                        SweetDialog.hideLoading();
                        showError("An error occurred, please try again");
                    }
                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        SweetDialog.hideLoading();
                        showError(err.getMessage());

                    } catch (Exception e) {
                        e.printStackTrace();
                        SweetDialog.hideLoading();
                        showError(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseData> call, @NonNull Throwable t) {
                SweetDialog.hideLoading();
                showError(t.getMessage());
            }
        });
    }

    private void updateCacheData(String conversationId) {

        ApiService.getInstance().getConversation(conversationId).enqueue(new Callback<Conversation>() {
            @Override
            public void onResponse(@NonNull Call<Conversation> call, @NonNull Response<Conversation> response) {
                if (response.isSuccessful()) {

                    Conversation c = response.body();

                    if (c != null) {

                        c.updateFromServer();
                        CacheService.getInstance().addOrUpdateCacheConversation(c);

                        for (User user : selectedMembers) {
                            CacheService.getInstance().setUserSelected(user.getUsername(), false);
                        }

                        SweetDialog.hideLoading();

                        SweetDialog.showSuccessMessage(SearchUsersActivity.this, "Successful", "Conversation is created.");

                    } else {
                        SweetDialog.hideLoading();
                        showError("An error occurred, please try again");
                    }

                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        SweetDialog.hideLoading();
                        showError(err.getMessage());

                    } catch (Exception e) {
                        e.printStackTrace();
                        SweetDialog.hideLoading();
                        showError(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Conversation> call, @NonNull Throwable t) {
                SweetDialog.hideLoading();
                showError(t.getMessage());
            }
        });
    }

    private void showError(String msg) {
        SearchUsersActivity.this.ShowErrorDialog(msg);
    }


    @Override
    public void onSelectUser(User user, boolean isSelected) {
        if (user != null) {
            CacheService.getInstance().setUserSelectedAsync(user.getUsername(), isSelected);
        }
    }

    @Override
    public void onClickRemove(User user) {
        if (user != null) {
            CacheService.getInstance().setUserSelectedAsync(user.getUsername(), false);
        }
    }

    @Override
    public void onDestroy() {

        matchedUsers.removeAllChangeListeners();
        selectedMembers.removeAllChangeListeners();

        super.onDestroy();
    }
}

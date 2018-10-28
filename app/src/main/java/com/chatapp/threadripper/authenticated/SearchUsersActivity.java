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
import com.chatapp.threadripper.utils.Constants;
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
        CacheService.getInstance().addOrUpdateCacheUser(user);
        mAdapterSearchUser.addItem(user);
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
                        mAdapterSearchUser.clearAllItems();
                        if (users != null) {
                            for (User user : users) handleUserResponse(user);
                        }
                        if (mAdapterSearchUser.getItemCount() == 0) {
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

        vMembersSelected = (RelativeLayout) findViewById(R.id.vMembersSelected);
        btnCreateConversation = (Button) findViewById(R.id.btnCreateConversation);

        // All People searched Recycler View
        mRcvSearchUser = (RecyclerView) findViewById(R.id.rcvMessages);
        mRcvSearchUser.setHasFixedSize(true);
        mRcvSearchUser.setLayoutManager(new LinearLayoutManager(this));

        mAdapterSearchUser = new SearchUsersAdapter(this, null, this);
        mRcvSearchUser.setAdapter(mAdapterSearchUser);

        // Selected Member Recycler View
        mRcvSelectedMember = (RecyclerView) findViewById(R.id.rcvSelectedMember);
        mRcvSelectedMember.setHasFixedSize(true);
        mRcvSelectedMember.setLayoutManager(new LinearLayoutManager(this));

        mAdapterSelectedMembers = new SelectedMemberAdapter(this, null, this);


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
        for (User user : mAdapterSelectedMembers.getAll()) {
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
                        SweetDialog.hideLoading();

                        c.update();
                        CacheService.getInstance().addOrUpdateCacheConversation(c);

                        for (User user : mAdapterSelectedMembers.getAll()) {
                            user.setRelationship(Constants.RELATIONSHIP_FRIEND);
                            user.setSelectedMember(false); // reset to not selected
                            CacheService.getInstance().addOrUpdateCacheUser(user);
                        }

                        mAdapterSelectedMembers.notifyDataSetChanged();

                    } else {
                        SweetDialog.hideLoading();
                        showError("An error occurred, please try again");
                    }


                } else {
                    Gson gson = new Gson();
                    try {
                        ErrorResponse err = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
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
    public void onSelect(int position, boolean isSelected) {
        User user = mAdapterSearchUser.getItem(position);

        vMembersSelected.setVisibility(View.VISIBLE);
        mAdapterSelectedMembers.addItem(user);
    }

    @Override
    public void onClickRemove(int position) {
        User user = mAdapterSelectedMembers.getItem(position);

        mAdapterSelectedMembers.removeItem(user);
        mAdapterSearchUser.unSelectItem(user);

        if (mAdapterSelectedMembers.getAll().isEmpty()) {
            vMembersSelected.setVisibility(View.GONE);
        }
    }
}

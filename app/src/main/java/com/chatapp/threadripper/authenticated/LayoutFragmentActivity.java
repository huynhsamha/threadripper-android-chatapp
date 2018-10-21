package com.chatapp.threadripper.authenticated;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.api.CacheService;
import com.chatapp.threadripper.authenticated.fragments.FragmentContacts;
import com.chatapp.threadripper.authenticated.fragments.FragmentGroups;
import com.chatapp.threadripper.authenticated.fragments.FragmentMessagesChat;
import com.chatapp.threadripper.authenticated.fragments.FragmentVideoCallList;
import com.chatapp.threadripper.authentication.LoginActivity;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.ImageLoader;
import com.chatapp.threadripper.utils.Preferences;
import com.chatapp.threadripper.utils.SweetDialog;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class LayoutFragmentActivity extends BaseMainActivity implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView, navigationViewBottom;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_fragment);

        FragmentTransaction ft;

        // Default Fragment is Messages Chat Screen
        setupToolbar(R.id.toolbar, "Messages");

        FragmentMessagesChat fragmentMessagesChat = new FragmentMessagesChat();
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frameLayout, fragmentMessagesChat).commit();

        // Drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationViewBottom = (NavigationView) findViewById(R.id.nav_view_bottom);
        navigationViewBottom.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        configDrawerUserInfo();
    }

    void configDrawerUserInfo() {
        changeDrawerUserDisplayName(Preferences.getCurrentUser().getDisplayName());
        changeDrawerUserAvatar(Preferences.getCurrentUser().getPhotoUrl());
    }

    void changeDrawerUserAvatar(String url) {
        View navHeaderView = navigationView.getHeaderView(0);
        CircleImageView imgDrawerUserAvatar = (CircleImageView) navHeaderView.findViewById(R.id.imgDrawerUserAvatar);
        ImageLoader.loadUserAvatar(imgDrawerUserAvatar, url);
    }

    void changeDrawerUserDisplayName(String displayName) {
        View navHeaderView = navigationView.getHeaderView(0);
        TextView tvDrawerUsername = (TextView) navHeaderView.findViewById(R.id.tvDrawerUsername);
        tvDrawerUsername.setText(displayName);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Double back to exit
            this.setupDoubleBackToExit();
            // super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentTransaction ft;

        switch (item.getItemId()) {
            case R.id.nav_contacts:
                FragmentContacts fragmentContacts = new FragmentContacts();
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, fragmentContacts).addToBackStack(null).commit();
                break;
            case R.id.nav_chats:
                FragmentMessagesChat fragmentMessagesChat = new FragmentMessagesChat();
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, fragmentMessagesChat).commit();
                break;
            case R.id.nav_groups:
                FragmentGroups fragmentGroups = new FragmentGroups();
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, fragmentGroups).commit();
                break;
            case R.id.nav_call:
                FragmentVideoCallList fragmentVideoCallList = new FragmentVideoCallList();
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, fragmentVideoCallList).commit();
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_logout:
                SweetDialog.showWarningMessageWithCancel(this, "Alert",
                        "Are you sure to logout your current account?",
                        new SweetDialog.OnCallbackListener() {
                            @Override
                            public void onConfirm() {
                                handleLogout();
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void handleLogout() {
        Preferences.setChatAuthToken("");
        Preferences.setCurrentUser(new User());

        CacheService.getInstance().clearCacheTokenAndUser();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
}

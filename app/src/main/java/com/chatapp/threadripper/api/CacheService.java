package com.chatapp.threadripper.api;

import com.chatapp.threadripper.models.AppState;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.Preferences;


import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class CacheService {

    private Realm realm;
    private static CacheService instance;

    public static CacheService getInstance() {
        if (instance == null) instance = new CacheService();
        return instance;
    }

    private CacheService() {
        realm = Realm.getDefaultInstance();
    }

    public void close() {
        realm.close();
        instance = null;
    }

    public void clearAllCache() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        realm.close();
    }

    /**
     * Get Chat Auth Token, don't make public
     *
     * @return Chat Auth Token
     */
    private String getCacheAuthToken() {
        AppState state = realm.where(AppState.class).findFirst();
        if (state == null) return null;
        if (state.getCurrentUser().getUsername() == null ||
                state.getCurrentUser().getUsername().isEmpty()) return null;
        if (state.getChatAuthToken().isEmpty()) return null;
        return state.getChatAuthToken();
    }

    public boolean isConnected() {
        return getCacheAuthToken() != null;
    }


    /**
     * On run Splash screen, get AppState from Cache into RAM
     */
    public void syncPreferencesOnRAM() {
        AppState state = realm.where(AppState.class).findFirst();

        if (state != null) {
            Preferences.setCurrentUser(state.getCurrentUser());
            Preferences.setChatAuthToken(state.getChatAuthToken());
            Preferences.setFirstUseApp(state.isFirstUseApp());
            Preferences.setFirstUseProfileSettings(state.isFirstUseProfileSettings());
            Preferences.setFirstUseChatting(state.isFirstUseChatting());
            Preferences.setFirstUseVideoCall(state.isFirstUseVideoCall());
        }
    }

    /**
     * On runtime, maybe update state of user to cache, via AppState on RAM
     */
    public void syncPreferencesInCache() {
        realm.executeTransaction(realm -> {
            AppState state = realm.where(AppState.class).findFirst();

            if (state == null) { // create new Cache AppState
                state = new AppState();
            }

            state.setCurrentUser(Preferences.getCurrentUser());
            state.setChatAuthToken(Preferences.getChatAuthToken());
            state.setFirstUseApp(Preferences.isFirstUseApp());
            state.setFirstUseProfileSettings(Preferences.isFirstUseProfileSettings());
            state.setFirstUseChatting(Preferences.isFirstUseChatting());
            state.setFirstUseVideoCall(Preferences.isFirstUseVideoCall());

            realm.copyToRealmOrUpdate(state);
        });
    }

    public void addOrUpdateCacheUser(User user) {
        realm.executeTransaction(realm -> {
            realm.copyToRealmOrUpdate(user);
        });
    }

    public void addOrUpdateCacheConversation(Conversation conversation) {
        realm.executeTransaction(realm -> {
            realm.copyToRealmOrUpdate(conversation);
        });
    }

    public void addOrUpdateCacheMessage(Message message) {
        realm.executeTransaction(realm -> {
            realm.copyToRealmOrUpdate(message);
        });
    }

    public RealmResults<User> retrieveCacheUsers() {
        return realm.where(User.class).findAll();
    }

    public RealmResults<User> retrieveCacheNotFriends() {
        return realm.where(User.class)
                .not().equalTo("relationship", Constants.RELATIONSHIP_FRIEND)
                .findAll();
    }

    public RealmResults<User> retrieveCacheFriends() {
        return realm.where(User.class)
                .equalTo("relationship", Constants.RELATIONSHIP_FRIEND)
                .findAll();
    }

    public RealmResults<User> retrieveCacheFriendsOnline() {
        return realm.where(User.class)
                .equalTo("relationship", Constants.RELATIONSHIP_FRIEND)
                .sort("online", Sort.DESCENDING) // online first
                .findAll();
    }


    public RealmResults<Conversation> retrieveCacheConversations() {
        return realm.where(Conversation.class).findAll();
    }

    public RealmResults<Conversation> retrieveCacheConversationsByLastActiveTime() {
        return realm.where(Conversation.class)
                // .sort("lastMessage.dateTime", Sort.DESCENDING)
                .findAll();
    }

    public RealmResults<Message> retrieveCacheMessages(String conversationId) {
        return realm.where(Message.class)
                .equalTo("conversationId", conversationId)
                .sort("messageId", Sort.ASCENDING)
                .findAll();
    }

    public void setUserOnlineOrOffline(String username, boolean isOnline) {
        realm.executeTransaction(realm -> {
            User user = realm.where(User.class).equalTo("username", username).findFirst();
            user.setOnline(isOnline);

            realm.copyToRealmOrUpdate(user);
        });
    }

    public void updateLastMessageConversation(String conversationId, long lastMessageId) {
        realm.executeTransaction(realm -> {
            Conversation conversation = realm.where(Conversation.class).equalTo("conversationId", conversationId).findFirst();
            Message message = realm.where(Message.class).equalTo("messageId", lastMessageId).findFirst();
            conversation.setLastMessage(message);
            conversation.increaseNotificationCount();

            realm.copyToRealmOrUpdate(conversation);
        });
    }

    public void setReadAllMessagesConversation(String conversationId) {
        realm.executeTransaction(realm -> {
            Conversation conversation = realm.where(Conversation.class).equalTo("conversationId", conversationId).findFirst();
            conversation.setNotiCount(0);

            realm.copyToRealmOrUpdate(conversation);
        });
    }
}

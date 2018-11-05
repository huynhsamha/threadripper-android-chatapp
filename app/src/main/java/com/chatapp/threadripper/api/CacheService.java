package com.chatapp.threadripper.api;

import com.chatapp.threadripper.models.AppState;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.DateTimeUtils;
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
        instance = null;
    }

    /**
     * Get Chat Auth Token, don't make public
     *
     * @return Chat Auth Token
     */
    private String getCacheAuthToken() {
        AppState state = realm.where(AppState.class).findFirst();
        if (state == null) return null;
        if (state.getUsername() == null ||
                state.getUsername().isEmpty()) return null;
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
            Preferences.setCurrentUser(new User(
                            state.getUsername(), state.getEmail(), state.getPassword(),
                            state.getDisplayName(), state.getPhotoUrl()
                    )
            );

            Preferences.setChatAuthToken(state.getChatAuthToken());

            Preferences.setFirstUseApp(state.isFirstUseApp());
            Preferences.setFirstUseProfileSettings(state.isFirstUseProfileSettings());
            Preferences.setFirstUseChatting(state.isFirstUseChatting());
            Preferences.setFirstUseVideoCall(state.isFirstUseVideoCall());
        }
    }

    /**
     * On runtime, maybe updateFromServer state of user to cache, via AppState on RAM
     */
    public void syncPreferencesInCache() {
        realm.executeTransaction(realm -> {
            AppState state = realm.where(AppState.class).findFirst();

            if (state == null) { // create new Cache AppState
                state = new AppState();
            }

            state.setUsername(Preferences.getCurrentUser().getUsername());
            state.setDisplayName(Preferences.getCurrentUser().getDisplayName());
            state.setEmail(Preferences.getCurrentUser().getEmail());
            state.setPhotoUrl(Preferences.getCurrentUser().getPhotoUrl());

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

    public User retrieveCacheUser(String username) {
        return realm.where(User.class).equalTo("username", username).findFirst();
    }

    public RealmResults<User> retrieveCacheFriendsOnline() {
        return realm.where(User.class)
                .not().equalTo("username", Preferences.getCurrentUser().getUsername())
                .equalTo("relationship", Constants.RELATIONSHIP_FRIEND)
                .sort("online", Sort.DESCENDING) // online first
                .findAll();
    }

    public RealmResults<User> retrieveCacheSelectedMember() {
        return realm.where(User.class)
                .not().equalTo("username", Preferences.getCurrentUser().getUsername())
                .equalTo("isSelectedMember", true)
                .findAll();
    }

    public RealmResults<User> retrieveCacheMatchedUsers() {
        return realm.where(User.class)
                .equalTo("isMatched", true)
                .not().equalTo("username", Preferences.getCurrentUser().getUsername())
                .limit(20)
                .findAll();
    }

    public RealmResults<Conversation> retrieveCacheConversations() {
        return realm.where(Conversation.class).findAll();
    }

    public RealmResults<Conversation> retrieveCacheConversationsByLastActiveTime() {
        return realm.where(Conversation.class)
                /**
                 * TODO: sort by time of last message
                 * last message maybe null -> cannot sort by realm
                 * Trick: first sort by isYou (for null to bottom) -> next sort by time
                 */
                // .beginGroup()
                    // .isNotNull("lastMessage")
                    // .sort("lastMessage.dateTime", Sort.DESCENDING)
                // .endGroup()
                // .sort("lastMessage.dateTime", Sort.DESCENDING)
                // .sort("notiCount", Sort.DESCENDING)
                .sort("lastMessage.isYou", Sort.ASCENDING, "lastMessage.dateTime", Sort.DESCENDING)
                .findAll();
    }

    public Conversation retrieveCacheConversation(String conversationId) {
        return realm.where(Conversation.class).equalTo("conversationId", conversationId).findFirst();
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
            if (user != null) {
                user.setOnline(isOnline);
                realm.copyToRealmOrUpdate(user);
            }
        });
    }

    public void setUserMatchedInSearching(String username, boolean isMatched) {
        realm.executeTransaction(realm -> {
            User user = realm.where(User.class).equalTo("username", username).findFirst();
            if (user != null) {
                user.setMatched(isMatched);
                realm.copyToRealmOrUpdate(user);
            }
        });
    }

    public void setUserSelected(String username, boolean isSelected) {
        realm.executeTransaction(realm -> {
            User user = realm.where(User.class).equalTo("username", username).findFirst();
            if (user != null) {
                user.setSelectedMember(isSelected);
                realm.copyToRealmOrUpdate(user);
            }
        });
    }

    public void setUserSelectedAsync(String username, boolean isSelected) {
        realm.executeTransactionAsync(realm -> {
            User user = realm.where(User.class).equalTo("username", username).findFirst();
            if (user != null) {
                user.setSelectedMember(isSelected);
                realm.copyToRealmOrUpdate(user);
            }
        });
    }

    public void updateLastMessageConversation(String conversationId, long lastMessageId) {
        realm.executeTransaction(realm -> {
            Conversation conversation = realm.where(Conversation.class).equalTo("conversationId", conversationId).findFirst();
            if (conversation != null) {
                Message message = realm.where(Message.class).equalTo("messageId", lastMessageId).findFirst();
                conversation.setLastMessage(message);
                conversation.increaseNotificationCount();

                realm.copyToRealmOrUpdate(conversation);
            }
        });
    }

    public void setReadAllMessagesConversation(String conversationId) {
        realm.executeTransaction(realm -> {
            Conversation conversation = realm.where(Conversation.class).equalTo("conversationId", conversationId).findFirst();
            if (conversation != null) {
                conversation.setNotiCount(0);
                realm.copyToRealmOrUpdate(conversation);
            }
        });
    }

    public void updateDateTimeMessagesListAsync(String conversationId) {
        realm.executeTransactionAsync(realm -> {
            RealmResults<Message> messages = realm.where(Message.class)
                    .equalTo("conversationId", conversationId)
                    .sort("messageId", Sort.ASCENDING)
                    .findAll();
            Message lastMessage = null;
            for (Message message : messages) {
                if (lastMessage == null) message.setLeadingBlock(true);
                else {
                    int diffInMinutes = DateTimeUtils.differentInMinutes(lastMessage.getDateTime(), message.getDateTime());
                    if (diffInMinutes > 30) { // later 30 minutes
                        message.setLeadingBlock(true);
                    } else {
                        message.setLeadingBlock(false);
                    }
                }
                lastMessage = message;
                realm.copyToRealmOrUpdate(message);
            }
        });
    }
}

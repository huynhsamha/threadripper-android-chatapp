package com.chatapp.threadripper.api;

import com.chatapp.threadripper.cacheRealm.ConversationRealm;
import com.chatapp.threadripper.cacheRealm.MessageRealm;
import com.chatapp.threadripper.cacheRealm.PreferencesRealm;
import com.chatapp.threadripper.cacheRealm.UserRealm;
import com.chatapp.threadripper.models.Conversation;
import com.chatapp.threadripper.models.Message;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.Preferences;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class CacheService {

    private Realm realm;

    public static CacheService getInstance() {
        return new CacheService();
    }

    public CacheService() {
        realm = Realm.getDefaultInstance();
    }

    public String getCacheAuthToken() {
        PreferencesRealm cache = realm.where(PreferencesRealm.class).findFirst();
        if (cache == null) return null;
        if (cache.getCurrentUser().getUsername() == null ||
                cache.getCurrentUser().getUsername().isEmpty()) return null;
        if (cache.getChatAuthToken().isEmpty()) return null;
        return cache.getChatAuthToken();
    }

    public boolean isConnected() {
        return getCacheAuthToken() != null;
    }

    public void updatePreferenceOnRAM() {
        PreferencesRealm cache = realm.where(PreferencesRealm.class).findFirst();
        Preferences.setCurrentUser(new User(cache.getCurrentUser()));
        Preferences.setChatAuthToken(cache.getChatAuthToken());
    }

    public void updateCurrentUser(User user, String chatAuthToken) {
        realm.executeTransaction(realm -> {
            PreferencesRealm cache = new PreferencesRealm();
            cache.setCurrentUser(new UserRealm(user));
            cache.setChatAuthToken(chatAuthToken);
            realm.copyToRealmOrUpdate(cache);
        });
    }

    public void updateCurrentUser(User user) {
        realm.executeTransaction(realm -> {
            PreferencesRealm cache = new PreferencesRealm();
            cache.setCurrentUser(new UserRealm(user));
            cache.setChatAuthToken(Preferences.getChatAuthToken());
            realm.copyToRealmOrUpdate(cache);
        });
    }

    public void addOrUpdateCacheUser(User user) {
        realm.executeTransaction(realm -> {
            realm.copyToRealmOrUpdate(new UserRealm(user));
        });
    }

    public void addOrUpdateCacheConversation(Conversation conversation) {
        realm.executeTransaction(realm -> {
            realm.copyToRealmOrUpdate(new ConversationRealm(conversation));
        });
    }

    public void addOrUpdateCacheMessage(Message message) {
        realm.executeTransaction(realm -> {
            realm.copyToRealmOrUpdate(new MessageRealm(message));
        });
    }


    public ArrayList<User> retrieveCacheUsers() {
        RealmResults<UserRealm> list = realm.where(UserRealm.class).findAll();
        ArrayList<User> cacheUsers = new ArrayList<>();
        if (list != null) {
            for (UserRealm user : list) {
                cacheUsers.add(new User(user));
            }
        }
        return cacheUsers;
    }

    public ArrayList<User> retrieveCacheNotFriends() {
        RealmResults<UserRealm> list = realm.where(UserRealm.class)
                .not().equalTo("relationship", Constants.RELATIONSHIP_FRIEND).findAll();
        ArrayList<User> cacheUsers = new ArrayList<>();
        if (list != null) {
            for (UserRealm user : list) {
                cacheUsers.add(new User(user));
            }
        }
        return cacheUsers;
    }

    public ArrayList<User> retrieveCacheFriends() {
        RealmResults<UserRealm> list = realm.where(UserRealm.class)
                .equalTo("relationship", Constants.RELATIONSHIP_FRIEND).findAll();
        ArrayList<User> cacheUsers = new ArrayList<>();
        if (list != null) {
            for (UserRealm user : list) {
                cacheUsers.add(new User(user));
            }
        }
        return cacheUsers;
    }

    public ArrayList<Conversation> retrieveCacheConversations() {
        RealmResults<ConversationRealm> list = realm.where(ConversationRealm.class).findAll();
        ArrayList<Conversation> cache = new ArrayList<>();
        if (list != null) {
            for (ConversationRealm o : list) {
                cache.add(new Conversation(o));
            }
        }
        return cache;
    }

    public boolean checkRelationFriend(User user) {
        UserRealm cacheUser = realm.where(UserRealm.class)
                .equalTo("username", user.getUsername())
                .findFirst();

        if (cacheUser == null) {
            addOrUpdateCacheUser(user);
            return false;
        }

        return cacheUser.getRelationship().equals(Constants.RELATIONSHIP_FRIEND);
    }
}

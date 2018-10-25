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

    public void clearAllCache() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        realm.close();
    }

    /**
     * Get Chat Auth Token, don't make public
     * @return Chat Auth Token
     */
    private String getCacheAuthToken() {
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


    /**
     * On run Splash screen, get Preferences from Cache into RAM
     */
    public void syncPreferencesOnRAM() {
        PreferencesRealm cache = realm.where(PreferencesRealm.class).findFirst();

        if (cache != null) {
            Preferences.setCurrentUser(new User(cache.getCurrentUser()));
            Preferences.setChatAuthToken(cache.getChatAuthToken());
            Preferences.setFirstUseApp(cache.isFirstUseApp());
            Preferences.setFirstUseProfileSettings(cache.isFirstUseProfileSettings());
            Preferences.setFirstUseChatting(cache.isFirstUseChatting());
            Preferences.setFirstUseVideoCall(cache.isFirstUseVideoCall());
        }
    }

    /**
     * On runtime, maybe update state of user to cache, via Preferences on RAM
     */
    public void syncPreferencesInCache() {
        realm.executeTransaction(realm -> {
            PreferencesRealm cache = realm.where(PreferencesRealm.class).findFirst();

            if (cache != null) {
                cache.setCurrentUser(new UserRealm(Preferences.getCurrentUser()));
                cache.setChatAuthToken(Preferences.getChatAuthToken());
                cache.setFirstUseApp(Preferences.isFirstUseApp());
                cache.setFirstUseProfileSettings(Preferences.isFirstUseProfileSettings());
                cache.setFirstUseChatting(Preferences.isFirstUseChatting());
                cache.setFirstUseVideoCall(Preferences.isFirstUseVideoCall());
            }
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

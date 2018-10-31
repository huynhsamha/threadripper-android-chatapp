package com.chatapp.threadripper.utils;

public class Constants {

    public static final String CACHE_REALM_FILENAME = "threadripper.chatapp.db.cache.realm";

    public static final String USER_MODEL = "USER_MODEL";
    public static final String USER_USERNAME = "USER_USERNAME";
    public static final String USER_PHOTO_URL = "USER_PHOTO_URL";
    public static final String USER_DISPLAY_NAME = "USER_DISPLAY_NAME";

    public static final String CHAT_IMAGE_URL = "CHAT_IMAGE_URL";
    public static final String CHAT_IMAGE_BITMAP = "CHAT_IMAGE_BITMAP";
    public static final String CHAT_IS_TYPING_BOOLEAN = "CHAT_IS_TYPING_BOOLEAN";

    public static final String CONVERSATION_ID = "CONVERSATION_ID";
    public static final String CONVERSATION_NAME = "CONVERSATION_NAME";
    public static final String CONVERSATION_PHOTO = "CONVERSATION_PHOTO";
    public static final String CONVERSATION_IS_ONLINE = "CONVERSATION_IS_ONLINE";

    public static final String PLACEHOLDER_IMAGE_URL = "http://abc.com/xyz.jpg";

    public static final int REQUEST_CODE_PICK_IMAGE = 40001;
    public static final int REQUEST_CODE_PERMISSION_IMAGE_CAPTURE = 40002;
    public static final int REQUEST_CODE_CAPTURE_IMAGE = 40003;
    public static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL = 40004;
    public static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL = 40005;
    public static final int REQUEST_CODE_PICK_FILE = 40006;

    public static final String RELATIONSHIP_FRIEND = "FRIEND";
    public static final String RELATIONSHIP_SENT = "SENT";
    public static final String RELATIONSHIP_NONE = "NONE";

    public static final String ACTION_STRING_RECEIVER_NEW_MESSAGE = "ACTION_STRING_RECEIVER_NEW_MESSAGE";
    public static final String ACTION_STRING_RECEIVER_JOIN = "ACTION_STRING_RECEIVER_JOIN";
    public static final String ACTION_STRING_RECEIVER_LEAVE = "ACTION_STRING_RECEIVER_LEAVE";
    public static final String ACTION_STRING_RECEIVER_TYPING = "ACTION_STRING_RECEIVER_TYPING";
    public static final String ACTION_STRING_RECEIVER_READ = "ACTION_STRING_RECEIVER_READ";
    public static final String ACTION_STRING_RECEIVER_CALL = "ACTION_STRING_RECEIVER_CALL";

    public static final String MESSAGE_CHAT = "MESSAGE_CHAT";

    public static final String FRAGMENT_TAG_MESSAGE_CHAT_LIST = "FRAGMENT_TAG_MESSAGE_CHAT_LIST";
    public static final String FRAGMENT_TAG_VIDEO_CALL_LIST = "FRAGMENT_TAG_VIDEO_CALL_LIST";

    public static final String PLACEHOLDER_GROUP_AVATAR = "PLACEHOLDER_GROUP_AVATAR";

    public static final String EXTRA_VIDEO_CHANNEL_TOKEN = "EXTRA_VIDEO_CHANNEL_TOKEN";
    public static final String CALLING_VIDEO_OR_AUDIO = "CALLING_VIDEO_OR_AUDIO";
    public static final String IS_CALLER_SIDE = "IS_CALLER_SIDE";

    public static final String TYPE_CALLING = "TYPE_CALLING";
    public static final String CALLER_REQUEST_CALLING = "CALLER_REQUEST_CALLING";
    public static final String CALLER_CANCEL_REQUEST = "CALLER_CANCEL_REQUEST";
    public static final String CALLEE_ACCEPT_REQUEST_CALL = "CALLEE_ACCEPT_REQUEST_CALL";
    public static final String CALLEE_REJECT_REQUEST_CALL = "CALLEE_REJECT_REQUEST_CALL";
}

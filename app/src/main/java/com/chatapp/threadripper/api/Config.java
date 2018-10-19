package com.chatapp.threadripper.api;


public class Config {
    public final static String BASE_URL = "http://vre.hcmut.edu.vn/threadripper";
    // public final static String BASE_URL = "http://192.168.0.213:3000";
    // public final static String BASE_URL = "http://14.226.231.248";

    public final static String API_ROUTE = BASE_URL + "/api/";

    public final static String WEB_SOCKET_URL = BASE_URL;
    public final static String WEB_SOCKET_ENDPOINT = "/ws/websocket";
    public final static String WEB_SOCKET_FULL_PATH = WEB_SOCKET_URL + WEB_SOCKET_ENDPOINT;

}

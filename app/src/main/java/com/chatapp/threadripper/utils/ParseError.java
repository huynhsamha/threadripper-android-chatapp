package com.chatapp.threadripper.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ParseError {

    /**
     *
     * @param serverErrorMessage
     * @return message in JSON Object return from server
     */
    public static String getErrorMessage(String serverErrorMessage) {
        JSONObject json = null;
        String message = null;
        try {
            json = new JSONObject(serverErrorMessage);
            if (json.isNull("message"))
                message = "Something is wrong! Please try again!";
            message = json.get("message").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return  message;
        }
    }
}

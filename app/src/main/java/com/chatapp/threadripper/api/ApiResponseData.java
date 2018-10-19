package com.chatapp.threadripper.api;

import com.chatapp.threadripper.models.ErrorResponse;
import com.chatapp.threadripper.models.User;

import java.util.ArrayList;

public class ApiResponseData {

    /**
     * Contain anything which server response
     */

    ErrorResponse error;

    User user = new User();

    boolean success = false;
    boolean active = false;
    String message = "";
    String result = "";


    @Override
    public String toString() {
        return "Api Response Data";
    }

    /**
     * Getter & Setter
     */

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

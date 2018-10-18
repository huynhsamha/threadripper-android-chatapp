package com.chatapp.threadripper.api;

import com.chatapp.threadripper.models.ErrorResponse;

public class ApiResponseData {

    /**
     * Contain anything which server response
     */

    ErrorResponse error;

    boolean success = false;
    boolean active = false;
    String message = "";
    String result = "";


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

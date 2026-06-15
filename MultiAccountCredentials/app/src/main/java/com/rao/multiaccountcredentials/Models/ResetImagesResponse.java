package com.rao.multiaccountcredentials.Models;

import com.google.gson.annotations.SerializedName;

public class ResetImagesResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("error")
    private String error;

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public String getError() {
        return error;
    }
}

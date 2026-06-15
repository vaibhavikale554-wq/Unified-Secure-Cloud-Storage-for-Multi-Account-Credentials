package com.rao.multiaccountcredentials.Models;

import org.json.JSONObject;

public class CredentialItem {
    private String application;
    private JSONObject credentials;

    public CredentialItem(String application, JSONObject credentials) {
        this.application = application;
        this.credentials = credentials;
    }

    public String getApplication() {
        return application;
    }

    public JSONObject getCredentials() {
        return credentials;
    }
}

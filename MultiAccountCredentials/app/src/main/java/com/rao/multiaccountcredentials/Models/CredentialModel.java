package com.rao.multiaccountcredentials.Models;

public class CredentialModel {
    private String id;
    private String applicationName;
    private String url;
    private String firstField;
    private String data;

    public CredentialModel(String id, String applicationName, String url, String firstField, String data) {
        this.id = id;
        this.applicationName = applicationName;
        this.url = url;
        this.firstField = firstField;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getUrl() {
        return url;
    }

    public String getFirstField() {
        return firstField;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return applicationName; // For adapter display
    }
}

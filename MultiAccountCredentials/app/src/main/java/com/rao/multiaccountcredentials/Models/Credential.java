package com.rao.multiaccountcredentials.Models;

public class Credential {
    private String appName;
    private String username;
    private String unique_id;

    public Credential(String appName, String username, String unique_id) {
        this.appName = appName;
        this.username = username;
        this.unique_id = unique_id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getAppName() {
        return appName;
    }

    public String getUsername() {
        return username;
    }
}

package com.rao.multiaccountcredentials.Models;

public class ApplicationModel {
    private String name;
    private String iconUrl;
    private String data;

    public ApplicationModel(String name, String iconUrl, String data) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

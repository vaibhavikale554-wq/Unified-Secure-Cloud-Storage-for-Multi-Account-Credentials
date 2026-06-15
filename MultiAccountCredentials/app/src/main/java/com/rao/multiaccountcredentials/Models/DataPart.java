package com.rao.multiaccountcredentials.Models;

public class DataPart {
    private String fileName;
    private byte[] content;
    private String type;

    // Constructor that takes a content type
    public DataPart(String fileName, byte[] content, String type) {
        this.fileName = fileName;
        this.content = content;
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public String getType() {
        return type;
    }
}
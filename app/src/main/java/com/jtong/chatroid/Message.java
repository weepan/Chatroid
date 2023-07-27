package com.jtong.chatroid;

public class Message {
    private String content;
    private final long timestamp;
    private final boolean isSentByUser;

    public Message(String content, long timestamp, boolean isSentByUser) {
        this.content = content;
        this.timestamp = timestamp;
        this.isSentByUser = isSentByUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }
}


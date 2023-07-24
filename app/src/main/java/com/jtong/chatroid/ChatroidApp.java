package com.jtong.chatroid;

import android.app.Application;

public class ChatroidApp extends Application {
    private String globalVariable;

    public String getGlobalVariable() {
        return globalVariable;
    }

    public void setGlobalVariable(String value) {
        globalVariable = value;
    }
}

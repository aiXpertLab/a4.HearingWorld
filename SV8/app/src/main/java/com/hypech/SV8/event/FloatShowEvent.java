package com.hypech.SV8.event;

public class FloatShowEvent {
    public String getChatStr() {
        return chatStr;
    }

    public void setChatStr(String chatStr) {
        this.chatStr = chatStr;
    }

    String chatStr;

    public FloatShowEvent(String msg){
        chatStr = msg;
    }
}

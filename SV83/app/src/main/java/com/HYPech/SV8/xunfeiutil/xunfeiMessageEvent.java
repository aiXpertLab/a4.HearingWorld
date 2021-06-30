package com.HYPech.SV8.xunfeiutil;

public class xunfeiMessageEvent {

    private String message;
    private boolean isFinal;
    private String uid;
    public xunfeiMessageEvent(String msg,boolean ifFinal,String muid){
        message = msg;
        isFinal = ifFinal;
        uid = muid;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



}

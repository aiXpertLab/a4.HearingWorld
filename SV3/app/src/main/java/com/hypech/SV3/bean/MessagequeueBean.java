package com.hypech.SV3.bean;


import com.hypech.SV3.util.ConvertData;

public class MessagequeueBean extends MessagequeueBaseBean {
    private String text;
    private int numId;
    private int count;
    private int page;
    private int dataLength;
    public MessagequeueBean(String text,int numId,int count,int page){
        this.text = text;
        this.numId = numId;
        this.count =count;
        this.page = page;

        dataLength = ConvertData.hexStringToBytes(ConvertData.str2HexStr(text,false)).length;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNumId() {
        return numId;
    }

    public void setNumId(int numId) {
        this.numId = numId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }


    public int getDataLength() {
        return dataLength;
    }


}

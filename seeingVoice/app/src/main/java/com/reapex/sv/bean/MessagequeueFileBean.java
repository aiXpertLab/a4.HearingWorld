package com.reapex.sv.bean;

public class MessagequeueFileBean extends MessagequeueBaseBean{

    private String text;
    private int count;
    private int page;

    public MessagequeueFileBean(String text,int count,int page){
        this.text = text;
        this.count = count ;
        this.page = page;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

}

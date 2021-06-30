package com.seeingvoice.www.svhearing.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Date:2019/7/31
 * Time:14:04
 * auther:zyy
 */
public class FeedbackBean implements Parcelable {


    /**
     * data : {}
     * error_code :
     * error_info :
     * message_code : A000000
     */

    private DataBean data;
    private String error_code;
    private String error_info;
    private String message_code;

    protected FeedbackBean(Parcel in) {
        error_code = in.readString();
        error_info = in.readString();
        message_code = in.readString();
    }

    public static final Creator<FeedbackBean> CREATOR = new Creator<FeedbackBean>() {
        @Override
        public FeedbackBean createFromParcel(Parcel in) {
            return new FeedbackBean(in);
        }

        @Override
        public FeedbackBean[] newArray(int size) {
            return new FeedbackBean[size];
        }
    };

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_info() {
        return error_info;
    }

    public void setError_info(String error_info) {
        this.error_info = error_info;
    }

    public String getMessage_code() {
        return message_code;
    }

    public void setMessage_code(String message_code) {
        this.message_code = message_code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(error_code);
        dest.writeString(error_info);
        dest.writeString(message_code);
    }

    public static class DataBean {
    }
}

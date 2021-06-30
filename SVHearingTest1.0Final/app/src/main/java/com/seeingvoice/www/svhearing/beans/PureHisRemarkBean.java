package com.seeingvoice.www.svhearing.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Date:2019/8/21
 * Time:16:45
 * auther:zyy
 */
public class PureHisRemarkBean implements Parcelable {

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

    protected PureHisRemarkBean(Parcel in) {
        error_code = in.readString();
        error_info = in.readString();
        message_code = in.readString();
        data = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Creator<PureHisRemarkBean> CREATOR = new Creator<PureHisRemarkBean>() {
        @Override
        public PureHisRemarkBean createFromParcel(Parcel in) {
            return new PureHisRemarkBean(in);
        }

        @Override
        public PureHisRemarkBean[] newArray(int size) {
            return new PureHisRemarkBean[size];
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
        dest.writeParcelable(data,0);
    }

    public static class DataBean implements Parcelable{
        protected DataBean(Parcel in) {
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel in) {
                return new DataBean(in);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }
    }
}

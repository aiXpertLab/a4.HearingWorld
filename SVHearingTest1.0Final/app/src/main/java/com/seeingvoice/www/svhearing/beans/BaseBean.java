package com.seeingvoice.www.svhearing.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Date:2019/8/15
 * Time:14:23
 * auther:zyy
 */
public class BaseBean implements Parcelable {

    BaseBean() {

    }

    private BaseBean(Parcel in) {
    }

    public static final Creator<BaseBean> CREATOR = new Creator<BaseBean>() {
        @Override
        public BaseBean createFromParcel(Parcel in) {
            return new BaseBean(in);
        }

        @Override
        public BaseBean[] newArray(int size) {
            return new BaseBean[size];
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

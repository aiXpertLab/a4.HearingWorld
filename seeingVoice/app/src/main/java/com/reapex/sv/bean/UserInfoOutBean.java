package com.reapex.sv.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfoOutBean implements Parcelable {
    private UserInfoBean user_info;

    protected UserInfoOutBean(Parcel in) {
        user_info = in.readParcelable(UserInfoBean.class.getClassLoader());
    }

    public static final Creator<UserInfoOutBean> CREATOR = new Creator<UserInfoOutBean>() {
        @Override
        public UserInfoOutBean createFromParcel(Parcel in) {
            return new UserInfoOutBean(in);
        }

        @Override
        public UserInfoOutBean[] newArray(int size) {
            return new UserInfoOutBean[size];
        }
    };

    public UserInfoBean getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfoBean user_info) {
        this.user_info = user_info;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user_info, flags);
    }
}

package com.hypech.SV8.bean;


import android.os.Parcel;
import android.os.Parcelable;

/**
 *  "uid": "40010",
 *             "user_name": "用户40010",
 *             "user_tel": "18406554524",
 *             "user_sex": "1",
 *             "user_age": "20",
 *             "user_headimgurl": "0",
 *             "user_province": "0",
 *             "user_city": "0",
 *             "user_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX25hbWUiOiJcdTc1MjhcdTYyMzc0MDAxMCIsInRpbWVzdGFtcCI6MTU3NDE0NDY1OX0.Ox--EfNDppGkFdl6frwJDdbEN0bh6_O3UhJazNv9TuA",
 *             "is_bind_wechat": 0,
 *             "is_bind_QQ": 0
 */
public class UserInfoBean implements Parcelable {

    private String  user_name;
    private String  user_tel;
    private String  user_headimgurl;
    private String  user_token;
    private String  is_bind_wechat;
    private String  is_bind_QQ;

    protected UserInfoBean(Parcel in) {
        user_name = in.readString();
        user_tel = in.readString();
        user_headimgurl = in.readString();
        user_token = in.readString();
        is_bind_wechat = in.readString();
        is_bind_QQ = in.readString();
    }

    public static final Creator<UserInfoBean> CREATOR = new Creator<UserInfoBean>() {
        @Override
        public UserInfoBean createFromParcel(Parcel in) {
            return new UserInfoBean(in);
        }

        @Override
        public UserInfoBean[] newArray(int size) {
            return new UserInfoBean[size];
        }
    };

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_tel() {
        return user_tel;
    }

    public void setUser_tel(String user_tel) {
        this.user_tel = user_tel;
    }

    public String getUser_headimgurl() {
        return user_headimgurl;
    }

    public void setUser_headimgurl(String user_headimgurl) {
        this.user_headimgurl = user_headimgurl;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public String getIs_bind_wechat() {
        return is_bind_wechat;
    }

    public void setIs_bind_wechat(String is_bind_wechat) {
        this.is_bind_wechat = is_bind_wechat;
    }

    public String getIs_bind_QQ() {
        return is_bind_QQ;
    }

    public void setIs_bind_QQ(String is_bind_QQ) {
        this.is_bind_QQ = is_bind_QQ;
    }


    @Override
    public String toString() {
        return "userInfoBean:"+"userName:"+
                user_name+"phone:"+user_tel+
                "token:"+user_token+"headImage:"+
                user_headimgurl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

            dest.writeString(user_name);
            dest.writeString(user_tel);
            dest.writeString(user_headimgurl);
            dest.writeString(user_token);
            dest.writeString(is_bind_wechat);
        dest.writeString(is_bind_QQ);

    }
}

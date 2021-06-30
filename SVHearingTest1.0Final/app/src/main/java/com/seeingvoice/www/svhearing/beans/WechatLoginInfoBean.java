package com.seeingvoice.www.svhearing.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Date:2019/8/13
 * Time:9:20
 * auther:zyy
 */
public class WechatLoginInfoBean extends BaseBean implements Parcelable{
    /**
     * message_code : A000000
     * error_code :
     * error_info :
     * data : {"user_info":[{"uid":11,"user_openid":"o0CtX598yQ8KrxUXLg_Dnk9ZMZQY","user_name":".","user_sex":2,"user_tel":null,"user_age":null,"user_headimgurl":"http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83ers1uXDAmOze8ElgEicgQr7ttqGVkTv2UJEKuWgD443FW1jngc6v2RyQrOIWXBJost1ef1r2XaibqYA/132","user_province":"Shanxi","user_city":"Taiyuan","user_country":"CN","is_verify_tel":null}]}
     */

    private String message_code;
    private String error_code;
    private String error_info;
    private DataBean data;

    private WechatLoginInfoBean(Parcel in) {
        message_code = in.readString();
        error_code = in.readString();
        error_info = in.readString();
        data = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Creator<WechatLoginInfoBean> CREATOR = new Creator<WechatLoginInfoBean>() {
        @Override
        public WechatLoginInfoBean createFromParcel(Parcel in) {
            return new WechatLoginInfoBean(in);
        }

        @Override
        public WechatLoginInfoBean[] newArray(int size) {
            return new WechatLoginInfoBean[size];
        }
    };

    public String getMessage_code() {
        return message_code;
    }

    public void setMessage_code(String message_code) {
        this.message_code = message_code;
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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message_code);
        dest.writeString(error_code);
        dest.writeString(error_info);
        dest.writeParcelable(data,0);
    }

    public static class DataBean implements Parcelable {
        private List<UserInfoBean> user_info;

        protected DataBean(Parcel in) {
            user_info = in.readArrayList(Thread.currentThread().getContextClassLoader());
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

        public List<UserInfoBean> getUser_info() {
            return user_info;
        }

        public void setUser_info(List<UserInfoBean> user_info) {
            this.user_info = user_info;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(user_info);
        }

        public static class UserInfoBean implements Parcelable{
            /**
             * uid : 11
             * user_openid : o0CtX598yQ8KrxUXLg_Dnk9ZMZQY
             * user_name : .
             * user_sex : 2
             * user_tel : null
             * user_age : null
             * user_headimgurl : http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83ers1uXDAmOze8ElgEicgQr7ttqGVkTv2UJEKuWgD443FW1jngc6v2RyQrOIWXBJost1ef1r2XaibqYA/132
             * user_province : Shanxi
             * user_city : Taiyuan
             * user_country : CN
             * is_verify_tel : null
             */

            private int uid;
            private String user_openid;
            private String user_name;
            private int user_sex;
            private Object user_tel;
            private Object user_age;
            private String user_headimgurl;
            private String user_province;
            private String user_city;
            private String user_country;
            private Object is_verify_tel;

            private UserInfoBean(Parcel in) {
                uid = in.readInt();
                user_openid = in.readString();
                user_name = in.readString();
                user_sex = in.readInt();
                user_headimgurl = in.readString();
                user_province = in.readString();
                user_city = in.readString();
                user_country = in.readString();
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

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public String getUser_openid() {
                return user_openid;
            }

            public void setUser_openid(String user_openid) {
                this.user_openid = user_openid;
            }

            public String getUser_name() {
                return user_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }

            public int getUser_sex() {
                return user_sex;
            }

            public void setUser_sex(int user_sex) {
                this.user_sex = user_sex;
            }

            public Object getUser_tel() {
                return user_tel;
            }

            public void setUser_tel(Object user_tel) {
                this.user_tel = user_tel;
            }

            public Object getUser_age() {
                return user_age;
            }

            public void setUser_age(Object user_age) {
                this.user_age = user_age;
            }

            public String getUser_headimgurl() {
                return user_headimgurl;
            }

            public void setUser_headimgurl(String user_headimgurl) {
                this.user_headimgurl = user_headimgurl;
            }

            public String getUser_province() {
                return user_province;
            }

            public void setUser_province(String user_province) {
                this.user_province = user_province;
            }

            public String getUser_city() {
                return user_city;
            }

            public void setUser_city(String user_city) {
                this.user_city = user_city;
            }

            public String getUser_country() {
                return user_country;
            }

            public void setUser_country(String user_country) {
                this.user_country = user_country;
            }

            public Object getIs_verify_tel() {
                return is_verify_tel;
            }

            public void setIs_verify_tel(Object is_verify_tel) {
                this.is_verify_tel = is_verify_tel;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(uid);
                dest.writeString(user_openid);
                dest.writeString(user_name);
                dest.writeInt(user_sex);
                dest.writeString(user_headimgurl);
                dest.writeString(user_province);
                dest.writeString(user_city);
                dest.writeString(user_country);
            }
        }
    }
}

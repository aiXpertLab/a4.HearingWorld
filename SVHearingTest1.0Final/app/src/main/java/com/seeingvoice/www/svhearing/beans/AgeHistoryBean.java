package com.seeingvoice.www.svhearing.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Date:2019/8/26
 * Time:15:16
 * auther:zyy
 */
public class AgeHistoryBean implements Parcelable {

    /**
     * data : {"age_list":[{"age":"测试","created_at":"Mon, 09 Sep 2019 12:00:00 GMT"},{"age":"是是是","created_at":"Mon, 26 Aug 2019 14:12:14 GMT"},{"age":"您的听力年龄是：50岁。","created_at":"Mon, 26 Aug 2019 14:12:14 GMT"}]}
     * error_code :
     * error_info :
     * message_code : A000000
     */

    private DataBean data;
    private String error_code;
    private String error_info;
    private String message_code;

    private AgeHistoryBean(Parcel in) {
        error_code = in.readString();
        error_info = in.readString();
        message_code = in.readString();
        data = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Creator<AgeHistoryBean> CREATOR = new Creator<AgeHistoryBean>() {
        @Override
        public AgeHistoryBean createFromParcel(Parcel in) {
            return new AgeHistoryBean(in);
        }

        @Override
        public AgeHistoryBean[] newArray(int size) {
            return new AgeHistoryBean[size];
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

    public static class DataBean implements Parcelable {
        private List<AgeListBean> age_list;

        protected DataBean(Parcel in) {
            age_list = in.readArrayList(Thread.currentThread().getContextClassLoader());
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

        public List<AgeListBean> getAge_list() {
            return age_list;
        }

        public void setAge_list(List<AgeListBean> age_list) {
            this.age_list = age_list;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(age_list);
        }

        public static class AgeListBean implements Parcelable {
            /**
             * age : 测试
             * created_at : Mon, 09 Sep 2019 12:00:00 GMT
             */

            private String age;
            private String created_at;
            private String id;

            protected AgeListBean(Parcel in) {
                age = in.readString();
                id = in.readString();
                created_at = in.readString();
            }

            public static final Creator<AgeListBean> CREATOR = new Creator<AgeListBean>() {
                @Override
                public AgeListBean createFromParcel(Parcel in) {
                    return new AgeListBean(in);
                }

                @Override
                public AgeListBean[] newArray(int size) {
                    return new AgeListBean[size];
                }
            };

            public String getAge() {
                return age;
            }

            public void setAge(String age) {
                this.age = age;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(age);
                dest.writeString(id);
                dest.writeString(created_at);
            }
        }
    }
}

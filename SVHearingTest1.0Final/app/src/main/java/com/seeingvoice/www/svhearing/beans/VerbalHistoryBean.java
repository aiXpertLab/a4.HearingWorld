package com.seeingvoice.www.svhearing.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Date:2019/8/26
 * Time:9:09
 * auther:zyy
 */
public class VerbalHistoryBean implements Parcelable {

    /**
     * data : {"language_list":[{"created_at":"Fri, 09 Jan 1970 00:00:00 GMT","language_level":"3"},{"created_at":"Mon, 19 Aug 2019 15:36:26 GMT","language_level":"4"}]}
     * error_code :
     * error_info :
     * message_code : A000000
     */

    private DataBean data;
    private String error_code;
    private String error_info;
    private String message_code;

    protected VerbalHistoryBean(Parcel in) {
        error_code = in.readString();
        error_info = in.readString();
        message_code = in.readString();
        data = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Creator<VerbalHistoryBean> CREATOR = new Creator<VerbalHistoryBean>() {
        @Override
        public VerbalHistoryBean createFromParcel(Parcel in) {
            return new VerbalHistoryBean(in);
        }

        @Override
        public VerbalHistoryBean[] newArray(int size) {
            return new VerbalHistoryBean[size];
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
        private List<LanguageListBean> language_list;

        protected DataBean(Parcel in) {
            language_list = in.readArrayList(Thread.currentThread().getContextClassLoader());
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

        public List<LanguageListBean> getLanguage_list() {
            return language_list;
        }

        public void setLanguage_list(List<LanguageListBean> language_list) {
            this.language_list = language_list;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(language_list);
        }

        public static class LanguageListBean implements Parcelable{
            /**
             * created_at : Fri, 09 Jan 1970 00:00:00 GMT
             * language_level : 3
             */

            private String created_at;
            private String language_level;

            private LanguageListBean(Parcel in) {
                created_at = in.readString();
                language_level = in.readString();
            }

            public static final Creator<LanguageListBean> CREATOR = new Creator<LanguageListBean>() {
                @Override
                public LanguageListBean createFromParcel(Parcel in) {
                    return new LanguageListBean(in);
                }

                @Override
                public LanguageListBean[] newArray(int size) {
                    return new LanguageListBean[size];
                }
            };

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getLanguage_level() {
                return language_level;
            }

            public void setLanguage_level(String language_level) {
                this.language_level = language_level;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(created_at);
                dest.writeString(language_level);
            }
        }
    }
}

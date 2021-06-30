package com.seeingvoice.www.svhearing.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Date:2019/8/21
 * Time:16:15
 * auther:zyy
 */
public class PureHistoryItemBean implements Parcelable {

    /**
     * data : {"simple_detail":[{"frequency":"0.25","left_result":"100","right_result":"100"},{"frequency":"0.5","left_result":"100","right_result":"100"}]}
     * error_code :
     * error_info :
     * message_code : A000000
     */

    private DataBean data;
    private String error_code;
    private String error_info;
    private String message_code;

    protected PureHistoryItemBean(Parcel in) {
        error_code = in.readString();
        error_info = in.readString();
        message_code = in.readString();
        data = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Creator<PureHistoryItemBean> CREATOR = new Creator<PureHistoryItemBean>() {
        @Override
        public PureHistoryItemBean createFromParcel(Parcel in) {
            return new PureHistoryItemBean(in);
        }

        @Override
        public PureHistoryItemBean[] newArray(int size) {
            return new PureHistoryItemBean[size];
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
        private List<SimpleDetailBean> simple_detail;

        protected DataBean(Parcel in) {
            simple_detail = in.readArrayList(Thread.currentThread().getContextClassLoader());
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

        public List<SimpleDetailBean> getSimple_detail() {
            return simple_detail;
        }

        public void setSimple_detail(List<SimpleDetailBean> simple_detail) {
            this.simple_detail = simple_detail;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(simple_detail);
        }

        public static class SimpleDetailBean  implements  Parcelable{
            /**
             * frequency : 0.25
             * left_result : 100
             * right_result : 100
             */

            private String frequency;
            private String left_result;
            private String right_result;

            protected SimpleDetailBean(Parcel in) {
                frequency = in.readString();
                left_result = in.readString();
                right_result = in.readString();
            }

            public static final Creator<SimpleDetailBean> CREATOR = new Creator<SimpleDetailBean>() {
                @Override
                public SimpleDetailBean createFromParcel(Parcel in) {
                    return new SimpleDetailBean(in);
                }

                @Override
                public SimpleDetailBean[] newArray(int size) {
                    return new SimpleDetailBean[size];
                }
            };

            public String getFrequency() {
                return frequency;
            }

            public void setFrequency(String frequency) {
                this.frequency = frequency;
            }

            public String getLeft_result() {
                return left_result;
            }

            public void setLeft_result(String left_result) {
                this.left_result = left_result;
            }

            public String getRight_result() {
                return right_result;
            }

            public void setRight_result(String right_result) {
                this.right_result = right_result;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(frequency);
                dest.writeString(left_result);
                dest.writeString(right_result);
            }
        }
    }
}

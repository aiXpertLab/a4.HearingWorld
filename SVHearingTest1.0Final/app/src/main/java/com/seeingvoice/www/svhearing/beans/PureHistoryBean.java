package com.seeingvoice.www.svhearing.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Date:2019/8/20
 * Time:17:19
 * auther:zyy
 */
public class PureHistoryBean implements Parcelable {

    /**
     * data : {"all_list":[{"IMEI":"","L_average":"50","R_average":"99","creat_time":"Tue, 20 Aug 2019 11:41:40 GMT","remark":"this is remark","report_id":60,"user_id":100},{"IMEI":"","L_average":"50","R_average":"99","creat_time":"Tue, 20 Aug 2019 13:07:06 GMT","remark":"","report_id":61,"user_id":100}]}
     * error_code :
     * error_info :
     * message_code : A000000
     */

    private DataBean data;
    private String error_code;
    private String error_info;
    private String message_code;

    public PureHistoryBean(String message_code, String error_code, String error_info) {
        this.error_code = error_code;
        this.error_info = error_info;
        this.message_code = message_code;
    }

    private PureHistoryBean(Parcel in) {
        error_code = in.readString();
        error_info = in.readString();
        message_code = in.readString();
        data = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Creator<PureHistoryBean> CREATOR = new Creator<PureHistoryBean>() {
        @Override
        public PureHistoryBean createFromParcel(Parcel in) {
            return new PureHistoryBean(in);
        }

        @Override
        public PureHistoryBean[] newArray(int size) {
            return new PureHistoryBean[size];
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
        private List<AllListPureBean> all_list;

        private DataBean(Parcel in) {
            all_list = in.readArrayList(Thread.currentThread().getContextClassLoader());
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

        public List<AllListPureBean> getAll_list() {
            return all_list;
        }

        public void setAll_list(List<AllListPureBean> all_list) {
            this.all_list = all_list;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
             dest.writeList(all_list);
        }

        public static class AllListPureBean implements Parcelable {
            /**
             * IMEI :
             * L_average : 50
             * R_average : 99
             * creat_time : Tue, 20 Aug 2019 11:41:40 GMT
             * remark : this is remark
             * report_id : 60
             * user_id : 100
             */

            private String IMEI;
            private String L_average;
            private String R_average;
            private String creat_time;
            private String remark;
            private int report_id;
            private int user_id;

            public AllListPureBean(String IMEI, String l_average, String r_average, String creat_time, String remark, int report_id, int user_id) {
                this.IMEI = IMEI;
                this.L_average = l_average;
                this.R_average = r_average;
                this.creat_time = creat_time;
                this.remark = remark;
                this.report_id = report_id;
                this.user_id = user_id;
            }

            private AllListPureBean(Parcel in) {
                IMEI = in.readString();
                L_average = in.readString();
                R_average = in.readString();
                creat_time = in.readString();
                remark = in.readString();
                report_id = in.readInt();
                user_id = in.readInt();
            }

            public static final Creator<AllListPureBean> CREATOR = new Creator<AllListPureBean>() {
                @Override
                public AllListPureBean createFromParcel(Parcel in) {
                    return new AllListPureBean(in);
                }

                @Override
                public AllListPureBean[] newArray(int size) {
                    return new AllListPureBean[size];
                }
            };

            public String getIMEI() {
                return IMEI;
            }

            public void setIMEI(String IMEI) {
                this.IMEI = IMEI;
            }

            public String getL_average() {
                return L_average;
            }

            public void setL_average(String L_average) {
                this.L_average = L_average;
            }

            public String getR_average() {
                return R_average;
            }

            public void setR_average(String R_average) {
                this.R_average = R_average;
            }

            public String getCreat_time() {
                return creat_time;
            }

            public void setCreat_time(String creat_time) {
                this.creat_time = creat_time;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public int getReport_id() {
                return report_id;
            }

            public void setReport_id(int report_id) {
                this.report_id = report_id;
            }

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(IMEI);
                dest.writeString(L_average);
                dest.writeString(R_average);
                dest.writeString(creat_time);
                dest.writeString(remark);
                dest.writeInt(report_id);
                dest.writeInt(user_id);
            }
        }
    }
}
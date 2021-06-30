package seeingvoice.jskj.com.seeingvoice.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Date:2019/8/15
 * Time:15:44
 * auther:zyy
 */
public class PureTestResultBean_Original implements Parcelable {

    /**
     * user_id :
     * user_IMEI :
     * R_average :
     * L_average :
     * remark :
     * L_result : []
     * R_result : []
     */

    private int user_id;
    private String user_IMEI;
    private String created_at;
    private String R_average;
    private String L_average;
    private String remark;
    private List<String> L_result;
    private List<String> R_result;

    public PureTestResultBean_Original(int user_id, String user_IMEI, String timeStamp, String l_average, String r_average, String remark, List<String> l_result, List<String> r_result) {
        this.user_id = user_id;
        this.user_IMEI = user_IMEI;
        this.created_at = timeStamp;
        this.R_average = r_average;
        this.L_average = l_average;
        this.remark = remark;
        this.L_result = l_result;
        this.R_result = r_result;
    }

    private PureTestResultBean_Original(Parcel in) {
        user_id = in.readInt();
        user_IMEI = in.readString();
        created_at = in.readString();
        R_average = in.readString();
        L_average = in.readString();
        remark = in.readString();
        L_result = in.createStringArrayList();
        R_result = in.createStringArrayList();
    }

    public static final Creator<PureTestResultBean_Original> CREATOR = new Creator<PureTestResultBean_Original>() {
        @Override
        public PureTestResultBean_Original createFromParcel(Parcel in) {
            return new PureTestResultBean_Original(in);
        }

        @Override
        public PureTestResultBean_Original[] newArray(int size) {
            return new PureTestResultBean_Original[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(user_id);
        dest.writeString(user_IMEI);
        dest.writeString(created_at);
        dest.writeString(R_average);
        dest.writeString(L_average);
        dest.writeString(remark);
        dest.writeStringList(L_result);
        dest.writeStringList(R_result);
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_IMEI() {
        return user_IMEI;
    }

    public void setUser_IMEI(String user_IMEI) {
        this.user_IMEI = user_IMEI;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String time_Stamp) {
        this.created_at = time_Stamp;
    }

    public String getR_average() {
        return R_average;
    }

    public void setR_average(String R_average) {
        this.R_average = R_average;
    }

    public String getL_average() {
        return L_average;
    }

    public void setL_average(String L_average) {
        this.L_average = L_average;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<?> getL_result() {
        return L_result;
    }

    public void setL_result(List<String> L_result) {
        this.L_result = L_result;
    }

    public List<?> getR_result() {
        return R_result;
    }

    public void setR_result(List<String> R_result) {
        this.R_result = R_result;
    }
}

package seeingvoice.jskj.com.seeingvoice.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Date:2019/8/16
 * Time:15:45
 * auther:zyy
 */
public class SubmitPureResultBean implements Parcelable {

    /**
     * data : {}
     * error_code :
     * error_info :
     * message_code : A000000
     */

    private String error_code;
    private String error_info;
    private String message_code;

    public SubmitPureResultBean(String error_code, String error_info, String message_code) {
        this.error_code = error_code;
        this.error_info = error_info;
        this.message_code = message_code;
    }

    private SubmitPureResultBean(Parcel in) {
        error_code = in.readString();
        error_info = in.readString();
        message_code = in.readString();
    }

    public static final Creator<SubmitPureResultBean> CREATOR = new Creator<SubmitPureResultBean>() {
        @Override
        public SubmitPureResultBean createFromParcel(Parcel in) {
            return new SubmitPureResultBean(in);
        }

        @Override
        public SubmitPureResultBean[] newArray(int size) {
            return new SubmitPureResultBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(error_code);
        dest.writeString(error_info);
        dest.writeString(message_code);
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
}

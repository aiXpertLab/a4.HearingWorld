package com.hypech.SV8.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UpdateBean implements Parcelable{

    @SerializedName("version")
    private String version;
    //一下是手机app升级使用
    private String version_code;
    @SerializedName("file_url")
    private String download_url;
    @SerializedName("update_describe")
    private String update_info;
    @SerializedName("is_force_update")
    private String is_force;
    protected UpdateBean(Parcel in) {
        version = in.readString();
        version_code = in.readString();
        download_url = in.readString();
        update_info = in.readString();
        is_force = in.readString();
    }

    public static final Creator<UpdateBean> CREATOR = new Creator<UpdateBean>() {
        @Override
        public UpdateBean createFromParcel(Parcel in) {
            return new UpdateBean(in);
        }

        @Override
        public UpdateBean[] newArray(int size) {
            return new UpdateBean[size];
        }
    };

    public String getVersion_code() {
        return version_code;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getUpdate_info() {
        return update_info;
    }

    public void setUpdate_info(String update_info) {
        this.update_info = update_info;
    }

    public String getIs_force() {
        return is_force;
    }

    public void setIs_force(String is_force) {
        this.is_force = is_force;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    @Override
    public String toString() {
        return "file_name : "+ version;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(version);
        dest.writeString(version_code);
        dest.writeString(download_url);
        dest.writeString(update_info);
        dest.writeString(is_force);
    }
}

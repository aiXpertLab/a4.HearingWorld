package com.hypech.SV3.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GlassesUpdateBean implements Parcelable{

//    private String version;
    //一下是手机app升级使用
    @SerializedName("version")
    private String version_code;
    @SerializedName("file_url")
    private String download_url;
    @SerializedName("update_describe")
    private String update_info;
    @SerializedName("is_force_update")
    private String is_force;

    @SerializedName("file_name")
    private String file_name;




    protected GlassesUpdateBean(Parcel in) {
//        version = in.readString();
        version_code = in.readString();
        download_url = in.readString();
        update_info = in.readString();
        is_force = in.readString();
        file_name = in.readString();
    }

    public static final Creator<GlassesUpdateBean> CREATOR = new Creator<GlassesUpdateBean>() {
        @Override
        public GlassesUpdateBean createFromParcel(Parcel in) {
            return new GlassesUpdateBean(in);
        }

        @Override
        public GlassesUpdateBean[] newArray(int size) {
            return new GlassesUpdateBean[size];
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
    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }
//
//    public String getVersion() {
//        return version;
//    }
//
//    public void setVersion(String version) {
//        this.version = version;
//    }


//    @Override
//    public String toString() {
//        return "file_name : "+ version;
//    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(version);
        dest.writeString(version_code);
        dest.writeString(download_url);
        dest.writeString(update_info);
        dest.writeString(is_force);
        dest.writeString(file_name);
    }
}

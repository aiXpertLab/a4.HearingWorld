package seeingvoice.jskj.com.seeingvoice.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AppUpdateBean implements Parcelable{
    /**
     * message_code : A000000
     * error_code : E100510
     * error_info : 检测到新版本
     * data : {"info":[{"version":"4","file_name":"sv_test_4.pdf","file_size":"192.72","file_url":"http://file.seeingvoice.com/svheard_apk/sv_test_4.pdf","is_force_update":0,"update_describe":"4","create_time":"2019-09-20T14:41:23.059006+08:00"}]}
     */

    private String message_code;
    private String error_code;
    private String error_info;
    private DataBean data;

    protected AppUpdateBean(Parcel in) {
        message_code = in.readString();
        error_code = in.readString();
        error_info = in.readString();
        data = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Creator<AppUpdateBean> CREATOR = new Creator<AppUpdateBean>() {
        @Override
        public AppUpdateBean createFromParcel(Parcel in) {
            return new AppUpdateBean(in);
        }

        @Override
        public AppUpdateBean[] newArray(int size) {
            return new AppUpdateBean[size];
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
        private List<InfoBean> info;

        protected DataBean(Parcel in) {
            info = in.readArrayList(Thread.currentThread().getContextClassLoader());
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

        public List<InfoBean> getInfo() {
            return info;
        }

        public void setInfo(List<InfoBean> info) {
            this.info = info;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(info);
        }

        public static class InfoBean implements Parcelable{
            /**
             * version : 4
             * file_name : sv_test_4.pdf
             * file_size : 192.72
             * file_url : http://file.seeingvoice.com/svheard_apk/sv_test_4.pdf
             * is_force_update : 0
             * update_describe : 4
             * create_time : 2019-09-20T14:41:23.059006+08:00
             */

            private String version;
            private String file_name;
            private String file_size;
            private String file_url;
            private int is_force_update;
            private String update_describe;
            private String create_time;

            protected InfoBean(Parcel in) {
                version = in.readString();
                file_name = in.readString();
                file_size = in.readString();
                file_url = in.readString();
                is_force_update = in.readInt();
                update_describe = in.readString();
                create_time = in.readString();
            }

            public static final Creator<InfoBean> CREATOR = new Creator<InfoBean>() {
                @Override
                public InfoBean createFromParcel(Parcel in) {
                    return new InfoBean(in);
                }

                @Override
                public InfoBean[] newArray(int size) {
                    return new InfoBean[size];
                }
            };

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public String getFile_name() {
                return file_name;
            }

            public void setFile_name(String file_name) {
                this.file_name = file_name;
            }

            public String getFile_size() {
                return file_size;
            }

            public void setFile_size(String file_size) {
                this.file_size = file_size;
            }

            public String getFile_url() {
                return file_url;
            }

            public void setFile_url(String file_url) {
                this.file_url = file_url;
            }

            public int getIs_force_update() {
                return is_force_update;
            }

            public void setIs_force_update(int is_force_update) {
                this.is_force_update = is_force_update;
            }

            public String getUpdate_describe() {
                return update_describe;
            }

            public void setUpdate_describe(String update_describe) {
                this.update_describe = update_describe;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(version);
                dest.writeString(file_name);
                dest.writeString(file_size);
                dest.writeString(file_url);
                dest.writeInt(is_force_update);
                dest.writeString(update_describe);
                dest.writeString(create_time);
            }
        }
    }


//    private String version;
    //一下是手机app升级使用
//    @SerializedName("version")
//    private String version_code;
//    @SerializedName("file_url")
//    private String download_url;
//    @SerializedName("update_describe")
//    private String update_info;
//    @SerializedName("is_force_update")
//    private String is_force;
//    protected AppUpdateBean(Parcel in) {
////        version = in.readString();
//        version_code = in.readString();
//        download_url = in.readString();
//        update_info = in.readString();
//        is_force = in.readString();
//    }
//
//    public static final Creator<AppUpdateBean> CREATOR = new Creator<AppUpdateBean>() {
//        @Override
//        public AppUpdateBean createFromParcel(Parcel in) {
//            return new AppUpdateBean(in);
//        }
//
//        @Override
//        public AppUpdateBean[] newArray(int size) {
//            return new AppUpdateBean[size];
//        }
//    };
//
//    public String getVersion_code() {
//        return version_code;
//    }
//
//    public void setVersion_code(String version_code) {
//        this.version_code = version_code;
//    }
//
//    public String getDownload_url() {
//        return download_url;
//    }
//
//    public void setDownload_url(String download_url) {
//        this.download_url = download_url;
//    }
//
//    public String getUpdate_info() {
//        return update_info;
//    }
//
//    public void setUpdate_info(String update_info) {
//        this.update_info = update_info;
//    }
//
//    public String getIs_force() {
//        return is_force;
//    }
//
//    public void setIs_force(String is_force) {
//        this.is_force = is_force;
//    }
////
////    public String getVersion() {
////        return version;
////    }
////
////    public void setVersion(String version) {
////        this.version = version;
////    }
//
//
////    @Override
////    public String toString() {
////        return "file_name : "+ version;
////    }
//
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
////        dest.writeString(version);
//        dest.writeString(version_code);
//        dest.writeString(download_url);
//        dest.writeString(update_info);
//        dest.writeString(is_force);
//    }
}

/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hypech.SV8.base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ServerDataResponse<T> implements Parcelable {

    public String error_code;
    public String error_info;
    public String message_code;
    public T data;

    protected ServerDataResponse(Parcel in) {
        error_code = in.readString();
        error_info = in.readString();
        message_code = in.readString();
    }

    public static final Creator<ServerDataResponse> CREATOR = new Creator<ServerDataResponse>() {
        @Override
        public ServerDataResponse createFromParcel(Parcel in) {
            return new ServerDataResponse(in);
        }

        @Override
        public ServerDataResponse[] newArray(int size) {
            return new ServerDataResponse[size];
        }
    };

    @Override
    public String toString() {
        return "LzyResponse{\n" +//
               "\terror_code=" + error_code + "\n" +//
               "\terror_info='" + error_info + "\'\n" +//
                "\tmessage_code='" + message_code + "\'\n" +//
               "\tdata=" + data + "\n" +//
               '}';
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
    }
}

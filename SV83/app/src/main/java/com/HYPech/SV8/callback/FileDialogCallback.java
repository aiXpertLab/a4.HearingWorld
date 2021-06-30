package com.HYPech.SV8.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Window;

import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.convert.FileConvert;
import com.lzy.okgo.request.base.Request;

import java.io.File;

import okhttp3.Response;

public abstract class FileDialogCallback extends AbsCallback<File> {

    private FileConvert convert;    //文件转换类
    private ProgressDialog dialog;
//    private Activity mActivity;

    public FileDialogCallback() {
        this(null,null);
    }

    public FileDialogCallback(String destFileName,Activity activity) {
        this(null, destFileName,activity);
    }

    public FileDialogCallback(String destFileDir, String destFileName,Activity activity) {
        initDialog(activity);
        convert = new FileConvert(destFileDir, destFileName);
        convert.setCallback(this);
    }

    @Override
    public File convertResponse(Response response) throws Throwable {
        File file = convert.convertResponse(response);
        response.close();
        return file;
    }

    private void initDialog(Activity activity) {
        dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("文件下载中...");
    }
    @Override
    public void onStart(Request<File, ? extends Request> request) {
        super.onStart(request);
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}

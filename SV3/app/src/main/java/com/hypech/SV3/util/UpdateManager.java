package com.hypech.SV3.util;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hypech.SV3.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.hypech.SV3.constants.Constances.REQUEST_CODE_UNKNOWN_APP;


public class UpdateManager {
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    private Activity activity;

    private Context mContext;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private TextView tv;

    private Dialog mDownloadDialog;
    /**
     * 下载链接
     */
    private String path = "";
    /*文件名*/
    private String name = "";
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    tv.setText("已完成 ： " + progress + "%");
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件

                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            boolean installAllowed=mContext.getPackageManager().canRequestPackageInstalls();
            if (installAllowed){
                //权限许可，安装应用
                installApk();
            }else {
                Intent intent1=new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent1);
                activity.startActivityForResult(intent1,REQUEST_CODE_UNKNOWN_APP);
            }
        }
    }
    public UpdateManager(Context context, Activity activity ,String downLoadUrl) {
        this.mContext = context;
        this.activity = activity;
        this.path = downLoadUrl;
        if(!downLoadUrl.equals("")) {
            this.name = path.substring(path.lastIndexOf("/") + 1);
        }
        showDownloadDialog();
    }

    /**
     * 下载对话框
     */
    private void showDownloadDialog() {
        // 构造软件下载对话框
        mDownloadDialog = new Dialog(mContext, R.style.dialog);
        Builder builder = new Builder(mContext);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        tv = (TextView) v.findViewById(R.id.xiazaijindu);
        builder.setView(v);
//        // 取消更新
//        builder.setNegativeButton("取消", new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                // 设置取消状态
//                cancelUpdate = true;
//            }
//        });
        mDownloadDialog = builder.create();
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.show();
        // 现在文件
        if(!path.equals("")) {
            downloadApk();
        }
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(path);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, name);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    }


    /**
     * 安装APK文件
     */
    public void installApk() {
        File apkfile = new File(mSavePath, name);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri photoURI = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".FileProvider", apkfile);
            intent.setDataAndType(photoURI,
                    "application/vnd.android.package-archive");
        }else{
            intent.setDataAndType(Uri.fromFile(apkfile),
                    "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
        activity.finish();

    }


}

package com.seeingvoice.www.svhearing.bluetooth;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.seeingvoice.www.svhearing.AppConstant;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.seeingvoice.www.svhearing.AppConstant.REQUEST_ENABLE_BT;

/**
 * Date:2019/5/17
 * Time:14:00
 * auther:zyy
 */
public class BeforePairedBluetooth extends TopBarBaseActivity implements View.OnClickListener {

    private AlertDialog alertDialog;
    private AlertDialog mDialog;
    private Button mBtnSearchBlue;

    @Override
    protected int getConentView() {
        return R.layout.activity_before_paired_bluetooth;
    }

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("配对您的见声耳机");
        setTitleBack(true);
//        if (!checkGPSIsOpen()){
//            RequestPermissions();
//        }
        mBtnSearchBlue = findViewById(R.id.btn_start_search_bluetooth);
        mBtnSearchBlue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_search_bluetooth:
                skipAnotherActivity(this,SearchSVEarbudsActivity.class);
                break;
        }
    }

    private void RequestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具备模糊定位权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
                Toast.makeText(this, "您已经申请了权限!", Toast.LENGTH_SHORT).show();
            } else {
                //ACCESS_COARSE_LOCATION允许一个程序访问CellID或WiFi热点来获取粗略的位置
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {//选择了“始终允许”
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])){//选择禁止
                        AlertDialog.Builder builder = new AlertDialog.Builder(BeforePairedBluetooth.this);
                        builder.setTitle("本APP需要开启蓝牙权限")
                                .setMessage("点击允许才可以使用我们的app哦")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (alertDialog != null && alertDialog.isShowing()) {
                                            alertDialog.dismiss();
                                        }
                                        ActivityCompat.requestPermissions(BeforePairedBluetooth.this,
                                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);//requestCode还是1
                                    }
                                });
                        alertDialog = builder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }else {//用户选择了禁止不再询问
                        AlertDialog.Builder builder = new AlertDialog.Builder(BeforePairedBluetooth .this);
                        builder.setTitle("本APP需要开启蓝牙权限")
                                .setMessage("点击允许才可以使用我们的app哦")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (mDialog != null && mDialog.isShowing()) {
                                            mDialog.dismiss();
                                        }
                                        openGPSSettings();
//                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                        Uri uri = Uri.fromParts("package", getPackageName(), null);//注意就是"package",不用改成自己的包名
//                                        intent.setData(uri);
//                                        startActivityForResult(intent, AppConstant.NOT_NOTICE );  //resultCode为NOT_NOTICE
                                    }
                                });
                        mDialog = builder.create();
                        mDialog.setCanceledOnTouchOutside(false);//不允许在框外触碰取消
                        mDialog.show();
                        }
                    }

                }
            }
        }


    /**
     * 检测GPS是否打开
     *
     * @return
     */
    private boolean checkGPSIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    /**
     * 跳转GPS设置
     */
    private void openGPSSettings() {
        if (checkGPSIsOpen()) {
            Toast.makeText(this, "所需权限已经开启", Toast.LENGTH_SHORT).show();
        } else {
            //没有打开则弹出对话框
            new AlertDialog.Builder(this)
                    .setTitle(R.string.notifyTitle)
                    .setMessage(R.string.gpsNotifyMsg)
                    // 拒绝, 退出应用
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })

                    .setPositiveButton(R.string.setting,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //跳转GPS设置界面
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, AppConstant.NOT_NOTICE);
                                }
                            })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //做需要做的事情，比如再次检测是否打开GPS了 或者定位
        switch (requestCode){
            case AppConstant.NOT_NOTICE:
                openGPSSettings();
                break;
            case REQUEST_ENABLE_BT:
                switch (resultCode){
                    case RESULT_OK:
                        ToastUtil.showShortToastCenter("蓝牙开启成功，请点击下方搜索按钮");
                        break;
                    case RESULT_CANCELED:
                        ToastUtil.showShortToastCenter("蓝牙开启失败");
                        break;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

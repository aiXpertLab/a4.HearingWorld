package com.seeingvoice.www.svhearing.permissions;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.seeingvoice.www.svhearing.DisclaimerStatementActivity;
import com.seeingvoice.www.svhearing.MainActivity;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.TermsOfService;
import com.seeingvoice.www.svhearing.base.AntiShakeUtils;
import com.seeingvoice.www.svhearing.base.OnMultiClickListener;
import com.seeingvoice.www.svhearing.login.LoginActivity;
import com.seeingvoice.www.svhearing.privacyPolicyActivity;
import com.seeingvoice.www.svhearing.util.AlertDialogUtil;
import com.seeingvoice.www.svhearing.util.SharedPreferencesHelper;

import static com.seeingvoice.www.svhearing.AppConstant.ALEART_DIALOG_REQUEST_CODE;

/**
 * Date:2019/5/21
 * Time:13:20
 * auther:zyy
 */
public class PermissionsActivity extends AppCompatActivity {
    public static final int PERMISSIONS_GRANTED = 0; // 权限授权
    public static final int PERMISSIONS_DENIED = 1; // 权限拒绝

    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    private static final String EXTRA_PERMISSIONS = "com.seeingvoice.www.permission.extra_permission"; // 权限参数
    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案
    private static final String TAG = PermissionsActivity.class.getSimpleName();

    private PermissionsChecker mChecker; // 权限检测器
    private boolean isRequireCheck; // 是否需要系统权限检测
    private Button btnAccept,btnUnAccept;
    private TextView tvServiceTerms,tv_User_Agreement;

    public static final String[] PERMISSIONS = new String[]{    // 所需的全部权限
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private AlertDialogUtil alertDialog;

    // 启动当前权限页面的公开接口    String...可变长度参数列表
    public static void startActivityForResult(Activity activity, int requestCode, String... permissions) {
        Intent intent = new Intent(activity, PermissionsActivity.class);
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
        }else {
            activity.startActivity(intent);
        }
    }

    //    @Override
    //    protected void init(Bundle savedInstanceState) {
    //
    //    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSIONS)) {
        //            throw new RuntimeException("PermissionsActivity需要使用静态startActivityForResult方法启动!");
        //        }
        setContentView(R.layout.activity_permissions);
        //        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        //设置此界面为竖屏

        mChecker = new PermissionsChecker(this);
        //        isRequireCheck = true;
        btnAccept = findViewById(R.id.btn_accept);
        btnUnAccept = findViewById(R.id.btn_unaccept);
        btnAccept.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {//Android 大于等于6
                    String[] permissions = getPermissions();
                    if (mChecker.lacksPermissions(permissions)) {//缺少权限
                        requestPermissions(permissions); // 请求权限
                    } else {//不缺权限
                        allPermissionsGranted(); // 全部权限都已获取
                    }
                    Log.e(TAG, "onDialogButtonClick: Android 大于等于6，动态权限申请");
                }else {
                    Log.e(TAG, "onDialogButtonClick: Android 小于6，动态权限申请");
                    SharedPreferencesHelper.getInstance().saveData("isFirstLaunch",false);
                    startActivity(new Intent(PermissionsActivity.this, LoginActivity.class));
                }
            }
        });


        btnUnAccept.setOnClickListener(new OnMultiClickListener() {

            @Override
            public void onMultiClick(View v) {
                alertDialog = new AlertDialogUtil(PermissionsActivity.this, "温馨提示：","见声听力测试APP仅会将您的信息用于提供服务或改善服务体验，我们将保护您的信息安全。",false,
                        "确定", 0x001 , mListner);
                alertDialog.show();
                //                alertDialog = new AlertDialogUtil(PermissionsActivity.this, true, "温馨提示", "见声听力测试APP仅会将您的信息用于提供服务或改善服务体验，我们将保护您的信息安全。", false, 0x001, mListner).show(); 有问题

            }

            AlertDialogUtil.OnDialogButtonClickListener mListner = new AlertDialogUtil.OnDialogButtonClickListener() {
                @Override
                public void onDialogButtonClick(int requestCode, boolean isPositive) {
                    if (requestCode == 0x001){
                        if (isPositive){
                            alertDialog.dismiss();
                        }
                    }
                }
            };

        });

        tvServiceTerms = findViewById(R.id.tv_privacy_policy);
        tv_User_Agreement = findViewById(R.id.tv_user_agreement);
        //        tvDisclaimer = findViewById(R.id.tv_disclaimer);
        //        tvDisclaimer.setOnClickListener(new OnMultiClickListener() {
        //            @Override
        //            public void onMultiClick(View v) {
        //                ToastUtil.showShortToastCenter("请加免责声明！");
        //            }
        //        });

        tvServiceTerms.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                //              Intent intent = new Intent(PermissionsActivity.this, TermsOfService.class);
                Intent intent = new Intent(PermissionsActivity.this, privacyPolicyActivity.class);
                startActivity(intent);
            }
        });
        tv_User_Agreement.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(PermissionsActivity.this, DisclaimerStatementActivity.class);
                startActivity(intent);
            }
        });

        if (!((Boolean) SharedPreferencesHelper.getInstance().getData("isFirstLaunch", true))){//sp第一次启动
            allPermissionsGranted();
        }
    }

    // 返回传递的权限参数
    private String[] getPermissions() {
        //        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
        return PERMISSIONS;
    }

    // 请求权限兼容低版本
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    // 全部权限均已获取
    private void allPermissionsGranted() {
        //        this.startActivityForResult(new Intent(PermissionsActivity.this,LoginActivity.class),PERMISSIONS_GRANTED);
        startActivity(new Intent(PermissionsActivity.this,LoginActivity.class));
        SharedPreferencesHelper.getInstance().saveData("isFirstLaunch",false);//设置不是第一次登陆了
        //        setResult(PERMISSIONS_GRANTED);
        finish();
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (hasAllPermissionsGranted(grantResults)){
                //            isRequireCheck = false;
                allPermissionsGranted();//获得了所有权限
            }else {
                showMissingPermissionDialog();
            }
        }else {
            //            isRequireCheck = true;
            //缺少权限
            showMissingPermissionDialog();
        }
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionsActivity.this);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.quit, new android.content.DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                setResult(PERMISSIONS_DENIED);
                //                ActivityStackManager.getActivityStackManager().popAllActivity();
                finish();
            }
        });

        builder.setPositiveButton(R.string.settings, new android.content.DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.show();
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);

        //跳转GPS设置界面 ACCESS_COARSE_LOCATION
        //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //startActivityForResult(intent, AppConstant.NOT_NOTICE);
    }

    //    public static Bitmap zoomImg(Bitmap bm, int newWidth , int newHeight) {
    //        // 获得图片的宽高
    //        int width = bm.getWidth();
    //        int height = bm.getHeight();
    //        // 计算缩放比例
    //        float scaleWidth = ((float) newWidth) / width;
    //        float scaleHeight = ((float) newHeight) / height;
    //        // 取得想要缩放的matrix参数
    //        Matrix matrix = new Matrix();
    //        matrix.postScale(scaleWidth, scaleHeight);
    //        // 得到新的图片
    //        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    //        return newbm;
    //    }
}
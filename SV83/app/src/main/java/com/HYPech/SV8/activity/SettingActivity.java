package com.HYPech.SV8.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HYPech.SV8.App;
import com.HYPech.SV8.R;
import com.HYPech.SV8.base.BaseActivity;
import com.HYPech.SV8.base.ServerDataResponse;
import com.HYPech.SV8.bean.AppUpdateBean;
import com.HYPech.SV8.bean.AppUpdateOutBean;
import com.HYPech.SV8.callback.JsonCallback;
import com.HYPech.SV8.util.CommonUtil;
import com.HYPech.SV8.util.SPHelper;
import com.HYPech.SV8.util.TDevice;
import com.HYPech.SV8.util.UpdateManager;
import com.HYPech.SV8.util.Urls;
import com.HYPech.SV8.view.SelfDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import androidx.annotation.Nullable;

import static com.HYPech.SV8.constants.Constances.NO_NEED_TO_UPDATE;
import static com.HYPech.SV8.constants.Constances.OKGO_ON_ERROR;
//import butterknife.BindView;
//import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {

//    @BindView(R.id.mimagebutton)
    ImageView mBack;
    RelativeLayout mAboutusRl;
    TextView mGlassesSV;
    TextView mAppVersion;
    TextView mTitle;

    RelativeLayout mGlassesUpdateContainerRl;
    RelativeLayout mAppUpdateContainerRl;


    private SelfDialog selfDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        super.onCreate(savedInstanceState);
//        ButterKnife.bind(this);

    }

    @Override
    public void initViews() {
        mBack = this.findViewById(R.id.backbutton);
        mAboutusRl = this.findViewById(R.id.aboutus_rl);
        mGlassesSV = this.findViewById(R.id.update_glasses_des);
        mAppVersion = this.findViewById(R.id.update_app_des);
        mGlassesUpdateContainerRl = this.findViewById(R.id.update_glasses_rl);
        mAppUpdateContainerRl = this.findViewById(R.id.update_app_rl);
        mTitle = this.findViewById(R.id.title_text);

    }

    @Override
    public void initDatas() {
        mTitle.setText("设置");
        if(App.myApplication.getGlassesSoftVersion() == null || App.myApplication.getGlassesSoftVersion().equals("")){
            mGlassesSV.setText("未连接");
        }else {
            mGlassesSV.setText("V" + App.myApplication.getGlassesSoftVersion());
        }
        mAppVersion.setText("V"+ TDevice.getVersionName());
    }

    @Override
    public void installListeners() {
        mAboutusRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

//        mGlassesUpdateContainerRl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        mAppUpdateContainerRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAppUpdateFromServer();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void processHandlerMessage(Message msg) {
        switch (msg.what){

            case NO_NEED_TO_UPDATE:
                CommonUtil.showToast(SettingActivity.this,"当前已经是最新版本。");
                break;
            default:
                break;
        }
    }
    public void getAppUpdateFromServer(){
        OkGo.<ServerDataResponse<AppUpdateOutBean>>get(Urls.URL_GET_APP_UPDATE)//
                .tag(this)//
                .params("version", TDevice.getVersionCode())
                .execute(new JsonCallback<ServerDataResponse<AppUpdateOutBean>>() {
                    @Override
                    public void onSuccess(Response<ServerDataResponse<AppUpdateOutBean>> response) {
                        if(response.body() !=null && response.body().data != null && response.body().data.getInfo()!= null&& response.body().data.getInfo().size()>0) {

                            AppUpdateBean data = response.body().data.getInfo().get(0);
                            if(data != null) {
                                Log.d("ljp", "服务器数据：" + data.toString() + response.body().error_info);
                                updateApp(data);
                            }else{
                                getHandler().obtainMessage(NO_NEED_TO_UPDATE).sendToTarget();
                            }
                        }else{
                            getHandler().obtainMessage(NO_NEED_TO_UPDATE).sendToTarget();
                        }
                    }

                    @Override
                    public void onError(Response<ServerDataResponse<AppUpdateOutBean>> response) {
                        if(response != null) {
                            Throwable throwable = response.getException();
//                            Log.d("waha",throwable.toString()+"1"+throwable.getMessage()+"2"+throwable.getLocalizedMessage()+"3"+throwable.getCause());
                            if(throwable!= null && CommonUtil.catchNetException(throwable.toString())){
//                                Log.d("waha","进入一");
                                getHandler().obtainMessage(OKGO_ON_ERROR).sendToTarget();
                            }else{
//                                Log.d("waha","进入2");
                            }
                        }

                        getHandler().obtainMessage(NO_NEED_TO_UPDATE).sendToTarget();
                    }
                });
    }
    //手机app更新
    public void updateApp(AppUpdateBean bean){

        final String versionCode = bean.getVersion_code();

        String downLoadUrl = "";
        if (bean != null && bean.getDownload_url() != null) {
            downLoadUrl = bean.getDownload_url();
        }
        final String mFinalDownloadUrl = downLoadUrl;

        double serviceCode = Double.parseDouble(versionCode);
        double code = TDevice.getVersionCode();

        //从网上获取是否需强制升级 1 代表强制升级，0 代表非强制升级
        String isForceUpdate = "";
        if (!bean.getIs_force().equals("")) {
            isForceUpdate = bean.getIs_force();
        }
        //从网上获取的更新信息内容
        String updateInfo = "";
        if (!bean.getUpdate_info().equals("")) {
            updateInfo = bean.getUpdate_info();
        }
        final String updateDes = updateInfo;
        //保存从网上获取的serviceCode
        SPHelper.getInst().saveString("serviceCode", versionCode);
        //判断是否忽略过版本
        //判断发现新版本后是否是第一次弹出升级框
        //判断是否需要版本升级
        if (code != serviceCode && code < serviceCode) {
            SPHelper.getInst().saveString("downLoadUrl", downLoadUrl);
                selfDialog = new SelfDialog(SettingActivity.this, com.HYPech.SV8.R.style.dialog, updateDes);
                selfDialog.show();
                selfDialog.setYesOnclickListener("立即升级", new SelfDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        new UpdateManager(SettingActivity.this, SettingActivity.this, mFinalDownloadUrl);
                        selfDialog.dismiss();
                    }
                });
                    selfDialog.setNoOnclickListener("忽略此次", new SelfDialog.onNoOnclickListener() {
                        @Override
                        public void onNoClick() {
                            SPHelper.getInst().saveString("updateDes", updateDes);
                            selfDialog.dismiss();
                            getHandler().obtainMessage(NO_NEED_TO_UPDATE).sendToTarget();
                        }
                    });
        }else{
            getHandler().obtainMessage(NO_NEED_TO_UPDATE).sendToTarget();
        }
    }
}

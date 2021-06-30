package com.seeingvoice.www.svhearing;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.seeingvoice.www.svhearing.base.BaseActivity;
import com.seeingvoice.www.svhearing.bluetooth.BluetoothManager;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import static com.seeingvoice.www.svhearing.AppConstant.REQUEST_ENABLE_BT;

/**
 * Date:2019/5/29
 * Time:11:42
 * auther:zyy
 */
public class NoticeActivity extends BaseActivity implements View.OnClickListener {
    private Button mBtnOpenBluetooth;
    private BluetoothManager mBluetoothManager;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_notice);
        mBtnOpenBluetooth = findViewById(R.id.btn_open_bluetooth);
        mBtnOpenBluetooth.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_open_bluetooth:
                initBluetooth();
                break;
        }
    }

    /**
     * 开启手机蓝牙
     */
    private void initBluetooth() {
        mBluetoothManager = BluetoothManager.getInstance();//得到蓝牙管理者
        mBluetoothManager.setContext(this);//传递上下文对象
        if (mBluetoothManager != null && mBluetoothManager.isSupportBluetooth()){//如果手机支持蓝牙功能，则获得蓝牙适配器对象
            mBluetoothManager.initBlueProfileA2dpHeadSet(this);//初始化A2DP 耳机音频协议
            if (mBluetoothManager.isBluetoothOpen()){//手机蓝牙打开否？
                mBluetoothManager.findBluetoothDevice();
            }else {
                ToastUtil.showShortToastCenter("手机蓝牙未打开请先打开蓝牙");
                mBluetoothManager.requestStartBluetooth(REQUEST_ENABLE_BT,NoticeActivity.this);//请求启动蓝牙
            }
        } else {
            ToastUtil.showShortToastCenter("手机不支持蓝牙,无法运行该应用");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){ //做需要做的事情，比如再次检测是否打开GPS了 或者定位
            case REQUEST_ENABLE_BT:
                switch (resultCode){
                    case RESULT_OK:
                        ToastUtil.showShortToastCenter("手机蓝牙已经打开，请开始搜索耳机");
                        break;
                    case RESULT_CANCELED:
                        ToastUtil.showShortToastCenter("手机蓝牙打开失败");
                        mBluetoothManager.openBluetooth();
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

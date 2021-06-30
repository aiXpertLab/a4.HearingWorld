package com.seeingvoice.www.svhearing.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.base.util.ActivityStackManager;
import com.seeingvoice.www.svhearing.bluetooth.util.BluetoothClsUtils;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static com.seeingvoice.www.svhearing.AppConstant.REQUEST_ENABLE_BT;
import static com.seeingvoice.www.svhearing.AppConstant.SCAN_PERIOD;

/**
 * Date:2019/5/15
 * Time:15:00
 * auther:zyy
 */
public class SearchSVEarbudsActivity extends TopBarBaseActivity implements OnBluetoothListener, View.OnClickListener {

    public BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    //支持蓝牙耳机的名字
//    private String[] BlueHeadSetSupported = {"X-I8S","TicPods Free","W.mp","Hi-TWS R","Hi-TWS L","C7B","Hi-TWS","X8"};
    private TextView mTvSearchingState,mTvBlueName;
    private ProgressBar mBlueSearchProgressBar;
    private BluetoothReceiver mBluetoothReceiver;
    private BluetoothDevice mTargetDevice;
    private Handler mHandler = new Handler();
    private Button mBtnSearch,mBtnConnect;

    @Override
    protected int getConentView() {
        return R.layout.activity_search_sv_bluetooth;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("搜索见声蓝牙");
        setTitleBack(true);
        RegisterBrodCst();
        mTvSearchingState = findViewById(R.id.tv_searching_state);
        mTvBlueName = findViewById(R.id.tv_blue_name);
        mBlueSearchProgressBar = findViewById(R.id.pg_blue_search_progressbar);
        mBtnSearch = findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(this);
        mBtnConnect = findViewById(R.id.btn_connect);
        mBtnConnect.setOnClickListener(this);
        initBluetooth();
        mBluetoothReceiver.setOnBluetoothListener(this);
    }

    /**
     * 开启手机蓝牙
     */
    private void initBluetooth() {
        mBluetoothManager = BluetoothManager.getInstance();
        BluetoothManager.getInstance().setContext(this);
        if (mBluetoothManager != null && mBluetoothManager.isSupportBluetooth()){//如果手机支持蓝牙功能，则获得蓝牙适配器对象
            mBluetoothManager.initBlueProfileA2dpHeadSet(SearchSVEarbudsActivity.this);
            if (mBluetoothManager.isBluetoothOpen()){//手机蓝牙打开否？
                ToastUtil.showShortToastCenter("蓝牙已开启，请点击下方搜索按钮");
            }else {
                mBluetoothManager.requestStartBluetooth(REQUEST_ENABLE_BT,this);//请求启动蓝牙
            }
        } else {
            ToastUtil.showShortToastCenter("手机需要支持蓝牙,才能运行该应用");
        }
    }

    /**
     * 检测GPS是否打开
     * @return
     */
//    private boolean checkGPSIsOpen() {
//        boolean isOpen;
//        LocationManager locationManager = (LocationManager) this
//                .getSystemService(Context.LOCATION_SERVICE);
//        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
//        return isOpen;
//    }

    /**     * 动态注册广播     */
    private void RegisterBrodCst() { // 动态注册广播 start
        mBluetoothReceiver = new BluetoothReceiver();
        this.registerReceiver(mBluetoothReceiver,new IntentFilter(BluetoothDevice.ACTION_FOUND));
        this.registerReceiver(mBluetoothReceiver,new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        this.registerReceiver(mBluetoothReceiver,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        this.registerReceiver(mBluetoothReceiver,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        this.registerReceiver(mBluetoothReceiver,new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED));
        this.registerReceiver(mBluetoothReceiver,new IntentFilter(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search:
                mBlueSearchProgressBar.setVisibility(View.VISIBLE);
                scanBLEdevice(true);
                break;
            case R.id.btn_connect:
                mBlueSearchProgressBar.setVisibility(View.VISIBLE);
                if (null != mTargetDevice){
                    mBluetoothManager.connectBlueHeadset(mTargetDevice);
                }
                break;
        }
    }

    /**
     * @param enable
     * 蓝牙延时停止扫描操作
     */
    private void scanBLEdevice(final boolean enable) {
        if (enable) {
            mBluetoothManager.openBluetooth();
            mHandler.postDelayed(new Runnable() { // Stops scanning after a pre-defined scan period.
                @Override
                public void run() {
                    mTvSearchingState.setText("搜索结束...");
                    mBlueSearchProgressBar.setVisibility(View.INVISIBLE);
                    mBtnSearch.setClickable(true);
                    mBluetoothManager.cancelDiscover();
                }
            }, SCAN_PERIOD);
            mTvSearchingState.setText("正在搜索...");
            mBlueSearchProgressBar.setVisibility(View.VISIBLE);
            mBtnSearch.setClickable(false);
            mBluetoothManager.findBluetoothDevice();
        } else {
            mTvSearchingState.setText("搜索结束...");
            mBlueSearchProgressBar.setVisibility(View.VISIBLE);
            mBtnSearch.setClickable(true);
            mBluetoothManager.cancelDiscover();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //做需要做的事情，比如再次检测是否打开GPS了 或者定位
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                switch (resultCode){
                    case RESULT_OK:
                        mTvSearchingState.setText("蓝牙已经打开，点击搜索");
                        mBlueSearchProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case RESULT_CANCELED:
                        mTvSearchingState.setText("蓝牙开启失败");
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothManager.disableAdapter();
        if (mBluetoothReceiver != null){//注销广播
            this.unregisterReceiver(mBluetoothReceiver);
        }
    }

    /**  广播接收者  接口实现类  方法实现 start*/
    @Override
    public void deviceConnected(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice != null) {
            mTvSearchingState.setText(bluetoothDevice.getName()+"已经连接");
            mTvBlueName.setVisibility(View.VISIBLE);
            mBlueSearchProgressBar.setVisibility(View.GONE);
            mTvBlueName.setText("名称："+bluetoothDevice.getName()+"--地址："+bluetoothDevice.getAddress()+"已经连接");
            Intent intent = new Intent();
            intent.putExtra("connedBlueDevice",bluetoothDevice);
            intent.setAction("sv.bluetooth.conned");
            sendBroadcast(intent);
            ActivityStackManager.getActivityStackManager().popActivity(SearchSVEarbudsActivity.this);
        }
    }

    @Override
    public void deviceDisConnected(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void deviceFound(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice != null){
            mTvSearchingState.setText("发现蓝牙耳机");
            mTvBlueName.setVisibility(View.VISIBLE);
            mBlueSearchProgressBar.setVisibility(View.GONE);
            mTvBlueName.setText("名称："+bluetoothDevice.getName()+"--地址："+bluetoothDevice.getAddress());
            mBtnSearch.setVisibility(View.GONE);
            mTargetDevice = bluetoothDevice;
            try {
                BluetoothClsUtils.createBond(bluetoothDevice.getClass(),bluetoothDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void deviceBonded(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice != null) {
            mTvSearchingState.setText(bluetoothDevice.getName()+"已经配对");
            mTvBlueName.setVisibility(View.VISIBLE);
            mBlueSearchProgressBar.setVisibility(View.GONE);
            mTvBlueName.setText("名称："+bluetoothDevice.getName()+"--地址："+bluetoothDevice.getAddress()+"已经配对");
            mBluetoothManager.connectBlueHeadset(bluetoothDevice);
        }
    }

    @Override
    public void deviceBonding(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void deviceBondNone(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void bluetoothStateOn() {

    }

    @Override
    public void bluetoothStateOff() {

    }

    @Override
    public void noDeviceDiscovered() {
        mTvSearchingState.setText("没有发现见声耳机C7 或C7B");
        mBlueSearchProgressBar.setVisibility(View.GONE);
        mBtnSearch.setClickable(true);
    }
    /**  广播接收者   接口实现类  方法实现 end*/

}

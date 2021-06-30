package com.HYPech.SV8.fragment.second;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.Nullable;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.HYPech.SV8.App;
import com.HYPech.SV8.R;
import com.HYPech.SV8.base.BaseMainFragment;
import com.HYPech.SV8.event.BindMacChangeEvent;
import com.HYPech.SV8.event.ConnectEvent;
import com.HYPech.SV8.event.TabSelectedEvent;
import com.HYPech.SV8.fragment.MainFragment;
import com.HYPech.SV8.service.BtReceiver;
import com.HYPech.SV8.util.BtBase;
import com.HYPech.SV8.util.CommonUtil;
import com.HYPech.SV8.util.LogUtils;
import com.HYPech.SV8.view.BluetoothDeviceListDialog;
import com.HYPech.SV8.view.FontSizeView;
import com.HYPech.SV8.view.SelfDialogconnectFaild;

import java.util.ArrayList;

import static com.HYPech.SV8.constants.Constances.GLASSES_SETTING_CHANGE2;
import static com.HYPech.SV8.constants.Constances.MAC_BIND_CHANGE;
import static com.HYPech.SV8.util.CommonUtil.rotateScreen;


/**
 * Created by YoKeyword on 16/6/30.
 */
public class SecondTabFragment extends BaseMainFragment implements  BluetoothDeviceListDialog.OnDeviceSelectedListener, BtBase.Listener, BtReceiver.Listener {



    RelativeLayout bindGlassesRl;
    TextView bindTitleTv;
    TextView bindDexTv;
    FontSizeView fsvFontSize;

    RelativeLayout rotateRl;
    TextView rotateDexTv;

    private boolean mConnect;
    private String preMac = "";
    private SeekBar mBrightSb;

    //切换左右眼切换
    //设备重新连接弹出dialog
    private SelfDialogconnectFaild connectFaildDialog ;


    //绑定设备
    public BtReceiver mBtReceiver;
    BluetoothDeviceListDialog dialog;
    BluetoothAdapter bt;

    ArrayList<String> mSearchList = new ArrayList<String>();

    public static SecondTabFragment newInstance() {

        Bundle args = new Bundle();

        SecondTabFragment fragment = new SecondTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_second, container, false);
        EventBusActivityScope.getDefault(_mActivity).register(this);
        initView(view);
        initData();
        initListener();
        return view;
    }

    private void initView(View view) {
//        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);

        bindTitleTv = view.findViewById(R.id.bind_title);
        bindDexTv = view.findViewById(R.id.bind_des);
        bindGlassesRl = view.findViewById(R.id.bind_glasses_rl);
        rotateRl = view.findViewById(R.id.rotate_rl);
        rotateDexTv = view.findViewById(R.id.rotate_des);
        fsvFontSize = view.findViewById(R.id.fsv_font_size);
        mBrightSb = view.findViewById(R.id.tickEndSeek);
    }
    private void initData(){
        mBrightSb.setMax(8);
        fsvFontSize.setDefaultPosition(CommonUtil.getPositionFontSize());
        refreshUI();
        refreshRotate();
        connectFaildDialog = new SelfDialogconnectFaild(_mActivity, com.HYPech.SV8.R.style.dialog, "切换视觉","请调整设备左右位置");

        mBtReceiver = new BtReceiver(_mActivity, this);//注册蓝牙广播
        bt = BluetoothAdapter.getDefaultAdapter();
        dialog = new BluetoothDeviceListDialog(_mActivity);
        dialog.setOnDeviceSelectedListener(this);
        dialog.setTitle(R.string.paired_devices);

        LogUtils.d("ljp","secondtabfragment initdata 调用。");

    }
    private void initListener(){

        bindGlassesRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CommonUtil.isBlueEnable()){
                    Toast.makeText(_mActivity,"请打开手机蓝牙！",Toast.LENGTH_LONG).show();
                    return;
                }
                //TODO 绑定设备逻辑
                preMac = App.myApplication.getBindMacDevice();

                showDeviceListDialog();
//                refreshUI();
                searchDevice();
            }
        });
        rotateRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CommonUtil.isBlueEnable()){
                    Toast.makeText(_mActivity,"请打开手机蓝牙！",Toast.LENGTH_LONG).show();
                    return;
                }
                if(mConnect) {
                    connectFaildDialog.show();
                    //点击事件
                    connectFaildDialog.setYesOnclickListener("左眼视觉", new SelfDialogconnectFaild.onYesOnclickListener() {
                        @Override
                        public void onYesClick() {

                            App.myApplication.setRotateDevice("0");
                            rotateScreen(true,_mActivity);
                            connectFaildDialog.dismiss();
                            refreshRotate();
                        }
                    });
                    connectFaildDialog.setNoOnclickListener("右眼视觉", new SelfDialogconnectFaild.onNoOnclickListener() {
                        @Override
                        public void onNoClick() {
                            App.myApplication.setRotateDevice("1");
                            rotateScreen(false,_mActivity);
                            connectFaildDialog.dismiss();
                            refreshRotate();
                        }
                    });

                }else{
                    Toast.makeText(_mActivity, "请先连接眼镜设备！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fsvFontSize.setChangeCallbackListener(new FontSizeView.OnChangeCallbackListener() {
            @Override
            public void onChangeListener(int position) {
                if(!CommonUtil.isBlueEnable()){
                    Toast.makeText(_mActivity,"请打开手机蓝牙！",Toast.LENGTH_LONG).show();
                    return;
                }
                if(mConnect) {
                    CommonUtil.setEyeglassesFontSize(position,_mActivity);
                    Log.d("ljp","当前："+position);
                }else{
                    Toast.makeText(_mActivity, "请先连接眼镜设备！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBrightSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //主要是用于监听进度值的改变
                if(!CommonUtil.isBlueEnable()){
                    Toast.makeText(_mActivity,"请打开手机蓝牙！",Toast.LENGTH_LONG).show();
                    return;
                }
                if(mConnect) {
                    CommonUtil.setEyeglassesBright(progress,_mActivity);
                    Log.d("ljp","当前 progress："+progress);
                }else{
                    Toast.makeText(_mActivity, "请先连接眼镜设备！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                //监听用户开始拖动进度条的时候
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //监听用户结束拖动进度条的时候

            }
        });
    }
    @Subscribe
    public void onTabSelectedEvent(TabSelectedEvent event) {

    }
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

        LogUtils.d("ljp","secondTabFragment onLazyInitView 调用。");
    }
    @Override
    public void onSupportVisible() {

        super.onSupportVisible();
        stopFind();
        refreshUI();
        refreshRotate();
        mBtReceiver.registerReceiver(_mActivity);
        LogUtils.d("ljp","secondTabFragment onSupportVisible 调用。");
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        stopFind();
        _mActivity.unregisterReceiver(mBtReceiver);
        LogUtils.d("ljp","secondTabFragment onSupportInvisible 调用。");
    }
    @Override
    public void processHandlerMessage(Message msg) {
        switch (msg.what) {
            case GLASSES_SETTING_CHANGE2:
                refreshUI();
                refreshRotate();
                break;
            case MAC_BIND_CHANGE:
                refreshUI();
                break;
        }
    }

    public  void  refreshUI(){

        bindTitleTv.setText("绑定眼镜");
        //TODO 修改为服务器用户绑定设备唯一标识
        bindDexTv.setText(App.myApplication.getBindMacDevice());
        fsvFontSize.setCurrentPosition(CommonUtil.getPositionFontSize());
        mBrightSb.setProgress(CommonUtil.getPositionBright());
    }
    public void refreshRotate(){
        if(App.myApplication.getRotateDevice().equals("0")){
            rotateDexTv.setText("左眼");
        }else{
            rotateDexTv.setText("右眼");
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void connectStatusChange(ConnectEvent event) {
        mConnect = event.getMessage();
    }
    //眼镜设置修改
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void glassesSetChangeInfoSecond(GlassesInfoChangeShow2Event event) {
//        getHandler().obtainMessage(GLASSES_SETTING_CHANGE2).sendToTarget();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
    }

    private void showDeviceListDialog() {
        dialog.showAddress(true);
        dialog.show();
    }

    private void searchDevice() {
        if (!bt.isDiscovering())
            bt.startDiscovery();
    }

    @Override
    public void foundDev(BluetoothDevice dev) {
        if(!mSearchList.contains(dev.getAddress())){
            if(dev != null&&dev.getName()!=null&&dev.getName().toLowerCase().contains("seeingvoice")) {
                mSearchList.add(dev.getAddress());
                dialog.addDevices(dev);
                Log.d("ljp","seondTagfragment发现设备并加入数组："+dev.getAddress());
            }
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        LogUtils.d("ljp","secondTabFragment onPause 调用。");
    }
    @Override
    public void foundDevFinish(BluetoothDevice dev) {
        searchDevice();
    }

    @Override
    public void blueConnectionStateChanged(String state, BluetoothDevice dev) {

    }

    @Override
    public void socketNotify(int state, Object obj) {

    }

    @Override
    public void onBluetoothDeviceSelected(BluetoothDevice device) {
        //TODO 断掉当前的连接连接新的
        App.myApplication.setBindMacDevice(device.getAddress());
        String macBind = App.myApplication.getBindMacDevice();
        getHandler().obtainMessage(MAC_BIND_CHANGE).sendToTarget();
        if(!macBind.equals("") && !macBind.equals(preMac)){
            ((MainFragment) getParentFragment()).clickFirstTab(0,1);
            EventBusActivityScope.getDefault(_mActivity).post(new BindMacChangeEvent(macBind));
        }
        stopFind();
    }
    private void stopFind(){
        if(bt.isDiscovering()){
            bt.cancelDiscovery();
        }
    }
}

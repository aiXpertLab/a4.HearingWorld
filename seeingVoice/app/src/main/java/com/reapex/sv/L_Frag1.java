package com.reapex.sv;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.reapex.sv.util.L_CommonUtil;
import com.reapex.sv.util.L_LogUtils;
import com.reapex.sv.view.BatteryView;
import com.reapex.sv.view.SelfDialogconnectFaild;
import com.reapex.sv.xunfeiutil.WebIATWS;

/**
 * @author  LeoReny@hypech.com
 * @version 3.0.0
 * @since   2021-02-19
 */
public class L_Frag1 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1, mParam2;

    //notification 保持后台运行
    private boolean checkNotificationPermission = true;

    // 控制UI按钮的状态 ，12301关闭状态，12302打开状态
    public int status;
    int open_status = 12301;
    int close_status = 12302;

    private ImageView mConnect_recordIv;
    private RelativeLayout mBrightContainerRl, mFontContainerRl, mSightViewContainerRl, mBackgroundRl;
    private LinearLayout mUnconnectContainerLl;
    private TextView mConnectContainerTv,mBrightShowTv,mFontShowTv,mLeftRightShowTv;

    private SelfDialogconnectFaild connectFaildDialog ;
    //眼镜显示状态

    private BatteryView mBatteryView;

    private AppCompatActivity _mActivity;
    WebIATWS webapi;

    //TODO  测试 先假设设备已经连接就不用进行蓝牙连接眼镜了
    private boolean mConnected = false;
    private boolean mF7request = false;
    private boolean mCanSendMsg = true;

    public L_Frag1() {    }// Required empty public constructor

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment frag1.
     */
    // TODO: Rename and change types and number of parameters
    public static L_Frag1 newInstance(String param1, String param2) {
        L_Frag1 fragment = new L_Frag1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vv = inflater.inflate(R.layout.a_frag1, container, false);
        mConnect_recordIv = vv.findViewById(R.id.start_connect_iv);

        mBrightContainerRl = vv.findViewById(R.id.bright_container_rl);
        mFontContainerRl = vv.findViewById(R.id.font_container_rl);
        mSightViewContainerRl = vv.findViewById(R.id.left_container_rl);
        mBackgroundRl = vv.findViewById(R.id.middle_show_container_rl);
        mUnconnectContainerLl = vv.findViewById(R.id.unconnect_status_container_Ll);
        mConnectContainerTv = vv.findViewById(R.id.connect_status_tv);

        //眼镜显示状态
        mBrightShowTv = vv.findViewById(R.id.bright_tv);
        mFontShowTv = vv.findViewById(R.id.font_tv);
        mLeftRightShowTv = vv.findViewById(R.id.left_tv);

        //电池电量
        mBatteryView = vv.findViewById(R.id.batteryview);
        mBatteryView.setPower(70);

        //初始化讯飞webapi接口类
        webapi = new WebIATWS(_mActivity);
        if (!L_CommonUtil.hasNet()) {
            mConnected = false;
        } else if (typeOfNet() == "WIFI") {
            mConnected = true;
        } else if (typeOfNet() == "MO") {
            mConnected = true;
        } else {
            mConnected = true;
        }
        refreshConnectUI();
        return vv;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mConnect_recordIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                if (status == close_status) {
                    try {
                        webapi.StartAsr(_mActivity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    status = open_status;
                    updateBtnTextByStatus();
                } else if (status == open_status) {
                    try {
                        webapi.StopAsr();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    status = close_status;
                    updateBtnTextByStatus();
                } else {
                    autoConnectDevice();
                }
            }
        });
    }

    public String typeOfNet(){
        return "D";
    }

    public void refreshConnectUI(){
        if(mConnected){
            updateBtnTextByStatus();
            mBrightContainerRl.setVisibility(View.VISIBLE);
            mFontContainerRl.setVisibility(View.VISIBLE);
            mSightViewContainerRl.setVisibility(View.VISIBLE);
            mBatteryView.setVisibility(View.VISIBLE);
            mBackgroundRl.setBackgroundResource(R.drawable.background);
            mUnconnectContainerLl.setVisibility(View.INVISIBLE);
            mConnectContainerTv.setVisibility(View.VISIBLE);
        }else{
            mConnect_recordIv.setImageResource(R.drawable.icon_connect);
            mBrightContainerRl.setVisibility(View.INVISIBLE);
            mFontContainerRl.setVisibility(View.INVISIBLE);
            mSightViewContainerRl.setVisibility(View.INVISIBLE);
            mBatteryView.setVisibility(View.INVISIBLE);
            mBackgroundRl.setBackgroundResource(R.drawable.background_unconnected);
            mUnconnectContainerLl.setVisibility(View.VISIBLE);
            mConnectContainerTv.setVisibility(View.INVISIBLE);
        }
    }

    private void updateBtnTextByStatus() {
        if(mConnected){
            if(status == close_status){
                mConnect_recordIv.setImageResource(R.drawable.btn_recordings_no);
            }else if(status == open_status){
                mConnect_recordIv.setImageResource(R.drawable.btn_recordings);
            }
        }else{
            mConnect_recordIv.setImageResource(R.drawable.icon_connect);
        }

    }

    private void autoConnectDevice(){
        //每次进来都会检查一次是否有通知的权限
        if(checkNotificationPermission){
//sv            L_CommonUtil.notificationPermission(_mActivity);
            checkNotificationPermission = false;
        }
    }
}


/**


 @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
    }

    @Subscribe
    public void onTabSelectedEvent(TabSelectedEvent event) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SPPLinkUtil2.unRegisterReceiver(_mActivity);
        //程序被杀死结束录音以及讯飞识别
        webapi.StopAsr();
        SPPLinkUtil2.close(_mActivity);
        super.onDestroy();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
    }
    public void processHandlerMessage(Message msg) {
        switch (msg.what) {
            case OKGO_ON_ERROR:
                Toast.makeText(_mActivity,"当前网络状况出现了问题!",Toast.LENGTH_LONG).show();
                break;
            // 处理MessageStatusRecogListener中的状态回调
            case MSG_ON_DEVICE_FIND:
                BluetoothDevice dev = (BluetoothDevice)msg.obj;
                dialog.addDevices(dev);
                Log.d("devfound", "OnDevFindListener 发现设备并加入数组：" + dev.getAddress());

                break;
            case UPDATE_FINISH_SUCCESS:
                shapeLoadingDialog.dismiss();
                updateFinishRes();
                break;
            case UPDATE_FINISH_FAILED:
                shapeLoadingDialog.dismiss();
                mCanSendMsg = true;
                break;
            case BLE_STATUS_OPEN:
//                autoConnectDevice();
                break;
            case BLE_STATUS_CLOSED:
                Toast.makeText(_mActivity,"请打开手机蓝牙！",Toast.LENGTH_LONG).show();
                break;
            case BLE_DISCONNECT:
                //TODO 接收mac改变的广播或则消息需要修改mBindChange的值
                if(!mBindChange) {
//                    mConnected = false;
                    connectStatusChange(false);
                    shapeLoadingDialog.dismiss();
                    try {
                        webapi.StopAsr();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (App.isDebug) Log.d("ljp", "点击结束");
                    status = close_status;
//TODO 连接状态改变
                    refreshConnectUI();
//                    updateBtnTextByStatus();
                }
                mBindChange = false;
                break;
            case START_BLE_DATA_SEND:

                if(isReadyUpdate){
                    //如果此时正在翻译关闭翻译
                    //TODO 如果用户此时已经打开了翻译，需要关闭翻译，等待讯飞接入进行增加功能
                    //开始发送数据升级包
                    shapeLoadingDialog.show();
                    sendFile(downloadFile);
                }
                break;
            case CONNECT_FAILED:
                Toast.makeText(_mActivity,"请先打开眼镜设备再手动点击连接！",Toast.LENGTH_LONG).show();
                connectFaildDialog.show();
                //点击事件
                connectFaildDialog.setYesOnclickListener("重新连接", new SelfDialogconnectFaild.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        connectFaildDialog.dismiss();
                        autoConnectDevice();
                    }
                });
                connectFaildDialog.setNoOnclickListener("绑定眼镜", new SelfDialogconnectFaild.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        //TODO 绑定设备弹窗
                        connectFaildDialog.dismiss();
                        searchDevAfterCheck();
//                        macBindDialog.show();
//                        macBindDialog.setYesOnclickListener("绑定设备", new SelfDialogMacBind.onYesOnclickListener() {
//                            @Override
//                            public void onYesClick(String str) {
//                                //TODO 获取dialog editText 中数据
//
//                                if(str.equals("")){
//                                    Toast.makeText(_mActivity,"请输入正确设备唯一标识号",Toast.LENGTH_LONG).show();
//                                }else{
//                                    macBindDialog.dismiss();
//                                    autoConnectDevice();
//                                    //TODO 将新帮顶的mac发送到服务器
//                                }
//
//                            }
//                        });
//                        macBindDialog.setScanOnclickListener(new SelfDialogMacBind.onScanOnclickListener() {
//                            @Override
//                            public void onScanClick(String str) {
//                                macBindDialog.dismiss();
//                                Intent intent = new Intent(_mActivity,ScanMacCaptureActivity.class);
//                                startActivity(intent);
//                            }
//                        });
                    }
                });
                break;
            case GETDATA_FROM_SERVER:
                if(mBatteryView.getVisibility() == View.INVISIBLE) {
                    mBatteryView.setVisibility(View.VISIBLE);
                }
                mBatteryView.setPower(ConvertData.hexStrToShortLogin(msg.obj.toString()));
                short glassesVersion = ConvertData.hexStrToShortVersion(msg.obj.toString());
                short leftRightSetting = ConvertData.hexStrToShortleftRight(msg.obj.toString());
                short brightSetting = ConvertData.hexStrToShortBright(msg.obj.toString());
                short fontSetting = ConvertData.hexStrToShortFont(msg.obj.toString());

                App.myApplication.setRotateDevice(String.valueOf(leftRightSetting));
                App.myApplication.setEyeglassesBright(brightSetting);
                App.myApplication.setEyeglassesFontSize(fontSetting);
                App.myApplication.setGlassesSoftVersion(String.valueOf(glassesVersion));

                setGlassesShow();
                if(App.isDebug)Log.d("ljp","glassesVersion:"+String.valueOf(glassesVersion)+"原始数据leftrightsetting:"+leftRightSetting);
                //TODO 暂时注释眼镜升级逻辑代码
//                testLocalFile("/sdcard/ceshitest.txt");
//                Toast.makeText(this,"Version"+glassesVersion,Toast.LENGTH_LONG).show();
                getUpdateFromServer(String.valueOf(glassesVersion));
                break;
            case REFRESH_UI_BATTERY:
                if(mBatteryView.getVisibility() == View.INVISIBLE) {
                    mBatteryView.setVisibility(View.VISIBLE);
                }
                mBatteryView.setPower((short) msg.obj);

                break;
            case GLASSES_SETTING_CHANGE:
                setGlassesShow();
                break;

            case CONNECT_STARTLINK:
                if(!isSearchFlag &&!mConnected) {
                    progressDialog = ProgressDialog.show(_mActivity, "连接中", "请稍候...");
                }
                break;
            case CONNECT_SUCCESS:

                if(progressDialog!=null) {
                    progressDialog.dismiss();
                }
//                mConnected = true;
                connectStatusChange(true);
                // TODO 设备连接成功刷新状态
                refreshConnectUI();
                break;
            case NO_NEED_TO_UPDATE:
                autoConnectDevice();
                break;
            case NO_NEED_TO_UPDATE_GLASSES:
                startAsrAuto();
                break;
            default:
                break;

        }
    }
    //升级成功发送给眼镜消息
    public void updateFinishRes(){
        //升级成功最后发送给眼镜命令，发送完成后眼镜进行重启
        String content = "7f00058900"+ConvertData.makeChecksum("00058900")+"f7";
        Log.d("ljp","升级成功返回给眼镜！"+content);
        sendMsgBle(ConvertData.hexStringToBytes(content),SPPLinkUtil2);
        mCanSendMsg = true;
        nextFlag = true;
        //断开连接
        SPPLinkUtil2.close(_mActivity);
//        mConnected = false;
        connectStatusChange(false);
        try {
            webapi.StopAsr();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("ljp", "点击结束");
        status = close_status;
        //TODO 断开连接后刷新状态以及UI
        refreshConnectUI();
//        updateBtnTextByStatus();
    }
    public void bleMsgRespons(String msg){
        String[] splited = msg.split("\\s+");
        //首先对返回数据类型进行判断
        if (splited != null && splited.length > 3) {
            if(App.isDebug)Log.d("ljp", "收到命令：" + splited[3]);
            if (splited[3].equals("01")) {
                //登录
                //TODO 20190827
                if(App.isDebug){
                    Log.d("ljp", "收到命令：" + msg);
                    Log.d("ljp", "收到登录电池电量信息：" + ConvertData.hexStrToShortLogin(msg));
                }
                loginRes(SPPLinkUtil2);
                //TODO 打开眼镜升级接口网络获取
                getHandler().obtainMessage(GETDATA_FROM_SERVER,msg).sendToTarget();
//                sendFile("/sdcard/ceshitest.txt");
            } else if (splited[3].equals("02")) {
                //心跳包
                //TODO 20190827
                if(App.isDebug)Log.d("ljp", "心跳包返回的电池电量：" + ConvertData.hexStrToShortHeart(msg));
                getHandler().obtainMessage(REFRESH_UI_BATTERY,ConvertData.hexStrToShortHeart(msg)).sendToTarget();
                HeartRes(SPPLinkUtil2);
            } else if (splited[3].equals("83")) {
                //字体大小
                if(splited[4].equals("00")){
                    //字体修改成功
//                    EventBusActivityScope.getDefault(_mActivity).post(new GlassesInfoChangeShowEvent(FONT_CHANGE_INFO,CommonUtil.getPositionFontSize_Str()));
                    getHandler().obtainMessage(GLASSES_SETTING_CHANGE).sendToTarget();
                }else{
                    Toast.makeText(_mActivity,"字体设置失败。",Toast.LENGTH_LONG).show();
                }

            } else if (splited[3].equals("84")) {
                //收到文本消息响应
                //TODO 20190827
//                Log.d("ljp", "收到文本消息响应：");
                nextFlag = true;
                //TODO 20190827
//                Log.d("ljpNextFlag", "收到文本" + nextFlag);
            } else if (splited[3].equals("85")) {
                //收到翻页响应
            } else if(splited[3].equals("86")){
                //左右眼旋转屏幕
                if(splited[4].equals("00")){
                    //左右眼旋转修改成功
//                    if(App.myApplication.getRotateDevice().equals("0")){
//                        EventBusActivityScope.getDefault(_mActivity).post(new GlassesInfoChangeShowEvent(ROTATION_CHANGE_INFO,"左眼视觉"));
//                    }else{
//                        EventBusActivityScope.getDefault(_mActivity).post(new GlassesInfoChangeShowEvent(ROTATION_CHANGE_INFO,"右眼视觉"));
//                    }
                    getHandler().obtainMessage(GLASSES_SETTING_CHANGE).sendToTarget();
                 }else{
                    Toast.makeText(_mActivity,"左右眼切换失败。",Toast.LENGTH_LONG).show();
                }
            }else if(splited[3].equals("87")){
                // TODO 上方返回状态码现在未知 亮度 87
                if(splited[4].equals("00")){
                    //亮度修改成功
//                    EventBusActivityScope.getDefault(_mActivity).post(new GlassesInfoChangeShowEvent(BRIGHT_CHANGE_INFO,CommonUtil.getPositionBright_str()));
                    getHandler().obtainMessage(GLASSES_SETTING_CHANGE).sendToTarget();
                }else{
                    Toast.makeText(_mActivity,"亮度设置失败。",Toast.LENGTH_LONG).show();
                }
            } else if (splited[3].equals("91")) {
                //升级数据包接收成功发送下一个包
                if(splited[4].equals("00")) {
                    nextFlag = true;
                }else{
                    sendMsgBle(ConvertData.hexStringToBytes(updateFinalSend),SPPLinkUtil2);
                }
            } else if(splited[3].equals("90")){
                //发送需要更新后等待眼镜响应
                if(App.isDebug)Log.d("ljp", "收到文本消息响应："+splited[4]);
                if(splited[4].equals("00")){
                    isReadyUpdate = true;
                }else{
                    //接收不成功
                }
            }else if(splited[3].equals("08")){
                //TODO 20190827
//                Log.d("ljp", "收到文本消息响应：08");
                getHandler().obtainMessage(START_BLE_DATA_SEND).sendToTarget();
            }else if(splited[3].equals("09")){
                //眼镜设备升级数据传输完成准备重新启动
                if(splited[4].equals("00")){
                    //眼镜升级成功
                    getHandler().obtainMessage(UPDATE_FINISH_SUCCESS).sendToTarget();
                }else{
                    //眼镜升级失败
                    getHandler().obtainMessage(UPDATE_FINISH_FAILED).sendToTarget();
                }
            }
        }
    }
    //第一步连接mac设备
    //获取app更新信息
    //应用进行升级
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


    //获取眼镜硬件更新信息
    public void getUpdateFromServer(String glassesVersion){
        Log.d("ljp","获取更新版本参数：glassesversion:"+glassesVersion);
        OkGo.<ServerDataResponse<GlassesUpdateOutBean>>get(Urls.URL_GET_UPDATE)//
                .tag(this)//
                .params("version", glassesVersion)
                .execute(new JsonCallback<ServerDataResponse<GlassesUpdateOutBean>>() {
                    @Override
                    public void onSuccess(Response<ServerDataResponse<GlassesUpdateOutBean>> response) {
                        if(response.body() !=null && response.body().data != null && response.body().data.getInfo()!= null&& response.body().data.getInfo().size()>0) {

                            GlassesUpdateBean data = response.body().data.getInfo().get(0);
                            Log.d("ljp", "服务器数据：" + data.toString() + response.body().error_info);
                            fileDownload(data.getDownload_url(),data.getFile_name());
                        }else{
                            getHandler().obtainMessage(NO_NEED_TO_UPDATE_GLASSES).sendToTarget();
                        }
                    }

                    @Override
                    public void onError(Response<ServerDataResponse<GlassesUpdateOutBean>> response) {
//                        super.onError(response);
                        if(App.isDebug){
                            Log.d("ljpnet","response is null");
                        }
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
                    }
                });
    }
    //眼镜硬件更新文件下载
    public void fileDownload(String url,String fileName) {
//        Toast.makeText(this,"开始下载文件",Toast.LENGTH_LONG).show();
        OkGo.<File>get(url)//
                .tag(this)//
                .execute(new FileCallback(fileName){

                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
//                        btnFileDownload.setText("正在下载中");
                    }

                    @Override
                    public void onSuccess(Response<File> response) {
//                        btnFileDownload.setText("下载完成");
                        File mFile = response.body();
                        Log.d("ljp","fileDownload下载完成路径："+mFile.getAbsolutePath());
//                        Toast.makeText(ActivityUiRecog.this,"下载文件完成",Toast.LENGTH_LONG).show();
                        File file = new File(mFile.getAbsolutePath());
//                    mOut.writeInt(FLAG_FILE); //文件标记
//                    mOut.writeUTF(file.getName()); //文件名
//                    mOut.writeLong(file.length()); //文件长度

                        Log.d("ljp","download file length:"+file.length()+"");
                        //得到分包总包数

                        updateCount =  (int)Math.ceil((double)file.length()/(double)dispatchCount);
                        remainNum = (int)file.length()%dispatchCount;
                        Log.d("ljp","ramain Num:"+remainNum);
                        Log.d("ljp","updatecount:"+updateCount);
                        downloadFile = mFile.getAbsolutePath();
                        //TODO 动态变换参数
                        requestUpdate("04");
                    }

                    @Override
                    public void onError(Response<File> response) {
//                        handleError(response);
                        Log.d("ljp","文件下载失败");
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        System.out.println(progress);
                        Log.d("ljp","进度条："+progress.fraction * 10000);
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
        BANBENHAO = SPHelper.getInst().getString("BANBENHAO");
        if (!BANBENHAO.equals("")) {

            double SERVICECD = Double.parseDouble(BANBENHAO);
            if (SERVICECD < serviceCode) {
                isFirst = true;
            } else {
                isFirst = false;
            }
            SPHelper.getInst().saveBoolean("isFirst", isFirst);
        } else {
            isFirst = true;
            BANBENHAO = versionCode;
            SPHelper.getInst().saveBoolean("isFirst", isFirst);
        }
        //判断发现新版本后是否是第一次弹出升级框
        isFirst = SPHelper.getInst().getBoolean("isFirst");
        //判断是否需要版本升级
        if (code != serviceCode && code < serviceCode) {
            SPHelper.getInst().saveString("downLoadUrl", downLoadUrl);
            if (isFirst || isForceUpdate.equals("1")) {
                selfDialog = new SelfDialog(_mActivity, com.reapex.seeingvoice.R.style.dialog, updateDes);
                selfDialog.show();
                selfDialog.setYesOnclickListener("立即升级", new SelfDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        new UpdateManager(_mActivity, _mActivity, mFinalDownloadUrl);
                        selfDialog.dismiss();
                    }
                });

                //若强制升级显示
                if (isForceUpdate.equals("1")) {
                    selfDialog.setNoOnclickListener("退出", new SelfDialog.onNoOnclickListener() {
                        @Override
                        public void onNoClick() {
                            selfDialog.dismiss();
                            _mActivity.finish();
                        }
                    });
                } else if (isForceUpdate.equals("0")) {
                    //若非强制升级时显示
                    selfDialog.setNoOnclickListener("忽略此次", new SelfDialog.onNoOnclickListener() {
                        @Override
                        public void onNoClick() {
                            isFirst = false;
                            SPHelper.getInst().saveBoolean("isFirst", isFirst);
                            //保存到本地
                            BANBENHAO = versionCode;
                            SPHelper.getInst().saveString("updateDes", updateDes);
                            SPHelper.getInst().saveString("BANBENHAO", BANBENHAO);
                            selfDialog.dismiss();
                            getHandler().obtainMessage(NO_NEED_TO_UPDATE).sendToTarget();
                        }
                    });
                }
            }else{
                getHandler().obtainMessage(NO_NEED_TO_UPDATE).sendToTarget();
            }
        }else{
            getHandler().obtainMessage(NO_NEED_TO_UPDATE).sendToTarget();
        }
    }
    private void setupQueue() {
        CommonUtil.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        //升级放开
                        if (nextFlag) {
                            MessagequeueBaseBean product = queue.poll();

                            if (product != null) {

                                if (product instanceof MessagequeueBean) {
                                    synchronized (product) {
                                        MessagequeueBean msg = (MessagequeueBean) product;
                                        if(mCanSendMsg) {
                                            sendTextMSG(msg.getText(), msg.getNumId(), msg.getDataLength(), msg.getCount(), msg.getPage());
                                        }
//                                        nextFlag = false;
                                    }
                                } else if (product instanceof MessagequeueFileBean) {
                                    synchronized (product) {
                                        MessagequeueFileBean file = (MessagequeueFileBean) product;
                                        nextFlag = false;
                                        sendMsgBle(ConvertData.hexStringToBytes(file.getText()),SPPLinkUtil2);
                                        updateFinalSend = file.getText();
                                    }
                                }
                            }
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public void sendTextMSG(String text, int numId, int datalength, int count, int page) {
        if (text == null || text.isEmpty())
            return;


        try {
            String replaceStr = ConvertData.str2HexStr(text, false);
            StringBuilder resultStr = new StringBuilder();
            StringBuilder contentStr = new StringBuilder();
            contentStr.append(ConvertData.intToHexStringLe(ConvertData.hexStringToBytes(replaceStr).length + 10)).append("04").append(ConvertData.intToHexStringLe(numId)).append(ConvertData.intToHexStringLe(count)).append(ConvertData.intToHexStringLe(page))
                    .append(replaceStr);
            resultStr.append("7f").append(CommonUtil.specialCharReplace(contentStr.toString()+ConvertData.makeChecksum(contentStr.toString()))).append("f7");
            byte[] originData = ConvertData.hexStringToBytes(resultStr.toString());
            int size = (int) Math.ceil(originData.length / (MAX_SIZE * 1.0));
            byte[][] data = new byte[size][MAX_SIZE];

            int start = 0;
            int end = 0;
            int index = 0;
            while (index < size) {
                index++;


                end = Math.min(start + MAX_SIZE, originData.length);
                System.arraycopy(originData, start, data[index - 1], 0, end - start);

                if (index == size) {
//                    //最后一组数据为了清除末尾多余的0000单独发送
                    byte[] finalByte = new byte[end - start];
                    System.arraycopy(originData, start, finalByte, 0, end - start);
                    Log.d("finalByte", "最后：" + ConvertData.bytesToHexString(finalByte, false));
                    sendMsgBle(finalByte,SPPLinkUtil2);
                } else {
                    Log.d("finalByte", "中间:" + ConvertData.bytesToHexString(data[index - 1], false));
                    sendMsgBle(data[index - 1],SPPLinkUtil2);

                }
                start = end;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void requestUpdate(String updateItem){
        //TODO 开始发送升级包，这时候不能给眼镜发送说话数据包
        String content = "000710"+updateItem + ConvertData.intToHexStringLe(updateCount);
        String sendStr = "7f"+content+ConvertData.makeChecksum(content)+"f7";
        Log.d("ljp","requestUpdate:"+sendStr);
        mCanSendMsg = false;
        sendMsgBle(ConvertData.hexStringToBytes(sendStr),SPPLinkUtil2);
    }

    //TODO 讯飞接收到消息开始发送
    public void sendMsgContinue(xunfeiMessageEvent event){


        if(!event.isFinal()){
            //眼镜端不显示请检查网络这些文字信息
//            if (!event.getMessage().contains("请检查您的网络") && mConnected) {
            if (mConnected) {
                pageSendByStr(event.getMessage(), CommonUtil.getUidFromStr(event.getUid()));
                if(App.isDebug)Log.d("ljpcontinue",event.getMessage());

            }

        }else{

            if (mConnected) {

                pageSendByStr(event.getMessage(), CommonUtil.getUidFromStr(event.getUid()));
                if(App.isDebug)Log.d("ljpcontinue",event.getMessage());
            }
        }
    }

    public void pageSendByStr(String text, int numId) {
        if (text == null || text.isEmpty())
            return;
        int count = 0;
        int remain = text.length() % MAX_SIZE_CHAR;
        if (remain != 0) {
            count = 1 + text.length() / MAX_SIZE_CHAR;
        } else {
            count = text.length() / MAX_SIZE_CHAR;
        }
        String[] strArray = new String[count];
        int start = 0;

        try {
            for (int i = 0; i < count; i++) {
                if (i == count - 1) {
                    if (remain != 0) {
                        strArray[i] = text.substring(start, remain + start);
                    } else {
                        strArray[i] = text.substring(start, MAX_SIZE_CHAR + start);
                    }
//                    Log.d("ljp", "分页发送函数 i=" + i + "；count:" + count + "page:" + (i + 1) + "text:" + strArray[i]);
                    if(App.isDebug)Log.d("ljp","queue add to queue");
                    queue.add(new MessagequeueBean(strArray[i], numId, count, i + 1));
                } else {
                    strArray[i] = text.substring(start, start + MAX_SIZE_CHAR);
//                    Log.d("ljp", "分页发送函数中间 i=" + i + "count:" + count + "page:" + (i + 1) + "text:" + "text:" + strArray[i]);
                    queue.add(new MessagequeueBean(strArray[i], numId, count, i + 1));
                }
                start = start + MAX_SIZE_CHAR;
            }

        } catch (Exception e) {

        }
    }
    /**
     * 发送文件
    public void sendFile(final String filePath) {
        Log.d("ljp","进入sendFile"+filePath);
        CommonUtil.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream in = new FileInputStream(filePath);
                    Log.d("ljp","inputstream length:"+in.available()+"");
                    //得到分包总包数
//                    int count = in.available()%dispatchCount;
                    int currentNum = 1;
                    int r;
                    byte[] b;
                    if(1 == updateCount && remainNum != 0){
                        b = new byte[remainNum];
                        Log.d("ljp","进入："+remainNum+"byte 长度："+b.length);
                    }else {
                        b = new byte[dispatchCount];
                    }
                    //TODO 文件开始发送
//                    notifyUI(Listener.MSG, "正在发送文件(" + filePath + "),请稍后...");
                    while ( in.read(b) != -1) {
                        try {
                            String replaceStr = ConvertData.bytesToHexString(b,false);

                            StringBuilder resultStr = new StringBuilder();
                            StringBuilder contentStr = new StringBuilder();

                            contentStr.append(ConvertData.intToHexStringLe(ConvertData.hexStringToBytes(replaceStr).length + 8)).append("11").append("04").append(ConvertData.intToHexStringLe(updateCount))
                                    .append(ConvertData.intToHexStringLe(currentNum))
                                    .append(replaceStr);
                            resultStr.append("7f").append(CommonUtil.specialCharReplace(contentStr.toString()+ConvertData.makeChecksum(contentStr.toString()))).append("f7");
                            MessagequeueFileBean fileMsg = new MessagequeueFileBean(resultStr.toString(),updateCount,currentNum);
                            Log.d("ljp","当前编号："+currentNum);
                            queue.add(fileMsg);
                            currentNum++;
                            if(currentNum == updateCount){
                                if(remainNum !=0) {

                                    b = new byte[remainNum];
                                    Log.d("ljp","进入："+remainNum+"byte 长度："+b.length);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                } catch (Throwable e) {
                    //TODO 发送异常处理
                    e.printStackTrace();
                }
            }
        });
    }

    private void setGlassesShow(){
        mBrightShowTv.setText(CommonUtil.getPositionBright_str());
        mFontShowTv.setText(CommonUtil.getPositionFontSize_Str());
        if(App.myApplication.getRotateDevice().equals("0")){
            mLeftRightShowTv.setText("左眼");
        }else{
            mLeftRightShowTv.setText("右眼");
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessageAsr(xunfeiMessageEvent event) {
        sendMsgContinue(event);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bindMacChange(BindMacChangeEvent event) {
        //先断开当前连接，然后再连接
        LogUtils.d("ljp","firstTabFragment bindmacChange 广播接收调用");

        mBindChange = true;
        if(mConnected){
//            progressDialog = ProgressDialog.show(_mActivity, "连接中", "请稍候...");
            SPPLinkUtil2.close(_mActivity);
            mConnected = false;
            refreshConnectUI();

            try {
                webapi.StopAsr();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("ljp", "点击结束");
            status = close_status;
            updateBtnTextByStatus();
            autoConnectDevice();
        }else{
            //TODO 测试看需不需要加开启dialog
//            progressDialog = ProgressDialog.show(_mActivity, "连接中", "请稍候...");
            autoConnectDevice();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void glassesOrderSend(GlassesOrderEvent event) {
        byte[] bytes;
        //TODO 数据长度未计算

        bytes = ConvertData.hexStringToBytes(event.getMessage());

        if(App.isDebug)Log.d("ljp","收到命令前发送命令glassesOrderSend："+ConvertData.bytesToHexString(bytes, false));
        sendMsgBle(bytes,SPPLinkUtil2);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessageNoNetWork(NoNetWorkEvent event) {
        if(!CommonUtil.hasNet()){
            Toast.makeText(_mActivity,"请检查网络连接！",Toast.LENGTH_LONG).show();
            if(status == open_status) {
                try {
                    webapi.StopAsr();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("ljp", "点击结束");
                status = close_status;
                updateBtnTextByStatus();
            }
            return ;
        }
    }
    //眼镜设置修改
//    @Subscribe(threadMode = ThreadMode.MAIN)
////    public void glassesSetChangeInfo(GlassesInfoChangeShowEvent event) {
////        getHandler().obtainMessage(GLASSES_SETTING_CHANGE).sendToTarget();
////    }
    private void connectStatusChange(boolean connectStatus){
        mConnected = connectStatus;
        EventBusActivityScope.getDefault(_mActivity).post(new ConnectEvent(mConnected));
    }
    private void startAsrAuto(){
        try {
            webapi.StartAsr(_mActivity);
            status = open_status;
            updateBtnTextByStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("ljp","点击开始");

    }
    private void searchDev(){
        if(!mConnected) {
            isSearchFlag = true;
            showDeviceListDialog();
            SPPLinkUtil2.searchDevice();
        }
    }
    private void showDeviceListDialog() {
        dialog.show();
    }

    @Override
    public void onBluetoothDeviceSelected(BluetoothDevice device) {
        App.myApplication.setBindMacDevice(device.getAddress());
        SPPLinkUtil2.doStopFind();
        isSearchFlag = false;
        autoConnectDevice();

//        SPPLinkUtil.connectImmediately(ActivityUiRecog.this,App.myApplication.getBindMacDevice());
//        SPPLinkUtil.link(ActivityUiRecog.this, App.myApplication.getBindMacDevice());
        Log.d("ljp","bindMacaddress:"+App.myApplication.getBindMacDevice());
    }
    /**
     * 检测GPS、位置权限是否开启

    public void searchDevAfterCheck() {

        //得到系统的位置服务，判断GPS是否激活
        lm = (LocationManager) _mActivity.getSystemService(LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            searchDev();
        } else {
            //TODO 10.0显示这个msg
            if (Build.VERSION.SDK_INT >= 29) {
                Toast.makeText(_mActivity, "为保证可以正常搜索到眼镜设备，请开启定位服务。", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, PRIVATE_CODE);
            }else{
                searchDev();
            }
        }
    }
}
*/
//package seeingvoice.jskj.com.seeingvoice.history.fragment;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.DefaultItemAnimator;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//import seeingvoice.jskj.com.seeingvoice.MyApplication;
//import seeingvoice.jskj.com.seeingvoice.R;
//import seeingvoice.jskj.com.seeingvoice.beans.VerbalHistoryBean;
//import seeingvoice.jskj.com.seeingvoice.history.HistroryActivity;
//import seeingvoice.jskj.com.seeingvoice.history.adapter.RecycleViewAdapter;
//import seeingvoice.jskj.com.seeingvoice.okhttpUtil.OkHttpManager;
//import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;
//
//import java.io.IOException;
//import java.util.List;
//
//import okhttp3.Request;
//
//import static seeingvoice.jskj.com.seeingvoice.AppConstant.NET_STATE_SUCCESS;
//import static seeingvoice.jskj.com.seeingvoice.AppConstant.VERBAL_TEST_RESULT_LIST;
//
///**
// * Date:2019/8/26
// * Time:16:54
// * auther:zyy
// */
//public class VerbalTestHistoryFragment extends BaseViewPageFragment {
//
//    //服务端 查询出来的数据列表
//    private final String TAG =  VerbalTestHistoryFragment.class.getName();
//    private RecycleViewAdapter mRecycleViewAdapter;
//    private LinearLayoutManager layoutManager;
//    private RecyclerView mRecycleView;
//    private TextView mTvNoReuslt,mTv_state;
//    private static List<VerbalHistoryBean.DataBean.LanguageListBean> verbalDataList = null;//从服务端得到网络结果
//    private VerbalHistoryBean verbalHistoryBean = null;
//    private HistroryActivity mActivity;
//    private ProgressDialog dialog;
//    public Handler mHandler = new Handler(Looper.getMainLooper()){
//        @Override
//        public void handleMessage(android.os.Message msg) {
//            switch(msg.what){
//                case 0x006:
//                    stopDialog();
//                    initRecycleView(verbalDataList);
//                    break;
//            }
//        }
//    };
//
//    @Override
//    protected int getLayoutRes() {
//        return R.layout.fragment_pure_history;
//    }
//
//    @Override
//    public void initView(View v) {
//        mRecycleView = v.findViewById(R.id.recycleview);
//        mTvNoReuslt = v.findViewById(R.id.no_result);
//        mTv_state = v.findViewById(R.id.tv_state);
//        mTv_state.setText("言语测试历史记录");
//        initRecycleView(verbalDataList);
//    }
//
////    @Override
////    protected void requestData() {
////        requestDatalist();
////    }
//
//    @Override
//    protected void requestDataAutoRefresh() {
//        requestDatalist();
//    }
//
//    private void showDialog(String showMessageStr) {
//        if(mActivity.isFinishing()){
//            //show dialog
//            dialog = ProgressDialog.show(getActivity(),"数据加载中",showMessageStr,true,true);
//        }
//    }
//
//    private void stopDialog() {
//        if (dialog != null){
//            dialog.dismiss();
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mActivity = (HistroryActivity)context;
//        mActivity.setHandler(mHandler);
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        showDialog("言语测试结果加载中");
//    }
//
//    private void requestDatalist() {
//        //异步请求历史纪录
//        OkHttpManager.getInstence().getNet(VERBAL_TEST_RESULT_LIST+"?user_id="+MyApplication.userId, new OkHttpManager.ResultCallback() {
//            @Override
//            public void onFailed(Request request, IOException e) {
//                ToastUtil.showShortToast("网络错误,请稍后再试！");
//            }
//
//            @Override
//            public void onSuccess(String response) {
//                Log.e(TAG,response);
//                verbalHistoryBean = null;
//                try {
//                    Gson gson = new Gson();
//                    verbalHistoryBean = gson.fromJson(response, VerbalHistoryBean.class);
//                    if (null != verbalHistoryBean){
//                        Log.e(TAG,"verbalHistoryBean 得到数据:"+verbalHistoryBean.getMessage_code());
//                        if (verbalHistoryBean.getMessage_code().equals(NET_STATE_SUCCESS)){
//                            try {
//                                verbalDataList = verbalHistoryBean.getData().getLanguage_list();
//                                Log.e(TAG,"获得dataList长度为： "+verbalDataList.size());
//                                if (null != verbalDataList && !verbalDataList.isEmpty()){
//                                    Message msg = new Message();
//                                    msg.what = 0x006;
//                                    mHandler.sendMessage(msg);
//                                }else {
//                                    Log.e(TAG,verbalHistoryBean.getError_info()+verbalHistoryBean.getError_code());
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }else {
//                        ToastUtil.showLongToast("网络错误,请重新访问");
//                    }
//                } catch (JsonSyntaxException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    private void initRecycleView(List<VerbalHistoryBean.DataBean.LanguageListBean> list) {
//        if (null == list || list.isEmpty()){
//            isNoReult(null);
//        }else {
//            mTvNoReuslt.setVisibility(View.GONE);
//            mTv_state.setText("言语测试历史记录");
//            mTv_state.setVisibility(View.VISIBLE);
//            mRecycleViewAdapter = new RecycleViewAdapter(getActivity(),list);
//            mRecycleViewAdapter.notifyDataSetChanged();
//            layoutManager = new LinearLayoutManager(getActivity());
//            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//加上这句就可以设置方向
//            mRecycleView.setLayoutManager(layoutManager);
//            mRecycleView.setAdapter(mRecycleViewAdapter);
//            // 设置Item添加和移除的动画
//            mRecycleView.setItemAnimator(new DefaultItemAnimator());
//        }
//        mRecycleView.invalidate();
//    }
//
//    private void isNoReult(List<VerbalHistoryBean.DataBean.LanguageListBean> AgeResultList) {
//        if (null == AgeResultList || AgeResultList.isEmpty()){
//            mTvNoReuslt.setText("没有测试结果");
//            mTvNoReuslt.setVisibility(View.VISIBLE);
//            mTv_state.setVisibility(View.GONE);
//        }
//    }
//
////    @Override
////    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
////        super.onViewStateRestored(savedInstanceState);
////        if(savedInstanceState != null){
////            verbalDataList = savedInstanceState.getParcelableArrayList("datalist");
//////            initRecycleView(verbalDataList);
////        }
////    }
////
////    @Override
////    public void onSaveInstanceState(@NonNull Bundle outState) {
////        super.onSaveInstanceState(outState);
////        if (null != verbalDataList && !verbalDataList.isEmpty()){
////            List<VerbalHistoryBean.DataBean.LanguageListBean> list = verbalDataList;
////            outState.putParcelableArrayList("datalist", (ArrayList<? extends Parcelable>) list);
////        }
////    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        stopDialog();
//    }
//}
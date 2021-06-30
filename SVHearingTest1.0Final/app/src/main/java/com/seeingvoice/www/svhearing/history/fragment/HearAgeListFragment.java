package com.seeingvoice.www.svhearing.history.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;
import com.seeingvoice.www.svhearing.AppConstant;
import com.seeingvoice.www.svhearing.MyApplication;
import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.AntiShakeUtils;
import com.seeingvoice.www.svhearing.beans.AgeHistoryBean;
import com.seeingvoice.www.svhearing.beans.PureHistoryBean;
import com.seeingvoice.www.svhearing.history.HistroryActivity;
import com.seeingvoice.www.svhearing.history.SwipeRefreshView;
import com.seeingvoice.www.svhearing.history.adapter.NewRecycleAdapter;
import com.seeingvoice.www.svhearing.okhttpUtil.OkHttpManager;
import com.seeingvoice.www.svhearing.ui.SelfDialog;
import com.seeingvoice.www.svhearing.util.ToastUtil;

import static com.seeingvoice.www.svhearing.AppConstant.AGE_TEST_DELETE;
import static com.seeingvoice.www.svhearing.AppConstant.AGE_TEST_RESULT_LIST;
import static com.seeingvoice.www.svhearing.AppConstant.NET_STATE_SUCCESS;

/**
 * A simple {@link Fragment} subclass.
 */
public class HearAgeListFragment extends BaseViewPageFragment{

    //数据库中 查询出来的数据列表
    private final String TAG =  HearAgeListFragment.class.getName();
    private LinearLayoutManager layoutManager;//列表布局管理者
    private RecyclerView mNewRecycleView;//列表
    private NewRecycleAdapter mNewRecycleAdapter;
    private TextView mTvNoReuslt;//文字显示没结果，和状态
    private static List<AgeHistoryBean.DataBean.AgeListBean> ageDataList = null;//从服务端得到网络结果
    private AgeHistoryBean ageHistoryBean = null;
    private HistroryActivity mActivity;//上下文
    private ProgressDialog dialog;//进度条
    public Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x007) {
                stopDialog();
                releaseSelDialog();
                ToastUtil.showLongToastCenter("更新完成！");
                initRecycleView(ageDataList);
            }

            if (msg.what == 0x008) {
                ToastUtil.showLongToastCenter("网络错误！");
                stopDialog();
                ageDataList = null;
                initRecycleView(ageDataList);
                isNoReult(null,"网络访问失败，请检查网络！");
            }
        }
    };//主线程运行

    private SwipeRefreshView mSwipLayoutView;
    private int pageSize = 10,currentPage = 1,totalCount;//总条数，实际是请求回来赋值
    private SelfDialog selfDialog;


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_pure_history;
    }

    @Override
    public void initView(View v) {
        mActivity = (HistroryActivity) getActivity();
        mSwipLayoutView = v.findViewById(R.id.layout_swipe_refresh_view);
        mNewRecycleView = v.findViewById(R.id.recyclerView_new);
        mTvNoReuslt = v.findViewById(R.id.no_result);
        layoutManager = new LinearLayoutManager(mActivity);
        mNewRecycleView.setLayoutManager(layoutManager);
    }

    @Override
    protected void requestData() {
        requestDatalist();
    }

    @Override
    protected void requestDataAutoRefresh() {
        requestDatalist();
    }

    private void showDialog(String showMessageStr) {
        if(!mActivity.isFinishing()){
            //show dialog
            dialog = ProgressDialog.show(mActivity,"数据加载中",showMessageStr,true,true);
        }
    }

    private void stopDialog() {
        if (dialog != null){
            dialog.dismiss();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {//寄生的Activity被创建的时候回调该函数
        super.onActivityCreated(savedInstanceState);
    }

    private void requestDatalist() {
        //异步请求历史纪录
        OkHttpManager.getInstence().getNet(AGE_TEST_RESULT_LIST+"?user_id="+MyApplication.userId, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
//                ToastUtil.showLongToastCenter("网络访问失败，稍后再试！");
//                Log.e(TAG, "请求失败的时候执行的方法requestDatalist" + request.toString());
//                isNoReult(null,"网络访问失败，请检查网络！");
                Message msg = new Message();
                msg.what = 0x008;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onSuccess(String response) {
//                Log.e(TAG, "requestDatalist"+response);
                try {
                    Gson gson = new Gson();
                    ageHistoryBean = gson.fromJson(response, AgeHistoryBean.class);
                    if (!response.isEmpty() && ageHistoryBean == null) {
                        requestDatalist();
                        return;
                    }
                    if (null != ageHistoryBean) {
//                        Log.e(TAG, "pureHistoryBean 得到数据:" + ageHistoryBean.getData().getAge_list().get(0).getId());
                        if (ageHistoryBean.getMessage_code().equals(NET_STATE_SUCCESS)) {
                            try {
                                ageDataList = ageHistoryBean.getData().getAge_list();
                                if (null != ageDataList && !ageDataList.isEmpty()) {
                                    Log.e(TAG, "获得dataList长度为： " + ageDataList.size());
                                    totalCount = ageDataList.size();
                                    if (totalCount > 0){
                                        Message msg = new Message();
                                        msg.what = 0x007;
                                        mHandler.sendMessage(msg);
                                    }
                                } else {
                                    Log.e(TAG, "222222222222222"+ageHistoryBean.getError_info() + ageHistoryBean.getError_code());
//                                    ageDataList.clear();
                                    initRecycleView(null);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
            /**
             * RecyclerView 的花样全在这里，横向，纵向，各种花式布局，都是从这来的
             * 下面是简单的纵向列表，根据自己的需求选择不同的layoutManager
             * 具体有那些layoutManager 自行百度吧
             * */
    }

    private void initRecycleView(final List<AgeHistoryBean.DataBean.AgeListBean> list) {
        if (null != list && list.size()>0){//如果有数据
            mNewRecycleView.setVisibility(View.VISIBLE);
            mTvNoReuslt.setVisibility(View.INVISIBLE);
            mNewRecycleAdapter = new NewRecycleAdapter(mActivity,pageSize,currentPage,totalCount,list);
            mNewRecycleView.setAdapter(mNewRecycleAdapter);

            mNewRecycleAdapter.setOnItemClickListener(new NewRecycleAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (!AntiShakeUtils.isInvalidClick(view,800)){
                    }
                }
                @Override
                public void onItemDeleteClick(View view, int position) {
                    showSelfDialog(list.get(position).getId(),MyApplication.userId);
                }
            });
        }else {//如果没有数据
            isNoReult(null,"没有数据，请先测试保存测试结果，再查看历史记录");
        }
        mSwipLayoutView.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        mSwipLayoutView.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_blue_bright, R.color.colorPrimaryDark,
                android.R.color.holo_orange_dark, android.R.color.holo_red_dark, android.R.color.holo_purple);

        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        mSwipLayoutView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
    }


    private void DeleteItem(String age_id,int user_id) {
        OkHttpManager.getInstence().getNet(AGE_TEST_DELETE + "?age_id=" + age_id + "&user_id=" + user_id, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                ToastUtil.showShortToastCenter("网络错误，稍后再试！");
            }

            @Override
            public void onSuccess(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("message_code");
                    if (status.equals(AppConstant.NET_STATE_SUCCESS)){
                        ToastUtil.showShortToastCenter("删除成功");
                        selfDialog.dismiss();
                        mNewRecycleAdapter.notifyDataSetChanged();
                        requestDatalist();
                    }else {
                        String message = jsonObject.getString("error_info");
                        if (message!=null){
                            ToastUtil.showLongToast(message);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void showSelfDialog(final String ageId, final int userId) {
        selfDialog = new SelfDialog(mActivity, R.style.dialog, "确定删除此条记录？","提示：");
        selfDialog.show();
        selfDialog.setYesOnclickListener("是的", new SelfDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                DeleteItem(ageId,userId);
            }
        });
        selfDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                selfDialog.dismiss();
            }
        });
    }

    private void releaseSelDialog(){
        if (null != selfDialog){
            selfDialog.dismiss();
            selfDialog = null;
        }
    }

    //下拉刷新
    private void initData() {
        requestDatalist();
        // 加载完数据设置为不刷新状态，将下拉进度收起来
        if (mSwipLayoutView.isRefreshing()) {
            mSwipLayoutView.setRefreshing(false);
        }
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                currentPage = 1;
////                mNewRecycleAdapter.notifyDataSetChanged();
//                requestDatalist();
//                Toast.makeText(mActivity, "加载完成！", Toast.LENGTH_SHORT).show();
//                // 加载完数据设置为不刷新状态，将下拉进度收起来
//                if (mSwipLayoutView.isRefreshing()) {
//                    mSwipLayoutView.setRefreshing(false);
//                }
//            }
//        }, 2000);
    }

    private void isNoReult(List<PureHistoryBean.DataBean.AllListPureBean> pureTestResultList,String content) {
        if (null == pureTestResultList || pureTestResultList.isEmpty()){
            mTvNoReuslt.setVisibility(View.VISIBLE);
            mTvNoReuslt.setText(content);
            mNewRecycleView.setVisibility(View.INVISIBLE);
//            mTv_state.setVisibility(View.GONE);
        }

    }
    @Override
    public void onAttach(Context context) {//当fragment和activity捆绑的时候
        super.onAttach(context);
        mActivity = (HistroryActivity)context;
//        mActivity.setHandler(mHandler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDialog();
        releaseSelDialog();
    }
}

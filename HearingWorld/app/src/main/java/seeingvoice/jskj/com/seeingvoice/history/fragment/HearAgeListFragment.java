package seeingvoice.jskj.com.seeingvoice.history.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import seeingvoice.jskj.com.seeingvoice.MyApp;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.AntiShakeUtils;
import seeingvoice.jskj.com.seeingvoice.beans.AgeHistoryBean;
import seeingvoice.jskj.com.seeingvoice.beans.PureHistoryBean;
import seeingvoice.jskj.com.seeingvoice.history.HistroryL;
import seeingvoice.jskj.com.seeingvoice.history.SwipeRefreshView;
import seeingvoice.jskj.com.seeingvoice.history.adapter.NewRecycleAdapter;
import seeingvoice.jskj.com.seeingvoice.ui.SelfDialog;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

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
    private HistroryL mActivity;//上下文
    private ProgressDialog dialog;//进度条
    public Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(android.os.Message msg) {
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
        mActivity = (HistroryL) getActivity();
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
                    showSelfDialog(list.get(position).getId(), MyApp.userId);
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
        mActivity = (HistroryL)context;
//        mActivity.setHandler(mHandler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDialog();
        releaseSelDialog();
    }
}

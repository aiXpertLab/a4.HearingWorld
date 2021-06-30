package seeingvoice.jskj.com.seeingvoice.history.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;
import seeingvoice.jskj.com.seeingvoice.MyData;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.AntiShakeUtils;
import seeingvoice.jskj.com.seeingvoice.beans.PureHistoryBean;
import seeingvoice.jskj.com.seeingvoice.history.HistoryItemDetail;
import seeingvoice.jskj.com.seeingvoice.history.HistroryL;
import seeingvoice.jskj.com.seeingvoice.history.SwipeRefreshView;
import seeingvoice.jskj.com.seeingvoice.history.adapter.NewRecycleAdapter;
import seeingvoice.jskj.com.seeingvoice.history.adapter.RecycleViewAdapter;
import seeingvoice.jskj.com.seeingvoice.okhttpUtil.OkHttpManager;
import seeingvoice.jskj.com.seeingvoice.ui.SelfDialog;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

import static seeingvoice.jskj.com.seeingvoice.MyData.URL_DELETE_PURE_TEST_RESULT_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class PureHistoryFragment extends BaseViewPageFragment{

    //数据库中 查询出来的数据列表
    private final String TAG =  PureHistoryFragment.class.getName();
    private RecycleViewAdapter recycleViewAdapter;//列表数据适配器
    private LinearLayoutManager layoutManager;//列表布局管理者
    private RecyclerView mNewRecycleView;//列表
    private NewRecycleAdapter mNewRecycleAdapter;
    private TextView mTvNoReuslt,mTv_state;//文字显示没结果，和状态
    private static List<PureHistoryBean.DataBean.AllListPureBean> dataList = null;//从服务端得到网络结果 数据对象存储到列表中
    private PureHistoryBean pureHistoryBean = null;
    private HistroryL mActivity;//上下文
    private ProgressDialog dialog;//进度条
    public Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x007) {
                ToastUtil.showLongToastCenter("更新完成！");
                stopDialog();
                initRecycleView(dataList);
            }

            if (msg.what == 0x008) {
                ToastUtil.showLongToastCenter("网络错误！");
                stopDialog();
                dataList = null;
                initRecycleView(dataList);
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
//        mRecycleView = v.findViewById(R.id.recycleview);
        mSwipLayoutView = v.findViewById(R.id.layout_swipe_refresh_view);
        mNewRecycleView = v.findViewById(R.id.recyclerView_new);
        mTvNoReuslt = v.findViewById(R.id.no_result);
        layoutManager = new LinearLayoutManager(mActivity);
        mNewRecycleView.setLayoutManager(layoutManager);
//        mTv_state = v.findViewById(R.id.tv_state);
//        initRecycleView(dataList);
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

    private void initRecycleView(final List<PureHistoryBean.DataBean.AllListPureBean> list) {
        if (null != list && list.size()>0){
            mNewRecycleView.setVisibility(View.VISIBLE);
            mTvNoReuslt.setVisibility(View.INVISIBLE);
            mNewRecycleAdapter = new NewRecycleAdapter(mActivity,pageSize,currentPage,totalCount,list);
            mNewRecycleView.setAdapter(mNewRecycleAdapter);

            mNewRecycleAdapter.setOnItemClickListener(new NewRecycleAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (!AntiShakeUtils.isInvalidClick(view,800)){
                        int reportId = list.get(position).getReport_id();
                        String  remark = list.get(position).getRemark();
                        Intent intent = new Intent(mActivity, HistoryItemDetail.class);
                        intent.putExtra("report_id", reportId);
                        intent.putExtra("remark", remark);
                        mActivity.startActivity(intent);
                    }
                }

                @Override
                public void onItemDeleteClick(View view, int position) {
                    showSelfDialog(list.get(position).getUser_id(),list.get(position).getReport_id());
                }
            });
        }else {
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

        // 设置下拉加载更多
//        mSwipLayoutView.setOnLoadMoreListener(new SwipeRefreshView.OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                loadMoreData();//上拉加载
//            }
//        });

//        if (null == list || list.isEmpty()){
//            isNoReult(null);
//        }else {
//            mTvNoReuslt.setVisibility(View.GONE);
//            mTv_state.setText("纯音测试结果历史记录列表");
//            mTv_state.setVisibility(View.VISIBLE);
//            mRecycleViewAdapter = new RecycleViewAdapter(getActivity(), list);
//            mRecycleViewAdapter.notifyDataSetChanged();
//            layoutManager = new LinearLayoutManager(getActivity());
//            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//加上这句就可以设置方向
//            mRecycleView.setLayoutManager(layoutManager);
//            mRecycleView.setAdapter(mRecycleViewAdapter);
//            // 设置Item添加和移除的动画
//            mRecycleView.setItemAnimator(new DefaultItemAnimator());
//
//            /**
//             * 如果列表数据更新就可以用下列方法
//             * */
//            mRecycleViewAdapter.setOnItemClickListener(new RecycleViewAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(View view, int position) {
//                    if (!AntiShakeUtils.isInvalidClick(view,800)){
//                        int reportId = dataList.get(position).getReport_id();
//                        String  remark = dataList.get(position).getRemark();
//                        Intent intent = new Intent(mActivity, HistoryItemDetail.class);
//                        intent.putExtra("report_id", reportId);
//                        intent.putExtra("remark", remark);
//                        mActivity.startActivity(intent);
//                    }
//                }
//
//                @Override
//                public void onItemLongClick(View view, int position) {
////                mRecycleViewAdapter.deleteItem(position);
////                mRecycleViewAdapter.deleteItem();
//                    // 由于Adapter内部是直接在首个Item位置做删除操作，删除完毕后列表移动到首个Item位置
////                                    layoutManager.scrollToPosition(0);
////                Toast.makeText(getActivity(),"long click " + position + " item删除删除", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//        mRecycleView.invalidate();
    }

    //上拉加载
//    private void loadMoreData() {
//        //如果记录加载完毕，调用显示没有更多的方法
//        if(pageSize * currentPage >= totalCount){
//            mSwipLayoutView.setLoading(false);
//            ToastUtil.showShortToastCenter("数据已经全部加载完成！"+totalCount);
////                Toast.makeText(this,"数据已经全部加载完成！",Toast.LENGTH_LONG);
//        }else{
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    currentPage++;
//                    mNewRecycleAdapter.notifyDataSetChanged();
//                    Toast.makeText(mActivity, "加载了" + 5 + "条数据", Toast.LENGTH_SHORT).show();
//                    // 加载完数据设置为不加载状态，将加载进度收起来
//                    mSwipLayoutView.setLoading(false);
//                }
//            }, 1000);
//        }
//
//    }

    private void DeleteItem(int user_id, int report_id) {
        OkHttpManager.getInstence().getNet(URL_DELETE_PURE_TEST_RESULT_URL + "?simple_id=" + report_id + "&user_id=" + user_id, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                ToastUtil.showShortToastCenter("网络错误，稍后再试！");
            }

            @Override
            public void onSuccess(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("message_code");
                    if (status.equals(MyData.NET_STATE_SUCCESS)){
                        ToastUtil.showLongToast("删除成功");
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

    private void showSelfDialog(final int userId, final int reportId) {
        selfDialog = new SelfDialog(mActivity, R.style.dialog, "确定删除此条记录？","提示：");
        selfDialog.show();
        selfDialog.setYesOnclickListener("是的", new SelfDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                DeleteItem(userId,reportId);
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
        //                Toast.makeText(mActivity, "加载完成！", Toast.LENGTH_SHORT).show();
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
////                Toast.makeText(mActivity, "加载完成！", Toast.LENGTH_SHORT).show();
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
//            mTv_state.setVisibility(View.GONE);
            mNewRecycleView.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onAttach(Context context) {//当fragment和activity捆绑的时候
        super.onAttach(context);
        mActivity = (HistroryL)context;
//        mActivity.setHandler(mHandler);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        //查询到所有存储到数据库中的数据对象的集合
////        TestResultList = MyApplication.getAppInstance().getmDaoSession().queryBuilder(PureTestResult.class).list();
////        requestDatalist();
//        initRecycleView(dataList);
//    }


//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        if(savedInstanceState != null){
//            dataList = savedInstanceState.getParcelableArrayList("datalist");
////            initRecycleView(dataList);
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (null != dataList && !dataList.isEmpty()){
//            List<PureHistoryBean.DataBean.AllListPureBean> list = dataList;
//            outState.putParcelableArrayList("datalist", (ArrayList<? extends Parcelable>) list);
//        }
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDialog();
        releaseSelDialog();
    }
}

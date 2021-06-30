package seeingvoice.jskj.com.seeingvoice.history.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.beans.AgeHistoryBean;
import seeingvoice.jskj.com.seeingvoice.beans.PureHistoryBean;
import seeingvoice.jskj.com.seeingvoice.beans.VerbalHistoryBean;
import seeingvoice.jskj.com.seeingvoice.util.DateUtil;

public class NewRecycleAdapter extends RecyclerView.Adapter<NewRecycleAdapter.Hodler> {

//    public final static int NORMAL_TYPE = 0;
//    public final static int FOOTER_TYPE = 1;
    private List<PureHistoryBean.DataBean.AllListPureBean> PureResultList;
    private List<VerbalHistoryBean.DataBean.LanguageListBean> VerbalResultList;
    private List<AgeHistoryBean.DataBean.AgeListBean> AgeResultList;
    private OnItemClickListener onItemClickListener;
    private String type = "";
    private PureHistoryBean.DataBean.AllListPureBean PureResultBean = null;
    private VerbalHistoryBean.DataBean.LanguageListBean VerbalResultBean = null;
    private AgeHistoryBean.DataBean.AgeListBean AgeResultBean = null;
    private Activity mActivity;
    private int pageSize,currentPage,totalCount;//总条数，实际是请求回来赋值

    public NewRecycleAdapter(Activity activity, int pagesize,int currPage,int totalnum, List TestResultList) {
        this.type = TestResultList.get(0).getClass().getSimpleName();
        this.mActivity = activity;
        this.currentPage = currPage;
        this.totalCount = totalnum;
        this.pageSize = pagesize;
        switch (type){
            case "AllListPureBean":
                this.PureResultList = (List<PureHistoryBean.DataBean.AllListPureBean>)TestResultList;
                this.totalCount = PureResultList.size();
                break;
            case "LanguageListBean":
                this.VerbalResultList = (List<VerbalHistoryBean.DataBean.LanguageListBean>)TestResultList;
                this.totalCount = this.VerbalResultList.size();
                break;
            case "AgeListBean":
                this.AgeResultList = (List<AgeHistoryBean.DataBean.AgeListBean>)TestResultList;
                this.totalCount = this.AgeResultList.size();
                break;
        }
    }

    // ① 定义点击回调接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemDeleteClick(View view, int position);
    }

    // ② 定义一个设置点击监听器的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @NonNull
    @Override
    public Hodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Hodler(LayoutInflater.from(mActivity).inflate(R.layout.item_recycler_list_with_del_layout, null, false), viewType);
//        if(viewType == FOOTER_TYPE){
//            return new Hodler(LayoutInflater.from(mActivity).inflate(R.layout.view_footer, null, false), viewType);
//        }else{
//            return new Hodler(LayoutInflater.from(mActivity).inflate(R.layout.item_recycler_list_with_del_layout, null, false), viewType);
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull final Hodler holder, final int position) {
        switch (type){
            case "AllListPureBean":
                if (totalCount == 0){

                }else{
                    PureResultBean = PureResultList.get(position);
                    try {
                        holder.mTvItemTitle.setText(DateUtil.getDateToString(Long.valueOf(PureResultBean.getCreat_time()+"000"),"yyyy-MM-dd HH:mm:ss")+"\n备注："+PureResultBean.getRemark());
                        holder.content_layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onItemClickListener.onItemClick(holder.itemView,position);
//                            Intent i = new Intent();
//                            i.setClass(mActivity, HistoryItemDetail.class);
//                            mActivity.startActivity(i);
                            }
                        });
                        holder.mImgDel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemClickListener.onItemDeleteClick(holder.itemView,position);
                            }
                        });
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case "AgeListBean":
                if (totalCount == 0){

                }else {
                    AgeResultBean = AgeResultList.get(position);
                    try {
                        holder.mTvItemTitle.setText(DateUtil.getDateToString(Long.valueOf(AgeResultBean.getCreated_at()+"000"),"yyyy-MM-dd HH:mm:ss")+"\n"+"结果："+AgeResultBean.getAge());
                        Log.e("onBindViewHolder", "onBindViewHolder: "+AgeResultBean.getAge());
                        holder.mImgDel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemClickListener.onItemDeleteClick(holder.itemView,position);
                            }
                        });
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case "LanguageListBean":
                VerbalResultBean = VerbalResultList.get(position);
                try {
                    Log.e("shijiancuo", "onBindViewHolder: "+VerbalResultBean.getCreated_at());
                    holder.mTvItemTitle.setText(DateUtil.getDateToString(Long.valueOf(VerbalResultBean.getCreated_at()+"000"),"yyyy-MM-dd HH:mm:ss")+"\n"+"结果："+VerbalResultBean.getLanguage_level());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
        }

    }


    @Override
    public int getItemCount() {
        if (type.equals("AllListPureBean")){
            return PureResultList == null ? 0:PureResultList.size();
        }else if (type.equals("LanguageListBean")){
            return VerbalResultList == null ? 0:VerbalResultList.size();
        }else if (type.equals("AgeListBean")){
            return AgeResultList == null ? 0:AgeResultList.size();
        }
        return 0;
    }

    //删除纯音测试结果 user_id simple_id


    class Hodler extends RecyclerView.ViewHolder{

        TextView mTvItemTitle;
        ImageView mImgDel;
        ConstraintLayout content_layout;
        int viewType;

        Hodler(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            mTvItemTitle = itemView.findViewById(R.id.tv_result_time_remark);
            mImgDel = itemView.findViewById(R.id.img_del);
            content_layout = itemView.findViewById(R.id.content_layout);
        }
    }
}

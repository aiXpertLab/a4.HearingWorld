package seeingvoice.jskj.com.seeingvoice.history.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.beans.AgeHistoryBean;
import seeingvoice.jskj.com.seeingvoice.beans.PureHistoryBean;
import seeingvoice.jskj.com.seeingvoice.beans.VerbalHistoryBean;
import seeingvoice.jskj.com.seeingvoice.util.DateUtil;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Date:2019/6/25
 * Time:14:07
 * auther:zyy
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyHolder> {
    private List<PureHistoryBean.DataBean.AllListPureBean> PureResultList;
    private List<VerbalHistoryBean.DataBean.LanguageListBean> VerbalResultList;
    private List<AgeHistoryBean.DataBean.AgeListBean> AgeResultList;
//    private List<AgeHistoryBean.DataBean.LanguageListBean> VerbalResultList;
    private OnItemClickListener onItemClickListener;
    private String type = "";
    private PureHistoryBean.DataBean.AllListPureBean PureResultBean = null;
    private VerbalHistoryBean.DataBean.LanguageListBean VerbalResultBean = null;
    private AgeHistoryBean.DataBean.AgeListBean AgeResultBean = null;

    public RecycleViewAdapter(Activity a, List TestResultList) {
        this.type = TestResultList.get(0).getClass().getSimpleName();
        Log.e("testtest", "RecycleViewAdapter: "+type);
        switch (type){
            case "AllListPureBean":
                this.PureResultList = (List<PureHistoryBean.DataBean.AllListPureBean>)TestResultList;
                break;
            case "LanguageListBean":
                this.VerbalResultList = (List<VerbalHistoryBean.DataBean.LanguageListBean>)TestResultList;
                break;
            case "AgeListBean":
                this.AgeResultList = (List<AgeHistoryBean.DataBean.AgeListBean>)TestResultList;
                break;
        }
    }

//    public void update(List<PureHistoryBean.DataBean.AllListBean> list){
//        this.TestResultList = list;
//        notifyDataSetChanged();
//    }

    /**
     * 这个方法是用来创建ViewHolder的
     * 就是引入XML传送给viewHolder
     * */
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    /**
     * 绑定每一项
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        switch (type){
            case "AllListPureBean":
                PureResultBean = PureResultList.get(position);
                try {
                    holder.textView.setText(DateUtil.getDateToString(Long.valueOf(PureResultBean.getCreat_time()+"000"),"yyyy-MM-dd HH:mm:ss")+"\n备注："+PureResultBean.getRemark());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                //对控件进行监听
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(holder.itemView,position);
                    }
                });
                holder.textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(onItemClickListener != null) {
//                    int pos = holder.getLayoutPosition();
                            onItemClickListener.onItemLongClick(holder.itemView, position);
                        }
                        //表示此事件已经消费，不会触发单击事件
                        return true;
                    }
                });

                //对整个Item进行监听
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemLongClick(holder.itemView,position);
                    }
                });
                break;
            case "LanguageListBean":
                VerbalResultBean = VerbalResultList.get(position);
                try {
                    Log.e("shijiancuo", "onBindViewHolder: "+VerbalResultBean.getCreated_at());
                    holder.textView.setText(DateUtil.getDateToString(Long.valueOf(VerbalResultBean.getCreated_at()+"000"),"yyyy-MM-dd HH:mm:ss")+"\n"+"结果："+VerbalResultBean.getLanguage_level());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case "AgeListBean":
                AgeResultBean = AgeResultList.get(position);
                holder.textView.setText(DateUtil.getDateToString(Long.valueOf(AgeResultBean.getCreated_at()+"000"),"yyyy-MM-dd HH:mm:ss")+"\n"+"结果："+AgeResultBean.getAge());
                break;
        }
    }

    @Override
    public int getItemCount() {
//        Log.e("PureHistoryFragment", "initData: "+TestResultList.size());
        if (type.equals("AllListPureBean")){
            return PureResultList == null ? 0:PureResultList.size();
        }else if (type.equals("LanguageListBean")){
            return VerbalResultList == null ? 0:VerbalResultList.size();
        }else if (type.equals("AgeListBean")){
            return AgeResultList == null ? 0:AgeResultList.size();
        }
        return 0;
    }

    //recyclerView 添加一个数据项
    public void addNewItem() {
        if(PureResultList == null) {
            PureResultList = new ArrayList<>();
        }
//        TestResultList.add(0, "new Item");
        //更新数据集不是用adapter.notifyDataSetChanged()而是notifyItemInserted(position)与notifyItemRemoved(position) 否则没有动画效果。
        notifyItemInserted(0);
    }

    //recyclerView 删除一个数据项
    public void deleteItem(int flag) {
        if(PureResultList == null || PureResultList.isEmpty()) {
            ToastUtil.showShortToastCenter("没有找到纯音测试历史记录！");
        }else {
//            PureHistoryBean.DataBean.AllListBean mPureTestResult = TestResultList.get(flag);
//            MyApplication.getAppInstance().getmDaoSession().delete(mPureTestResult);
//            TestResultList.remove(flag);
//            notifyDataSetChanged();
        }

//        if(TestResultList == null || TestResultList.isEmpty()) {
//            return;
//        }
//        TestResultList.remove(0);
//        notifyItemRemoved(0);
    }

    // ① 定义点击回调接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    // ② 定义一个设置点击监听器的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.m_item_text);
        }
    }
}

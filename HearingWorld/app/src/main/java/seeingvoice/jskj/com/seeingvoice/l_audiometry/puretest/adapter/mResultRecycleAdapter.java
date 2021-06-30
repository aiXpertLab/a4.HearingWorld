package seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.database.entity.PureTestResult;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest.util.TimeStampUtil;

import java.util.List;

public class mResultRecycleAdapter extends RecyclerView.Adapter<mResultRecycleAdapter.MyHolder> {
    Context context;
    List<PureTestResult> list;

    public mResultRecycleAdapter(Context context, List<PureTestResult> list) {
        this.context = context;
        this.list = list;
    }

    public void update(List<PureTestResult> list){
        this.list = list;
        notifyDataSetChanged();
    }


    /**
     * 这个方法是用来创建viewholder的
     * 就是引入xml传送给viewholder的
     * */

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_history_item,parent,false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    /**
     * 这里是我们操作item的地方
     * */
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        PureTestResult testResult = list.get(position);
        holder.textView.setText(TimeStampUtil.stampToDate(testResult.getTimeStamp()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public MyHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.test_time);
        }
    }
}

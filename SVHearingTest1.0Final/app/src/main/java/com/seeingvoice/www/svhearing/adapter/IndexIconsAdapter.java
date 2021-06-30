package com.seeingvoice.www.svhearing.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.seeingvoice.www.svhearing.R;

import java.util.List;

/**
 * Date:2019/5/10
 * Time:8:45
 * auther:zyy
 * function: index 页面的功能块的GridView Adapter
 */
public class IndexIconsAdapter extends BaseAdapter {

    private Context context;
    private List imgList;

    public IndexIconsAdapter(Context context, List imgList) {
        this.context = context;
        this.imgList = imgList;
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public Object getItem(int position) {
        return imgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = View.inflate(context, R.layout.index_gridview_item,null);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageView.setImageResource((Integer) imgList.get(position));
        return convertView;
    }


    public class ViewHolder{
        ImageView imageView;
    }
}

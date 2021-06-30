package com.seeingvoice.www.svhearing.heartests.verbaltests.numKeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seeingvoice.www.svhearing.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Date:2019/2/12
 * Time:12:01
 * auther:zyy
 */
public class NumInputView extends RelativeLayout implements View.OnClickListener {

    Context context;

    private String strNums; //输入的数字
    private TextView[] tvList; //用数组保存3个TextView，为什么用数组？
    //因为就3个输入框不会变了，用数组内存申请固定空间，比List省空间（自己认为）
    private GridView gridview; //用GrideView布局键盘，其实并不是真正的键盘，只是模拟键盘的功能
    private ArrayList<Map<String,String>> valueList;//有人可能有疑问，为何这里不用数组了？

    //因为要用Adapter中适配，用数组不能往adapter中填充
//    private ImageView imgCancel;
    private TextView tvRePlay,mtvTestProgress,tvTotalNo,tvPraciceOrOfficialFinish,tvNumKeyTitle; //重新播放,第几次测试

    private int currentIndex = -1; //用于记录当前输入密码格位置

    public NumInputView(Context context) {
        this(context,null);
    }

    public NumInputView(Context context,AttributeSet attrs){
        super(context,attrs);
        this.context = context;
        View view = View.inflate(context, R.layout.activity_num_keyboard,null);  //R.layout.layout_popup_bottom
        valueList = new ArrayList<Map<String,String>>();
        tvList = new TextView[3];
//        imgCancel = view.findViewById(R.id.img_cancel);
        Log.e("test", "关闭数字键盘 ",null );
//        imgCancel.setOnClickListener(this);

        tvRePlay = view.findViewById(R.id.tv_replay);
        tvRePlay.setOnClickListener(this);
        tvTotalNo = view.findViewById(R.id.tv_total_no);
        tvNumKeyTitle = view.findViewById(R.id.tv_numkey_title);
        tvPraciceOrOfficialFinish = view.findViewById(R.id.tv_pracice_or_official_finish);
        mtvTestProgress = view.findViewById(R.id.tv_testProgress);
        mtvTestProgress.setOnClickListener(this);

        tvList[0] = view.findViewById(R.id.tv_num1);
        tvList[1] = view.findViewById(R.id.tv_num2);
        tvList[2] = view.findViewById(R.id.tv_num3);

        gridview = view.findViewById(R.id.gv_keybord);

        setView(); //设置数字键盘

        addView(view); //必须要，不然不显示控件
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.img_cancel:
//                Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.tv_replay:
                Toast.makeText(context,"replay",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_testProgress:
                Toast.makeText(context,"replay",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setView(){
        /* 初始化按钮上应该显示的数字 */
        for (int i = 1; i < 13; i++) {
            Map<String,String> map = new HashMap<String, String>();
            if (i < 10){
                map.put("name",String.valueOf(i));
            }else if (i == 10){
                map.put("name","?");
            }else if (i == 12){
                map.put("name","×");
            }else if (i == 11){
                map.put("name",String.valueOf(0));
            }
            valueList.add(map);
        }

        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 11 ) {    //点击0~9按钮
                    if (currentIndex >= -1 && currentIndex < 2) {      //判断输入位置————要小心数组越界
                        tvList[++currentIndex].setText(valueList.get(position).get("name"));
                    }
                } else {
                    if (position == 11) {      //点击退格键
                        if (currentIndex - 1 >= -1) {      //判断是否删除完毕————要小心数组越界
                            tvList[currentIndex--].setText("");
                        }
                    }
//                    if (position == 9){
//                        Toast.makeText(context,"??",Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        });
    }


    //设置监听方法，在第3位输入完成后触发
    public void setOnFinishInput(final OnNumInputFinish nums) {
        tvList[2].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                ThreeTextViewEmpty();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    strNums = "";     //每次触发都要先将strNums置空，再重新获取，避免由于输入删除再输入造成混乱
                    for (int i = 0; i < 3; i++) {
                        strNums += tvList[i].getText().toString().trim();
                    }
                    nums.inputFinish();    //接口中要实现的方法，完成密码输入完成后的响应逻辑
                }
            }
        });
    }

    //获取输入的数字

    public String getStrNums() {
        return strNums;
    }

    /* 暴露取消的按钮，可以灵活改变响应 */
//    public ImageView getCancelImageView() {
//        return imgCancel;
//    }

    /* 暴露重新播放的按钮，可以灵活改变响应 */
    public TextView getTvRePlay() {
        return tvRePlay;
    }

    public TextView gettvNumKeyTitle() {
        return tvNumKeyTitle;
    }
    public TextView gettvPraciceOrOfficialFinish() {
        return tvPraciceOrOfficialFinish;
    }
    public TextView gettvTotalNo() {
        return tvTotalNo;
    }

    /* 暴露重新播放的按钮，可以灵活改变响应 */
    @SuppressLint("SetTextI18n")
    public void setTvTestProgress(int test_no) {
        mtvTestProgress.setText(""+test_no);
    }
    /** 三个数字框置空，index恢复到最初的位置*/
    public void ThreeTextViewEmpty(){
        currentIndex = -1;
        for (int i = 2; i > -1; i--) {
            tvList[i].setText("");
        }
    }

    //GrideView的适配器
    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return valueList.size();
        }

        @Override
        public Object getItem(int position) {
            return valueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_gride, null);
                viewHolder = new ViewHolder();
                viewHolder.btnKey = convertView.findViewById(R.id.btn_keys);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.btnKey.setText(valueList.get(position).get("name"));

            if(position == 9){
//                viewHolder.btnKey.setBackgroundResource(R.drawable.selector_key_del);
//                viewHolder.btnKey.setEnabled(false);
            }
            if(position == 11){
                viewHolder.btnKey.setBackgroundResource(R.drawable.selector_key_del);
            }
            return convertView;
        }
    };

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView btnKey;
    }
}

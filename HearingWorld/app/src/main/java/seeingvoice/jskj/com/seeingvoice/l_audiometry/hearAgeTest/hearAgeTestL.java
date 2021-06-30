package seeingvoice.jskj.com.seeingvoice.l_audiometry.hearAgeTest;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.base.OnMultiClickListener;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.L_HearAgeResultExplain;
import seeingvoice.jskj.com.seeingvoice.util.AlertDialogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static seeingvoice.jskj.com.seeingvoice.MyData.HEAR_AGE_REQUEST;

/**
 * Date:2019/7/16
 * Time:14:50
 * auther:zyy
 */
public class hearAgeTestL extends MyTopBar {
    private static final String TAG = hearAgeTestL.class.getName();
    private TextView mTv_result,mTv_result1;
    private AlertDialogUtil alertDialog;
    private String messageCode;
    private String errorInfo;
    private String errorCode;
    private static Long timeStamp = System.currentTimeMillis();
    private String result;
    @Override
    protected int getContentView_sv() {
        return R.layout.activity_hear_age_test;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle("听力年龄测试");
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, null);

//        setToolBarMenuTwo("", R.mipmap.jiaocheng, new OnMenuClickListener() {
//            @Override
//            public void onMultiClick(MenuItem v) {
//                toNextActivity(null, hearAgeTestActivity.this, PureTestCourse.class);
//            }
//        });

        setToolBarMenuTwo("", R.mipmap.jiaocheng,null);

        String DB_PATH = "/data/data/seeingvoice.jskj.com.seeingvoice/databases/";
        String DB_NAME = "question.db";

        //应用启动时，判断数据库是否存在，不存在则将提前打包好的数据库文件复制到数据库目录下
        //数据库目录不存在时，创建数据库目录

        if ((new File(DB_PATH + DB_NAME).exists()) == false) {

            File dir = new File(DB_PATH);
            if (!dir.exists()) {
                dir.mkdir();
            }
        }

        //定义输入输出流，用于复制文件
        try {
            InputStream is = getBaseContext().getAssets().open(DB_NAME);
            Log.e("TAG_SERVICE",is.toString());
            OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            //刷新输出流，关闭输入输出流
            os.flush();
            os.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(hearAgeTestL.this, ExamL.class);
                startActivityForResult(intent,HEAR_AGE_REQUEST);
            }
        });

        mTv_result = findViewById(R.id.tv_result);
        mTv_result1 = findViewById(R.id.tv_result1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == HEAR_AGE_REQUEST){
                try {
                    if (null != data)
                        result = data.getStringExtra("HEAR_AGE_RESULT");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mTv_result.setText("测试结果："+result);
                mTv_result1.setVisibility(View.VISIBLE);
                mTv_result1.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        toNextActivity(null, hearAgeTestL.this, L_HearAgeResultExplain.class);
                    }
                });

                new AlertDialogUtil(hearAgeTestL.this, "温馨提示：",
                        "保存本次测试结果？", "确定", "放弃", 0x964, mDialogListener).show();
//                mTv_result1.setMovementMethod(ScrollingMovementMethod.getInstance());
            }
        }
    }

    private AlertDialogUtil.OnDialogButtonClickListener mDialogListener =  new AlertDialogUtil.OnDialogButtonClickListener() {
        @Override
        public void onDialogButtonClick(int requestCode, boolean isPositive) {
            switch (requestCode){
                case 0x964:
                    if (isPositive) {
                        upLoadResult(result);
                    }
                    break;
            }
        }
    };

    private void upLoadResult(String result) {
    }
}

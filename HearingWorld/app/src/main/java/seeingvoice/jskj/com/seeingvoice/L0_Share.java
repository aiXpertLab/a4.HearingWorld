package seeingvoice.jskj.com.seeingvoice;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class L0_Share extends AppCompatActivity {

    public void allShare(){
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("text/plain");//设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.topbar_share));  //添加分享内容标题
        share_intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.topbar_share_content)+"你说我看，见声看见");//添加分享内容
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, getString(R.string.topbar_share));
        startActivity(share_intent);
    }
}

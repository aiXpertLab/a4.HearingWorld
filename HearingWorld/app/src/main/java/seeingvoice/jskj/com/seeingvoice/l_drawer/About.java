package seeingvoice.jskj.com.seeingvoice.l_drawer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.MyBaseActivity;
import seeingvoice.jskj.com.seeingvoice.MyWeb;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.BuildConfig;

/**
 * @author  LeoReny@hypech.com
 * @version 1.0
 * @since   2021-04-07
 */
public class About extends MyBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_about);

        ((TextView)findViewById(R.id.text_view_version)).setText(getString(R.string.version, BuildConfig.VERSION_NAME));

        ((TextView)findViewById(R.id.text_view_user_agreement)).setOnClickListener(this);
        ((TextView)findViewById(R.id.text_view_privacy_policy)).setOnClickListener(this);
        findViewById(R.id.relative_layout_contact).setOnClickListener(this);
        findViewById(R.id.relative_layout_intro).setOnClickListener(this);
        findViewById(R.id.relative_layout_complain).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()== R.id.text_view_user_agreement){
            Intent intent = new Intent(this, MyWeb.class);
            intent.putExtra("from", "agreement");
            startActivity(intent);
        }else if(view.getId()== R.id.text_view_privacy_policy){
            Intent intent = new Intent(this, MyWeb.class);
            intent.putExtra("from", "privacy");
            startActivity(intent);
        }else if(view.getId()== R.id.relative_layout_contact){
            Intent intent = new Intent(this, SVQRCode.class);
            intent.putExtra("from", "web");
            startActivity(intent);
        }else if(view.getId()== R.id.relative_layout_intro){
            Intent intent = new Intent(this, Intro.class);
            intent.putExtra("from", "intro");
            startActivity(intent);
        }else if(view.getId()== R.id.relative_layout_complain){
            Intent intent = new Intent(this, SVQRCode.class);
            intent.putExtra("from", "complain");
            startActivity(intent);
        }
    }

    public void back(View view) {        finish();    }
}
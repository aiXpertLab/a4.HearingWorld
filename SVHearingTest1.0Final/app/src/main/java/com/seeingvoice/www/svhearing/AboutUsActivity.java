package com.seeingvoice.www.svhearing;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;

/**
 * Date:2019/7/16
 * Time:15:03
 * auther:zyy
 */
public class AboutUsActivity extends TopBarBaseActivity {

    private WebView mWV_disclaimer;
    private ProgressBar mCircleProgress;

    @Override
    protected int getConentView() {
        return R.layout.activity_disclaimer_statement;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        setTitle("关于我们");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, null);

        setToolBarMenuTwo("", R.mipmap.return_icon,null);

        mWV_disclaimer = findViewById(R.id.WV_pure_course);
        mCircleProgress = findViewById(R.id.progress_Bar);
        WebSettings mysettings = mWV_disclaimer.getSettings();
        mysettings.setSupportZoom(true);
        mysettings.setBuiltInZoomControls(true);
        mWV_disclaimer.loadUrl(AppConstant.URL_ABOUT_US);
        mWV_disclaimer.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWV_disclaimer.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mCircleProgress.setVisibility(View.GONE);
                } else {
                    mCircleProgress.setVisibility(View.VISIBLE);
                    mCircleProgress.setProgress(newProgress);
                }
            }
        });
    }
}

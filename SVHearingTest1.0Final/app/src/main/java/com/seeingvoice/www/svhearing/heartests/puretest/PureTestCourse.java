package com.seeingvoice.www.svhearing.heartests.puretest;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.OnMenuClickListener;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.share.ShareUtil;

import static com.seeingvoice.www.svhearing.AppConstant.URL_PURE_COURSE;

public class PureTestCourse extends TopBarBaseActivity {

    private WebView mWV_pure_course;
    private ProgressBar mCircleProgress;

    @Override
    protected int getConentView() {
        return R.layout.activity_disclaimer_statement;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("纯音测试教程");
        setTitleBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, new OnMenuClickListener() {
            @Override
            public void onMultiClick(MenuItem v) {
                ShareUtil.getInstance().shareFunction(PureTestCourse.this);
            }
        });

        setToolBarMenuTwo("", R.mipmap.return_icon, null);
        initUI();
    }

    private void initUI() {
        mWV_pure_course = findViewById(R.id.WV_pure_course);
        mCircleProgress = findViewById(R.id.progress_Bar);
        WebSettings mysettings = mWV_pure_course.getSettings();
        mysettings.setSupportZoom(true);
        mysettings.setBuiltInZoomControls(true);
        mWV_pure_course.loadUrl(URL_PURE_COURSE);
        mWV_pure_course.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWV_pure_course.setWebChromeClient(new WebChromeClient(){
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

package seeingvoice.jskj.com.seeingvoice.l_drawer;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import seeingvoice.jskj.com.seeingvoice.MyData;
import seeingvoice.jskj.com.seeingvoice.R;
import seeingvoice.jskj.com.seeingvoice.MyTopBar;
/**
 * Date:2021/1/23
 * auther:Leo Reny@hypech.com
 */
@SuppressWarnings("FieldCanBeLocal")

public class L_Privacy extends MyTopBar {

    private WebView mWV_disclaimer;
    private ProgressBar mCircleProgress;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_privacy_policy;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolbarTitle(getString(R.string.privacy_policy));
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.return_icon, null);
        setToolBarMenuTwo("", R.mipmap.return_icon, null);
        initUI();
    }

    private void initUI() {
        mWV_disclaimer = findViewById(R.id.WV_pure_course);
        mCircleProgress = findViewById(R.id.progress_Bar);
        WebSettings mysettings = mWV_disclaimer.getSettings();
        mysettings.setSupportZoom(true);
        mysettings.setBuiltInZoomControls(true);
        mWV_disclaimer.loadUrl(MyData.URL_PRIVACY);//URL需要改成隐私政策的
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
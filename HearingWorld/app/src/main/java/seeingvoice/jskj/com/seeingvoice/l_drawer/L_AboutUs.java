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
 * auther: LeoReny@hypech.com
 */
@SuppressWarnings("FieldCanBeLocal")
public class L_AboutUs extends MyTopBar {

    private WebView mWV_disclaimer;
    private ProgressBar mCircleProgress;

    @Override
    protected int getContentView_sv() {
        return R.layout.activity_disclaimer_statement;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        setToolbarTitle(getString(R.string.about_us));
        setToolbarBack(true);

        setToolBarMenuOne("", R.mipmap.share_icon, null);
        setToolBarMenuTwo("", R.mipmap.return_icon,null);

        mWV_disclaimer = findViewById(R.id.WV_pure_course);
        mCircleProgress = findViewById(R.id.progress_Bar);
        WebSettings mysettings = mWV_disclaimer.getSettings();
        mysettings.setSupportZoom(true);
        mysettings.setBuiltInZoomControls(true);
        mWV_disclaimer.loadUrl(MyData.URL_ABOUT_US);
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

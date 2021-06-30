package seeingvoice.jskj.com.seeingvoice;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Date:2021/1/23
 * auther:Leo Reny@hypech.com
 */

public class MyWeb extends MyBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_webview);

        TextView tvTitle = findViewById(R.id.text_view_title);
        WebView  wv      = findViewById(R.id.WV_pure_course);
        ProgressBar bar  = findViewById(R.id.progress_Bar);

        wv.getSettings().setSupportZoom(true);
        wv.getSettings().setBuiltInZoomControls(true);

        String pFrom = getIntent().getStringExtra("from");

        if (pFrom.equals("privacy")) {
            wv.loadUrl(MyData.URL_PRIVACY);
            tvTitle.setText(getString(R.string.privacy_policy));
        }else if (pFrom.equals("agreement")) {
            wv.loadUrl(MyData.URL_USER_AGREEMENT);
            tvTitle.setText(getString(R.string.user_agreement));
        }

        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wv.canGoBack();
        wv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.GONE);
                } else {
                    bar.setVisibility(View.VISIBLE);
                    bar.setProgress(newProgress);
                }
            }
        });
    }

    public void back(View view) {
        finish();
    }
}
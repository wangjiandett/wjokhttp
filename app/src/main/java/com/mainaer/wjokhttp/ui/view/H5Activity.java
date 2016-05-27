package com.mainaer.wjokhttp.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mainaer.wjokhttp.R;
import com.mainaer.wjokhttp.model.LoadResponse;


/**
 * @date 2015年8月19日
 */
public class H5Activity extends AppCompatActivity {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_TITLE = "title";

    private ProgressBar mPb;
    private WebView mWebView;

    private String mUrl = "";

    LoadResponse response;

    private static final int PB_FAKE_MAX = 85;

    private Handler mPbHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mPb.getProgress() < PB_FAKE_MAX) {
                mPb.setProgress(mPb.getProgress() + 1);
                mPbHandler.sendEmptyMessageDelayed(0, 100);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5);
        mPb = (ProgressBar) findViewById(R.id.pb);
        mWebView = (WebView) findViewById(R.id.wv_h5);
        Bundle bundle = getIntent().getExtras();
        response = (LoadResponse) bundle.getSerializable(EXTRA_URL);
        if (response != null) {
            setTitle(response.title);
            mUrl = response.article_url;
        }

        initData();
    }

    protected void initData() {

        // 能使用JavaScript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mWebView.setWebViewClient(new TencentWebViewClient());

        // 设置setWebChromeClient对象
        mWebView.setWebChromeClient(new TencentWebChromeClient());

        // 优先使用缓存
        // 不是用缓存（webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);）
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        mWebView.getSettings().setDomStorageEnabled(true);

        // WebView加载web资源
        mWebView.loadUrl(mUrl);
    }


    protected boolean shouldOverrideUrlLoading(WebView webView, String url) {
        webView.loadUrl(url);
        return false;
    }

    private class TencentWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView webView, String s) {
            super.onReceivedTitle(webView, s);
        }

        @Override
        public void onProgressChanged(WebView webView, int i) {
            Log.i("H5Activity", "progress: " + i);
            mPb.setProgress(i);
            if (i >= 100) {
                mPb.setVisibility(View.GONE);
            }
            else {
                mPb.setVisibility(View.VISIBLE);
            }
            super.onProgressChanged(webView, i);
        }
    }

    private class TencentWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String s) {
            return H5Activity.this.shouldOverrideUrlLoading(webView, s);
        }

        @Override
        public void onReceivedError(WebView webView, int i, String s, String s1) {
            Log.i("H5Activity", "onReceiveError: " + s);
            super.onReceivedError(webView, i, s, s1);
        }
    }

    public static void go(Context context, LoadResponse response) {
        Intent intent = new Intent(context, H5Activity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_URL, response);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


}

package com.dhy.coffeesecret.ui.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.Global;
import com.dhy.coffeesecret.utils.SPPrivateUtils;

/**
 * Created by mxf on 2017/3/27.
 */
public class GuidanceActivity extends AppCompatActivity{
    private static final String TAG = "GuidanceActivity";

    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guidance);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.loadUrl("file:///android_asset/guidance.html");
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if ("http://start/".equals(url)) {
                    Intent intent = new Intent(GuidanceActivity.this, FirstConnectedActivity.class);
                    GuidanceActivity.this.startActivity(intent);
                    GuidanceActivity.this.finish();
                }
                return true;
            }
        });
        settings.setUseWideViewPort(true);

    }

    public void onClick(View view) {
        Log.i(TAG, "skip");
        SPPrivateUtils.put(this, Global.IS_FIRST_TIME, false);
        Intent intent = new Intent(this, FirstConnectedActivity.class);
        startActivity(intent);
        this.finish();
    }
}


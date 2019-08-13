package com.sp.beactive.Homepage;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.sp.beactive.R;

public class More_Info extends AppCompatActivity {



    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moreinfo_main);
        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= 24) {
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    Log.i("debug", "The url has changed");
                    view.loadUrl(request.getUrl().toString());
                    return super.shouldOverrideUrlLoading(view, request);
                }
            });
            webView.loadUrl("https://www.pa.gov.sg/our-programmes/active-ageing");
        }
        else {
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return super.shouldOverrideUrlLoading(view, url);
                }
            });
            webView.loadUrl("https://www.pa.gov.sg/our-programmes/active-ageing");
        }
    }

    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
            Intent intent = new Intent(this,Home.class);
            startActivity(intent);
        }
    }
}

package com.example.exam6;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;


public class NewsDetailActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    public static final String EXTRA_NEWS_URL = "news_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // 获取传递过来的URL
        String newsUrl = getIntent().getStringExtra(EXTRA_NEWS_URL);
        // 处理URL，确保有完整的协议前缀
        String fullUrl = getFullUrl(newsUrl);

        // 添加请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        headers.put("Referer", "https://news.sina.com.cn");


        // 初始化WebView
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);  // 添加这行

        setupWebView();

        // 加载URL
        if (newsUrl != null && !newsUrl.isEmpty()) {
            webView.loadUrl(fullUrl);
        }

        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();

        // 1. 设置移动端User-Agent
        String userAgent = "Mozilla/5.0 (Linux; Android 11; Pixel 4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36";
        webSettings.setUserAgentString(userAgent);

        // 2. 基本设置
        webSettings.setJavaScriptEnabled(true);  // 启用JS
        webSettings.setDomStorageEnabled(true);  // 启用DOM storage
        webSettings.setDatabaseEnabled(true);    // 启用数据库

        // 3. 缓存设置
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  // 默认缓存模式

        // 4. 页面自适应设置
        webSettings.setUseWideViewPort(true);    // 将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);  // 缩放至屏幕的大小

        // 5. 缩放设置
        webSettings.setSupportZoom(true);        // 支持缩放
        webSettings.setBuiltInZoomControls(true);  // 设置内置的缩放控件
        webSettings.setDisplayZoomControls(false);  // 隐藏原生的缩放控件

        // 6. 文本编码
        webSettings.setDefaultTextEncodingName("UTF-8");

        // 7. 混合内容（HTTP + HTTPS）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        // 8. 设置WebViewClient
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // 可以在这里处理错误情况
                Toast.makeText(NewsDetailActivity.this, "加载失败: " + description, Toast.LENGTH_SHORT).show();
            }
        });

        // 9. 设置WebChromeClient
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressBar != null) {
                    progressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(title);
                }
            }
        });

        // 10. 添加通用请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Requested-With", "com.example.exam6");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5");

        // 加载URL
        String url = getIntent().getStringExtra(EXTRA_NEWS_URL);
        if (url != null && !url.isEmpty()) {
            String fullUrl = getFullUrl(url);
            webView.loadUrl(fullUrl, headers);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 处理返回键
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // 清理WebView
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    private String getFullUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        // 移除可能存在的 "file:///" 前缀
        url = url.replace("file:///", "");

        // 如果URL以//开头，添加https:
        if (url.startsWith("//")) {
            return "https:" + url;
        }
        // 如果URL既不是以http也不是以https开头，添加https://
        else if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "https://" + url;
        }

        return url;
    }



}
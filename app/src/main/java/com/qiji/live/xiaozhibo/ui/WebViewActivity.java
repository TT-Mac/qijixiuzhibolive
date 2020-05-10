package com.qiji.live.xiaozhibo.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.qiji.live.xiaozhibo.R;
import com.qiji.live.xiaozhibo.base.TCBaseActivity;
import com.qiji.live.xiaozhibo.inter.UIInterface;
import com.qiji.live.xiaozhibo.ui.customviews.TCActivityTitle;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebViewActivity extends TCBaseActivity implements UIInterface {


    @Bind(R.id.wv_webview)
    WebView mWebView;

    @Bind(R.id.tct_title)
    TCActivityTitle mTCActivityTitle;

    @Bind(R.id.pb_webview)
    ProgressBar mProgressBar;

    private String url;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;
    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    public void initData() {
        Intent _Extras = getIntent();
        url = _Extras.getStringExtra("url");
        mWebView.loadUrl(url);
    }

    @Override
    public void initView() {


        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //webSettings.setBuiltInZoomControls(true);
        WebChromeClient chromeClient = new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTCActivityTitle.setTitle(title);
            }

            //扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
            }

            // For Android > 5.0
            public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {

                openFileChooserImplForAndroid5(uploadMsg);
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                super.onProgressChanged(view, newProgress);
            }
        };


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mTCActivityTitle.setTitle(view.getTitle());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        mWebView.clearHistory();
                        finish();
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return false;
                }

                if (url.startsWith("close:")) {
                    finish();
                    return false;
                }

                if (url.startsWith("login:")) {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        mWebView.clearHistory();
                        finish();
                    }
                    return false;
                }
                view.loadUrl(url);
                return false;
            }
        });
        mWebView.setWebChromeClient(chromeClient);


        mTCActivityTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(null != mWebView){
                    if(mWebView.canGoBack()){
                        mWebView.goBack();
                    }else {
                        finish();
                    }

                    if(mWebView.getTitle().equals("提交成功")){
                        finish();
                        return;
                    }
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK  && mWebView.getTitle().equals("提交成功")){
            finish();
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 16908332){

            if(mWebView.getTitle().equals("提交成功")){
                finish();
                return true;
            }

            if(mWebView.canGoBack()){
                mWebView.goBack();
                return true;

            }else {
                finish();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null: intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5){
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null: intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }
}

package com.svrpublicschool.fragments.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.svrpublicschool.BaseActivity;
import com.svrpublicschool.R;
import com.svrpublicschool.customview.TouchImageView;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ZoomViewActivity extends BaseActivity {

    String url;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_view);
        url = getIntent().getExtras().getString("URL");
        webView = findViewById(R.id.webView);
        settingWebview();
    }
    private void settingWebview() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportMultipleWindows(false);

        settings.setLoadsImagesAutomatically(true);
        settings.setLightTouchEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);

        Log.d("URL", url);
        //webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
        //webView.loadUrl("file:///android_asset/VechicleInsurance.html");
        webView.loadUrl(url);
    }


}

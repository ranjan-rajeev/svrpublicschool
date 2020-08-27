package com.svrpublicschool.ui.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.svrpublicschool.BaseActivity;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.FileUtility;
import com.svrpublicschool.Util.GenericImageLoader;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.customview.TouchImageView;
import com.svrpublicschool.models.ChatEntity;

import java.io.File;

public class ZoomActivity extends BaseActivity {

    PhotoView photo_view;
    Bitmap bitmap;
    ChatEntity chatEntity;
    String title, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
        setContentView(R.layout.activity_zoom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        photo_view = findViewById(R.id.photo_view);
        getIntentData();
        if (title != null) {
            getSupportActionBar().setTitle(title);
        }
        if (chatEntity != null) {
            GenericImageLoader.loadImageFromFile(this, photo_view, FileUtility.getImageDirectoryName() + chatEntity.getFileName(), R.drawable.logo);
        }
        if (url != null) {
            GenericImageLoader.loadImage(this, photo_view, url, 0);
        }
    }

    public void getIntentData() {
        try {
            title = getIntent().getStringExtra(Constants.INTENT_PARAM_TITLE);
            url = getIntent().getStringExtra(Constants.INTENT_PARAM_URL);
            chatEntity = (ChatEntity) getIntent().getSerializableExtra(Constants.INTENT_PARAM_CHAT);
        } catch (Exception e) {

        }
    }

}

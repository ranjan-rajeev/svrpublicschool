package com.svrpublicschool.fragments.gallery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.svrpublicschool.R;
import com.svrpublicschool.customview.TouchImageView;

public class ZoomActivity extends AppCompatActivity {

    TouchImageView ivZoom;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        url = getIntent().getExtras().getString("URL");
        url = "http://www.svrpublicschool.com/images/building.jpg";
        ivZoom = findViewById(R.id.ivZoom);
        Glide.with(this)
                .load(url)
                //.placeholder(R.drawable.circle_placeholder)
                //.transform(new CircleTransform(HomeActivity.this)) // applying the image transformer
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                //.override(200, 200)
                .into(ivZoom);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}

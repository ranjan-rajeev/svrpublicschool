package com.svrpublicschool.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.svrpublicschool.R;
import com.svrpublicschool.SVRApplication;

import java.io.File;


public class GenericImageLoader {

    public static void loadImage(Context context, final ImageView imageView, String url,
                                 int ic_placeholder) {
        if (!TextUtils.isEmpty(url) && context != null) {
            try {
                Glide.with(context)
                        .load(url)
                        .error(R.drawable.bg_blur)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform()
                        .into(imageView);
            } catch (Exception e) {

            }
        }
    }

    public static void loadImageFromFile(Context context, final ImageView imageView, String path,
                                         int ic_placeholder) {
        File file = new File(path);
        if (!TextUtils.isEmpty(path) && context != null) {
            try {
                Glide.with(context)
                        .load(file)
                        .error(R.drawable.bg_blur)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform()
                        .into(imageView);
            } catch (Exception e) {

            }
        }
    }

    public static void loadImageAsBitmap(Context context, String url, CustomTarget<Bitmap> customTarget) {
        if (!TextUtils.isEmpty(url) && context != null) {
            try {
                Glide.with(context).asBitmap().load(url).skipMemoryCache(true).into(customTarget);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

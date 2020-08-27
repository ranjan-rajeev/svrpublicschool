package com.svrpublicschool.ui.chat.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.DateUtility;
import com.svrpublicschool.Util.FileUtility;
import com.svrpublicschool.Util.GenericImageLoader;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.anim.ProgressBarAnimation;
import com.svrpublicschool.models.ChatEntity;
import com.svrpublicschool.ui.gallery.ZoomActivity;

import java.io.File;

public class ImageReceivedViewHolder extends RecyclerView.ViewHolder {
    Context context;
    ChatEntity chatEntity;
    RelativeLayout rlParent;
    TextView tvTime, tvRetry, tvSender;
    AppCompatImageView ivPreview, ivCancel;
    ProgressBar progressBar;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    boolean fileExhist = false;
    String filePath = "";

    public ImageReceivedViewHolder(View itemView) {
        super(itemView);
        this.ivPreview = itemView.findViewById(R.id.ivPreview);
        this.rlParent = itemView.findViewById(R.id.rlParent);
        this.tvTime = itemView.findViewById(R.id.tvTime);
        this.tvRetry = itemView.findViewById(R.id.tvRetry);
        this.ivCancel = itemView.findViewById(R.id.ivCancel);
        this.progressBar = itemView.findViewById(R.id.progressBar);
        this.tvSender = itemView.findViewById(R.id.tvSender);
    }

    public void setData(Context context, ChatEntity chatEntity, int position) {
        this.chatEntity = chatEntity;
        this.context = context;
        tvTime.setText("" + DateUtility.getFormattedTime(chatEntity.getCreatedAt()));
        tvSender.setText(chatEntity.getSenName());
        filePath = FileUtility.getImageDirectoryName() + chatEntity.getFileName();
        new CheckFileExhist().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        rlParent.setOnClickListener(v -> {
            if (fileExhist) {
                ActivityOptionsCompat activityOptionsCompat;
                activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ivPreview, "imageMain");
                Intent intent = new Intent(context, ZoomActivity.class);
                intent.putExtra(Constants.INTENT_PARAM_CHAT, chatEntity);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    context.startActivity(intent, activityOptionsCompat.toBundle());
                } else {
                    context.startActivity(intent);
                }
            }
        });
        tvRetry.setOnClickListener(v -> {
            if (chatEntity.getUrl() != null && Utility.isNetworkConnected(context)) {
                setDownloadState(Constants.VIEW_STATE_PROGRESS);
                downloadFileFromServer(chatEntity.getUrl());
            } else {
                Toast.makeText(context, "Check internet connection!!", Toast.LENGTH_SHORT).show();
                setDownloadState(Constants.VIEW_STATE_FAILED);
            }
        });
    }

    public void downloadFileFromServer(String url) {
        Logger.d("Fetching image from server");
        StorageReference gsReference = storage.getReferenceFromUrl(url);
        File root = new File(filePath);
        gsReference.getFile(root).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Logger.d("Image downloaded from server");
                new CheckFileExhist().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setDownloadState(Constants.VIEW_STATE_FAILED);
                Logger.d("Image failed to download from server" + e.getMessage());
            }
        });
    }

    public void setDownloadState(int status) {
        switch (status) {
            case Constants.VIEW_STATE_PROGRESS:
                animateProgressBar();
                tvRetry.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                ivCancel.setVisibility(View.VISIBLE);
                break;

            case Constants.VIEW_STATE_FAILED:
                tvRetry.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                ivCancel.setVisibility(View.GONE);
                break;
            case Constants.VIEW_STATE_SUCCESS:
                tvRetry.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                ivCancel.setVisibility(View.GONE);
                break;
        }
    }

    class CheckFileExhist extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            File file = new File(filePath);
            if (file.exists()) {
                Logger.d("File Exhists in local");
                fileExhist = true;
            } else {
                fileExhist = false;
                Logger.d("File Not Exhists in local");
            }
            return  fileExhist;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                setDownloadState(Constants.VIEW_STATE_SUCCESS);
                GenericImageLoader.loadImageFromFile(context, ivPreview, filePath, R.drawable.logo);
            } else {
                setDownloadState(Constants.VIEW_STATE_FAILED);
                GenericImageLoader.loadImageFromFile(context, ivPreview, filePath, R.drawable.logo);
            }
        }
    }

    public void animateProgressBar() {
        ProgressBarAnimation animation = new ProgressBarAnimation(progressBar, 0, 100);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(10000);
        progressBar.startAnimation(animation);
    }
}
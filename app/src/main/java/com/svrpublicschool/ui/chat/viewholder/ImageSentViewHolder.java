package com.svrpublicschool.ui.chat.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.DateUtility;
import com.svrpublicschool.Util.FileUtility;
import com.svrpublicschool.Util.GenericImageLoader;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.anim.ProgressBarAnimation;
import com.svrpublicschool.models.ChatEntity;
import com.svrpublicschool.ui.chat.ChatActivity;
import com.svrpublicschool.ui.chat.GroupChatActivity;
import com.svrpublicschool.ui.gallery.ZoomActivity;

import java.io.File;

public class ImageSentViewHolder extends RecyclerView.ViewHolder {
    Context context;
    ChatEntity chatEntity;
    RelativeLayout rlParent;
    TextView tvTime, tvRetry, tvSender;
    AppCompatImageView ivPreview, ivCancel, ivDoubleTick;
    ProgressBar progressBar;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    UploadTask imageUploadTask;
    boolean fileExhist = false;
    String filePath = "";
    String fileSize = "";

    public ImageSentViewHolder(View itemView) {
        super(itemView);
        this.ivPreview = itemView.findViewById(R.id.ivPreview);
        this.rlParent = itemView.findViewById(R.id.rlParent);
        this.tvTime = itemView.findViewById(R.id.tvTime);
        this.tvRetry = itemView.findViewById(R.id.tvRetry);
        this.ivCancel = itemView.findViewById(R.id.ivCancel);
        this.progressBar = itemView.findViewById(R.id.progressBar);
        this.ivDoubleTick = itemView.findViewById(R.id.ivDoubleTick);
        this.tvSender = itemView.findViewById(R.id.tvSender);
    }

    public void setData(Context context, ChatEntity chatEntity, int position) {
        this.chatEntity = chatEntity;
        this.context = context;
        tvSender.setText(chatEntity.getSenName());
        tvTime.setText("" + DateUtility.getFormattedTime(chatEntity.getCreatedAt()));

        filePath = FileUtility.getImageDirectoryName() + chatEntity.getFileName();

        new UpdateFileSize().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        GenericImageLoader.loadImageFromFile(context, ivPreview, filePath, 0);

        if (chatEntity.getUrl() == null) {
            if (Utility.isNetworkConnected(context)) {
                Logger.d("Uploading image to server");
                setUploadState(Constants.VIEW_STATE_PROGRESS);
                new UploadImageToServer().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            } else {
                setUploadState(Constants.VIEW_STATE_FAILED);
            }

        } else {
            setUploadState(Constants.VIEW_STATE_SUCCESS);
        }
        ivCancel.setOnClickListener(v -> {
            if (imageUploadTask != null) {
                imageUploadTask.pause();
                setUploadState(Constants.VIEW_STATE_FAILED);
            }
        });
        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkConnected(context)) {
                    setUploadState(Constants.VIEW_STATE_PROGRESS);
                    if (imageUploadTask != null) {
                        Logger.d("Resuming Upload image to server");
                        imageUploadTask.resume();
                    } else {
                        Logger.d("Uploading image to server");
                        new UploadImageToServer().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    }
                } else {
                    setUploadState(Constants.VIEW_STATE_FAILED);
                    Toast.makeText(context, "Check internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
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
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (context instanceof GroupChatActivity) {
                    ((GroupChatActivity) context).openDeletePopUp(chatEntity, position);
                }
                return false;
            }
        });
    }

    class UpdateFileSize extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            File file = new File(filePath);
            if (file.exists()) {
                Logger.d("File Exhists in local");
                fileExhist = true;
                if (chatEntity.getUrl() == null)
                    fileSize = FileUtility.getFileSize(context, file);
            } else {
                fileExhist = false;
                Logger.d("File Not Exhists in local");
            }
            return null;
        }
    }

    class UploadImageToServer extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            storageRef = storage.getReference().child("image/" + chatEntity.getFileName());
            Logger.d("Upload URL : " + storageRef.toString());
            // Create the file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .setCustomMetadata("sender", chatEntity.getSender())
                    .build();
            File file = new File(filePath);
            if (file.exists()) {
                imageUploadTask = storageRef.putFile(Uri.fromFile(file), metadata);
                setUploadListener();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void setUploadListener() {
        imageUploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progressPer = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Logger.d("Upload is " + progressPer + "% done");
                progressBar.setProgress((int) progressPer);

            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Logger.d("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Logger.d("" + exception.getMessage());
                setUploadState(Constants.VIEW_STATE_FAILED);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Logger.d("Upload Finished");
                setUploadState(Constants.VIEW_STATE_SUCCESS);
                chatEntity.updateImageDetails(storageRef.toString(), fileSize);
                Logger.d("Updated chat  :" + chatEntity.toString());
                if (context instanceof ChatActivity) {
                    ((ChatActivity) context).updateChatEntitytoFirebAse(chatEntity);
                } else {
                    ((GroupChatActivity) context).pushChatToFireBase(chatEntity);
                }
            }
        });

    }

    public void setUploadState(int status) {
        switch (status) {
            case Constants.VIEW_STATE_PROGRESS:
                animateProgressBar();
                tvRetry.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                ivCancel.setVisibility(View.VISIBLE);
                ivDoubleTick.setImageResource(R.drawable.ic_not_sent);
                break;

            case Constants.VIEW_STATE_FAILED:
                tvRetry.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                ivCancel.setVisibility(View.GONE);
                ivDoubleTick.setImageResource(R.drawable.ic_not_sent);
                break;
            case Constants.VIEW_STATE_SUCCESS:
                tvRetry.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                ivCancel.setVisibility(View.GONE);
                ivDoubleTick.setImageResource(R.drawable.ic_read);
                break;
        }
    }

    public void animateProgressBar() {
        ProgressBarAnimation animation = new ProgressBarAnimation(progressBar, 0, 100);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(500);
        progressBar.startAnimation(animation);
    }
}
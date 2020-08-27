package com.svrpublicschool.ui.chat.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;
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

import java.io.File;


public class PdfSentViewHolder extends RecyclerView.ViewHolder {

    RelativeLayout rlParent;
    TextView tvSender, tvMsg, tvTime, tvPdfName, tvPdfPages, tvPdfSize;
    AppCompatImageView ivPreview, ivDoubleTick;
    RelativeLayout rlPdfDetails;
    Context context;
    ChatEntity chatEntity;

    TextView tvRetry, tvProgressPercent;
    AppCompatImageView ivCancel;
    ProgressBar progressBar, progress_dummy;
    UploadTask fileUploadTask;

    private int pageIndex = 0;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    boolean fileExhist = false;
    String filePath = "";
    String fileSize = "";


    public PdfSentViewHolder(View itemView) {
        super(itemView);
        this.ivPreview = itemView.findViewById(R.id.ivPreview);
        this.rlParent = itemView.findViewById(R.id.rlParent);
        this.tvTime = itemView.findViewById(R.id.tvTime);
        this.tvPdfName = itemView.findViewById(R.id.tvPdfName);
        this.tvPdfPages = itemView.findViewById(R.id.tvPdfPages);
        this.tvPdfPages = itemView.findViewById(R.id.tvPdfPages);
        this.tvPdfSize = itemView.findViewById(R.id.tvPdfSize);

        tvRetry = itemView.findViewById(R.id.tvRetry);
        ivCancel = itemView.findViewById(R.id.ivCancel);
        progressBar = itemView.findViewById(R.id.progressBar);
        progress_dummy = itemView.findViewById(R.id.progress_dummy);
        tvProgressPercent = itemView.findViewById(R.id.tvProgressPercent);
        ivDoubleTick = itemView.findViewById(R.id.ivDoubleTick);
        rlPdfDetails = itemView.findViewById(R.id.rlPdfDetails);
        tvSender = itemView.findViewById(R.id.tvSender);
    }

    public void setData(Context context, ChatEntity chatEntity, int position) {
        this.context = context;
        this.chatEntity = chatEntity;
        tvTime.setText("" + DateUtility.getFormattedTime(chatEntity.getCreatedAt()));
        tvPdfName.setText(getFileName());
        tvSender.setText(chatEntity.getSenName());
        filePath = FileUtility.getPdfDirectoryName() + chatEntity.getFileName();

        if (chatEntity.getUrl() == null) {
            if (Utility.isNetworkConnected(context)) {
                Logger.d("Uploading pdf to server");
                setUploadState(Constants.VIEW_STATE_PROGRESS);
                new UploadPdfToServer().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            } else {
                setUploadState(Constants.VIEW_STATE_FAILED);
            }
        } else {
            setUploadState(Constants.VIEW_STATE_SUCCESS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            new OpenPdfPreview().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }

        rlParent.setOnClickListener(v -> {
            if (fileExhist) {
                openPdfViewerApp();
            } else {
                Toast.makeText(context, "Download the file ", Toast.LENGTH_SHORT).show();
            }
        });

        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkConnected(context)) {
                    setUploadState(Constants.VIEW_STATE_PROGRESS);
                    if (fileUploadTask != null) {
                        Logger.d("Resuming Upload image to server");
                        fileUploadTask.resume();
                    } else {
                        Logger.d("Uploading image to server");
                        new UploadPdfToServer().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    }
                } else {
                    setUploadState(Constants.VIEW_STATE_FAILED);
                    Toast.makeText(context, "Check internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ivCancel.setOnClickListener(v -> {
            if (fileUploadTask != null) {
                fileUploadTask.pause();
                setUploadState(Constants.VIEW_STATE_FAILED);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (context instanceof GroupChatActivity) {
                    ((GroupChatActivity) context).openDeletePopUp(chatEntity,position);
                }
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateUi() {
        int pageCount = pdfRenderer.getPageCount();
        tvPdfPages.setText("" + pageCount + " Page");
        tvPdfName.setText(getFileName());
        tvPdfSize.setText(fileSize);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getPageCount() {
        return pdfRenderer.getPageCount();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class OpenPdfPreview extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    fileSize = FileUtility.getFileSize(context, file);
                    fileExhist = true;
                    /*InputStream asset = context.getAssets().open(chatEntity.getFileName());
                    FileOutputStream output = new FileOutputStream(file);
                    final byte[] buffer = new byte[1024];
                    int size;
                    while ((size = asset.read(buffer)) != -1) {
                        output.write(buffer, 0, size);
                    }
                    asset.close();
                    output.close();*/
                    parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                    if (parcelFileDescriptor != null) {
                        pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                    }

                    if (pdfRenderer.getPageCount() <= 0) {
                        return null;
                    }
                    if (null != currentPage) {
                        currentPage.close();
                    }
                    currentPage = pdfRenderer.openPage(0);
                    Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                            Bitmap.Config.ARGB_8888);
                    currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    return bitmap;
                } else {
                    fileExhist = false;
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                ivPreview.setImageBitmap(bitmap);
                updateUi();
            } else {
                GenericImageLoader.loadImage(context, ivPreview, "", 0);
            }
        }
    }

    public void openPdfViewerApp() {
        String mFilePath = FileUtility.getPdfDirectoryName() + chatEntity.getFileName();
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File file = new File(mFilePath);
            Uri uri = FileProvider.getUriForFile(context, "com.svrpublicschool" + ".provider", file);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(mFilePath), "application/pdf");
            intent = Intent.createChooser(intent, "Open File");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    class UploadPdfToServer extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            storageRef = storage.getReference().child("pdf/" + chatEntity.getFileName());
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("application/pdf")
                    .setCustomMetadata("sender", chatEntity.getSender())
                    .build();
            File file = new File(filePath);
            fileSize = FileUtility.getFileSize(context, file);
            fileUploadTask = storageRef.putFile(Uri.fromFile(file), metadata);
            setUploadListener();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void setUploadListener() {
        fileUploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    chatEntity.updatePdfDetails(storageRef.toString(), fileSize, "" + pdfRenderer.getPageCount());
                } else {
                    chatEntity.updatePdfDetails(storageRef.toString(), fileSize, "0");
                }
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
        animation.setDuration(10000);
        progressBar.startAnimation(animation);
    }

    public String getFileName() {
        String[] fileNameArray = chatEntity.getFileName().split("-", 5);
        String result = "";
        for (int i = 1; i < fileNameArray.length; i++) {
            result = result + fileNameArray[i];
        }
        return result;
    }
}
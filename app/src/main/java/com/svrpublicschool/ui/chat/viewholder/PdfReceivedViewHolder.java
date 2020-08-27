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
import android.widget.LinearLayout;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class PdfReceivedViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout rlParent;
    LinearLayout llPdfDetails;
    TextView tvSender, tvTime, tvPdfName, tvPdfPages, tvPdfSize;
    AppCompatImageView ivPreview;
    Context context;
    ChatEntity chatEntity;

    TextView tvRetry, tvProgressPercent;
    AppCompatImageView ivCancel;
    ProgressBar progressBar;


    String fileSize = "100 kB";
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    boolean fileExhist;
    String filePath = "";

    public PdfReceivedViewHolder(View itemView) {
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
        tvProgressPercent = itemView.findViewById(R.id.tvProgressPercent);
        tvSender = itemView.findViewById(R.id.tvSender);
        llPdfDetails = itemView.findViewById(R.id.llPdfDetails);
    }

    public void setData(Context context, ChatEntity chatEntity, int position) {
        this.context = context;
        this.chatEntity = chatEntity;
        tvTime.setText("" + DateUtility.getFormattedTime(chatEntity.getCreatedAt()));
        tvPdfName.setText(getFileName());
        tvSender.setText(chatEntity.getSenName());
        tvPdfName.setText(getFileName());
        tvPdfPages.setText(chatEntity.getPages() + " Page");
        tvPdfSize.setText(chatEntity.getSize());

        filePath = FileUtility.getPdfDirectoryName() + chatEntity.getFileName();

        new CheckFileExhist().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

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
                if (chatEntity.getUrl() != null && Utility.isNetworkConnected(context)) {
                    setDownloadState(Constants.VIEW_STATE_PROGRESS);
                    downloadFileFromServer(chatEntity.getUrl());
                } else {
                    Toast.makeText(context, "Check internet connection!!", Toast.LENGTH_SHORT).show();
                    setDownloadState(Constants.VIEW_STATE_FAILED);
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        String fileDir = FileUtility.getPdfDirectoryName();
        File file = new File(fileDir + chatEntity.getFileName());

        fileSize = FileUtility.getFileSize(context, file);
        if (!file.exists()) {
            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
            // the cache directory.
            InputStream asset = context.getAssets().open(chatEntity.getFileName());
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        // This is the PdfRenderer we use to render the PDF.
        if (parcelFileDescriptor != null) {
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeRenderer() throws IOException {
        if (null != currentPage) {
            currentPage.close();
        }
        pdfRenderer.close();
        parcelFileDescriptor.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != currentPage) {
            currentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        ivPreview.setImageBitmap(bitmap);
        updateUi();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateUi() {
        int index = currentPage.getIndex();
        int pageCount = pdfRenderer.getPageCount();
        tvPdfPages.setText("" + pageCount + "Page");
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
                    fileExhist = true;
                    parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                    // This is the PdfRenderer we use to render the PDF.
                    if (parcelFileDescriptor != null) {
                        pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                    }

                    if (pdfRenderer.getPageCount() <= 0) {
                        return null;
                    }
                    // Make sure to close the current page before opening another one.
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
            } else {
                ivPreview.setImageResource(R.drawable.bg_blur);
            }

        }
    }

    public void downloadFileFromServer(String url) {
        Logger.d("Fetching pdf from server");
        StorageReference gsReference = storage.getReferenceFromUrl(url);
        File root = new File(filePath);
        gsReference.getFile(root).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Logger.d("Pdf downloaded from server");
                setDownloadState(Constants.VIEW_STATE_SUCCESS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    new OpenPdfPreview().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Logger.d("Pdf failed to download from server" + e.getMessage());
                setDownloadState(Constants.VIEW_STATE_FAILED);
            }
        });
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
            return fileExhist;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                setDownloadState(Constants.VIEW_STATE_SUCCESS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    new OpenPdfPreview().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }
            } else {
                setDownloadState(Constants.VIEW_STATE_FAILED);
            }
        }
    }

    public boolean checkFileExhist() {
        String fileDir = FileUtility.getPdfDirectoryName();
        File root = new File(fileDir + chatEntity.getFileName());
        if (root.exists()) {
            return true;
        }
        return false;
    }

    public String getFileName() {
        String[] fileNameArray = chatEntity.getFileName().split("-", 5);
        String result = "";
        for (int i = 1; i < fileNameArray.length; i++) {
            result = result + fileNameArray[i];
        }
        return result;
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

    public void animateProgressBar() {
        ProgressBarAnimation animation = new ProgressBarAnimation(progressBar, 0, 100);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(10000);
        progressBar.startAnimation(animation);
    }
}
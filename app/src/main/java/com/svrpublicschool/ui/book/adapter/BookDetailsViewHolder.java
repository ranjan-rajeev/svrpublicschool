package com.svrpublicschool.ui.book.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.svrpublicschool.R;
import com.svrpublicschool.Util.FileUtility;
import com.svrpublicschool.Util.GenericImageLoader;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.database.DatabaseClient;
import com.svrpublicschool.models.BooksEntity;
import com.svrpublicschool.ui.faculty.FacultyFragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class BookDetailsViewHolder extends RecyclerView.ViewHolder {
    ImageView ivIcon;
    TextView tvName;
    RelativeLayout rl, rlIcon;
    CardView cvParent;
    TextView tvRetry,tvProgressPercent;
    AppCompatImageView ivCancel;
    ProgressBar progressBar, progress_dummy;
    Context context;
    BooksEntity booksEntity;

    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;

    public BookDetailsViewHolder(View view) {
        super(view);
        ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
        tvName = (TextView) view.findViewById(R.id.tvName);
        rl = view.findViewById(R.id.rl);
        rlIcon = view.findViewById(R.id.rlIcon);
        cvParent = view.findViewById(R.id.cvParent);

        tvRetry = view.findViewById(R.id.tvRetry);
        ivCancel = view.findViewById(R.id.ivCancel);
        progressBar = view.findViewById(R.id.progress);
        progress_dummy = view.findViewById(R.id.progress_dummy);
        tvProgressPercent = view.findViewById(R.id.tvProgressPercent);
    }

    public void setData(Context context, BooksEntity item, int width) {
        this.context = context;
        this.booksEntity = item;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        cvParent.setUseCompatPadding(true);
        cvParent.setLayoutParams(layoutParams);
        tvName.setText(item.getSubject() + " ( " + item.getChapter() + ")");
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFileExhist()) {
                    openPdfViewerApp();
                } else {
                    Toast.makeText(context, "Download the file ", Toast.LENGTH_SHORT).show();
                }

            }
        });
        /*if (!item.getUrl().equals("")) {
            GenericImageLoader.loadImage(context, ivIcon, item.getUrl(), 0);
        } else {
            GenericImageLoader.loadImage(context, ivIcon, "abc", 0);
        }*/
        if (checkFileExhist()) {
            showProgress(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                new OpenPdfPreview().execute();
            }
        } else {
            showProgress(false);
        }

        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadFileFromURL().execute(booksEntity.getUrl());
            }
        });
    }

    public void showProgress(Boolean b) {
        if (b == null) {
            progressBar.setVisibility(View.GONE);
            tvRetry.setVisibility(View.GONE);
            tvProgressPercent.setVisibility(View.GONE);
            return;
        }
        if (b) {
            progressBar.setVisibility(View.VISIBLE);
            tvProgressPercent.setVisibility(View.VISIBLE);
            tvRetry.setVisibility(View.GONE);

        } else {
            tvProgressPercent.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            tvRetry.setVisibility(View.VISIBLE);
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);


                // Output stream
                OutputStream output = new FileOutputStream(FileUtility.getBookDirectoryName(booksEntity));

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Logger.d(e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            try {
                Logger.d("Dowloaded(%) : " + progress[0] + "%");
                Integer per = Integer.parseInt(progress[0]);
                progressBar.setProgress(per);
                tvProgressPercent.setText(per + "%");
            } catch (Exception e) {
                e.printStackTrace();
            }


            //progressBar.setProgress((int)progress[0]);
            //showDialog("Dowloaded(%) : " + progress[0] + "%");
            //pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            Logger.d("Download complete");
            showProgress(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                new OpenPdfPreview().execute();
            }
            /*booksEntity.setDownloaded(true);
            new UpdateBookEntity(booksEntity).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);*/

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class OpenPdfPreview extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                // In this sample, we read a PDF from the assets directory.
               /* String fileDir = FileUtility.getPdfDirectoryName();
                File file = new File(fileDir + chatEntity.getFileName());*/

                String fileName = booksEntity.getId() + booksEntity.getSubject() + booksEntity.getClassX();
                fileName = fileName.replaceAll("\\s", "");
                File file = new File(FileUtility.getBookDirectoryName(booksEntity));
                if (!file.exists()) {
                    // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
                    // the cache directory.
                    InputStream asset = context.getAssets().open(fileName);
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

                if (pdfRenderer.getPageCount() <= 0) {
                    return null;
                }
                // Make sure to close the current page before opening another one.
                if (null != currentPage) {
                    currentPage.close();
                }
                // Use `openPage` to open a specific page in PDF.
                currentPage = pdfRenderer.openPage(0);
                // Important: the destination bitmap must be ARGB (not RGB).
                Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                        Bitmap.Config.ARGB_8888);
                // Here, we render the page onto the Bitmap.
                // To render a portion of the page, use the second and third parameter. Pass nulls to get
                // the default result.
                // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                ivIcon.setImageBitmap(bitmap);
            }else {
                ivIcon.setImageResource(R.drawable.bg);
            }
        }
    }

    public void openPdfViewerApp() {
        String mFilePath = FileUtility.getBookDirectoryName(booksEntity);
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
      /*  File file = new File(FileUtility.getBookDirectoryName(booksEntity));

        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);*/
    }

    public boolean checkFileExhist() {
        return FileUtility.isBookAvailable(booksEntity);
    }
}
package com.svrpublicschool.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.text.format.Formatter;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.svrpublicschool.BuildConfig;
import com.svrpublicschool.models.BooksEntity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtility {
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static double getFileSizeIfCompatible(Context mContext, File file) {
        String fileSize = Formatter.formatFileSize(mContext, file.length()).trim();
        Logger.d("file image size " + fileSize);
        if (fileSize.endsWith("kB") || fileSize.endsWith("KB") || fileSize.endsWith("Mb") || fileSize.endsWith("MB")) {
            float size = getFloatFromString(fileSize);
            if (fileSize.endsWith("Mb") || fileSize.endsWith("MB")) {
//                if (size < 5)
                return size;
            } else {
                return (size / 1024);
            }
        }
        return 0;
    }

    public static String getFileSize(Context mContext, File file) {
        String fileSize = Formatter.formatFileSize(mContext, file.length()).trim();
        Logger.d("file image size " + fileSize);
        /*if (fileSize.endsWith("kB") || fileSize.endsWith("KB") || fileSize.endsWith("Mb") || fileSize.endsWith("MB")) {
            float size = getFloatFromString(fileSize);
            if (fileSize.endsWith("Mb") || fileSize.endsWith("MB")) {
//                if (size < 5)
                return size;
            } else {
                return (size / 1024);
            }
        }*/
        return fileSize;
    }

    public static File getFile(final String path) {
        Bitmap imgThumbBitmap;
        File file = new File(path);
        File selectedFile = null;
        try {
            final int THUMBNAIL_SIZE = 128;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            FileInputStream fis = new FileInputStream(file);
            imgThumbBitmap = BitmapFactory.decodeStream(fis, null, options);
            if (imgThumbBitmap != null) {
                imgThumbBitmap = Bitmap.createScaledBitmap(imgThumbBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
            }

            String fileName = Environment.getExternalStorageDirectory() + "/" + file.getName();
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
//            ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
            if (imgThumbBitmap != null) {
                imgThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            }
            file = new File(fileName);
            selectedFile = file;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return selectedFile;
    }

    public static Bitmap getBitmap(Context context, final String path) {
        Bitmap imgThumbBitmap = null;
        File file = new File(path);
        Logger.d("File Size  :  " + getFileSizeIfCompatible(context, file));
        File selectedFile = null;
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            FileInputStream fis = new FileInputStream(file);
            imgThumbBitmap = BitmapFactory.decodeStream(fis, null, options);
            if (imgThumbBitmap != null) {
                int height = imgThumbBitmap.getHeight();
                int width = imgThumbBitmap.getWidth();
                final int THUMBNAIL_SIZE = 1024;
                imgThumbBitmap = Bitmap.createScaledBitmap(imgThumbBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
            }

            String fileName = Environment.getExternalStorageDirectory() + "/" + file.getName();
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
//            ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
            if (imgThumbBitmap != null) {
                imgThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            }
            file = new File(fileName);
            Logger.d("File Size  :  " + getFileSizeIfCompatible(context, file));
            selectedFile = file;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imgThumbBitmap;
    }

    public static String getRealPathFromURI(Context mContext, Uri uri) {
        @SuppressLint("Recycle") Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /*private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title" + filesPath.size(), null);
        if (!TextUtils.isEmpty(path)) {
            try {
                return Uri.parse(path);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }*/

    public static float getFloatFromString(String stringValue) {
        return Float.parseFloat(stringValue.replaceAll("[^\\d.]", "").replace(" ", ""));
    }

    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            context.getContentResolver().notifyChange(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",
                    imageFile), null);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Logger.d("Exif orientation: " + orientation);
            Logger.d("Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri, String path) throws IOException {
        float MAX_IMAGE_DIMENSION = 800f;
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getCameraPhotoOrientation(context, photoUri, path);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }
        return srcBitmap;
    }

    public static void storeBitmap(Context context, Bitmap bitmap, String fileDir, String fileName) {
        try {
            File root = new File(fileDir);
            if (!root.exists()) {
                root.mkdirs();
            }
            File file;
            FileOutputStream fileOutputStream = new FileOutputStream(fileDir + fileName);
//            ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            }
            file = new File(fileDir);
            Logger.d("File Size  :  " + getFileSizeIfCompatible(context, file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void storePdf(Context context, String fileDir, String fileName) {
        try {
            File root = new File(fileDir);
            if (!root.exists()) {
                root.mkdirs();
            }
            File file;
            FileOutputStream fileOutputStream = new FileOutputStream(fileDir + fileName);
//            ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
           /* if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            }*/
            file = new File(fileDir);
            Logger.d("File Size  :  " + getFileSizeIfCompatible(context, file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getImageDirectoryName() {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.APP_NAME + "/" + "images/");
        if (!file.exists()) {
            file.mkdirs();
        }
        return Environment.getExternalStorageDirectory() + "/" + Constants.APP_NAME + "/" + "images/";
    }

    public static String getBookDirectoryName(BooksEntity booksEntity) {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.APP_NAME + "/" + "book/");
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = booksEntity.getId() + booksEntity.getSubject() + booksEntity.getClassX();
        fileName = fileName.replaceAll("\\s", "");
        return Environment.getExternalStorageDirectory() + "/" + Constants.APP_NAME + "/" + "book/" + fileName + ".pdf";
    }

    public static boolean isBookAvailable(BooksEntity booksEntity) {
        new File(Environment.getExternalStorageDirectory() + "/" + Constants.APP_NAME + "/" + "book/");
        String fileName = booksEntity.getId() + booksEntity.getSubject() + booksEntity.getClassX();
        fileName = fileName.replaceAll("\\s", "");
        File file = new File(Environment.getExternalStorageDirectory() + "/" + Constants.APP_NAME + "/" + "book/" + fileName + ".pdf");
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static String getPdfDirectoryName() {
        return Environment.getExternalStorageDirectory() + "/" + Constants.APP_NAME + "/" + "pdf/";
    }

    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(sourceFile);
            os = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public static void copyFile(String sourceFilePath, String destFilePath) throws IOException {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        copyFile(sourceFile, destFile);
    }

    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


}

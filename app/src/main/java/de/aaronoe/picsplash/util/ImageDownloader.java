package de.aaronoe.picsplash.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by aaron on 01.06.17.
 */


public class ImageDownloader {

    private OnImageLoaderListener mImageLoaderListener;
    private Set<String> mUrlsInProgress = new HashSet<>();
    private final String TAG = this.getClass().getSimpleName();

    public ImageDownloader(@NonNull OnImageLoaderListener listener) {
        this.mImageLoaderListener = listener;
    }

    public interface OnImageLoaderListener {
        void onError(ImageError error);
        void onProgressChange(int percent);
        void onComplete(Bitmap result);
    }


    public interface OnBitmapSaveListener {
        void onBitmapSaved();
        void onBitmapSaveError(ImageError error);
    }


    public static void writeToDisk(@NonNull final File imageFile, @NonNull final Bitmap image,
                                   @NonNull final OnBitmapSaveListener listener,
                                   @NonNull final Bitmap.CompressFormat format, boolean shouldOverwrite) {

        if (imageFile.isDirectory()) {
            listener.onBitmapSaveError(new ImageError("The specified path points to a directory, should be a file").setErrorCode(ImageError.ERROR_IS_DIRECTORY));
            return;
        }

        if (imageFile.exists()) {
            if (!shouldOverwrite) {
                listener.onBitmapSaveError(new ImageError("Image already downloaded").setErrorCode(ImageError.ERROR_FILE_EXISTS));
                return;
            } else if (!imageFile.delete()) {
                listener.onBitmapSaveError(new ImageError("Could not delete existing file, most likely the write permission was denied")
                        .setErrorCode(ImageError.ERROR_PERMISSION_DENIED));
                return;
            }
        }

        File parent = imageFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            listener.onBitmapSaveError(new ImageError("Could not create parent directory")
                    .setErrorCode(ImageError.ERROR_PERMISSION_DENIED));
            return;
        }

        try {
            if (!imageFile.createNewFile()) {
                listener.onBitmapSaveError(new ImageError("Could not create file")
                        .setErrorCode(ImageError.ERROR_PERMISSION_DENIED));
                return;
            }
        } catch (IOException e) {
            listener.onBitmapSaveError(new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION));
            return;
        }

        new AsyncTask<Void, Void, Void>() {

            private ImageError error;

            @Override
            protected Void doInBackground(Void... params) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(imageFile);
                    image.compress(format, 100, fos);
                } catch (IOException e) {
                    error = new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION);
                    this.cancel(true);
                } finally {
                    if (fos != null) {
                        try {
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onCancelled() {
                listener.onBitmapSaveError(error);
            }

            @Override
            protected void onPostExecute(Void result) {
                listener.onBitmapSaved();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static Bitmap readFromDisk(@NonNull File imageFile) {
        if (!imageFile.exists() || imageFile.isDirectory()) return null;
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    public interface OnImageReadListener {
        void onImageRead(Bitmap bitmap);
        void onReadFailed();
    }

    public static void readFromDiskAsync(@NonNull File imageFile, @NonNull final OnImageReadListener listener) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                return BitmapFactory.decodeFile(params[0]);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null)
                    listener.onImageRead(bitmap);
                else
                    listener.onReadFailed();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageFile.getAbsolutePath());
    }

    public static final class ImageError extends Throwable {

        private int errorCode;
        public static final int ERROR_GENERAL_EXCEPTION = -1;
        public static final int ERROR_INVALID_FILE = 0;
        public static final int ERROR_DECODE_FAILED = 1;
        public static final int ERROR_FILE_EXISTS = 2;
        public static final int ERROR_PERMISSION_DENIED = 3;
        public static final int ERROR_IS_DIRECTORY = 4;


        public ImageError(@NonNull String message) {
            super(message);
        }

        public ImageError(@NonNull Throwable error) {
            super(error.getMessage(), error.getCause());
            this.setStackTrace(error.getStackTrace());
        }

        public ImageError setErrorCode(int code) {
            this.errorCode = code;
            return this;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }
}

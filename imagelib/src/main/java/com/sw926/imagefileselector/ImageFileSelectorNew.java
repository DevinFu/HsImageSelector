package com.sw926.imagefileselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import java.io.File;

/**
 * Created by huasheng on 16/8/3.
 *
 * @author <a href="mailto:huasheng@2dfire.com">花生</a>.
 */
public class ImageFileSelectorNew {

    private static final String TAG = ImageFileSelectorNew.class.getSimpleName();

    private Callback mCallback;
    private ImagePickHelper mImagePickHelper;
    private HsImageCaptureHelper mImageTaker;
    private ImageCompressHelper mImageCompressHelper;

    public ImageFileSelectorNew(final Context context) {
        mImagePickHelper = new ImagePickHelper(context);
        mImagePickHelper.setCallback(new ImagePickHelper.Callback() {
            @Override
            public void onSuccess(String file) {
                AppLogger.d(TAG, "select image from sdcard: " + file);
                handleResult(file, false);
            }

            @Override
            public void onError() {
                handleError();
            }
        });

        mImageTaker = new HsImageCaptureHelper();
        mImageTaker.setCallback(new ImageCaptureHelper.Callback() {
            @Override
            public void onSuccess(String file) {
                AppLogger.d(TAG, "select image from camera: " + file);
                handleResult(file, true);
            }

            @Override
            public void onError() {
                handleError();
            }
        });

        mImageCompressHelper = new ImageCompressHelper(context);
        mImageCompressHelper.setCallback(new ImageCompressHelper.CompressCallback() {
            @Override
            public void onCallBack(String outFile) {
                AppLogger.d(TAG, "compress image output: " + outFile);
                if (mCallback != null) {
                    mCallback.onSuccess(outFile);
                }
            }
        });
    }

    public static void setDebug(boolean debug) {
        AppLogger.DEBUG = debug;
    }

    /**
     * 设置压缩后的文件大小
     *
     * @param maxWidth  压缩后文件宽度
     * @param maxHeight 压缩后文件高度
     */
    @SuppressWarnings("unused")
    public void setOutPutImageSize(int maxWidth, int maxHeight) {
        mImageCompressHelper.setOutPutImageSize(maxWidth, maxHeight);
    }

    /**
     * 设置压缩后保存图片的质量
     *
     * @param quality 图片质量 0 - 100
     */
    @SuppressWarnings("unused")
    public void setQuality(int quality) {
        mImageCompressHelper.setQuality(quality);
    }

    /**
     * set image compress format
     *
     * @param compressFormat compress format
     */
    @SuppressWarnings("unused")
    public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
        mImageCompressHelper.setCompressFormat(compressFormat);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImagePickHelper.onActivityResult(requestCode, resultCode, data);
        mImageTaker.onActivityResult(requestCode, resultCode, data);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if( requestCode ==ImagePickHelper.READ_EXTERNAL_STORAGE_REQUEST_CODE){
            mImagePickHelper.onRequestPermissionsResult(ImagePickHelper.READ_EXTERNAL_STORAGE_REQUEST_CODE, permissions, grantResults);
        }else if(requestCode ==HsImageCaptureHelper.CAMERA_REQUEST_CODE) {
            mImageTaker.onRequestPermissionsResult(HsImageCaptureHelper.CAMERA_REQUEST_CODE, permissions, grantResults);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        mImageTaker.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mImageTaker.onRestoreInstanceState(savedInstanceState);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void selectImage(Activity activity) {
        mImagePickHelper.selectorImage(activity,ImagePickHelper.READ_EXTERNAL_STORAGE_REQUEST_CODE);
    }

    public void selectImage(android.support.v4.app.Fragment fragment) {
        mImagePickHelper.selectImage(fragment);
    }

    public void selectImage(android.app.Fragment  fragment) {
        mImagePickHelper.selectImage(fragment);
    }

    public void takePhoto(Activity activity) {
        mImageTaker.captureImageHs(activity,HsImageCaptureHelper.CAMERA_REQUEST_CODE);
    }

    public void takePhoto(android.support.v4.app.Fragment fragment) {
        mImageTaker.captureImage(fragment);
    }

    public void takePhoto(android.app.Fragment fragment) {
        mImageTaker.captureImage(fragment);
    }

    private void handleResult(String fileName, boolean deleteSrc) {
        File file = new File(fileName);
        if (file.exists()) {
            mImageCompressHelper.compress(fileName, deleteSrc);
        } else {
            if (mCallback != null) {
                mCallback.onSuccess(null);
            }
        }
    }

    private void handleError() {
        if (mCallback != null) {
            mCallback.onError();
        }
    }

    public interface Callback {
        void onSuccess(String file);

        void onError();
    }

}

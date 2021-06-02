package com.moyihen.mylibyuv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Size;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.ImageCaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

/**
 * 创建日期：2021/6/1 10:32
 *
 * @author moyihen
 * 包名： com.moyihen.mylibyuv
 * 类说明：
 */
public class CameraHelp {
    //前后置
    private CameraSelector mode;

    //预览view
    private PreviewView preview;

    //拍照大小
    private Size captureSize;

    //预览大小
    private Size previewSize;
    private Preview mPreview;
    private ImageCapture mImageCapture;
    private CameraSelector.Builder mCameraSelect;
    private ProcessCameraProvider mCameraProvider;


    private CameraHelp(Builder builder) {
        mode = builder.mode;
        preview = builder.preview;
        captureSize = builder.captureSize;
        previewSize = builder.previewSize;

        initCamera();
    }

    @SuppressLint("RestrictedApi")
    private void initCamera() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(
                preview.getContext());

        future.addListener(() -> {
            try {
                mCameraProvider = future.get();
                mPreview = new Preview.Builder().build();
                mPreview.setSurfaceProvider(preview.getSurfaceProvider());

                //初始化拍照用例
                ImageCapture.Builder builder = new ImageCapture.Builder();
                //默认720*1280
                mImageCapture = builder.setTargetResolution(captureSize == null ?
                        new Size(720, 1280) : captureSize)
                        .build();

                mCameraSelect = CameraSelector.Builder.fromSelector(mode == null ?
                        CameraSelector.DEFAULT_BACK_CAMERA : mode);


            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }



        }, ContextCompat.getMainExecutor(preview.getContext()));
    }

    public void setLa(AppCompatActivity lifecycleOwner){

        mCameraProvider.unbindAll();
       // Camera camera = mCameraProvider.bindToLifecycle(lifecycleOwner.getLifecycle(), mCameraSelect, mImageCapture, mPreview);

       // Camera camera = cameraProvider.bindToLifecycle(new LifecycleOwner(p), cameraSelect, imageCapture ,p);
    }
    public static class Builder {
        private CameraSelector mode;
        private PreviewView preview;
        private Size captureSize;
        private Size previewSize;

        public Builder setMode(CameraSelector mode) {
            this.mode = mode;
            return this;
        }

        public Builder setPreview(PreviewView preview) {
            this.preview = preview;
            return this;
        }

        public Builder setCaptureSize(Size captureSize) {
            this.captureSize = captureSize;
            return this;
        }

        public Builder setPreviewSize(Size previewSize) {
            this.previewSize = previewSize;
            return this;
        }

        public CameraHelp builder() {
            return new CameraHelp(this);
        }
    }
}

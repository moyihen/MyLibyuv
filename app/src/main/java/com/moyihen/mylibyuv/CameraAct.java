package com.moyihen.mylibyuv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.impl.VideoCaptureConfig;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.internal.utils.ImageUtil;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.moyihen.libyuv.YUVUtils;
import com.moyihen.mylibyuv.R;
import com.moyihen.mylibyuv.databinding.ActivityCameraBinding;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CameraAct extends AppCompatActivity {

    private static final String TAG = "CameraAct";
    private ActivityCameraBinding mBinding;
    private CameraSelector camera_mode = CameraSelector.DEFAULT_BACK_CAMERA;
    //private CameraSelector camera_mode = CameraSelector.DEFAULT_FRONT_CAMERA;

    private ImageCapture mImageCapture;
    private String mPath;
    private int mLeft_1;
    private int mRight_1;
    private int mTop_1;
    private int mBottom_1;
    private int mLeft_2;
    private int mRight_2;
    private int mTop_2;
    private int mBottom_2;
    private int mRatio;
    private VideoCapture mVideoCapture;
    private boolean isRecord = false;
    private ProcessCameraProvider mCameraProvider;
    private ImageAnalysis mImageAnalysis;
    private CameraSelector mCameraSelector;
    private Preview mBuild;
    private Rect mFrameRect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        //setContentView(R.layout.activity_camera);

        mPath = FileUtils.getRootPath() + File.separator + "camera124" + File.separator;
        boolean file = FileUtils.createDirs(mPath);

        initView();
        initEvent();
    }

    private void initEvent() {
        mBinding.img.setOnClickListener(v -> {
            mBinding.img.setImageBitmap(null);
        });
        mBinding.take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Bitmap bitmap = mBinding.previewView.getBitmap();
                Log.i(TAG, "bitmap: w:"+bitmap.getWidth()+"--h"+bitmap.getHeight());

                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, mLeft_2, mTop_2, mRight_2-mLeft_2, mBottom_2-mTop_2);
                //Bitmap.createBitmap(bitmap,);
                Log.i(TAG, "bitmap1: w:"+bitmap1.getWidth()+"--h"+bitmap1.getHeight());
                mBinding.img.setImageBitmap(bitmap1);*/


               /* ImageCapture.OutputFileOptions build = new ImageCapture.OutputFileOptions.Builder(
                        new File(mPath + System.currentTimeMillis() + ".png")).build();*/
               /*mImageCapture.takePicture(build, ContextCompat.getMainExecutor(CameraAct.this), new ImageCapture.OnImageSavedCallback() {
                   @Override
                   public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                       Log.i(TAG, "onImageSaved: hah ");
                   }
                   @Override
                   public void onError(@NonNull ImageCaptureException exception) {
                       Log.i(TAG, "onError: "+exception.getMessage());
                   }
               });*/
                mImageCapture.takePicture(ContextCompat.getMainExecutor(CameraAct.this), new ImageCapture.OnImageCapturedCallback() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        int rotationDegrees = image.getImageInfo().getRotationDegrees();

                        super.onCaptureSuccess(image);
                        int width = image.getWidth();
                        int height = image.getHeight();
                        Log.i(TAG, "onCaptureSuccess: rotationDegrees:" + rotationDegrees + "w:" + width + "------h:" + height);
                        try {
                            byte[] bytes = ImageUtil.imageToJpegByteArray(image);
                            image.close();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            // 1280 *720

                            float x = (float) width / (float) mRight_1;
                            float y = (float) height / (float) mBottom_1;

                             int left = (int) (mFrameRect.left * x);
                            int right = (int) (mFrameRect.right * x);
                            int top = (int) (mFrameRect.top * y);
                            int bottom = (int) (mFrameRect.bottom * y);

                            Bitmap bitmap1 = Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top);

                            mBinding.img.setImageBitmap(bitmap1);
                        } catch (ImageUtil.CodecFailedException e) {
                            e.printStackTrace();
                        }


                    }
                });


            }
        });

        mBinding.take.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onLongClick(View v) {

                if (isRecord){
                    mVideoCapture.stopRecording();
                }else {


                    //预览前先绑定
                    mCameraProvider.unbindAll();
                    mCameraProvider.bindToLifecycle(CameraAct.this, mCameraSelector, mImageAnalysis, mVideoCapture, mBuild);


                     isRecord = true;
                    Toast.makeText(CameraAct.this,"开始录制",Toast.LENGTH_LONG).show();
                    if (ActivityCompat.checkSelfPermission(CameraAct.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                    }

                    /*ImageCapture.OutputFileOptions build = new ImageCapture.OutputFileOptions.Builder(
                            new File(mPath + System.currentTimeMillis() + ".png")).build();*/
                    VideoCapture.OutputFileOptions build = new VideoCapture.OutputFileOptions.Builder(
                            new File(mPath + System.currentTimeMillis() + ".mp4")).build();

                    mVideoCapture.startRecording(build, Executors.newSingleThreadExecutor(), new VideoCapture.OnVideoSavedCallback() {
                        @Override
                        public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                            String absolutePath = outputFileResults.getSavedUri().getPath();
                            Log.i(TAG, "onVideoSaved: "+absolutePath);

                            isRecord = false;
                           // Toast.makeText(CameraAct.this,"停止录制",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                            Log.i(TAG, "onError: "+message);

                            isRecord = false;
                            //Toast.makeText(CameraAct.this,"停止录制",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return false;
            }
        });
    }

    private void initView() {

        mBinding.previewView.post(new Runnable() {
            @Override
            public void run() {
                mLeft_1 = mBinding.previewView.getLeft();
                mRight_1 = mBinding.previewView.getRight();
                mTop_1 = mBinding.previewView.getTop();
                mBottom_1 = mBinding.previewView.getBottom();

                mFrameRect = mBinding.maskView.getFrameRect();
                mRatio = getRatio();
                initCamera();
               // Log.i(TAG, "initView:previewView left:" + mLeft_1 + "right:" + mRight_1 + "top:" + mTop_1 + "bottom:" + mBottom_1);
            }
        });

    }

    private int getRatio() {

        double screenRatio = mBinding.previewView.getHeight() / (mBinding.previewView.getWidth() * 1.0f);
        if (Math.abs(screenRatio - 4.0 / 3.0) <= Math.abs(screenRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    @SuppressLint({"RestrictedApi"})
    private void initCamera() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                mCameraProvider = future.get();

                mBuild = new Preview.Builder()
                        .build();

                mBuild.setSurfaceProvider(mBinding.previewView.getSurfaceProvider());

                ImageCapture.Builder builder = new ImageCapture.Builder();
                //拍照用例
                mImageCapture = builder
                        .setTargetResolution(new Size(1280, 720))
                        //.setTargetAspectRatio(mRatio)
                        .setTargetRotation(mBinding.previewView.getDisplay().getRotation())
                        .build();
                //back  front camera
                mCameraSelector = CameraSelector.Builder.fromSelector(camera_mode).build();

                //数据处理用例
                mImageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        //.setTargetAspectRatio(mRatio)
                        .setTargetRotation(mBinding.previewView.getDisplay().getRotation())
                        //  0 不阻塞  1 阻塞
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        //.setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                        .build();

                //录制用例
                mVideoCapture = new VideoCapture.Builder()
                        //设置当前旋转
                        // .setTargetRotation(rotation)
                        .setTargetResolution(new Size(1280, 720))
                        //设置宽高比
                        // .setTargetAspectRatio(screenAspectRatio)
                        //分辨率
                        //.setTargetResolution(resolution)
                        //视频帧率  越高视频体积越大
                        .setVideoFrameRate(25)
                        //bit率  越大视频体积越大
                        .setBitRate(3 * 1024 * 1024)
                        .build();

                //预览数据
                mImageAnalysis.setAnalyzer(CameraXExecutors.mainThreadExecutor(), new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy image) {
                        Log.i(TAG, "analyze: width:" + image.getWidth() + "height:" + image.getHeight());
                        //image.close();
                    }
                });

                //预览前先绑定
                mCameraProvider.unbindAll();
                mCameraProvider.bindToLifecycle(this, mCameraSelector,
                        mImageAnalysis, mImageCapture, mBuild);


            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(this));
    }


}
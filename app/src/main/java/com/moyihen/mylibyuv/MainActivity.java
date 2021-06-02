package com.moyihen.mylibyuv;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.ImageOutputConfig;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.internal.utils.ImageUtil;
import androidx.camera.extensions.BokehImageCaptureExtender;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.moyihen.libyuv.YUVUtils;
import com.moyihen.mylibyuv.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private Preview mPreview;
    private ActivityMainBinding mBinding;
    private ImageCapture mImageCapture;
    private String mPath;
    private CameraSelector camera_mode = CameraSelector.DEFAULT_BACK_CAMERA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initPermission();
        initEvent();
        mPath = FileUtils.getRootPath() + File.separator + "camera123" + File.separator;
        boolean file = FileUtils.createDirs(mPath);
    }


    private void initEvent() {

        mBinding.btTake.setOnClickListener(v -> {

            ImageCapture.OutputFileOptions build = new ImageCapture.OutputFileOptions.Builder(
                    new File(mPath+System.currentTimeMillis()+".png")).build();
            mImageCapture.takePicture(build, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                    Log.i(TAG, "onImageSaved: hah ");
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    Log.i(TAG, "onError: "+exception.getMessage());
                }
            });

        });

        mBinding.imgSelect.setOnClickListener(v -> {
            if (camera_mode == CameraSelector.DEFAULT_BACK_CAMERA)
                camera_mode = CameraSelector.DEFAULT_FRONT_CAMERA;
            else
                camera_mode =CameraSelector.DEFAULT_BACK_CAMERA;

            startCamera();

        });
    }

    private void initPermission() {
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .permission(Permission.CAMERA)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            Log.i(TAG, "onGranted: 权限通过");
                            startCamera();
                        } else {
                            Log.i(TAG, "onGranted: 获取部分权限成功,但部分权限未正常授予");
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Log.i(TAG, "onDenied: 被永久拒绝授权，请手动授予");
                            XXPermissions.startPermissionActivity(MainActivity.this, permissions);
                        } else {
                            Log.i(TAG, "onDenied: 权限获取失败");
                        }
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = future.get();

                mPreview = new Preview.Builder().build();

                mPreview.setSurfaceProvider(mBinding.preview.getSurfaceProvider());

                int rotation = mBinding.img.getDisplay().getRotation();
                Log.i(TAG, "startCamera: rotation:"+rotation);
                ImageCapture.Builder builder = new ImageCapture.Builder();
                //拍照用例
                mImageCapture = builder
                        .setTargetResolution(new Size(720,1280))
                        .build();

                CameraSelector cameraSelector = CameraSelector.Builder.fromSelector(camera_mode).build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(720, 1280))
                        //  0 不阻塞  1 阻塞
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        //.setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                        .build();

                //预览数据
                imageAnalysis.setAnalyzer(CameraXExecutors.mainThreadExecutor(), new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy image) {
                        int format = image.getFormat();
                        int rotationDegrees = image.getImageInfo().getRotationDegrees();
                        int width = image.getWidth();
                        int height = image.getHeight();
                        //Log.i(TAG, "analyze: " + rotationDegrees + "format:" + format + "-----w:" + width + "h:" + height);

                        long time = System.currentTimeMillis();


                        //1.image => i420 => nv21 =>bitmap 显示正常。
                        /* byte[] i420 = CameraUtils.ImageToI420(image, image.getWidth(), image.getHeight());

                         *//*byte[] dst_i420 = new byte[i420.length];
                        YUVUtils.rotateI420(i420,image.getWidth(),image.getHeight(),dst_i420,90);*//*

                        byte[] dst_nv21= new byte[width*height*4];
                        YUVUtils.I420ToNv21(i420,image.getWidth(),image.getHeight(),dst_nv21);*/

                        //Bitmap bitmap = NV21ToBitmap.nv21ToBitmap(MainActivity.this,dst_nv21, image.getWidth(), image.getHeight());


                       /* byte[] dst_i420 = new byte[i420.length];
                        YUVUtils.rotateI420(i420,image.getWidth(),image.getHeight(),dst_i420,90);*/


                        //2.image => nv21 =>bitmap 正常。
                        //byte[] dst_nv21 = CameraUtils.ImageToNv21(image, width, height);

                       /* FileUtils.saveBitmapToFile(bitmap,FileUtils.getRootPath()+ File.separator+"cameraX"+File.separator,
                                System.currentTimeMillis()+".png");*/

                        //3.
                        byte[] dst_nv21 = ImageUtil.yuv_420_888toNv21(image);
                        image.close();

                        /*byte[] nv21_90 = new byte[dst_nv21.length];
                        YUVUtils.rotateI420(dst_nv21,width,height,nv21_90,90);*/
                        //byte[] nv21_90 = NV21ToBitmap.rotateYUV420Degree90(dst_nv21, width, height);

                       // Log.i(TAG, "analyze: image==>i420 耗时:" + (System.currentTimeMillis() - time) + "ms");

                        byte[] dst_i420 = new byte[width * height * 3 / 2];
                        YUVUtils.NV21ToI420(dst_nv21, dst_i420, width, height);

                        byte[] dst_90 = new byte[width * height * 3 / 2];
                        YUVUtils.rotateI420(dst_i420,width,height,dst_90,90);
                        /*//左右镜像
                        byte[] mirror_l_r = new byte[width * height * 3 / 2];
                        YUVUtils.MirrorI420LeftRight(dst_90,height,width,mirror_l_r);*/
                       /* //上下镜像
                        byte[] mirror_u_d = new byte[width * height * 3 / 2];
                        YUVUtils.MirrorI420UpDown(dst_90,height,width,mirror_u_d);*/
                        //镜像
                        byte[] mirror = new byte[width * height * 3 / 2];
                        YUVUtils.MirrorI420(dst_90,height,width,mirror);


                        byte[] nv21_90 =new byte[width * height * 3 / 2];
                        YUVUtils.I420ToNv21(mirror,height,width,nv21_90);


                        byte[] dst_180 = new byte[width * height * 3 / 2];
                        YUVUtils.rotateI420(dst_i420,width,height,dst_180,180);

                        byte[] nv21_180 =new byte[width * height * 3 / 2];
                        YUVUtils.I420ToNv21(dst_180,width,height,nv21_180);

                        byte[] dst_270 = new byte[width * height * 3 / 2];
                        YUVUtils.rotateI420(dst_i420,width,height,dst_270,270);

                        byte[] nv21_270 =new byte[width * height * 3 / 2];
                        YUVUtils.I420ToNv21(dst_270,height,width,nv21_270);


                        Bitmap bitmap = NV21ToBitmap.nv21ToBitmap(MainActivity.this, nv21_90, image.getHeight(), image.getWidth());
                        mBinding.img.setImageBitmap(bitmap);

                        Bitmap bitmap2 = NV21ToBitmap.nv21ToBitmap(MainActivity.this, nv21_180, image.getWidth(), image.getHeight());
                        mBinding.img2.setImageBitmap(bitmap2);

                        Bitmap bitmap3 = NV21ToBitmap.nv21ToBitmap(MainActivity.this, nv21_270, image.getHeight(), image.getWidth());
                        mBinding.img3.setImageBitmap(bitmap3);

                        /*FileUtils.saveBitmapToFile(bitmap, FileUtils.getRootPath() +
                                        File.separator + "cameraX" + File.separator,
                                System.currentTimeMillis() + ".png");*/
                    }
                });

                //预览前先绑定
                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector,
                        imageAnalysis, mImageCapture, mPreview);


            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(this));
    }
}
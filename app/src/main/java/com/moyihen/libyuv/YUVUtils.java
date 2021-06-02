package com.moyihen.libyuv;

/**
 * 创建日期：2021/5/25 10:28
 *
 * @author moyihen
 * 包名： com.moyihen.libyuv
 * 类说明：
 */
public class YUVUtils {

    static {
        System.loadLibrary("yuvutil");
    }

    public static native void NV21ToI420(byte[] src_nv21,byte[] src_i420,int width,int height);


    public static native void rotateI420(byte[] src_i420,int width,int height,byte[] dst_i420,int degree);

    public static native void I420ToRGB(byte[] src_i420,int width,int height,byte[] dst_rgb);

    public static native void I420ToNv21(byte[] src_i420,int width,int height,byte[] dst_nv21);

    public static native void MirrorI420LeftRight(byte[] src_i420,int width,int height,byte[] dst_i420);

    public static native void MirrorI420UpDown(byte[] src_i420,int width,int height,byte[] dst_i420);

    public static native void MirrorI420(byte[] src_i420,int width,int height,byte[] dst_i420);



}

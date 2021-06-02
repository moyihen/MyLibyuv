//
// Created by moyihen on 2021/5/25.
//
#include <jni.h>
#include "YuvConvert.h"




//NV21-->I420
extern "C"
JNIEXPORT void JNICALL
Java_com_moyihen_libyuv_YUVUtils_NV21ToI420(JNIEnv *env, jclass clazz, jbyteArray src_nv21,
                                            jbyteArray src_i420, jint width, jint height) {
    jbyte *src_nv21_data = env->GetByteArrayElements(src_nv21, JNI_FALSE);
    jbyte *dst_i420_data = env->GetByteArrayElements(src_i420, JNI_FALSE);

    /* jint src_y_size = width * height;
     jint src_u_size = (width >> 1) * (height >> 1);

     jbyte *src_nv21_y_data = src_nv21_data;
     jbyte *src_nv21_uv_data = src_nv21_data + src_y_size;

     jbyte *dst_i420_y_data = dst_i420_data;
     jbyte *dst_i420_u_data = dst_i420_data + src_y_size;
     jbyte *dst_i420_v_data = dst_i420_data + src_y_size + src_u_size;

     libyuv::NV21ToI420((const uint8_t *)src_nv21_y_data, width,
                        (const uint8_t *)src_nv21_uv_data, width,
                        (uint8_t *)dst_i420_y_data, width,
                        (uint8_t *)dst_i420_u_data, width >> 1,
                        (uint8_t *)dst_i420_v_data, width >> 1,
                        width, height
     );*/
    NV21ToI420(src_nv21_data, width, height, dst_i420_data);

    env->ReleaseByteArrayElements(src_nv21, src_nv21_data, 0);
    env->ReleaseByteArrayElements(src_i420, dst_i420_data, 0);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_moyihen_libyuv_YUVUtils_rotateI420(JNIEnv *env, jclass clazz, jbyteArray src_i420,
                                            jint width, jint height, jbyteArray dst_i420,
                                            jint degree) {
    jbyte *src_i420_data = env->GetByteArrayElements(src_i420, JNI_FALSE);
    jbyte *dst_i420_data = env->GetByteArrayElements(dst_i420, JNI_FALSE);

    /*libyuvRotateYUV420P(reinterpret_cast<unsigned char *>(src_i420_data),
                        reinterpret_cast<unsigned char *>(dst_i420_data), width, height, degree);*/

    // i420数据旋转
    rotateI420(src_i420_data, width, height, dst_i420_data, degree);

    env->ReleaseByteArrayElements(src_i420, src_i420_data, JNI_FALSE);
    env->ReleaseByteArrayElements(dst_i420, dst_i420_data, JNI_FALSE);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_moyihen_libyuv_YUVUtils_I420ToRGB(JNIEnv *env, jclass clazz, jbyteArray src_i420,
                                           jint width, jint height, jbyteArray dst_rgb) {
    jint src_i420_y_size = width * height;
    jint src_i420_u_size = (width >> 1) * (height >> 1);

    jbyte *src_i420_data = env->GetByteArrayElements(src_i420, JNI_FALSE);
    jbyte *dst_rgb_data = env->GetByteArrayElements(dst_rgb, JNI_FALSE);

    jbyte *src_i420_y_data = src_i420_data;
    jbyte *src_i420_u_data = src_i420_data + src_i420_y_size;
    jbyte *src_i420_v_data = src_i420_data + src_i420_y_size + src_i420_u_size;

}


extern "C"
JNIEXPORT void JNICALL
Java_com_moyihen_libyuv_YUVUtils_I420ToNv21(JNIEnv *env, jclass clazz, jbyteArray src_i420,
                                            jint width, jint height, jbyteArray dst_nv21) {

    jbyte *src_i420_data = env->GetByteArrayElements(src_i420, JNI_FALSE);
    jbyte *dst_nv21_data = env->GetByteArrayElements(dst_nv21, JNI_FALSE);

    I420ToNv21(src_i420_data, width, height, dst_nv21_data);

    env->ReleaseByteArrayElements(dst_nv21, dst_nv21_data, JNI_FALSE);

}


extern "C"
JNIEXPORT void JNICALL
Java_com_moyihen_libyuv_YUVUtils_MirrorI420LeftRight(JNIEnv *env, jclass clazz, jbyteArray src_i420,
                                                     jint width, jint height, jbyteArray dst_i420) {

    jbyte *src_i420_data = env->GetByteArrayElements(src_i420, JNI_FALSE);
    jbyte *dst_i420_data = env->GetByteArrayElements(dst_i420, JNI_FALSE);

    MirrorI420LeftRight(src_i420_data, width, height, dst_i420_data);

    env->ReleaseByteArrayElements(dst_i420, dst_i420_data, JNI_FALSE);

}extern "C"
JNIEXPORT void JNICALL
Java_com_moyihen_libyuv_YUVUtils_MirrorI420UpDown(JNIEnv *env, jclass clazz, jbyteArray src_i420,
                                                  jint width, jint height, jbyteArray dst_i420) {
    jbyte *src_i420_data = env->GetByteArrayElements(src_i420, JNI_FALSE);
    jbyte *dst_i420_data = env->GetByteArrayElements(dst_i420, JNI_FALSE);

    MirrorI420UpDown(src_i420_data, width, height, dst_i420_data);

    env->ReleaseByteArrayElements(dst_i420, dst_i420_data, JNI_FALSE);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_moyihen_libyuv_YUVUtils_MirrorI420(JNIEnv *env, jclass clazz, jbyteArray src_i420,
                                            jint width, jint height, jbyteArray dst_i420) {
    jbyte *src_i420_data = env->GetByteArrayElements(src_i420, JNI_FALSE);
    jbyte *dst_i420_data = env->GetByteArrayElements(dst_i420, JNI_FALSE);

    MirrorI420(src_i420_data,width,height,dst_i420_data);

    env->ReleaseByteArrayElements(dst_i420, dst_i420_data, JNI_FALSE);
}
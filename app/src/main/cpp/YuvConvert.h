//
// Created by moyihen on 2021/5/27.
//
#include <jni.h>
#include <string>
#include "libyuv.h"


#ifndef MYLIBYUV_YUVCONVERT_H
#define MYLIBYUV_YUVCONVERT_H


void I420ToNv21(jbyte *src_i420_data, jint width, jint height, jbyte *src_nv21_data);

void NV21ToI420(jbyte *src_nv21_data, jint width, jint height, jbyte *dst_i420_data);

void libyuvRotateYUV420P(unsigned char *src, unsigned char *dst, int width, int height, float degree);

void rotateI420(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data, jint degree);

void MirrorI420LeftRight(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data);

void MirrorI420UpDown(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data);

void MirrorI420(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data);

#endif //MYLIBYUV_YUVCONVERT_H

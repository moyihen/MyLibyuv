//
// Created by moyihen on 2021/5/27.
//

#include "YuvConvert.h"

void I420ToNv21(jbyte *src_i420_data, jint width, jint height, jbyte *src_nv21_data) {
    jint src_y_size = width * height;
    jint src_u_size = (width >> 1) * (height >> 1);

    jbyte *src_nv21_y_data = src_nv21_data;
    jbyte *src_nv21_uv_data = src_nv21_data + src_y_size;

    jbyte *src_i420_y_data = src_i420_data;
    jbyte *src_i420_u_data = src_i420_data + src_y_size;
    jbyte *src_i420_v_data = src_i420_data + src_y_size + src_u_size;


    libyuv::I420ToNV21(
            (const uint8_t *) src_i420_y_data, width,
            (const uint8_t *) src_i420_u_data, width >> 1,
            (const uint8_t *) src_i420_v_data, width >> 1,
            (uint8_t *) src_nv21_y_data, width,
            (uint8_t *) src_nv21_uv_data, width,
            width, height);

}

void libyuvRotateYUV420P(unsigned char *src, unsigned char *dst, int width, int height, float degree) {
    unsigned char *pSrcY = src;
    unsigned char *pSrcU = src + width * height;
    unsigned char *pSrcV = src + width * height * 5 / 4;

    unsigned char *pDstY = dst;
    unsigned char *pDstU = dst + width * height;
    unsigned char *pDstV = dst + width * height * 5 / 4;

    if (degree == 90.0f) {
        I420Rotate(pSrcY, width, pSrcU, width >> 1, pSrcV, width >> 1,
                   pDstY, height, pDstU, height >> 1, pDstV, height >> 1,
                   width, height, libyuv::kRotate90);
    } else if (degree == 180.0f) {
        I420Rotate(pSrcY, width, pSrcU, width >> 1, pSrcV, width >> 1,
                   pDstY, width, pDstU, width >> 1, pDstV, width >> 1,
                   width, height, libyuv::kRotate180);
    } else if (degree == 270.0f) {
        I420Rotate(pSrcY, width, pSrcU, width >> 1, pSrcV, width >> 1,
                   pDstY, height, pDstU, height >> 1, pDstV, height >> 1,
                   width, height, libyuv::kRotate270);
    } else {
        return;
    }

}

void NV21ToI420(jbyte *src_nv21_data, jint width, jint height, jbyte *dst_i420_data) {
    // NV21 = y plane followed by an interleaved V/U plane, i.e. same as NV12
    // but the U and the V are switched. Use the NV12 function and switch the U
    // and V planes.
    jbyte *src_yplane = src_nv21_data;
    jbyte *src_uvplane = src_nv21_data + width * height;

    jbyte *dst_yplane = dst_i420_data;
    jbyte *dst_uplane = dst_i420_data + width * height;
    jbyte *dst_vplane = dst_uplane + (width * height / 4);
    libyuv::NV12ToI420(
            (const uint8_t *) src_yplane, width,
            (const uint8_t *) src_uvplane, width,
            (uint8_t *) dst_yplane, width,
            (uint8_t *) dst_vplane, width / 2,
            (uint8_t *) dst_uplane, width / 2,
            width, height);
}

void rotateI420(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data, jint degree) {
    jint src_i420_y_size = width * height;
    jint src_i420_u_size = (width >> 1) * (height >> 1);

    jbyte *src_i420_y_data = src_i420_data;
    jbyte *src_i420_u_data = src_i420_data + src_i420_y_size;
    jbyte *src_i420_v_data = src_i420_data + src_i420_y_size + src_i420_u_size;

    jbyte *dst_i420_y_data = dst_i420_data;
    jbyte *dst_i420_u_data = dst_i420_data + src_i420_y_size;
    jbyte *dst_i420_v_data = dst_i420_data + src_i420_y_size + src_i420_u_size;

    //要注意这里的width和height在旋转之后是相反的
    if (degree == libyuv::kRotate90 || degree == libyuv::kRotate270) {
        libyuv::I420Rotate((const uint8_t *) src_i420_y_data, width,
                           (const uint8_t *) src_i420_u_data, width >> 1,
                           (const uint8_t *) src_i420_v_data, width >> 1,
                           (uint8_t *) dst_i420_y_data, height,
                           (uint8_t *) dst_i420_u_data, height >> 1,
                           (uint8_t *) dst_i420_v_data, height >> 1,
                           width, height,
                           (libyuv::RotationMode) degree);
    } else {
        libyuv::I420Rotate((const uint8_t *) src_i420_y_data, width,
                           (const uint8_t *) src_i420_u_data, width >> 1,
                           (const uint8_t *) src_i420_v_data, width >> 1,
                           (uint8_t *) dst_i420_y_data, width,
                           (uint8_t *) dst_i420_u_data, width >> 1,
                           (uint8_t *) dst_i420_v_data, width >> 1,
                           width, height,
                           (libyuv::RotationMode) degree);
    }
}


void MirrorI420LeftRight(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data) {
    jbyte *src_yplane = src_i420_data;
    jbyte *src_uplane = src_yplane + width * height;
    jbyte *src_vplane = src_uplane + (width * height / 4);

    jbyte *dst_yplane = dst_i420_data;
    jbyte *dst_uplane = dst_yplane + width * height;
    jbyte *dst_vplane = dst_uplane + (width * height / 4);
    libyuv::I420Mirror(
            (const uint8_t *) src_yplane, width,
            (const uint8_t *) src_uplane, width / 2,
            (const uint8_t *) src_vplane, width / 2,
            (uint8_t *) dst_yplane, width,
            (uint8_t *) dst_uplane, width / 2,
            (uint8_t *) dst_vplane, width / 2,
            width, height);
}

void MirrorI420UpDown(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data) {
    jbyte *src_yplane = src_i420_data;
    jbyte *src_uplane = src_i420_data + width * height;
    jbyte *src_vplane = src_uplane + (width * height / 4);

    jbyte *dst_yplane = dst_i420_data;
    jbyte *dst_uplane = dst_i420_data + width * height;
    jbyte *dst_vplane = dst_uplane + (width * height / 4);

    // Inserting negative height flips the frame.
    libyuv::I420Copy(
            (const uint8_t *) src_yplane, width,
            (const uint8_t *) src_uplane, width / 2,
            (const uint8_t *) src_vplane, width / 2,
            (uint8_t *) dst_yplane, width,
            (uint8_t *) dst_uplane, width / 2,
            (uint8_t *) dst_vplane, width / 2,
            width, -height);
}

void MirrorI420(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data) {
    jint src_i420_y_size = width * height;
    // jint src_i420_u_size = (width >> 1) * (height >> 1);
    jint src_i420_u_size = src_i420_y_size >> 2;

    jbyte *src_i420_y_data = src_i420_data;
    jbyte *src_i420_u_data = src_i420_data + src_i420_y_size;
    jbyte *src_i420_v_data = src_i420_data + src_i420_y_size + src_i420_u_size;

    jbyte *dst_i420_y_data = dst_i420_data;
    jbyte *dst_i420_u_data = dst_i420_data + src_i420_y_size;
    jbyte *dst_i420_v_data = dst_i420_data + src_i420_y_size + src_i420_u_size;

    libyuv::I420Mirror((const uint8_t *) src_i420_y_data, width,
                       (const uint8_t *) src_i420_u_data, width >> 1,
                       (const uint8_t *) src_i420_v_data, width >> 1,
                       (uint8_t *) dst_i420_y_data, width,
                       (uint8_t *) dst_i420_u_data, width >> 1,
                       (uint8_t *) dst_i420_v_data, width >> 1,
                       width, height);
}

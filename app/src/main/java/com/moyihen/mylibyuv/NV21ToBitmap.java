package com.moyihen.mylibyuv;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.renderscript.Type.Builder;

/**
 * 创建日期：2021/2/25 13:46
 *
 * @author moyihen
 * 包名： com
 * 类说明：
 */
public class NV21ToBitmap {

    public static Bitmap nv21ToBitmap(Context context, byte[] nv21, int width, int height){
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        Builder yuvType = null;
        yuvType = (new Builder(rs, Element.U8(rs))).setX(nv21.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
        Builder rgbaType = (new Builder(rs, Element.RGBA_8888(rs))).setX(width).setY(height);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        in.copyFrom(nv21);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        Bitmap bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bmpout);


        return bmpout;

    }



    /**
     * 视频顺时针旋转90
     * 该方法仅仅在竖屏时候使用
     * */
    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth,
                                              int imageHeight) {

        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth)
                        + (x - 1)];
                i--;
            }
        }
        return yuv;
    }



}

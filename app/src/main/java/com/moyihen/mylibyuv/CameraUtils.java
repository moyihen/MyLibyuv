package com.moyihen.mylibyuv;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.media.Image;

import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;

/**
 * 创建日期：2021/5/26 16:48
 *
 * @author moyihen
 * 包名： com.moyihen.mylibyuv
 * 类说明：
 */
public class CameraUtils {
    static ByteBuffer i420;
    static byte[] scaleBytes;

    public static byte[] ImageToI420(ImageProxy image, int width, int height) {

        int format = image.getFormat();
        if (format != ImageFormat.YUV_420_888) {
            //抛出异常
        }
        //创建一个ByteBuffer i420对象，其字节数是height*width*3/2，存放最后的I420图像数据
        int size = height * width * 3 / 2;
        //TODO:防止内存抖动
        if (i420 == null || i420.capacity() < size) {
            i420 = ByteBuffer.allocate(size);
        }
        i420.position(0);
        //YUV planes数组
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        //TODO:取出Y数据，放入i420
        int pixelStride = planes[0].getPixelStride();
        ByteBuffer yBuffer = planes[0].getBuffer();
        int rowStride = planes[0].getRowStride();
        //1.若rowStride等于Width，skipRow是一个空数组
        //2.若rowStride大于Width，skipRow就刚好可以存储每行多出来的几个byte
        byte[] skipRow = new byte[rowStride - width];
        byte[] row = new byte[width];
        for (int i = 0; i < height; i++) {
            yBuffer.get(row);
            i420.put(row);
            //1.若不是最后一行，将无效占位数据放入skipRow数组
            //2.若是最后一行，不存在无效无效占位数据，不需要处理，否则报错
            if (i < height - 1) {
                yBuffer.get(skipRow);
            }
        }

        //TODO:取出U/V数据，放入i420
        for (int i = 1; i < 3; i++) {
            ImageProxy.PlaneProxy plane = planes[i];
            pixelStride = plane.getPixelStride();
            rowStride = plane.getRowStride();
            ByteBuffer buffer = plane.getBuffer();

            int uvWidth = width / 2;
            int uvHeight = height / 2;

            //一次处理一行
            for (int j = 0; j < uvHeight; j++) {
                //一次处理一个字节
                for (int k = 0; k < rowStride; k++) {
                    //1.最后一行
                    if (j == uvHeight - 1) {
                        //1.I420：UV没有混合在一起，rowStride大于等于Width/2，如果是最后一行，不理会占位数据
                        if (pixelStride == 1 && k >= uvWidth) {
                            break;
                        }
                        //2.NV21：UV混合在一起，rowStride大于等于Width-1，如果是最后一行，不理会占位数
                        if (pixelStride == 2 && k >= width - 1) {
                            break;
                        }
                    }
                    //2.非最后一行
                    byte b = buffer.get();
                    //1.I420：UV没有混合在一起，仅保存索引为偶数的有效数据，不理会占位数据
                    if (pixelStride == 1 && k < uvWidth) {
                        i420.put(b);
                        continue;
                    }
                    //2.NV21：UV混合在一起，仅保存索引为偶数的有效数据，不理会占位数据
                    if (pixelStride == 2 && k < width - 1 && k % 2 == 0) {
                        i420.put(b);
                        continue;
                    }
                }
            }
        }

        //TODO:将i420数据转成byte数组，执行旋转，并返回
        int srcWidth = image.getWidth();
        int srcHeight = image.getHeight();
        byte[] result = i420.array();

        return result;
    }


    public static byte[] ImageToNv21(ImageProxy imageProxy, int w, int h) {
        @SuppressLint("UnsafeOptInUsageError") Image image = imageProxy.getImage();
        //yuv420_888(包含多种格式如YUV420P(I420=YU12 YV12)和YUV420SP(NV12,NV21)都可以叫做YUV_420_888)-->yuv_n21

        // size是宽乘高的1.5倍 可以通过ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)得到
        int i420Size = w * h * 3 / 2;
        Image.Plane[] planes = image.getPlanes();
        //remaining0 = rowStride*(h-1)+w => 27632= 192*143+176 Y分量byte数组的size
        int remaining0 = planes[0].getBuffer().remaining();
        int remaining1 = planes[1].getBuffer().remaining();
        //remaining2 = rowStride*(h/2-1)+w-1 =>  13807=  192*71+176-1 V分量byte数组的size
        int remaining2 = planes[2].getBuffer().remaining();
        //获取pixelStride，可能跟width相等，可能不相等
        int pixelStride = planes[2].getPixelStride();
        int rowOffest = planes[2].getRowStride();
        byte[] nv21 = new byte[i420Size];
        //分别准备三个数组接收YUV分量。
        byte[] yRawSrcBytes = new byte[remaining0];
        byte[] uRawSrcBytes = new byte[remaining1];
        byte[] vRawSrcBytes = new byte[remaining2];
        planes[0].getBuffer().get(yRawSrcBytes);
        planes[1].getBuffer().get(uRawSrcBytes);
        planes[2].getBuffer().get(vRawSrcBytes);
        if (pixelStride == w) {
            //两者相等，说明每个YUV块紧密相连，可以直接拷贝
            System.arraycopy(yRawSrcBytes, 0, nv21, 0, rowOffest * h);
            System.arraycopy(vRawSrcBytes, 0, nv21, rowOffest * h, rowOffest * h / 2 - 1);
        } else {
            //根据每个分量的size先生成byte数组
            byte[] ySrcBytes = new byte[w * h];
            byte[] uSrcBytes = new byte[w * h / 2 - 1];
            byte[] vSrcBytes = new byte[w * h / 2 - 1];
            for (int row = 0; row < h; row++) {
                //源数组每隔 rowOffest 个bytes 拷贝 w 个bytes到目标数组
                System.arraycopy(yRawSrcBytes, rowOffest * row, ySrcBytes, w * row, w);
                //y执行两次，uv执行一次
                if (row % 2 == 0) {
                    //最后一行需要减一
                    if (row == h - 2) {
                        System.arraycopy(vRawSrcBytes, rowOffest * row / 2, vSrcBytes, w * row / 2, w - 1);
                    } else {
                        System.arraycopy(vRawSrcBytes, rowOffest * row / 2, vSrcBytes, w * row / 2, w);
                    }
                }
            }
            //yuv拷贝到一个数组里面
            System.arraycopy(ySrcBytes, 0, nv21, 0, w * h);
            System.arraycopy(vSrcBytes, 0, nv21, w * h, w * h / 2 - 1);
        }

        return nv21;
    }
}

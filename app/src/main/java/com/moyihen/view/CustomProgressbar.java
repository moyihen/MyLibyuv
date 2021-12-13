package com.moyihen.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 创建日期：2021/7/15 11:09
 *
 * @author moyihen
 * 包名： com.moyihen.view
 * 类说明：
 */
public class CustomProgressbar extends View {
    private static final String TAG = "CustomProgressbar";
    //圆环宽度
    private float ring_w = 30;
    //刻度长度
    private float ring_w_2 = 30;
    //刻度数量
    private int scale_num = 120;

    //最外层圆环半径
    private float mRing_radius;
    private RectF mRectF;
    private Paint mRing_paint;

    //2层刻度圆环半径
    private float mRing_radius_2;
    //3层圆环半径
    private float mRing_radius_3;
    private float mSweepAngle_3 = 0;
    private ValueAnimator mValueAnimator;


    public CustomProgressbar(Context context) {
        this(context, null);
    }

    public CustomProgressbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        mRing_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRing_paint.setStyle(Paint.Style.STROKE);
        mRing_paint.setColor(Color.parseColor("#3D76FF"));
        mRing_paint.setStrokeWidth(ring_w);


        mRectF = new RectF();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width_mode = MeasureSpec.getMode(widthMeasureSpec);
        int height_mode = MeasureSpec.getMode(heightMeasureSpec);

        int width_size = getSize_(width_mode, MeasureSpec.getSize(widthMeasureSpec));
        int height_size = getSize_(height_mode, MeasureSpec.getSize(heightMeasureSpec));

        Log.i(TAG, "onMeasure: width_size:" + width_size + "--height_size:" + height_size);
        //取长边作为宽高  保证正方形 .
        if (width_size > height_size) {
            setMeasuredDimension(width_size, width_size);
            mRing_radius = (width_size - (2 * ring_w)) / 2;
        } else {
            setMeasuredDimension(height_size, height_size);
            mRing_radius = (height_size - (2 * ring_w)) / 2;
        }

    }

    private int getSize_(int mode, int size) {

        int size_ = 400;

        if (mode == MeasureSpec.AT_MOST) {       //wrap_content
            Log.i(TAG, "getSize_: AT_MOST");
            return size_;
        } else if (mode == MeasureSpec.EXACTLY) {//在xml布局中设置为具体的值比如100dp或者match_parent或者fill_parent
            Log.i(TAG, "getSize_: EXACTLY");
            return size;
        } else {
            Log.i(TAG, "getSize_: default");
            return size_;
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        draw_ring_1(canvas);

        draw_scale(canvas);

        draw_ring_3(canvas);
    }

    private void draw_ring_3(Canvas canvas) {
        mRing_radius_3 = mRing_radius_2 - ring_w_2 / 2 - 60;
        mRing_paint.setStrokeWidth(ring_w);
        //原点还原到左上角
        canvas.translate(0, 0);
        float left = getWidth() / 2f - mRing_radius_3 - ring_w / 2;
        float top = getHeight() / 2f - mRing_radius_3 - ring_w / 2;
        float right = getWidth() / 2f + mRing_radius_3 + ring_w / 2;
        float bottom = getHeight() / 2f + mRing_radius_3 + ring_w / 2;

        //mRectF.set(left, top, right, bottom);
        Log.i(TAG, "draw_ring_3: mRing_radius_3 = " + mRing_radius_3);
        canvas.drawArc(new RectF(left, top, right, bottom), -90, mSweepAngle_3, false, mRing_paint);
    }

    private void draw_scale(Canvas canvas) {
        //刻度圆环半径
        mRing_radius_2 = mRing_radius - 20;
        Log.i(TAG, "draw_scale: mRing_radius_2 = " + mRing_radius_2);
        canvas.save();
        //移动坐标系原点到控件中心
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        //刻度颜色
        mRing_paint.setColor(Color.parseColor("#3D76FF"));
        //刻度宽度
        mRing_paint.setStrokeWidth(5);

        float rotateAngle = 360 / 120f;

        for (int i = 0; i < 120; i++) {
            //20 是和外层圆的间隔
            canvas.drawLine(0, mRing_radius_2, 0, mRing_radius_2 - ring_w_2, mRing_paint);
            canvas.rotate(rotateAngle);
        }

        canvas.restore();
    }

    int ring_length = 30;
    int index = 1;
    float startA = 45;
    float endA = 0;

    private void draw_ring_1(Canvas canvas) {
        Log.i(TAG, "draw_ring_1: mRing_radius=" + mRing_radius);
        float left = getWidth() / 2f - mRing_radius - ring_w / 2;
        float top = getHeight() / 2f - mRing_radius - ring_w / 2;
        float right = getWidth() / 2f + mRing_radius + ring_w / 2;
        float bottom = getHeight() / 2f + mRing_radius + ring_w / 2;

        mRectF.set(left, top, right, bottom);
        int a = (360 - ring_length * 3) / 3;
        canvas.save();
        for (int i = 1; i < 7; i++) {

            if (i % 2 == 0) {
                mRing_paint.setColor(Color.WHITE);
                canvas.drawArc(mRectF, startA, ring_length, false, mRing_paint);
                canvas.rotate(ring_length, getWidth() / 2f, getHeight() / 2f);
            } else {
                mRing_paint.setColor(Color.parseColor("#3D76FF"));
                canvas.drawArc(mRectF, startA, a, false, mRing_paint);
                canvas.rotate(a, getWidth() / 2f, getHeight() / 2f);
            }
        }
        canvas.restore();


    }


    /**
     * @param sweepAngle 进度 0-360
     */
    public void startAni(int sweepAngle) {
        mValueAnimator = ValueAnimator.ofFloat(sweepAngle);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSweepAngle_3 = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator.setDuration(2 * 1000);
        mValueAnimator.start();
    }


}

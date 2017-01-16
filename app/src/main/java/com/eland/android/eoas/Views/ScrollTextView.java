package com.eland.android.eoas.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liu.wenbin on 2016/2/19.
 */
public class ScrollTextView extends View {

    //背景画笔
    private Paint backPaint;
    //背景颜色
    private int backColor = Color.GRAY;
    //文本画笔
    private Paint textPaint;
    //字体颜色
    private int textColor = Color.BLACK;
    //字体大小
    private int textSize = 20;
    //文字内容
    private String textContent = "textView";
    //X轴偏移量
    private float OffsetX = 0;
    //刷新线程
    private RefreshRunnable mRefreshRunnable;

    public ScrollTextView(Context context) {
        super(context);
        init();
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化画笔
     */
    private void init() {
        backPaint = new Paint();
        backPaint.setAntiAlias(true);
        backPaint.setColor(backColor);
        backPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //启动刷新
        mRefreshRunnable = new RefreshRunnable();
        post(mRefreshRunnable);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //关闭刷新
        removeCallbacks(mRefreshRunnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景
        canvas.drawRect(0, 0, getWidth(), textSize + 10, backPaint);// 长方形
        //绘制文本内容
        canvas.drawText(textContent, OffsetX, textSize, textPaint);
    }

    /**
     * 测量控件宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measure(widthMeasureSpec);
        int height = measure(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measure(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 70;
        if (specMode == MeasureSpec.AT_MOST) {//最大可获空间
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {//精确尺寸
            result = specSize;
        }
        return result;
    }

    /**
     * 刷新线程
     * @author qiulong
     */
    private class RefreshRunnable implements Runnable {
        public void run() {
            synchronized (ScrollTextView.this) {
                OffsetX--;
                //获取字体宽度
                float txtWidth = textPaint.measureText(textContent, 0, textContent.length());
                if ((0 - OffsetX) >= txtWidth) {
                    OffsetX = getWidth();
                }
                invalidate();
                postDelayed(this, 5);
            }
        }
    }

    /**
     * 设置文本内容
     * @param value
     */
    public void setTextContent(String value) {
        this.textContent = value;
    }

    /**
     * 获取文本内容
     * @return
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * 设置文本颜色
     * @param color
     */
    public void setTextColor(int color) {
        this.textColor = color;
        textPaint.setColor(textColor);
    }

    /**
     * 获取文本颜色
     * @return
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * 设置文本大小
     * @param size
     */
    public void setTextSize(int size) {
        this.textSize = size;
        textPaint.setTextSize(textSize);
    }

    /**
     * 获取文本大小
     *
     * @return
     */

    public int getTextSize() {
        return textSize;
    }

    /**
     * 设置背景颜色
     */
    public void setBackgroundColor(int color) {
        this.backColor = color;
        backPaint.setColor(backColor);
    }

    /**
     * 获取背景颜色
     * @return
     */
    public int getBackgroundColor() {
        return backColor;
    }
}

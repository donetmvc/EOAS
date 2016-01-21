package com.eland.android.eoas.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eland.android.eoas.R;


/**
 * Created by liu.wenbin on 15/11/17.
 */
public class SegmentView extends LinearLayout{

    private TextView textView1;
    private TextView textView2;
    private onSegmentViewClickListener listener;
    private int nowColor;

    public SegmentView(Context context) {
        super(context);
        initView();
    }

    public SegmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SegmentText);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int itemId = ta.getIndex(i); // 获取某个属性的Id值
            switch (itemId) {
                case R.styleable.SegmentText_seg_text_color: // 设置当前按钮的状态
                    nowColor = ta.getColor(itemId, Color.RED);
                    break;
                case R.styleable.SegmentText_seg_back_color: // 设置按钮的背景图
                    int backgroundId = ta.getResourceId(itemId, -1);

                    break;
                default:
                    break;
            }
        }
        initView();
    }

    public SegmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        textView1 = new TextView(getContext());
        textView2 = new TextView(getContext());
        textView1.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        textView2.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        textView1.setText("item1");
        textView2.setText("item2");

        textView1.setGravity(Gravity.CENTER);
        textView2.setGravity(Gravity.CENTER);
        textView1.setPadding(3, 6, 3, 6);
        textView2.setPadding(3, 6, 3, 6);
        setSegmentTextSize(16);

        textView1.setBackgroundResource(R.drawable.seg_left);
        textView2.setBackgroundResource(R.drawable.seg_right);

        textView1.setSelected(true);
        textView1.setTextColor(getResources().getColor(R.color.seg_text_selected));
        textView1.setBackgroundColor(nowColor);
        textView2.setBackgroundColor(getResources().getColor(R.color.seg_text_selected));
        textView2.setTextColor(nowColor);
        this.removeAllViews();
        this.addView(textView1);
        this.addView(textView2);
        this.invalidate();

        textView1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (textView1.isSelected()) {
                    return;
                }
                textView1.setSelected(true);
                textView1.setTextColor(getResources().getColor(R.color.seg_text_selected));
                textView1.setBackgroundColor(nowColor);
                textView2.setSelected(false);
                textView2.setTextColor(nowColor);
                textView2.setBackgroundColor(getResources().getColor(R.color.seg_text_selected));
                if (listener != null) {
                    listener.onSegmentViewClick(textView1, 0);
                }
            }
        });
        textView2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (textView2.isSelected()) {
                    return;
                }
                textView2.setSelected(true);
                textView2.setTextColor(getResources().getColor(R.color.seg_text_selected));
                textView2.setBackgroundColor(nowColor);
                textView1.setSelected(false);
                textView1.setTextColor(nowColor);
                textView1.setBackgroundColor(getResources().getColor(R.color.seg_text_selected));
                if (listener != null) {
                    listener.onSegmentViewClick(textView2, 1);
                }
            }
        });

    }

    public void setSegmentTextSize(int dp) {
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
    }

    private static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setOnSegmentViewClickListener(onSegmentViewClickListener listener) {
        this.listener = listener;
    }

    public void setSegmentText(CharSequence text,int position) {
        if (position == 0) {
            textView1.setText(text);
        }
        if (position == 1) {
            textView2.setText(text);
        }
    }

    public static interface onSegmentViewClickListener{
        public void onSegmentViewClick(View v, int position);
    }

}

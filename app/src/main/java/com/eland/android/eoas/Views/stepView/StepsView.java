package com.eland.android.eoas.Views.stepView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eland.android.eoas.R;

import java.util.List;


public class StepsView extends LinearLayout implements StepsViewIndicator.OnDrawListener {

    private StepsViewIndicator mStepsViewIndicator;
    private FrameLayout mLabelsLayout;
    private String[] mLabels;
    private int mCompletedPosition;
    private int mLabelColor = Color.BLACK;
    private int mIndicatorColor = Color.RED;

    public StepsView(Context context) {
        this(context, null);
    }

    public StepsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepsView(Context context, AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StepsViewIndicator, defStyleAttr, 0);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int itemId = ta.getIndex(i); // 获取某个属性的Id值
            switch (itemId) {
                case R.styleable.StepsViewIndicator_indicatorColor: // 设置当前按钮的状态
                    mIndicatorColor = ta.getColor(itemId, Color.RED);
                    break;
                default:
                    break;
            }
        }
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_steps_view, this);
        mStepsViewIndicator = (StepsViewIndicator) rootView.findViewById(R.id.steps_indicator_view);
        mStepsViewIndicator.setDrawListener(this);
        mLabelsLayout = (FrameLayout) rootView.findViewById(R.id.labels_container);
    }

    public StepsView setLabels(String[] labels) {
        mLabels = labels;
        mStepsViewIndicator.setSize(mLabels.length);
        return this;
    }

    public int getCompletedPosition() {
        return mCompletedPosition;
    }

    public StepsView setCompletedPosition(int completedPosition) {
        mCompletedPosition = completedPosition;
        mStepsViewIndicator.setCompletedPosition(completedPosition);
        return this;
    }

    public StepsView setColorIndicator(int color) {
        mStepsViewIndicator.setThumbColor(mIndicatorColor);
        return this;
    }

    public StepsView setBarColor(int color) {
        mStepsViewIndicator.setBarColor(color);
        return this;
    }

    public StepsView setLabelColor(int labelColor) {
        mLabelColor = labelColor;
        return this;
    }

    @Override
    public void onFinish() {
        drawLabels();
    }

    private void drawLabels() {
        List<Float> indicatorPosition = mStepsViewIndicator.getThumbContainerXPosition();

        if (mLabels != null) {
            for (int i = 0; i < mLabels.length; i++) {
                TextView textView = new TextView(getContext());
                textView.setText(mLabels[i]);
                textView.setTextColor(mLabelColor);
                textView.setX(indicatorPosition.get(i));
                textView.setLayoutParams(
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));

                if (i <= mCompletedPosition) {
                    textView.setTypeface(null, Typeface.BOLD);
                }

                mLabelsLayout.addView(textView);
            }
        }
    }

}

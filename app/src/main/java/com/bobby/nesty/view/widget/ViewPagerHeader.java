package com.bobby.nesty.view.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * Created by corous360 on 2016/10/11.
 */
public class ViewPagerHeader extends ViewPager {
    public ViewPagerHeader(Context context) {
        super(context);
    }

    public ViewPagerHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        setMeasuredDimension(dm.widthPixels, dm.widthPixels/2);
    }
}

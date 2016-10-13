package com.bobby.nesty.view.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bobby.nesty.R;

/**
 * Created by corous360 on 2016/8/4.
 */
public class ViewPagerIndicator2 extends RelativeLayout implements ViewPager.OnPageChangeListener{

    private ViewPager viewPager;
    private Context context;
    private ViewPagerIndicator viewPagerIndicator;
    private ImageView imageView;
    private int sum;
    private OnPageChangeListener onPageChangeListener;

    public ViewPagerIndicator2(Context context) {
        super(context);
        init(context);
    }

    public ViewPagerIndicator2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ViewPagerIndicator2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ViewPagerIndicator2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
    }

    public void setViewPager(ViewPager viewPager, int sum){
        this.viewPager = viewPager;
        this.sum = sum;
        draw(context);
        this.viewPager.addOnPageChangeListener(this);
    }

    public void draw(Context context){
        if(viewPager!=null && viewPager.getAdapter().getCount()!=0){
            viewPagerIndicator = new ViewPagerIndicator(context);
            viewPagerIndicator.setLength(sum);
            imageView = new ImageView(context);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_banner_dot_over));
            addView(viewPagerIndicator);
            addView(imageView);

            LayoutParams params = (LayoutParams) imageView.getLayoutParams();
            params.height = 10;
            params.width = 10;
            params.leftMargin = 5;
            params.topMargin = 5;
            imageView.setLayoutParams(params);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(sum>1){
            int dis = (int)viewPagerIndicator.getDistance();
            position = position % sum;
            float leftMargin;
            /**
             * current selected is 0, scroll left
             */
            if(viewPagerIndicator.getSelected()==0 && position==viewPagerIndicator.getSum()-1){
                leftMargin = 0;
            }
            /**
             * current selected is the last one, scroll right
             */
            else if(viewPagerIndicator.getSelected()==viewPagerIndicator.getSum()-1 && position==viewPagerIndicator.getSum()-1){
                leftMargin = dis * (position);
            }
            /**
             * others
             */
            else{
                leftMargin = dis * (position + positionOffset);
            }
            LayoutParams params = (LayoutParams) imageView.getLayoutParams();
            params.leftMargin = Math.round(leftMargin)+5;
            imageView.setLayoutParams(params);
        }

        if(onPageChangeListener != null){
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        viewPagerIndicator.setSelected(position);
        if(onPageChangeListener != null){
            onPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(onPageChangeListener != null){
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void addOnPageChangeListener(OnPageChangeListener onPageChangelistener){
        this.onPageChangeListener = onPageChangelistener;
    }

    public interface OnPageChangeListener {

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

}

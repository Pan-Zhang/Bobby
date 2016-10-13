package com.bobby.nesty.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bobby.nesty.R;

/**
 * Created by corous360 on 2016/7/25.
 */
public class ViewPagerIndicator extends LinearLayout {
    private int sum=0;
    private int selected=0;
    private Context context;

    public ViewPagerIndicator(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(){
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void setLength(int sum){
        this.sum=sum;
        this.selected = 0;
        draw();
    }

    public void setSelected(int selected){
        removeAllViews();
        this.selected=sum==0?0:selected%sum;
        draw();
    }

    public void draw(){
        for(int i=0;i<sum;i++){
            ImageView imageview=new ImageView(context);
            imageview.setLayoutParams(new LayoutParams(20, 20));
            imageview.setPadding(5, 0, 5, 0);
            if(i==selected){
                imageview.setImageDrawable(getResources().getDrawable(R.drawable.icon_banner_dot));
            }else{
                imageview.setImageDrawable(getResources().getDrawable(R.drawable.icon_banner_dot));
            }
            addView(imageview);
        }
    }

    public float getDistance(){
        return getChildAt(1).getX()-getChildAt(0).getX();
    }

    public int getSelected(){
        return selected;
    }

    public int getSum(){
        return sum;
    }
}

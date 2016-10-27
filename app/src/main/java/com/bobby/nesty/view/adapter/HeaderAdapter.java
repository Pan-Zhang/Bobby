package com.bobby.nesty.view.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bobby.nesty.MyApplication;
import com.bobby.nesty.R;
import com.bobby.nesty.view.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by corous360 on 2016/10/11.
 */
public class HeaderAdapter extends PagerAdapter {

    Context context;
    List<String> list;

    public HeaderAdapter(Context context, List<String> list){
        this.context = context.getApplicationContext();
        this.list = list;
    }

    public void refresh(List<String> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_recycle_test, null);
        ImageView iv = (ImageView) view.findViewById(R.id.id_item);
        Picasso.with(context).load(list.get(position))
                .resize(400, 400).centerInside().transform(new CircleTransform(context)).placeholder(R.mipmap.ic_launcher).into(iv);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}

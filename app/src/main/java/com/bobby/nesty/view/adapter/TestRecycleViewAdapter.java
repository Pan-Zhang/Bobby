package com.bobby.nesty.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bobby.nesty.R;
import com.bobby.nesty.model.GirlsBean;
import com.squareup.picasso.Picasso;


/**
 * Created by corous360 on 2016/10/11.
 */
public class TestRecycleViewAdapter extends BaseRecyclerAdapter<GirlsBean.ResultsEntity, TestRecycleViewAdapter.MyViewHolder> {

    private Context context;

    public TestRecycleViewAdapter(Context context, GirlsBean girlsBean){
        this.context = context.getApplicationContext();
        if(girlsBean!=null){
            refreshDatas(girlsBean.getResults());
        }
    }

    public void refresh(GirlsBean girlsBean){
        if(girlsBean!=null){
            refreshDatas(girlsBean.getResults());
        }
    }

    @Override
    public MyViewHolder onCreate(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.adapter_recycle_test, parent,
                false));
    }

    @Override
    public void onBind(MyViewHolder viewHolder, int RealPosition, GirlsBean.ResultsEntity data) {
        Picasso.with(context).load(data.getUrl()).resize(400,400)
                .centerInside().placeholder(R.mipmap.ic_launcher).into(viewHolder.iv);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {

        ImageView iv;

        public MyViewHolder(View view)
        {
            super(view);
            iv = (ImageView) view.findViewById(R.id.id_item);
        }
    }
}

package com.bobby.nesty.util.retrofit;

import com.bobby.nesty.model.GirlsBean;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by corous360 on 2016/10/10.
 */
public interface ServiceRetrofit {

    @GET("api/data/{type}/{count}/{page}")
    Observable<GirlsBean> getBaidu(
            @Path("type") String type,
            @Path("count") int count,
            @Path("page") int page
    );

}

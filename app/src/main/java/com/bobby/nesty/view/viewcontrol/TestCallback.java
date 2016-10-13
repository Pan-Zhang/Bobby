package com.bobby.nesty.view.viewcontrol;

import com.bobby.nesty.model.GirlsBean;

/**
 * Created by corous360 on 2016/10/10.
 */
public interface TestCallback extends BaseCallback {

    void beginRefresh();

    void stopRefrech(GirlsBean girlsBean);

    void interval();

    void refreshTextView(String s);

}

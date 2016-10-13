package com.bobby.nesty.view.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.bobby.nesty.MyApplication;
import com.bobby.nesty.present.BasePresent;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by corous360 on 2016/10/10.
 */
public abstract class BaseActivity<T extends BasePresent> extends Activity {

    public T present;

    public CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.mInstance.addActivity(this);
        setPresent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MyApplication.mInstance.removeActivity(this);

        if(compositeSubscription != null && compositeSubscription.hasSubscriptions()){
            compositeSubscription.unsubscribe();
        }
    }

    public abstract void setPresent();

    public void addSubscription(Subscription subscription){
        if(compositeSubscription == null){
            compositeSubscription = new CompositeSubscription();
        }
        compositeSubscription.add(subscription);
    }

    public void showToastShort(int id){
        Toast.makeText(this, getResources().getString(id), Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(int id){
        Toast.makeText(this, getResources().getString(id), Toast.LENGTH_LONG).show();
    }

    public void showToastShort(String mess){
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(String mess){
        Toast.makeText(this, mess, Toast.LENGTH_LONG).show();
    }

    public void HideKeyboard(EditText etHide)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etHide.getWindowToken(), 0);
    }
}

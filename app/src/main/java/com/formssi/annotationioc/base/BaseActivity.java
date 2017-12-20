package com.formssi.annotationioc.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.api.ViewUtils;

/**
 * Description :
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/27
 * Email : 27674569@qq.com
 * Version : 1.0
 */


public abstract class BaseActivity extends AppCompatActivity{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        ViewUtils.bind(this);
        onNext();
    }


    protected abstract void setContentView();

    protected abstract void onNext();

    protected void startActivity(Class<? extends Activity> clazz){
        startActivity(new Intent(this,clazz));
    }
}

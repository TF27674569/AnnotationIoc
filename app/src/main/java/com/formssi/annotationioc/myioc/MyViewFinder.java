package com.formssi.annotationioc.myioc;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.api.ViewFinder;

/**
 * Description :
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/27
 * Email : 27674569@qq.com
 * Version : 1.0
 */


public class MyViewFinder extends ViewFinder{

    public MyViewFinder(Object object, View view) {
        super(object, view);
    }

    @Override
    protected void showToast() {
        Toast.makeText(context, "定义了进入检测网络", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean isOpenNetWork() {
        Log.e("MyViewFinder","isOpenNetWork");
        return super.isOpenNetWork();
    }
}

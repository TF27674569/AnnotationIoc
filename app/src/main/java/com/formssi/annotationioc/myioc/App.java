package com.formssi.annotationioc.myioc;

import android.app.Application;
import android.view.View;

import org.api.ViewBinder;
import org.api.ViewFinder;
import org.api.ViewUtils;

/**
 * Description :
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/27
 * Email : 27674569@qq.com
 * Version : 1.0
 */


public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        // 定义自己的 ViewFinder 重写ViewFinder
        ViewUtils.setViewBinder(new ViewBinder() {
            @Override
            protected ViewFinder getViewFinder(Object object, View view) {
                return new MyViewFinder(object, view);
            }
        });
    }
}

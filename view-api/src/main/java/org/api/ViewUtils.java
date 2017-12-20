package org.api;
import android.app.Activity;
import android.view.View;

/**
 * Description :
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/24
 * Email : 27674569@qq.com
 * Version : 1.0
 */


public class ViewUtils {
    private static ViewBinder mViewBinder = new ViewBinder() {
        @Override
        protected ViewFinder getViewFinder(Object object, View view) {
            return new ViewFinder(object, view);
        }
    };

    /**
     * 设置自己的ViewBinder
     */
    public static void setViewBinder(ViewBinder viewBinder){
        mViewBinder = viewBinder;
    }


    /**
     * 绑定activity
     */
    public static void bind(Activity activity){
        mViewBinder.bindActivity(activity);
    }

    /**
     * 绑定其他
     */
    public static void bind(Object object,View rootView){
        mViewBinder.bindObject(object,rootView);
    }

    /**
     * 绑定View
     */
    public static void bind(View view){
        mViewBinder.bindObject(view,view);
    }

}

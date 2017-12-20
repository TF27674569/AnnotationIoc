package org.api;

import android.app.Activity;
import android.view.View;

import org.compiler.ProcessorHelper;

/**
 * Description :
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/24
 * Email : 27674569@qq.com
 * Version : 1.0
 */


public abstract class ViewBinder {

    /**
     * 绑定activity
     */
    protected void bindActivity(Activity activity) {
        bind(activity, null);
    }

    protected void bindObject(Object object, View rootView) {
        bind(object, rootView);
    }

    protected void bind(Object object, View view) {
        // 拿到当前对象的class
        Class cls = object.getClass();
        // 拿到辅助类的全类名
        String className = cls.getName() + ProcessorHelper.HELPER;
        try {
            // 反射加载辅助类并实例化对象
            ViewInject viewInject = (ViewInject) Class.forName(className).newInstance();
            // 实现初始化等操作
            viewInject.inject(object, getViewFinder(object, view));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建ViewFinder
     */
    protected abstract ViewFinder getViewFinder(Object object, View view);


}

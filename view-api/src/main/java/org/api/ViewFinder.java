package org.api;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Description : findviewbyid辅助类
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/24
 * Email : 27674569@qq.com
 * Version : 1.0
 */

public class ViewFinder {

    private Activity mActivity;
    private View mView;
    private Object mObject;
    protected Context context;

    public ViewFinder(Object object, View view) {
        if (object instanceof Activity) {
            this.mActivity = (Activity) object;
            this.context = ((Activity) object);
        } else {
            this.mActivity = null;
            this.context = view.getContext();
        }
        this.mView = view;
        this.mObject = object;
    }

    /**
     * 初始化Id
     */
    public <T extends View> T findViewById(int viewId) {
        return (T) (mActivity != null ? mActivity.findViewById(viewId) : mView.findViewById(viewId));
    }

    /**
     * 多id回掉同一事件
     */
    public void setOnclickListenetByIds(ArrayList<Integer> viewIds, final IFinderClickListener listener) {
        for (int viewId : viewIds) {
            View view = findViewById(viewId);
            if (view == null) {
                throw new NullPointerException("not find view , view id is " + viewId);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v);
                }
            });
        }
    }

    /**
     * Intent  bundle传参数
     */
    public <T> T getExtra(String key) {
        if (mObject instanceof Activity) {
            return (T) ((Activity) mObject).getIntent().getExtras().get(key);
        }
        if (mObject instanceof Fragment) {
            return (T) ((Fragment) mObject).getArguments().get(key);
        }
        return null;
    }

    /**
     * 判断是否有网络
     */
    public boolean isOpenNetWork() {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isAvailable()) {
            return true;
        }
        showToast();
        return false;
    }


    public boolean isFirstClick(long time){
        return Utils.isFirstClick(time);
    }

    /**
     * Toast 样式 重写 可以自定义
     */
    protected void showToast() {
        Toast.makeText(context, "当前无网络~", Toast.LENGTH_SHORT).show();
    }

    /**
     * 判断是否拦截click 事件  true 拦截  false 不拦截
     * @param view     点击的控件
     * @param object      对象
     * @param methodName     当前方法名
     * @param paramsClassName       参数的全类名
     * @return
     */
    public boolean onInterceptClickEvent(View view,Object object,String methodName,String paramsClassName) {
        return false;
    }

    /**
     * 点击监听回掉
     */
    public interface IFinderClickListener {
        void onClick(View view);
    }
}

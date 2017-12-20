package org.api;

class Utils {
	// 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static long lastClickTime;


    public static boolean isFirstClick(long time) {

        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= time) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}

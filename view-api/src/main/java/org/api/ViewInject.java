package org.api;

/**
 * Description :
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/24
 * Email : 27674569@qq.com
 * Version : 1.0
 */


public interface ViewInject<T> {
     void inject(T object, ViewFinder finder);
}

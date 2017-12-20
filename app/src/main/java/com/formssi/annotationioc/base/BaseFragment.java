package com.formssi.annotationioc.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.api.ViewUtils;

/**
 * Description :
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/27
 * Email : 27674569@qq.com
 * Version : 1.0
 */


public abstract class BaseFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(),container,false);
        ViewUtils.bind(this,view);
        onNext();
        return view;
    }

    protected abstract int getLayoutId();
    protected abstract void onNext();

}

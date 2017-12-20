package com.formssi.annotationioc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import android.widget.Toast;

import com.formssi.annotationioc.base.BaseActivity;
import com.formssi.annotationioc.fragment.BlankFragment;
import com.formssi.annotationioc.test.Preson;

import org.annotation.CheckNet;
import org.annotation.EchoEnable;
import org.annotation.Event;
import org.annotation.Extra;
import org.annotation.ViewById;

public class Main2Activity extends BaseActivity {

    @ViewById(R.id.btn_test)
    public Button btnTest;
    @ViewById(R.id.viewPager)
    public ViewPager viewPager;
    @Extra("key")
    String name;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_test);
    }

    @Override
    protected void onNext() {
        btnTest.setText(name);
        setAdapter();
    }


    private void setAdapter() {
        Fragment fragment1 = new BlankFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("preson", new Preson("张三", 18));
        fragment1.setArguments(bundle1);

        Fragment fragment2 = new BlankFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("preson", new Preson("李四", 20));
        fragment2.setArguments(bundle2);

        final Fragment[] fragments = {fragment1, fragment2};

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
    }

    @Event(R.id.btn_test)
    @EchoEnable
    @CheckNet
    public void btnTestClick(Button btnTest) {
        Toast.makeText(this, "我点击了", Toast.LENGTH_LONG).show();
    }
}

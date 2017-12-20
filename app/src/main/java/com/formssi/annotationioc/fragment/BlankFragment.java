package com.formssi.annotationioc.fragment;

import android.widget.TextView;

import com.formssi.annotationioc.R;
import com.formssi.annotationioc.base.BaseFragment;
import com.formssi.annotationioc.test.Preson;

import org.annotation.Extra;
import org.annotation.ViewById;

public class BlankFragment extends BaseFragment {

    @ViewById(R.id.text_fragment)
    TextView textView;

    @Extra("preson")
    Preson preson;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blank;
    }

    @Override
    protected void onNext() {
        textView.setText(preson.toString());
    }
}

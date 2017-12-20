package com.formssi.annotationioc;

import android.content.Intent;
import android.widget.Button;

import com.formssi.annotationioc.base.BaseActivity;

import org.annotation.Event;
import org.annotation.ViewById;

public class MainActivity extends BaseActivity {
    @ViewById(R.id.main_btn)
    public Button mainBtn;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onNext() {
        mainBtn.setText("跳转");
    }


    @Event(R.id.main_btn)
    public void clickBtn(){
        Intent intent = new Intent(this,Main2Activity.class);
        intent.putExtra("key","我是main1的参数");
        startActivity(intent);
    }


}

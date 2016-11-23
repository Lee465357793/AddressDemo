package com.example.lee.addressdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.lee.addressdemo.activity.AreaListActivity;
import com.example.lee.addressdemo.activity.BaseActivity;
import com.example.lee.addressdemo.bean.AddressInfo;

import java.io.Serializable;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int MAIN = 0;
    private EditText mEt_address;

    @Override
    public int getLayoutResouce() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mEt_address = (EditText) findViewById(R.id.et_address);
    }

    @Override
    protected void initLineWidth() {

    }

    @Override
    protected void initListener() {
        mEt_address.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, AreaListActivity.class);
        startActivityForResult(intent, MAIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AddressInfo result = (AddressInfo) data.getSerializableExtra("areaList");
            if(resultCode == 0 && result.province!=null && result.city!=null && result.area !=null){
                mEt_address.setText(result.province + result.city + result.area);
            }
    }
}

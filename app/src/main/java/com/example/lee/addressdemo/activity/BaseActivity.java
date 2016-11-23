package com.example.lee.addressdemo.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.lee.addressdemo.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by leeyan on 2016/11/17.
 */
public abstract class BaseActivity extends Activity {
    /** 本地省数据 */
    public String mProvinceJson;
    /** 本地市数据 */
    public String mCityJson;
    /** 本地区数据 */
    public String mAreaJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResouce());
        initView();
        initListener();
        initLineWidth();
    }

    protected abstract void initView();

    protected abstract void initLineWidth();

    protected abstract void initListener();

    /** 设置布局资源 */
    public abstract int getLayoutResouce();


//    /** Json解析
//     * @param areaJson*/
//    private  void paserJson(String areaJson) {
//        Gson gson = new Gson();
//        AreaJsonBean areaBean = gson.fromJson(areaJson, AreaJsonBean.class);
//    }
}

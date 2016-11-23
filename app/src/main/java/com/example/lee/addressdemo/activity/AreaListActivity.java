package com.example.lee.addressdemo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.addressdemo.R;
import com.example.lee.addressdemo.bean.AddressInfo;
import com.example.lee.addressdemo.bean.AreaJsonBean;
import com.example.lee.addressdemo.bean.AreaJsonBean.AddressArea;
import com.example.lee.addressdemo.pager.AreaPager;
import com.example.lee.addressdemo.utils.HttpUtils;
import com.example.lee.addressdemo.utils.HttpUtils.ResquestBackCall;
import com.example.lee.addressdemo.utils.StreamUtils;
import com.google.gson.Gson;
import com.example.lee.addressdemo.view.LazyViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加地址：三级列表
 * Created by leeyan on 2016/11/17.
 */
@SuppressLint("NewApi")
public class AreaListActivity extends BaseActivity implements View.OnClickListener {
    /** ViewPager控件对象 */
    private LazyViewPager area_viewpager;
    /**  适配器 */
    private AreaPagerAdapter adapter;
    /** 省标题 */
    private TextView tv_province;
    /** 市标题 */
    private TextView tv_city;
    /** 区，县标题 */
    private TextView tv_area;
    /**  地址条目页面集合 */
    private ArrayList<AreaPager> pagers = new ArrayList<>();
    /** 1=市， 2=区、县， other=省 */
    private int TYPE = 1;
    /** 地址列表页面 */
    private AreaPager mProvince;
    private AreaPager mCity;
    private AreaPager mArea;
    /** 指示线长度 */
    private int lineWidth;
    /** 标题指示线控件 */
    private View indicate_line;
    /** 地址信息bean */
    private AddressInfo mAddressInfo = new AddressInfo();

    /**
     * 界面改变事件监听
     */
    private LazyViewPager.OnPageChangeListener dealOnPagerChangeListener = new LazyViewPager.OnPageChangeListener() {
        /** 滑动中 */
        @Override
        public void onPageScrolled(int position, float positionOffset
                , int positionOffsetPixels) {
            float disX = positionOffset * lineWidth;//获取滑动的距离
            float startX = disX + position * lineWidth;//获取指示线起始位置
            indicate_line.setTranslationX(startX);//设置位移动画
        }
        /** 当页面改变后 */
        @Override
        public void onPageSelected(int position) {
            TYPE = position + 1;  //这里更改TYPE的值需要TYPE初值=1.否则将不能触发页面改变监听
            int bule = getResources().getColor(R.color.colorPrimary);
            int accent = getResources().getColor(R.color.colorAccent);
            tv_province.setTextColor(position==0 ? accent : bule); //字体颜色选择
            tv_city.setTextColor(position==1 ? accent : bule);
            tv_area.setTextColor(position==2 ? accent : bule);
        }
        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    /**
     * 处理地址列表条目点击事件
     */
    private AreaPager.DealOnItemClickListener dealOnItemClickLdistener = new AreaPager.DealOnItemClickListener() {
        @Override
        public void dealOnItemClickListener(List areaList, int position) {
            AddressArea addressArea = (AddressArea) areaList.get(position);
            switchRecord(addressArea); //选择记录地址信息
//            requestNet(addressArea.areaID);  //开启子线程，获取网络数据
            //TODO 联网开启
        }
    };

    /**
     * 选择记录地址信息(省，市，区)
     * @param addressArea 当前点击的地址信息
     */
    private void switchRecord(AddressArea addressArea) {
        if(TYPE == 1){  //记录省
            mAddressInfo.province = addressArea.value;
            insertAssets("city.json");  //TODO 本地资源获取，联网可删除
            flashUI();  //这里销毁 区 界面，防止地址选择错误
        }else if(TYPE == 2){ //记录市
            insertAssets("area.json"); //TODO 本地资源获取，联网可删除
            mAddressInfo.city = addressArea.value;
        }else if(TYPE == 3){ //记录区
            mAddressInfo.area = addressArea.value;
            finish();
            return;
        }
    }

    /**
     * 处理网络请求回调
     */
    private ResquestBackCall dealOnRequsetBackCall = new ResquestBackCall() {
        @Override
        public void onSucesss(String json) {
            parseJson(json);
            Log.i("AddressInfo=========", json);
        }

        @Override
        public void onFailure(String errorMsg) {

        }
    };

    /**
     * 设置Activity布局资源
     * @return 当前Activity布局资源
     */
    @Override
    public int getLayoutResouce() {
        return R.layout.activity_arealist;
    }

    @Override
    protected void initView() {
        //TODO 联网开启 打开网络链接开关
//        requestNet("0");  //默认请求省列表
        //TODO 联网开启 使用本地资源演示案列
        insertAssets("province.json");
        area_viewpager = (LazyViewPager) findViewById(R.id.address_viewpager);
        tv_province = (TextView) findViewById(R.id.address_tv_province); //省标题
        tv_city = (TextView) findViewById(R.id.address_tv_city);//市标题
        tv_area = (TextView) findViewById(R.id.address_tv_area);//区标题
        indicate_line = findViewById(R.id.main_indicate_line); //标题指示线
        mProvince = new AreaPager(this);
        mCity = new AreaPager(this);
        mArea = new AreaPager(this);
    }

    /** 网络URL,IP地址为自己的服务器Ip,需要自己的服务器 *///TODO 联网修改地址
    private String ADDRESS_AREA = "http://192.168.12.71:8080/ECServerz19/addressarea";

    /**
     * 根据地址Id请求网络，获取相应的地址数据
     * @param areaID 地址Id
     */
    private void requestNet(String areaID) {
        HttpUtils httpUtils = new HttpUtils(this,
                ADDRESS_AREA + "?request=" + TYPE + "&areaId=" + areaID, dealOnRequsetBackCall);
        httpUtils.start();
    }
    //TODO 本地资源获取，联网可删除
    private void insertAssets(final String fileName){
        new Thread(){
            @Override
            public void run() {
                try {
                    InputStream inputStream = getAssets().open(fileName);
                    final String stringFromStream = StreamUtils.getStringFromStream(inputStream);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseAssetsJson(stringFromStream);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    protected void initListener() {
        adapter = new AreaPagerAdapter();
        area_viewpager.setAdapter(adapter);//设置适配器
        area_viewpager.setOnPageChangeListener(dealOnPagerChangeListener);//添加页面改变监听
        tv_province.setOnClickListener(this);
        tv_city.setOnClickListener(this);
        tv_area.setOnClickListener(this);
    }

    /**
     * 设置指示线
     */
    @Override
    protected void initLineWidth() {
        //获取屏幕的宽，设置标题指示线的宽，根据fragments.size 等分
        if(pagers.size() == 1 || pagers.size() == 0){
            lineWidth = getResources().getDisplayMetrics().widthPixels;
            tv_city.setVisibility(View.GONE);
            tv_area.setVisibility(View.GONE);
        }else if(pagers.size() == 2){
            lineWidth = getResources().getDisplayMetrics().widthPixels / pagers.size();
            tv_city.setVisibility(View.VISIBLE);
            tv_area.setVisibility(View.GONE);
        }else {
            lineWidth = getResources().getDisplayMetrics().widthPixels / pagers.size();
            tv_city.setVisibility(View.VISIBLE);
            tv_area.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();//刷新适配器
        indicate_line.getLayoutParams().width = lineWidth;  //设置指示线长度
    }

    /**
     * json解析
     * @param json
     */
    protected void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String response = jsonObject.getString("response");
            if ("error".equals(response)) {
                Toast.makeText(getApplicationContext(), "服务器忙，请稍后再试",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();  //解析
                AreaJsonBean address = gson.fromJson(json, AreaJsonBean.class);
//分割线>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                if(address.province != null){  //加载 省列表Fragment
                    ArrayList<AddressArea> provinces = address.province;
                    if(pagers.size() == 0){
                        pagers.add(mProvince);
                    }else {
                        mProvince = pagers.get(0);
                    }
                    mProvince.setData(provinces);//设置数据
                    initLineWidth();//刷新viewPager适配器
                    mProvince.setOnItemClick(dealOnItemClickLdistener);
                    area_viewpager.setCurrentItem(0);
                }
//分割线>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                if(address.city != null){  //加载市列表Fragment
                    ArrayList<AddressArea> citys = address.city;
                    if(pagers.size() == 1){
                        pagers.add(mCity);
                    }else{
                        mCity = pagers.get(1);
                    }
                    mCity.setData(citys); //设置数据
                    initLineWidth();//刷新viewPager适配器
                    mCity.setOnItemClick(dealOnItemClickLdistener);
                    area_viewpager.setCurrentItem(1);
                }
//分割线>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                if(address.area != null){
                    ArrayList<AreaJsonBean.AddressArea> aeras = address.area;
                    if(pagers.size() == 2){
                        pagers.add(mArea);
                    }else{
                        mArea = pagers.get(2);
                    }
                    mArea.setData(aeras); //填充数据
                    initLineWidth();//刷新viewPager适配器
                    mArea.setOnItemClick(dealOnItemClickLdistener);
                    area_viewpager.setCurrentItem(2);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.address_tv_province: //省
                if(!(area_viewpager.getCurrentItem()  == 0)){ //如果当前不是省界面
                    area_viewpager.setCurrentItem(0); //切换省界面
                }
                break;
            case R.id.address_tv_city: //市
                if(!(area_viewpager.getCurrentItem()  == 1)){ //如果当前不是市界面
                    area_viewpager.setCurrentItem(1);  //切换市界面
                }
                break;
            case R.id.address_tv_area: //区，县
                if(!(area_viewpager.getCurrentItem()  == 2)){ //如果当前不是区，县界面
                    area_viewpager.setCurrentItem(2);  //切换区，县界面
                }
                break;
        }
    }
    /**
     * json解析本地
     * @param json
     *///TODO 本地资源获取，联网可删除
    protected void parseAssetsJson(String json) {
        try {
                Gson gson = new Gson();  //解析
                AreaJsonBean address = gson.fromJson(json, AreaJsonBean.class);
//分割线>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                if(address.province != null){  //加载 省列表Fragment
                    ArrayList<AddressArea> provinces = address.province;
                    Log.e("province====", provinces.toString());
                    if(pagers.size() == 0){
                        pagers.add(mProvince);
                    }else {
                        mProvince = pagers.get(0);
                    }
                    mProvince.setData(provinces);//设置数据
                    initLineWidth();//刷新viewPager适配器
                    mProvince.setOnItemClick(dealOnItemClickLdistener);
                    area_viewpager.setCurrentItem(0);
                }
//分割线>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                if(address.city != null){  //加载市列表Fragment
                    ArrayList<AddressArea> citys = address.city;
                    if(pagers.size() == 1){
                        pagers.add(mCity);
                    }else{
                        mCity = pagers.get(1);
                    }
                    mCity.setData(citys); //设置数据
                    initLineWidth();//刷新viewPager适配器
                    mCity.setOnItemClick(dealOnItemClickLdistener);
                    area_viewpager.setCurrentItem(1);
                }
//分割线>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                if(address.area != null){
                    ArrayList<AreaJsonBean.AddressArea> aeras = address.area;
                    if(pagers.size() == 2){
                        pagers.add(mArea);
                    }else{
                        mArea = pagers.get(2);
                    }
                    mArea.setData(aeras); //填充数据
                    initLineWidth();//刷新viewPager适配器
                    mArea.setOnItemClick(dealOnItemClickLdistener);
                    area_viewpager.setCurrentItem(2);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新UI
     */
    private void flashUI() {
        if(pagers.size() > 2){
                pagers.remove(2);
            }
        initLineWidth();  //刷新指示线
    }

    @Override
    public void finish() {
        // 声明意图对象
        Intent data = getIntent();
        // 把数据封装到意图对象内;intent中可以存放八大基本数据类型，序列化封装Bean，bundle对象
        data.putExtra("areaList", mAddressInfo);
        //当此Activity销毁时,会把这个意图对象返回至上一个Activity
        setResult(0, data);
        // 销毁当前Activity
        super.finish();
    }
    /**
     * viewPager适配器
     */
    private class AreaPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return pagers.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = pagers.get(position).getView();
            pagers.get(position).initListener();
            pagers.get(position).flashUI();
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}

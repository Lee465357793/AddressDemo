package com.example.lee.addressdemo.pager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;


import com.example.lee.addressdemo.bean.AreaJsonBean.AddressArea;

import java.util.ArrayList;
import java.util.List;

/**
 * 地址条目显示页面
 * Created by Administrator on 2016/11/11.
 */
public class AreaPager implements AdapterView.OnItemClickListener {

    private Context mContext;
    private List<AddressArea> mAreaList = new ArrayList<>();
    public ListView mListView;
    private BaseAdapter mAdapter;

    private DealOnItemClickListener dealOnItemClick;

    public AreaPager(Context context) {
        mContext = context;
    }
    /** 获取显示的布局view */
    public View getView(){
        mListView = new ListView(mContext);
        return mListView;
    }
    /** 设置控件监听 */
    public void initListener() {
        mAdapter = new ArrayAdapter<AddressArea>(mContext, android.R.layout.simple_list_item_1, mAreaList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        flashUI();
    }
    /** 条目点击事件 */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        if(dealOnItemClick != null){
            dealOnItemClick.dealOnItemClickListener(mAreaList, position);
        }
    }
    /** 加载更新数据 */
    public void setData(List addressList){
        mAreaList.clear();
        if (addressList != null){
            mAreaList.addAll(addressList);
        }
        Log.e("size", mAreaList.size()+"=============");
        flashUI();
    }
    /** 更新UI */
    public void flashUI(){
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged(); // 获取数据更新UI
        }
        Log.e("size", mAreaList.size()+"=============");
    }
    /** 设置条目点击事件 */
    public void setOnItemClick(DealOnItemClickListener dealOnItemClickListener){
        this.dealOnItemClick = dealOnItemClickListener;
    }
    /** 处理条目点击事件接口 */
    public interface DealOnItemClickListener{
        /**
         * 处理条目点击事件
         * @param areaList 当前ListView显示的集合
         * @param position 当前点击的条目
         */
        void dealOnItemClickListener(List areaList, int position);
    }

}

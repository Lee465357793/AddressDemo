package com.example.lee.addressdemo.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by leeyan on 2016/11/17.
 */
public class HttpUtils extends Thread{
    private Context mContent;
    /** 地址 */
    private String mUrl;
    /** 网络请求回调接口 */
    private ResquestBackCall mResquestBackCall;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                mResquestBackCall.onSucesss((String)msg.obj);
            }else {
                mResquestBackCall.onFailure((String)msg.obj);
            }
        }
    };

    public HttpUtils(Context content, String url, ResquestBackCall resquestBackCall) {
        this.mContent = content;
        this.mUrl = url;
        mResquestBackCall = resquestBackCall;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(mUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            int code = connection.getResponseCode();
            if(code == 200){ //请求成功
                InputStream is = connection.getInputStream();
                String json = StreamUtils.getStringFromStream(is);
                if(mResquestBackCall != null){
                    Message msg = new Message();
                    msg.obj = json;
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }else {  //2请求失败
                InputStream is = connection.getInputStream();
                String errorMsg = StreamUtils.getStringFromStream(is);
                if(mResquestBackCall != null){
                    Message msg = new Message();
                    msg.obj = errorMsg;
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /** 网络请求回调接口 */
    public interface ResquestBackCall{
        /**
         * 网络请求成功
         * @param json 获取的资源
         */
        public void onSucesss(String json);

        /**
         * 网络请求失败
         * @param errorMsg 错误信息
         */
        public void onFailure(String errorMsg);
    }
}

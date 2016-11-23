package com.example.lee.addressdemo.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 地址三级列表
 * Created by Administrator on 2016/11/11.
 */
public class AreaJsonBean implements Serializable {
    public ArrayList<AddressArea> province;
    public ArrayList<AddressArea> city;
    public ArrayList<AddressArea> area;

    public String response;
    public class AddressArea implements Serializable {
        public String id;
        public String value;
        public String areaID;
        public String father;

        @Override
        public String toString() {
            return value;
        }
    }

}

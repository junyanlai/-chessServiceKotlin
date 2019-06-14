package com.shine.controller.poker.Landlords.tool;

import com.alibaba.fastjson.JSON;

import java.util.List;

public class JSONUtil {
    /**
     * 对象转化为jSON字符串
     */
    public static String toJson(Object obj){
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * jSON字符串转化为集合
     */
    public static <T> List<T> toList(String s, Class<T> c){
        try {
            return JSON.parseArray(s,c);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 简单转换
     * 字符串转化为对象
     */
    public static <T> T toObj(String json, Class<T> cs) {
        try {
            return JSON.parseObject(json, cs);
        } catch (Exception e) {
            return null;
        }
    }
    public static void main(String[] arg){

    }
}

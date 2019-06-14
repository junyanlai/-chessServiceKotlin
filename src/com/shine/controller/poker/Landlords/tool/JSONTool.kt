package com.shine.controller.poker.Landlords.tool

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;

/**
 * 字符串 转换
 * @Gjq
 */
object JSONTool {
    /**
     * 对象转化为jSON字符串
     */
    fun toJson(obj: Any): String? {
        try {
            return JSON.toJSONString(obj)
        } catch (e: Exception) {
            return ""
        }

    }

    /**
     * jSON字符串转化为集合
     */
    fun <T> toList(s: String, c: Class<T>): MutableList<T>? {
        try {
            return JSON.parseArray(s,c)
        } catch (e: Exception) {
            return null
        }

    }
    /**
     * 根据泛型转换
     * @param json
     * @param type
     * @return
     */
    fun <T> toObj(json: String,c: Class<T>): T? {
        try {
            return JSON.parseObject(json,c)
        } catch (e: Exception) {
        }
        return null
    }
    /**
     * 简单转换
     * 字符串转化为对象
     */
    fun <T> toObj(s: String, c: Class<T>,  features: Feature): T? {
        try {
            return JSON.parseObject(s, c, features)
        } catch (e: Exception) {
            return null
        }

    }

    /**
     * 根据泛型转换
     * @param json
     * @param type
     * @return
     */
    fun <T> toObj(json: String, type: TypeReference<T>, features: Feature): T? {
        try {
            return JSON.parseObject(json, type, features)
        } catch (e: Exception) {
            return null
        }

    }

    /**
     * 根据泛型转换
     * @param json
     * @param type
     * @return
     */
    fun <T> toObj(json: String, type: JavaType<T>): T? {
        try {
            return JSON.parseObject(json, type.getType())
        } catch (e: Exception) {
            return null
        }

    }


    /**
     * 泛型类型设置
     * @param <T>
    </T> */
    class JavaType<T> : TypeReference<T>()
}
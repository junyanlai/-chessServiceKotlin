package com.shine.dao

import com.shine.amodel.Fmcc
import org.apache.ibatis.annotations.*

@Mapper
interface FmccMapper {


    //根据用户id 查询 所拥有的所有buf
    fun selectFmccByUid(fmcc: Fmcc): List<Fmcc>

    //根据用户类型查询
    fun selectFmccByType(type: String): List<Fmcc>

    fun selectFmcc(fmcc: Fmcc): List<Fmcc>
    //根据用户id 商品id 查询具体一个buf 的信息
    fun selectFmccOne(fmcc: Fmcc): Fmcc

    //添加用户buf信息
    fun insertFmcc(fmcc: Fmcc): Int

    //删除到期buf
    fun deleteFmcc(fmcc: Fmcc): Int

    //修改buf信息
    fun updateFmcc(fmcc: Fmcc): Int
}
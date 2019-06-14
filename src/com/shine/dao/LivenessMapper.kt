package com.shine.dao
import com.shine.amodel.Liveness
import org.apache.ibatis.annotations.Mapper

@Mapper
interface LivenessMapper {


    //获取奖励信息
    fun selectLiveness(liveness:Liveness): List<Liveness>

    //获取奖励信息
    fun selectLivenessCount(liveness:Liveness): Int

    //添加成就奖励信息
    fun insertLiveness(liveness:Liveness):Int


}
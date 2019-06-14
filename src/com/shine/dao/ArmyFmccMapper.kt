package com.shine.dao
import com.shine.amodel.ArmyFmcc
import org.apache.ibatis.annotations.Mapper
@Mapper
interface ArmyFmccMapper {

    //根据建筑id查询所有军团加成技能状态
    fun selectArmyFmcc(armyFmcc:ArmyFmcc):List<ArmyFmcc>
    //查询单条buf信息
    fun selectArmyFmccOne(armyFmcc:ArmyFmcc):ArmyFmcc
    //存入军团buff
    fun insertArmyFmcc(armyFmcc:ArmyFmcc):Int
    //修改buff
    fun updateArmyFmcc(armyFmcc:ArmyFmcc):Int
    //删除buff
    fun deleteArmyFmcc(armyFmcc:ArmyFmcc)
    //修改科技树等级
    fun updateTreeGrade(armyFmcc:ArmyFmcc)

    //查询状态等级对应的可升级等级
    fun selectArmyTemp(armyFmcc:ArmyFmcc):Int

}
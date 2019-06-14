package com.shine.dao
import com.shine.amodel.ArmyBuild
import org.apache.ibatis.annotations.Mapper
@Mapper
interface ArmyBuildMapper {

    //根据军团id 查询所有成员
    fun selectArmyBuildByArmyId(armyId:Int):List<ArmyBuild>
    //查询一个建筑
    fun selectArmyBuildOne(armyBuild:ArmyBuild):ArmyBuild
    //存入军团建筑
    fun insertArmyBuild(armyBuild:ArmyBuild)
    //修改建筑物信息
    fun updateArmyBuild(armyBuild:ArmyBuild)
    //删除建筑物信息
    fun deleteArmyBuild(armyBuild:ArmyBuild)

    //升级修改建筑物等级  type  level armyId
    fun updateArmyBuildByTemp(armyBuild:ArmyBuild)
}
package com.shine.aservice.army

import com.shine.amodel.ArmyBuild

interface IArmyBuildService {

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

    //升级修改建筑物等级
    fun updateArmyBuildByTemp(armyBuild:ArmyBuild)
}
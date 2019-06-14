package com.shine.dao

import com.shine.amodel.*
import org.apache.ibatis.annotations.*


@Mapper
interface ArmyAdminMapper {

    @Select("""select * from army""")
    fun selectAllArmyInfo(): List<Army>

    @Insert("""insert into army (adminid,level,num,icon,name,announcement,armyTitle,donate,donategem,competcoin,competgem,grain,createDate)
                        values (#{adminid},#{level},#{num},#{icon},#{name},#{announcement},#{armyTitle},#{donate},#{donategem},
                        #{competcoin},#{competgem},#{grain},now())""")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertOneArmy(army: Army): Int

    @Insert("""
insert into army_build
    (name, armyId, buildLevel, centreId, numberCoin, maxNumberCoin, numberExp, maxNumberExp, numberGem, maxNumberGem,type,count)
values(#{name},#{armyId},#{buildLevel},#{centreId},#{numberCoin},#{maxNumberCoin},#{numberExp},#{maxNumberExp},#{numberGem},#{maxNumberGem},#{type},#{count})""")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun createArmyBuild(armyBuild: ArmyBuild): Int

    @Select("""select * from army where name like "%"#{name}"%" """)
    fun selectNameByArmy(name: String): List<Army>

    @Select("""select * from army where id=#{id}""")
    fun getArmyInfo(id: Int): Army

    @Select("""select icon,name from army where id=#{id}""")
    fun armyByName(id: Int): armyByNmae

    @Select("""select * from army where id=#{id}""")
    fun getArmyInfoString(id: String): Army

    @Delete("delete from army where id=#{id}")
    fun deleteArmy(id: Int): Int

    @Select("""select * from building_formwork where type=#{type} and level=#{level}""")
    fun getBuildingFormwork(@Param("type") type: String, @Param("level") level: Int): BuildingFormwork

    @Insert("""insert into army_user (uid, status, name,icon, armyId, armyJob, donationCoin, orderId, donationGem, inerTime)
VALUES(#{uid},#{status},#{name},#{icon},#{armyId},#{armyJob},#{donationCoin},#{orderId},#{donationGem},now())""")
    fun insertArmyUser(armyUser: ArmyUser): Int

    @Delete("""delete from army_user where uid=#{uid} and armyId=#{armyId}""")
    fun deleteArmyUser(@Param("uid") uid: Int, @Param("armyId") armyId: Int): Int

    @Select("""select * from army_user where armyId=#{armyId} and status=2""")
    fun applyArmyList(armyId: Int): List<ArmyUser>

    @Update("""update army set donate=#{donate} where id=#{armyId}""")
    fun updateArmyDonate(@Param("donate") donate: Long, @Param("armyId") armyId: Int): Int

    @Update("""update army set donategem=#{donategem} where id=#{armyId}""")
    fun updateArmyDiamonds(@Param("donategem") donategem: Long, @Param("armyId") armyId: Int): Int

    @Select("""select  * from army_build where armyId=#{armyId}""")
    @Options(useCache = false, flushCache = Options.FlushCachePolicy.TRUE)
    fun getArmyBuiling(armyId: Int): List<ArmyBuild>

    @Update("""update army set competcoin=#{competcoin} where id=#{armyId}""")
    fun updateAarmyCoin(@Param("armyId") armyId: Int, @Param("competcoin") competcoin: Long): Int

    @Update("""update army set competgem=#{competgem} where id=#{armyId}""")
    fun updateAarmyDiamonds(@Param("competgem") competgem: Long, @Param("armyId") armyId: Int): Int

    @Update("""update army set icon=#{icon} where id=#{armyId}""")
    fun updateArmyIcon(@Param("icon") icon: String, @Param("armyId") armyId: Int): Int

    @Update("""update army set name=#{name} where id=#{armyId}""")
    fun updateArmyName(@Param("name") name: String, @Param("armyId") armyId: Int): Int

    @Update("""update army set num=#{currentNumberPeople} where id=#{armyId}""")
    fun updateCurrentNumberPeople(@Param("currentNumberPeople") currentNumberPeople: String, @Param("armyId") armyId: Int): Int

    @Update("""update army set announcement=#{announcement} where id=#{armyId}""")
    fun updateArmyAnnouncement(@Param("announcement") announcement: String, @Param("armyId") armyId: Int): Int

    @Update("""update army set grain=#{grain} where id=#{armyId}""")
    fun updategrain(@Param("grain") grain: Long, @Param("armyId") armyId: Int): Int

    @Select("""select * from army_user where uid=#{uid}""")
    fun userArmyInfo(uid: Int): ArmyUser

    @Select("""select armyId from army_user where uid=#{uid} and status=1""")
    fun isJoinArmy(uid: Int): List<String>

    @Select("""select armyId from army_user where uid=#{uid} and status=2""")
    fun repeatJoinArmy(uid: Int): List<Int>

    @Select("""select * from army_user where uid=#{uid}""")
    fun getArmyUser(uid: Int): List<ArmyUser>

    @Insert("""insert into army_fmcc(name,buildId,addition,level,type,number,fullgold,nextAddition)
        VALUES (#{name},#{buildId},#{addition},#{level},#{type},0,#{fullgold},#{nextAddition})""")
    fun insertArmyFmcc(armyFmcc: ArmyFmcc): Int

    @Select("""SELECT * FROM army_user WHERE armyId=#{armyId} and status=1 ORDER BY armyJob DESC """)
    fun getArmyAllPeople(armyId: Int): List<ArmyUser>

    @Update("""UPDATE  army_user SET armyJob=#{armyJob} WHERE armyId=#{armyId} and uid=#{uid} and status=1""")
    fun updatePosition(@Param("uid") uid: Int, @Param("armyId") armyId: Int, @Param("armyJob") armyJob: Int): Int

    @Update("""UPDATE army_user SET STATUS=1 WHERE uid=#{uid} and armyId=#{armyId}""")
    fun agreeJoinArmy(@Param("uid") uid: Int, @Param("armyId") armyId: Int): Int

    @Delete("""DELETE FROM army_user WHERE (uid=#{uid} AND armyId=#{armyId}) AND status=2""")
    fun ignoreJoinArmy(@Param("uid") uid: Int, @Param("armyId") armyId: Int): Int

    @Delete("""DELETE FROM army_user WHERE (uid=#{uid} AND armyId=#{armyId}) AND status=1""")
    fun proposeArmy(@Param("uid") uid: Int, @Param("armyId") armyId: Int): Int

    @Delete("""DELETE FROM army_user WHERE armyId=#{armyId}""")
    fun delAllArmyUser(armyId: Int)

    @Update("""update army set adminid=#{uid} where id=#{armyId}""")
    fun updateArmyUid(@Param("armyId") armyId: Int, @Param("uid") uid: Int): Int

    @Select("""SELECT name,icon,armyJob,donationCoin FROM army_user WHERE status=1 and armyId=#{armyId} ORDER BY  donationCoin DESC""")
    fun descCoin(armyId: Int): List<descCoin>

    @Select("""SELECT grain FROM army WHERE id=#{armyId}""")
    fun armyProvisions(armyId: Int): String

    @Insert("""INSERT INTO army_ranking (id,score,name,icon,armyTitle) VALUES(#{id},#{score},#{name},#{icon},#{armyTitle})""")
    fun insertRanking(r: ArmyRanking): Int

    @Select("""SELECT * from army_ranking ORDER BY score DESC""")
    fun queryAll(): List<ArmyRanking>

    @Update("""UPDATE  army_ranking SET score=#{score} WHERE id=#{id}""")
    fun updateRanking(@Param("id") id: Int, @Param("score") score: Long): Int

    @Select("""SELECT * from army_ranking where id=#{id}""")
    fun queryByArmyId(id: Int): ArmyRanking

    @Select("""SELECT armyId,numberExp FROM  army_build WHERE type='center' """)
    fun armyExp(): List<armyExp>

    @Select("""SELECT count(*)  FROM army_user WHERE armyId=#{armyId} AND  status=1""")
    fun currentNumberPeople(armyId: Int): Int

    @Select("""select * from army_user where uid=#{uid} and status=2""")
    fun repeatArmyUser(uid: Int): List<ArmyUser>

    @Delete("""DELETE FROM army_user WHERE id=#{id}""")
    fun delSurplusApplyUser(id: Int): Int

    @Select("""SELECT count(*) FROM army WHERE name=#{name}""")
    fun armyNameCheck(name: String): Int

    @Delete("""DELETE FROM army_build WHERE armyId=#{armyId}""")
    fun delArmyBuil(armyId: Int): Int

    @Select("""SELECT * FROM army_build WHERE armyId=#{armyId} AND type=#{type}""")
    fun queryArmyBuilType(@Param("armyId") armyId: Int, @Param("type") type: String): ArmyBuild

    @Delete("""DELETE FROM army_fmcc WHERE buildId=#{buildId}""")
    fun delArmyFmcc(buildId: Int): Int

    @Delete("""DELETE FROM army_order WHERE armyId=#{armyId}""")
    fun delArmyOrder(armyId: Int): Int

    @Delete("""DELETE FROM army_order WHERE uid=#{uid}""")
    fun delUserOrder(uid: Int): Int

    @Select("""SELECT * FROM army_action WHERE id=#{id}""")
    fun queryArmyAction(id: Int): ArmyAction

    @Update("""UPDATE army_action SET time=#{time},name=#{name},boo=#{boo},type=#{type} WHERE id=#{id}""")
    fun updateArmyAction(armyAction: ArmyAction): Int

    @Update("""UPDATE army_action SET time=#{time} WHERE id=#{id}""")
    fun updateArmyActionTime(@Param("id") id: Int, @Param("time") time: String): Int

    @Delete("""DELETE FROM army_ranking""")
    fun deleteArmyRankingAll(): Int


}
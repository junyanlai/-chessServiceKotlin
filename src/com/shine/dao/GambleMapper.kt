package com.shine.dao

import com.shine.amodel.Gamble
import com.shine.amodel.HangRecord
import org.apache.ibatis.annotations.*


@Mapper
interface GambleMapper {

    @Select("""SELECT * FROM gamble where type=#{type}""")
    fun queryAllRoom(type: Int): ArrayList<Gamble>

    @Select("""  SELECT * FROM gamble WHERE rid = #{rid}""")
    fun queryRid(rid: Int): Gamble

    @Update("""UPDATE gamble SET occupy= #{occupy} WHERE rid=#{rid}""")
    fun updateOccupy(@Param("rid") rid: Int, @Param("occupy") occupy: Int): Int

    @Update("""UPDATE gamble SET total=#{total},win=#{win},rate= #{rate} WHERE rid =#{rid}""")
    fun updateLhjRoom(robot: Gamble): Int

    @Update("""UPDATE gamble SET total=#{total},win=#{win},rate= #{rate},bb=#{bb},rr= #{rr},banker= #{banker} WHERE rid= #{rid}""")
    fun updateXcslData(robot: Gamble): Int

    @Insert("""INSERT INTO gamble (total, win, rate, type,occupy,bb, rr, banker) VALUES (#{total},#{win},#{rate},#{type},#{occupy},#{bb},#{rr},#{banker})""")
    fun insertGamble(gamble: Gamble): Int

    @Insert("""INSERT INTO hang_record(rid,uid,type,money,statr,end,profit,time,sign)VALUES(#{rid},#{uid},#{type},#{money},#{statr},#{end},#{profit},now(),#{sign})""")
    fun insertHangRecord(hangRecord: HangRecord): Int

    @Select("""  SELECT * FROM hang_record WHERE uid = #{uid} AND sign=1""")
    fun queryUid(uid: Int): List<HangRecord>

    @Delete(""" DELETE FROM hang_record WHERE id = #{id}""")
    fun deleteHangRecord(id: Int): Int

    @Update(""" UPDATE hang_record SET sign=2 WHERE id = #{id}""")
    fun updateHangRecordSign(id: Int): Int

    @Select("""  SELECT * FROM hang_record WHERE  sign=1""")
    fun queryOccupy(): List<HangRecord>

}
package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.Liveness

class LivenessDao : LivenessMapper {

    val session = getSessionFactory().openSession()
    val livenessMapper = session.getMapper(LivenessMapper::class.java)

    override fun selectLiveness(liveness: Liveness): List<Liveness> {
        val r = livenessMapper.selectLiveness(liveness)
        session.close()
        return r
    }

    override fun selectLivenessCount(liveness: Liveness): Int {
        val c = livenessMapper.selectLivenessCount(liveness)
        session.close()
        return c
    }

    override fun insertLiveness(liveness: Liveness): Int {
        val r = livenessMapper.insertLiveness(liveness)
        session.commit()
        session.close()
        return r
    }

}
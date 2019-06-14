package com.shine.aservice.user

import com.shine.amodel.Liveness
import com.shine.dao.LivenessDao

object LivenessService : ILivenessService {

    override fun selectLiveness(liveness: Liveness): List<Liveness> {
        return LivenessDao().selectLiveness(liveness)
    }

    override fun selectLivenessCount(liveness: Liveness): Int {
        return LivenessDao().selectLivenessCount(liveness)
    }

    override fun insertLiveness(liveness: Liveness): Int {
        LivenessDao().insertLiveness(liveness)
        return 1
    }


}
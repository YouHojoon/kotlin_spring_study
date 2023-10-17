package ac.kr.smu.prlab_server.repository

import ac.kr.smu.prlab_server.domain.MeasurementData
import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.util.RecentData

interface MeasurementDataSupport {
    fun findRecentData(user: User): RecentData
}
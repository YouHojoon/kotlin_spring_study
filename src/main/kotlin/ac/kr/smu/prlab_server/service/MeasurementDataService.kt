package ac.kr.smu.prlab_server.service

import ac.kr.smu.prlab_server.domain.MeasurementData
import ac.kr.smu.prlab_server.util.RecentData

interface MeasurementDataService {
    fun findById(id: Long): MeasurementData?
    fun findRecentData(): RecentData
    fun deleteById(id: Long)
}
package ac.kr.smu.prlab_server.service

import ac.kr.smu.prlab_server.util.RecentData

interface MeasurementDataService {
    fun findRecentData(): RecentData
}
package ac.kr.smu.prlab_server.service

import ac.kr.smu.prlab_server.domain.MeasurementData
import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.enums.Metric
import ac.kr.smu.prlab_server.enums.Period
import ac.kr.smu.prlab_server.util.MetricData
import ac.kr.smu.prlab_server.util.RecentData
import java.util.Date

interface MeasurementDataService {
    fun findById(userID: Long, id: Long): MeasurementData?
    fun findRecentData(userId:Long): RecentData
    fun deleteById(userId: Long, id: Long)
    fun findMetricDatasByPeriodAndDate(userId:Long, metric:Metric, period: Period, date: Date): List<MetricData>
}
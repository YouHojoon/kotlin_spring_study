package ac.kr.smu.prlab_server.service

import ac.kr.smu.prlab_server.domain.MeasurementData
import ac.kr.smu.prlab_server.enum.Metric
import ac.kr.smu.prlab_server.enum.Period
import ac.kr.smu.prlab_server.util.MetricData
import ac.kr.smu.prlab_server.util.RecentData
import java.util.Date

interface MeasurementDataService {
    fun findById(id: Long): MeasurementData?
    fun findRecentData(): RecentData
    fun deleteById(id: Long)
    fun findMetricDatasByPeriodAndDate(metric:Metric, period: Period, date: Date): List<MetricData>
}
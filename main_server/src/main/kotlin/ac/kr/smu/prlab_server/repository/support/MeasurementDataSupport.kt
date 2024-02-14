package ac.kr.smu.prlab_server.repository.support

import ac.kr.smu.prlab_server.domain.MeasurementData
import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.enums.Metric
import ac.kr.smu.prlab_server.util.MetricData
import ac.kr.smu.prlab_server.util.RecentData
import ac.kr.smu.prlab_server.enums.Period
import ac.kr.smu.prlab_server.util.MetricDataValueType
import com.querydsl.core.Tuple
import java.util.*

interface MeasurementDataSupport {
    fun findRecentData(userId:Long): RecentData
    fun findMetricDatasByPeriodAndDate(userId:Long, metric: Metric, period: Period, date: Date): List<MetricData>
}
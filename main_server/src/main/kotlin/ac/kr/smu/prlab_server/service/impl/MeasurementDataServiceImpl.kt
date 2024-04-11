package ac.kr.smu.prlab_server.service.impl

import ac.kr.smu.prlab_server.domain.FaceMeasurementData
import ac.kr.smu.prlab_server.domain.FingerMeasurementData
import ac.kr.smu.prlab_server.domain.MeasurementData
import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.enums.Expression
import ac.kr.smu.prlab_server.enums.MeasurementTarget
import ac.kr.smu.prlab_server.enums.Metric
import ac.kr.smu.prlab_server.enums.Period
import ac.kr.smu.prlab_server.exception.MeasurementDataPermissionException
import ac.kr.smu.prlab_server.repository.MeasurementDataRepository
import ac.kr.smu.prlab_server.service.MeasurementDataService
import ac.kr.smu.prlab_server.util.*
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.HashMap
import kotlin.jvm.optionals.getOrNull

@Service
class MeasurementDataServiceImpl(
    private val repo: MeasurementDataRepository
): MeasurementDataService {
    override fun findRecentData(userId:Long): RecentData {
        return repo.findRecentData(userId)
    }

    override fun findById(userId: Long, id: Long): MeasurementData? {
        val data = repo.findById(id).getOrNull() ?: return null
        if(data.user.uid != userId)
            throw MeasurementDataPermissionException()
        return data
    }

    override fun deleteById(userID: Long, id: Long) {
        val data = repo.findById(id).getOrNull()

        checkNotNull(data){"데이터가 존재하지 않습니다."}
        if (data.user.uid != userID)
            throw MeasurementDataPermissionException()

        repo.deleteById(id)
    }

    override fun findMetricDatasByPeriodAndDate(userId:Long, metric: Metric, period: Period, date: Date): List<MetricData> {
        return repo
            .findMetricDatasByPeriodAndDate(userId,metric,period,date)
    }
}
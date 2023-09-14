package ac.kr.smu.prlab_server.service.impl

import ac.kr.smu.prlab_server.domain.FaceMeasurementData
import ac.kr.smu.prlab_server.domain.FingerMeasurementData
import ac.kr.smu.prlab_server.domain.MeasurementData
import ac.kr.smu.prlab_server.enum.Expression
import ac.kr.smu.prlab_server.enum.MeasurementTarget
import ac.kr.smu.prlab_server.enum.Metric
import ac.kr.smu.prlab_server.enum.Period
import ac.kr.smu.prlab_server.repository.MeasurementDataRepository
import ac.kr.smu.prlab_server.service.MeasurementDataService
import ac.kr.smu.prlab_server.util.*
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.HashMap
import kotlin.jvm.optionals.getOrNull

@Service
class MeasurementDataServiceImpl(
    private val repo: MeasurementDataRepository
): MeasurementDataService {
    override fun findRecentData(): RecentData {
        val pageable = PageRequest.of(0,1)

        val faceMeasurementDataList = repo.findByTargetOrderByMeasurementDateDesc(MeasurementTarget.FACE, pageable).content
        val fingerMeasurementDataList = repo.findByTargetOrderByMeasurementDateDesc(MeasurementTarget.FINGER, pageable).content

        when{
            faceMeasurementDataList.isEmpty() && fingerMeasurementDataList.isEmpty() -> return RecentData()
            fingerMeasurementDataList.isEmpty() -> {
                val data = faceMeasurementDataList.first() as FaceMeasurementData
                return RecentData(data)
            }
            faceMeasurementDataList.isEmpty() -> {
                val data = fingerMeasurementDataList.first() as FingerMeasurementData
                return RecentData(data)
            }
            else -> {
                val faceData = faceMeasurementDataList.first() as FaceMeasurementData
                val fingerData = fingerMeasurementDataList.first() as FingerMeasurementData
                return RecentData(faceData,fingerData)
            }
        }
    }

    override fun findById(id: Long): MeasurementData? {
        return repo.findById(id).getOrNull()
    }

    override fun deleteById(id: Long) {
        repo.deleteById(id)
    }

    override fun findMetricDatasByPeriodAndDate(metric: Metric, period: Period, date: Date): List<MetricData> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val startDateTime = when(period){
            Period.WEEK -> LocalDateTime.of(localDate.minusDays(7), LocalTime.of(0,0,0))
            Period.MONTH -> LocalDateTime.of(localDate.minusMonths(1), LocalTime.of(0,0,0))
            Period.YEAR -> LocalDateTime.of(localDate.minusYears(1), LocalTime.of(0,0,0))
        }
        val endDateTime = LocalDateTime.of(localDate, LocalTime.of(23,59,59))

        return repo
            .findAllByMeasurementDateBetween(startDateTime, endDateTime)
            .groupBy {
                when(period){
                    Period.WEEK -> it.measurementDate.dayOfWeek.value
                    Period.MONTH -> ChronoUnit.DAYS.between(endDateTime, it.measurementDate) / 7
                    Period.YEAR -> it.measurementDate.month.value
                }
            }
            .map { it.value }
            .mapNotNull {
                mapMeasurementDatasToMetricDataValueType(metric, it)
            }
    }


    private fun mapMeasurementDatasToMetricDataValueType(metric: Metric, datas: List<MeasurementData>): MetricData{
        val date = datas.maxOf { it.measurementDate }.toLocalDate()
        return when(metric){
            Metric.BPM -> {
                val bpms = datas.map { it.bpm }

                MetricData(MinMaxData(bpms.min(), bpms.max()),date)
            }
            Metric.SpO2 -> {
                val SpO2s = datas.map { it.SpO2 }

                MetricData(MinMaxData(SpO2s.min(), SpO2s.max()),date)
            }
            Metric.RR -> {
                val RRs = datas.map { it.RR }
                MetricData(MinMaxData(RRs.min(), RRs.max()),date)
            }
            Metric.STRESS -> {
                val stresses = datas.map { it.stress }

                MetricData(MinMaxData(stresses.min(), stresses.max()),date)
            }
            Metric.BMI -> {
                val BMIs = datas.mapNotNull {(it as? FaceMeasurementData)?.BMI}

                MetricData(MinMaxData(BMIs.min(), BMIs.max()),date)
            }
            Metric.EXPRESSION_ANALYSIS -> {
                val faceMeasurementDatas = datas.mapNotNull { it as? FaceMeasurementData }
                val map = HashMap<Expression,Int>()
                var minValence = 0f
                var maxValence = 0f
                var minArousal = 0f
                var maxArousal = 0f

                faceMeasurementDatas.forEach {
                    if(minValence == 0f || minValence > it.valence)
                        minValence = it.valence
                    if(maxValence == 0f || maxValence < it.valence)
                        maxValence = it.valence
                    if(minArousal == 0f || minArousal > it.arousal)
                        minArousal = it.arousal
                    if (maxArousal == 0f || maxArousal < it.arousal)
                        maxArousal = it.arousal

                    map[it.expression] = map[it.expression] ?: 0 + 1
                }

                MetricData(
                    ExpressionAnalysisMetricDataValueType(MinMaxData(minValence, maxArousal), MinMaxData(minArousal,maxArousal)
                        ,ExpressionMetricData(map)), date
                )
            }
            Metric.BLOOD_PRESSURE -> {
                val fingerMeasurementDatas = datas
                    .mapNotNull { it as? FingerMeasurementData }
                var minSYS= 0
                var maxSYS = 0
                var minDIA = 0
                var maxDIA = 0

                fingerMeasurementDatas.forEach {
                    if(minSYS == 0 || minSYS > it.SYS)
                        minSYS = it.SYS
                    if(maxSYS == 0 || maxSYS < it.SYS)
                        maxSYS = it.SYS
                    if(minDIA == 0 || minDIA > it.DIA)
                        minDIA = it.DIA
                    if (maxDIA == 0 || maxDIA < it.DIA)
                        maxDIA = it.DIA
                }

                MetricData( BloodPressureMetricDataValueType(MinMaxData(minSYS, maxSYS), MinMaxData(minDIA, maxDIA)), date)
            }
            Metric.BLOOD_SUGAR -> {
                val bloodSugars = datas.mapNotNull {it as? FingerMeasurementData}.map { it.bloodSugar }

                MetricData(MinMaxData(bloodSugars.min(), bloodSugars.max()), date)
            }
        }
    }
}
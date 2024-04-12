package ac.kr.smu.prlab_server.repository.support.impl


import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import ac.kr.smu.prlab_server.domain.QMeasurementData.measurementData
import ac.kr.smu.prlab_server.domain.QFingerMeasurementData.fingerMeasurementData
import ac.kr.smu.prlab_server.domain.QFaceMeasurementData.faceMeasurementData
import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.enums.Expression
import ac.kr.smu.prlab_server.enums.Metric
import ac.kr.smu.prlab_server.enums.Period
import ac.kr.smu.prlab_server.repository.support.MeasurementDataSupport
import ac.kr.smu.prlab_server.util.*
import com.querydsl.core.Tuple
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class MeasurementDataSupportImpl(
    private val queryFactory: JPAQueryFactory
): MeasurementDataSupport {
    override fun findRecentData(userID:Long): RecentData {
        val fingerMeasurementData = queryFactory
            .selectFrom(fingerMeasurementData)
            .where(fingerMeasurementData.user.userID.eq(userID))
            .orderBy(fingerMeasurementData.measurementDate.desc())
            .limit(1)
            .fetchOne()

        val faceMeasurementData = queryFactory
            .selectFrom(faceMeasurementData)
            .where(faceMeasurementData.user.userID.eq(userID))
            .orderBy(faceMeasurementData.measurementDate.desc())
            .limit(1)
            .fetchOne()

        when{
            fingerMeasurementData == null && faceMeasurementData == null -> return RecentData()
            fingerMeasurementData == null -> return RecentData(faceMeasurementData!!)
            faceMeasurementData == null -> return RecentData(fingerMeasurementData!!)
            else -> return RecentData(faceMeasurementData!!, fingerMeasurementData!!)
        }
    }
    override fun findMetricDatasByPeriodAndDate(userID:Long, metric: Metric, period: Period, date: Date): List<MetricData> {
        val baseQuery = findMetricDatasOfBaseQuery(metric)
        val (startDateTime, endDateTime) = calcStartAndEndDateTime(period,date)
        return baseQuery
            .where(measurementData.user.userID.eq(userID), measurementData.measurementDate.between(startDateTime, endDateTime))
            .fetch()
            .groupBy {
                val measurementDate = it.get(measurementData.measurementDate)!!
                when(period){
                    Period.WEEK -> measurementDate.dayOfWeek.value
                    Period.MONTH ->  (ChronoUnit.DAYS.between(endDateTime, measurementDate) / 7).toInt()
                    Period.YEAR -> measurementDate.month.value
                }
            }
            .map { it.value }
            .map { mapMeasurementDatasToMetricDataValueType(metric,it) }
    }
    private fun calcStartAndEndDateTime(period: Period, date: Date): Pair<LocalDateTime, LocalDateTime>{
        val calendar = Calendar.getInstance()
        calendar.time = date
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val startDateTime = when(period){
            Period.WEEK -> LocalDateTime.of(localDate.minusDays(7), LocalTime.of(0,0,0))
            Period.MONTH -> LocalDateTime.of(localDate.minusMonths(1), LocalTime.of(0,0,0))
            Period.YEAR -> LocalDateTime.of(localDate.minusYears(1), LocalTime.of(0,0,0))
        }
        val endDateTime = LocalDateTime.of(localDate, LocalTime.of(23,59,59))

        return Pair(startDateTime, endDateTime)
    }
    private fun findMetricDatasOfBaseQuery(metric: Metric) = when(metric){
        Metric.BPM -> queryFactory
            .select(measurementData.bpm, measurementData.measurementDate)
            .from(measurementData)
        Metric.SpO2 -> queryFactory
            .select(measurementData.SpO2, measurementData.measurementDate)
            .from(measurementData)
        Metric.RR -> queryFactory
            .select(measurementData.RR, measurementData.measurementDate)
            .from(measurementData)
        Metric.STRESS -> queryFactory
            .select(measurementData.stress, measurementData.measurementDate)
            .from(measurementData)
        Metric.BMI -> queryFactory
            .select(faceMeasurementData.BMI, measurementData.measurementDate)
            .from(measurementData)
            .innerJoin(faceMeasurementData)
            .on(measurementData.id.eq(faceMeasurementData.id))
        Metric.EXPRESSION_ANALYSIS -> queryFactory
            .select(faceMeasurementData.valence, faceMeasurementData.arousal, faceMeasurementData.expression, measurementData.measurementDate)
            .from(measurementData)
            .innerJoin(faceMeasurementData)
            .on(measurementData.id.eq(faceMeasurementData.id))
        Metric.BLOOD_PRESSURE -> queryFactory
            .select(fingerMeasurementData.SYS, fingerMeasurementData.DIA, measurementData.measurementDate).from(measurementData)
            .innerJoin(fingerMeasurementData)
            .on(measurementData.id.eq(fingerMeasurementData.id))
        Metric.BLOOD_SUGAR -> queryFactory
            .select(fingerMeasurementData.bloodSugar, measurementData.measurementDate).from(measurementData)
            .innerJoin(fingerMeasurementData)
            .on(measurementData.id.eq(fingerMeasurementData.id))
    }
    private fun mapMeasurementDatasToMetricDataValueType(metric: Metric, datas: List<Tuple>): MetricData{
        val date = datas.maxOf { it.get(measurementData.measurementDate)!!}.toLocalDate()
        return when(metric){
            Metric.BPM -> {
                val bpms = datas.map { it.get(measurementData.bpm) }.filterNotNull()
                MetricData(MinMaxData(bpms.min(), bpms.max()),date)
            }
            Metric.SpO2 -> {
                val SpO2s = datas.map { it.get(measurementData.SpO2) }.filterNotNull()
                MetricData(MinMaxData(SpO2s.min(), SpO2s.max()),date)
            }
            Metric.RR -> {
                val RRs =datas.map { it.get(measurementData.RR) }.filterNotNull()
                MetricData(MinMaxData(RRs.min(), RRs.max()),date)
            }
            Metric.STRESS -> {
                val stresses = datas.map { it.get(measurementData.stress) }.filterNotNull()

                MetricData(MinMaxData(stresses.min(), stresses.max()),date)
            }
            Metric.BMI -> {
                val BMIs = datas.map { it.get(faceMeasurementData.BMI) }.filterNotNull()

                MetricData(MinMaxData(BMIs.min(), BMIs.max()),date)
            }
            Metric.EXPRESSION_ANALYSIS -> {
                val map = HashMap<Expression,Int>()
                var minValence = 0f
                var maxValence = 0f
                var minArousal = 0f
                var maxArousal = 0f

                datas.forEach {
                    val valence = it.get(faceMeasurementData.valence) ?: 0f
                    val arousal = it.get(faceMeasurementData.arousal) ?: 0f
                    val expression = it.get(faceMeasurementData.expression)!!

                    if(minValence == 0f || minValence > valence)
                        minValence = valence
                    if(maxValence == 0f || maxValence < valence)
                        maxValence = valence
                    if(minArousal == 0f || minArousal > arousal)
                        minArousal = arousal
                    if (maxArousal == 0f || maxArousal < arousal)
                        maxArousal = arousal

                    map[expression] = map[expression] ?: 0 + 1
                }

                MetricData(
                    ExpressionAnalysisMetricDataValueType(
                        MinMaxData(minValence, maxValence),
                        MinMaxData(minArousal,maxArousal)
                        , ExpressionMetricData(map)
                    ), date
                )
            }
            Metric.BLOOD_PRESSURE -> {
                var minSYS= 0
                var maxSYS = 0
                var minDIA = 0
                var maxDIA = 0

                datas.forEach {
                    val SYS = it.get(fingerMeasurementData.SYS) ?: 0
                    val DIA = it.get(fingerMeasurementData.DIA) ?: 0

                    if(minSYS == 0 || minSYS > SYS)
                        minSYS = SYS
                    if(maxSYS == 0 || maxSYS < SYS)
                        maxSYS = SYS
                    if(minDIA == 0 || minDIA > DIA)
                        minDIA = DIA
                    if (maxDIA == 0 || maxDIA < DIA)
                        maxDIA = DIA
                }

                MetricData( BloodPressureMetricDataValueType(MinMaxData(minSYS, maxSYS), MinMaxData(minDIA, maxDIA)), date)
            }
            Metric.BLOOD_SUGAR -> {
                val bloodSugars = datas.map { it.get(fingerMeasurementData.bloodSugar) }.filterNotNull()

                MetricData(MinMaxData(bloodSugars.min(), bloodSugars.max()), date)
            }
        }
    }
}
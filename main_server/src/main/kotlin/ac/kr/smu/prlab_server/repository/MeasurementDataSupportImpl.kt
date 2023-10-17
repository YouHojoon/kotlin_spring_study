package ac.kr.smu.prlab_server.repository


import ac.kr.smu.prlab_server.util.RecentData
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import ac.kr.smu.prlab_server.domain.QFingerMeasurementData.fingerMeasurementData
import ac.kr.smu.prlab_server.domain.QFaceMeasurementData.faceMeasurementData
import ac.kr.smu.prlab_server.domain.User

@Component
class MeasurementDataSupportImpl(
    private val queryFactory: JPAQueryFactory
): MeasurementDataSupport {
    override fun findRecentData(user:User): RecentData {
        val fingerMeasurementData = queryFactory
            .selectFrom(fingerMeasurementData)
            .where(fingerMeasurementData.user.uid.eq(user.uid))
            .orderBy(fingerMeasurementData.measurementDate.desc())
            .limit(1)
            .fetchOne()

        val faceMeasurementData = queryFactory
            .selectFrom(faceMeasurementData)
            .where(faceMeasurementData.user.uid.eq(user.uid))
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
}
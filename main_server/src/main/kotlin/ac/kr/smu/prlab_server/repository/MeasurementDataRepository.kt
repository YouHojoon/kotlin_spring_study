package ac.kr.smu.prlab_server.repository

import ac.kr.smu.prlab_server.domain.MeasurementData
import ac.kr.smu.prlab_server.repository.support.MeasurementDataSupport
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface MeasurementDataRepository: JpaRepository<MeasurementData,Long>, MeasurementDataSupport {
    fun findAllByMeasurementDateBetween(start: LocalDateTime, end: LocalDateTime): List<MeasurementData>
}
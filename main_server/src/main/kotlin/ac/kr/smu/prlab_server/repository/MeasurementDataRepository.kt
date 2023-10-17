package ac.kr.smu.prlab_server.repository

import ac.kr.smu.prlab_server.domain.MeasurementData
import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.enums.MeasurementTarget
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface MeasurementDataRepository: JpaRepository<MeasurementData,Long>, MeasurementDataSupport{

    fun findByUserAndTargetOrderByMeasurementDateDesc(user:User, target: MeasurementTarget, pageable: Pageable): Slice<MeasurementData>

    fun findByTargetOrderByMeasurementDateDesc(target: MeasurementTarget, pageable: Pageable): Slice<MeasurementData>
    fun findAllByMeasurementDateBetween(start: LocalDateTime, end: LocalDateTime): List<MeasurementData>
}
package ac.kr.smu.prlab_server.domain

import ac.kr.smu.prlab_server.enums.MeasurementTarget
import com.fasterxml.jackson.annotation.JsonGetter
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.PrimaryKeyJoinColumn
import java.time.LocalDateTime

@Entity
@PrimaryKeyJoinColumn(name="id")
@DiscriminatorValue("FINGER")
class FingerMeasurementData(
    bpm: Int,
    SpO2: Int,
    RR: Int,
    stress: Int,
    measurementDate: LocalDateTime,
    confidence: Float,
    user: User,

    @get:JsonGetter("SYS")
    @Column(updatable = false, nullable = false)
    val SYS: Int,

    @get:JsonGetter("DIA")
    @Column(updatable = false, nullable = false)
    val DIA: Int,

    @Column(updatable = false, nullable = false)
    val bloodSugar: Int
): MeasurementData(bpm,SpO2,RR,stress, measurementDate, confidence, MeasurementTarget.FINGER,user){

}
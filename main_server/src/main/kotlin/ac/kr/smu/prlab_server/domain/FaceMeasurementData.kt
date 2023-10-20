package ac.kr.smu.prlab_server.domain

import ac.kr.smu.prlab_server.enums.Expression
import ac.kr.smu.prlab_server.enums.MeasurementTarget
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@PrimaryKeyJoinColumn(name="id")
@DiscriminatorValue("FACE")
class FaceMeasurementData(
    bpm: Int,
    SpO2: Int,
    RR: Int,
    stress: Int,
    measurementDate: LocalDateTime,
    confidence:Float,
    user: User,

    @Column(updatable = false, nullable = false)
    val BMI : Int,

    @Column(updatable = false, nullable = false)
    val arousal: Float,

    @Column(updatable = false, nullable = false)
    val valence: Float,

    @Column(updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    val expression: Expression
): MeasurementData(bpm,SpO2,RR,stress,measurementDate, confidence, MeasurementTarget.FACE,user) {

}
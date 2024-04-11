package ac.kr.smu.prlab_server.domain

import ac.kr.smu.prlab_server.enums.Expression
import ac.kr.smu.prlab_server.enums.MeasurementTarget
import com.fasterxml.jackson.annotation.JsonGetter
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime

@Entity
@PrimaryKeyJoinColumn(name="id")
@DiscriminatorValue("FACE")
@OnDelete(action = OnDeleteAction.CASCADE)
class FaceMeasurementData(
    bpm: Int,
    SpO2: Int,
    RR: Int,
    stress: Int,
    measurementDate: LocalDateTime,
    confidence:Float,
    user: User,

    @get:JsonGetter("BMI")
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
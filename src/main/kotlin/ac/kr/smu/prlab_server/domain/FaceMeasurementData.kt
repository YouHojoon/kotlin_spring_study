package ac.kr.smu.prlab_server.domain

import ac.kr.smu.prlab_server.enum.MeasurementTarget
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@PrimaryKeyJoinColumn(name="id")
class FaceMeasurementData(
    bpm: Int,
    SpO2: Int,
    RR: Int,
    stressIndex: Int,
    measurementDate: Timestamp,
    confidence:Float,

    @Column(updatable = false, nullable = false)
    val BMI : Int,

    @Column(updatable = false, nullable = false)
    val arousal: Float,

    @Column(updatable = false, nullable = false)
    val valence: Float,

    @Column(updatable = false, nullable = false)
    val expression: String
): MeasurementData(bpm,SpO2,RR,stressIndex,measurementDate, confidence) {

}
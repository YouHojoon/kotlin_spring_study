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
    date: Timestamp,

    @Column
    val BMI : Int,

    @Column
    val arousal: Float,

    @Column
    val valence: Float,

    @Column
    val expression: String
): MeasurementData(bpm,SpO2,RR,stressIndex,date) {

}
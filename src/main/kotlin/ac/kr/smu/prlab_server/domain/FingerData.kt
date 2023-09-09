package ac.kr.smu.prlab_server.domain

import ac.kr.smu.prlab_server.enum.MeasurementTarget
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.PrimaryKeyJoinColumn
import java.sql.Timestamp

@Entity
@PrimaryKeyJoinColumn(name="id")
class FingerData(
    bpm: Int,
    SpO2: Int,
    RR: Int,
    stressIndex: Int,
    date: Timestamp,

    @Column
    val SYS: Int,

    @Column
    val DIA: Int,

    @Column
    val bloodSugar: Int
): MeasurementData(bpm,SpO2,RR,stressIndex, date){

}
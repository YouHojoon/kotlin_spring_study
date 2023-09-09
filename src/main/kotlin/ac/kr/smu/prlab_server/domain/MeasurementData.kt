package ac.kr.smu.prlab_server.domain

import ac.kr.smu.prlab_server.annotation.AllOpen

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "target")
@AllOpen
abstract class MeasurementData(
    @Column
    val bpm: Int,

    @Column
    val SpO2: Int,

    @Column
    val RR: Int,

    @Column
    val stressIndex: Int,

    @Column
    val date: Timestamp,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long = 0L
) {


}
package ac.kr.smu.prlab_server.domain

import ac.kr.smu.prlab_server.annotation.AllOpen
import ac.kr.smu.prlab_server.enums.MeasurementTarget

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "target")
@AllOpen
abstract class MeasurementData(
    @Column(updatable = false, nullable = false)
    val bpm: Int,

    @Column(updatable = false, nullable = false)
    val SpO2: Int,

    @Column(updatable = false, nullable = false)
    val RR: Int,

    @Column(updatable = false, nullable = false)
    val stress: Int,

    @Column(updatable = false, nullable = false)
    val measurementDate: LocalDateTime,

    @Column(updatable = false, nullable = false)
    val confidence: Float,

    @Column(insertable = false,updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    val target: MeasurementTarget,


    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private val user: User,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long = 0L
) {


}
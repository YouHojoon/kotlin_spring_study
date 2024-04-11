package ac.kr.smu.prlab_server.domain

import ac.kr.smu.prlab_server.annotation.AllOpen
import ac.kr.smu.prlab_server.enums.MeasurementTarget
import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore

import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import java.time.LocalDateTime

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "target")
@AllOpen
abstract class MeasurementData(
    @Column(updatable = false, nullable = false)
    val bpm: Int,

    @get:JsonGetter("SpO2")
    @Column(updatable = false, nullable = false)
    val SpO2: Int,

    @get:JsonGetter("RR")
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


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User,

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long = 0L
) {


}
package ac.kr.smu.prlab_server.util

import java.time.LocalDate

sealed class MetricDataValueType
data class MetricData(
    val value: MetricDataValueType,
    val basisDate: LocalDate
)


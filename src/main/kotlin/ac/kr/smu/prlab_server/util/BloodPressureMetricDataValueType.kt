package ac.kr.smu.prlab_server.util

data class BloodPressureMetricDataValueType(
    val SYS: MinMaxData<Int>,
    val DIA: MinMaxData<Int>
): MetricDataValueType()
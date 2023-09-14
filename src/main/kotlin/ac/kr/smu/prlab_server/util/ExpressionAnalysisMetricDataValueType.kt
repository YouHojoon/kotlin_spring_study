package ac.kr.smu.prlab_server.util

data class ExpressionAnalysisMetricDataValueType(
    val valence: MinMaxData<Float>,
    val arousal: MinMaxData<Float>,
    val expression: ExpressionMetricData
):MetricDataValueType() {

}
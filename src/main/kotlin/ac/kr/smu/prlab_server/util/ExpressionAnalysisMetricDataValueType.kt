package ac.kr.smu.prlab_server.util

data class ExpressionAnalysisMetricDataValueType(
    val valence: MinMaxData<Float>,
    val arousal: MinMaxData<Float>,
    //val expression: Expression
):MetricDataValueType() {
    inner class Expression(
        val neutral: Float,
        val happy : Float,
        val smile: Float,
        val fear: Float,
        val angry: Float,
        val disgust: Float,
        val contempt: Float
    )
}
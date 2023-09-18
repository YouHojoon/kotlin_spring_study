package ac.kr.smu.prlab_server.util

import ac.kr.smu.prlab_server.enum.Expression
import kotlin.math.E

class ExpressionMetricData{
    val neutral: Float
    val happy : Float
    val sad: Float
    val fear: Float
    val angry: Float
    val disgust: Float
    val contempt: Float

    constructor(neutral: Float, happy: Float, sad: Float, fear: Float, angry: Float, disgust: Float, contempt: Float){
        this.neutral = neutral
        this.happy = happy
        this.sad = sad
        this.fear = fear
        this.angry = angry
        this.disgust = disgust
        this.contempt = contempt
    }
    constructor(map: Map<Expression, Int>){
        val size = map.values.sum()
        val avg = map.map { it.key to ((it.value as Float) / size) }.toMap()

        this.neutral = avg[Expression.NEUTRAL] ?: 0f
        this.happy = avg[Expression.HAPPY] ?: 0f
        this.sad = avg[Expression.SAD] ?: 0f
        this.fear = avg[Expression.FEAR] ?: 0f
        this.angry = avg[Expression.ANGRY] ?: 0f
        this.disgust = avg[Expression.DISGUST] ?: 0f
        this.contempt = avg[Expression.CONTEMPT] ?: 0f
    }
}
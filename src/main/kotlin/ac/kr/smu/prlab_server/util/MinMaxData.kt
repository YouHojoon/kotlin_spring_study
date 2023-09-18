package ac.kr.smu.prlab_server.util

data class MinMaxData<T> (
    val min: T,
    val max: T
): MetricDataValueType() where T: Number, T:Comparable<T>{
    init {
        if (min > max){
            throw IllegalArgumentException("min이 max보다 클 수 없습니다.")
        }
    }
}
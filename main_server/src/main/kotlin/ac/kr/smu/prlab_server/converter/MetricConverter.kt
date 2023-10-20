package ac.kr.smu.prlab_server.converter

import ac.kr.smu.prlab_server.enums.Metric
import org.springframework.core.convert.converter.Converter

class MetricConverter: Converter<String, Metric> {
    override fun convert(source: String): Metric? {
        return Metric.values().firstOrNull { it.value == source}
    }
}
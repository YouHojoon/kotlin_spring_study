package ac.kr.smu.prlab_server.converter

import ac.kr.smu.prlab_server.enums.Period
import org.springframework.core.convert.converter.Converter

class PeriodConverter: Converter<String, Period> {
    override fun convert(source: String): Period? {
        return Period.values().firstOrNull { source == it.value }
    }
}
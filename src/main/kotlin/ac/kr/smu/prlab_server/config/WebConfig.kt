package ac.kr.smu.prlab_server.config

import ac.kr.smu.prlab_server.converter.MetricConverter
import ac.kr.smu.prlab_server.converter.PeriodConverter
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig: WebMvcConfigurer {
    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(MetricConverter())
        registry.addConverter(PeriodConverter())
    }
}
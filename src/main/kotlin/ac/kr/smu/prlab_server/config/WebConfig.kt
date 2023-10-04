package ac.kr.smu.prlab_server.config

import ac.kr.smu.prlab_server.converter.MetricConverter
import ac.kr.smu.prlab_server.converter.PeriodConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig: WebMvcConfigurer {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
    
    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(MetricConverter())
        registry.addConverter(PeriodConverter())
    }
}
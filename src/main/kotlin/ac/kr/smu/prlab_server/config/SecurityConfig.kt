package ac.kr.smu.prlab_server.config

import ac.kr.smu.prlab_server.jwt.JWTAuthenticationFilter
import ac.kr.smu.prlab_server.jwt.JWTTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(private val tokenProvider: JWTTokenProvider) {
    @Bean
    fun httpConfig(http: HttpSecurity): SecurityFilterChain{
        http{
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeRequests {
                authorize("/login",permitAll)
                authorize(HttpMethod.POST, "/users",permitAll)// 회원가입
                authorize("/users?id**",permitAll)// id 중복 확인, password 찾기
                authorize("/users?email**",permitAll) // id 찾기
                authorize(anyRequest, authenticated)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(JWTAuthenticationFilter(tokenProvider))
        }
        return http.build()
    }
}
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
import org.springframework.security.web.util.matcher.RegexRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher


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
                authorize(RequestMatcher { request -> request.requestURI == "/users" && request.getParameter("id") != null}, permitAll)
                authorize(RequestMatcher { request -> request.requestURI == "/users" && request.getParameter("email") != null}, permitAll)
                authorize(anyRequest, authenticated)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(JWTAuthenticationFilter(tokenProvider))
        }
        return http.build()
    }
}
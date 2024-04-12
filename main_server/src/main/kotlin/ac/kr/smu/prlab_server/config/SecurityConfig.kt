package ac.kr.smu.prlab_server.config

import ac.kr.smu.prlab_server.jwt.JWTAuthenticationFilter
import ac.kr.smu.prlab_server.jwt.JWTTokenProvider

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper
import org.springframework.boot.context.properties.EnableConfigurationProperties

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy

import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import org.springframework.security.web.util.matcher.RequestMatcher


@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(OAuth2ClientProperties::class)
class SecurityConfig(
    private val tokenProvider: JWTTokenProvider,
    private val OAuthProperties: OAuth2ClientProperties
) {
    @Bean
    fun clientRegistrationRepository(): InMemoryClientRegistrationRepository{
        val registrations = OAuth2ClientPropertiesMapper(OAuthProperties).asClientRegistrations().values.toList()
        return InMemoryClientRegistrationRepository(registrations)
    }
    @Bean
    fun httpConfig(http: HttpSecurity): SecurityFilterChain{
        http{
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            authorizeRequests {
                authorize("/oauth/**",permitAll)
                authorize("/login/**",permitAll)
                authorize(HttpMethod.POST, "/users",permitAll)// 회원가입
                authorize("/users/check-**", permitAll)

                authorize(anyRequest, authenticated)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(JWTAuthenticationFilter(tokenProvider))
        }
        return http.build()
    }
}
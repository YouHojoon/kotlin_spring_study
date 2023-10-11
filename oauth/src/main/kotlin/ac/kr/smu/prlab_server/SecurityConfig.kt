package ac.kr.smu.prlab_server

import ac.kr.smu.prlab_server.repository.UserRepository
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.lang.Exception
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Configuration
@EnableWebSecurity
class SecurityConfig(private val repo: UserRepository) {
    @Bean
    fun filterChain(http: HttpSecurity,
                    userDetailsService: UserDetailsService,
                    registeredClientRepository: RegisteredClientRepository,
                    authorizationService: OAuth2AuthorizationService,
                    tokenEncoder: JwtEncoder,
                    serverSettings: AuthorizationServerSettings): SecurityFilterChain{
        OAuth2AuthorizationServerConfigurer()
            .apply {
            http.apply(this)
        }
            .registeredClientRepository(registeredClientRepository)
            .authorizationService(authorizationService) //access 토큰이 저장
            .tokenGenerator(JwtGenerator(tokenEncoder))
            .authorizationServerSettings(serverSettings)

//        http.userDetailsService(userDetailsService)
        http{
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
//            authorizeRequests {
//                authorize(anyRequest, authenticated)
//            }
        }

        return http.build()
    }

    @Bean
    fun userDetailService(): UserDetailsService{
        return UserDetailsService {
            repo.findById(it).getOrNull()
        }
    }

    @Bean
    fun registeredClientRepository(): RegisteredClientRepository{
        val registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("test")
            .clientSecret("{noop}test")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost:8080/oauth2/ours/callback")
            .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(2)).build())
            .build()

        return InMemoryRegisteredClientRepository(registeredClient)
    }

    @Bean
    fun authorizationService(): OAuth2AuthorizationService = InMemoryOAuth2AuthorizationService()

    @Bean
    fun jwkSource(): JWKSource<SecurityContext>{
        val keyPair: KeyPair = generateRSAKey()
        val publicKey:RSAPublicKey = keyPair.public as RSAPublicKey
        val privateKey: RSAPrivateKey = keyPair.private as RSAPrivateKey
        val rsaKey: RSAKey = RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build()
        val jwkSet = JWKSet(rsaKey)

        return ImmutableJWKSet(jwkSet)
    }

    private fun generateRSAKey(): KeyPair =
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(2048)
            keyPairGenerator.generateKeyPair()
        }catch (e: Exception){
            throw IllegalArgumentException(e)
        }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder = OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    @Bean
    fun jwtEncoder(jwkSource: JWKSource<SecurityContext>): JwtEncoder = NimbusJwtEncoder(jwkSource)

    @Bean
    fun authorizationServerSetting(): AuthorizationServerSettings{
        return AuthorizationServerSettings
            .builder()
            .build()
    }
}

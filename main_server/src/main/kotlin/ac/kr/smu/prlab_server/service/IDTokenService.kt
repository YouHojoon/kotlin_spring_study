package ac.kr.smu.prlab_server.service

import ac.kr.smu.endTicket.infra.OAuth2.IDToken.IDTokenHeader
import ac.kr.smu.endTicket.infra.OAuth2.IDToken.IDTokenPayLoad
import ac.kr.smu.endTicket.infra.OAuth2.IDToken.exception.IDTokenNotVerifyException
import ac.kr.smu.endTicket.infra.OAuth2.IDToken.exception.JWKParseException
import ac.kr.smu.prlab_server.enums.UserType
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.jwk.JWKSet
import io.jsonwebtoken.Jwts
import kotlinx.coroutines.runBlocking
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.security.PublicKey
import java.time.Instant
import java.util.Base64

@Service
class IDTokenService(
    private val clientRegistrationRepository: ClientRegistrationRepository,
) {

    @Throws(IDTokenNotVerifyException::class)
    fun parseSocialUserNumber(userType: UserType, idToken: String): String{
        require(userType != UserType.COMMON){"지원하지 않는 사용자 타입입니다."}

        val (header, payload, _) = parseIDToken(idToken)
        val key = findPublicKey(userType, header.kid)

        try {
            verifyIDToken(userType,idToken, payload, key)
        }catch (e:IllegalArgumentException){
            throw IDTokenNotVerifyException(e.message)
        }

        return payload.sub
    }

    @Throws(IllegalArgumentException::class)
    private fun verifyIDToken(userType: UserType, idToken: String, payload: IDTokenPayLoad, key:PublicKey){
        val provider = clientRegistrationRepository.findByRegistrationId(userType.name.lowercase())

        require(payload.iss == provider.providerDetails.issuerUri){
            "payload의 iss가 일치하지 않습니다. iss: ${payload.iss}, provider iss: ${provider.providerDetails.issuerUri}"
        }
        require(payload.aud == provider.clientId){
            "aud가 client id와 일치하지 않습니다. aud: ${payload.aud}, provider clientId: ${provider.clientId}"
        }
        require(payload.exp > Instant.now().epochSecond){
            "id 토큰이 만료되었습니다."
        }

        require(Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .isSigned(idToken)
        ){
            "서명 검증에 실패하였습니다."
        }
    }

    @Throws(IllegalStateException::class)
    private fun findPublicKey(userType: UserType, kid: String): PublicKey {
        val provider = clientRegistrationRepository.findByRegistrationId(userType.name.lowercase())
        return runBlocking {
            val jwkSet = getJwkSet(provider)

            val key = jwkSet.getKeyByKeyId(kid)
            checkNotNull(key)

            key.toRSAKey().toPublicKey()
        }
    }


    @Throws(JWKParseException::class)
    private suspend fun getJwkSet(provider: ClientRegistration): JWKSet{
        val json = WebClient.create()
            .get()
            .uri(provider.providerDetails.jwkSetUri)
            .retrieve()
            .onStatus({ it.isError }) {
                it.createException()
                    .map {
                        JWKParseException(it.getResponseBodyAs(Map::class.java).toString(), it)
                    }
            }
            .awaitBody<String>()

        return JWKSet.parse(json)
    }

    private fun parseIDToken(token: String): IDToken{
        val objectMapper = ObjectMapper()
        val (header, payload, signature) = token.split(".")
        val decoder = Base64.getDecoder()

        val decodedPayload = String(decoder.decode(payload))
        val decodedHeader= String(decoder.decode(header))

        return Triple(objectMapper.readValue(decodedHeader, IDTokenHeader::class.java)
            , objectMapper.readValue(decodedPayload, IDTokenPayLoad::class.java), signature)
    }

}

private typealias IDToken = Triple<IDTokenHeader, IDTokenPayLoad,String>
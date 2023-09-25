package ac.kr.smu.prlab_server.service.impl;

import ac.kr.smu.prlab_server.service.OAuthService
import ac.kr.smu.prlab_server.util.OAuthTokenResponse
import kotlinx.coroutines.*
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
public class OAuthServiceImpl(
        private val inMemoryClientRegistrationRepo: InMemoryClientRegistrationRepository
) : OAuthService{
         override fun oauth(sns: String, code: String): OAuthTokenResponse{
            val provider = inMemoryClientRegistrationRepo.findByRegistrationId(sns)
            return runBlocking { getToken(provider,code) }
        }

        private suspend fun getToken(provider: ClientRegistration, code: String): OAuthTokenResponse{
            return WebClient.create()
                .post()
                .uri(provider.providerDetails.tokenUri)
                .headers {
                    it.contentType = MediaType.APPLICATION_FORM_URLENCODED
                }
                .bodyValue(tokenRequest(provider, code))
                .retrieve()
                .awaitBody()
        }

        private fun tokenRequest(provider: ClientRegistration, code: String): LinkedMultiValueMap<String, String>{
            var body = LinkedMultiValueMap<String, String>()
            body.add("code", code)
            body.add("grant_type","authorization_code")
            body.add("redirect_uri", provider.redirectUri)
            body.add("client_secret", provider.clientSecret)
            body.add("client_id",provider.clientId)

            return body
        }
}

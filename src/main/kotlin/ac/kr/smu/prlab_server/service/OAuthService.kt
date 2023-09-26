package ac.kr.smu.prlab_server.service

import ac.kr.smu.prlab_server.util.OAuthTokenResponse

interface OAuthService {
    fun oauth(sns: String, code: String): Result<OAuthTokenResponse>
}
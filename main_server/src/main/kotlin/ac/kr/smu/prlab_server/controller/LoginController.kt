package ac.kr.smu.prlab_server.controller

import ac.kr.smu.prlab_server.enums.UserType
import ac.kr.smu.prlab_server.jwt.JWTTokenProvider
import ac.kr.smu.prlab_server.service.IDTokenService
import ac.kr.smu.prlab_server.service.OAuthService
import ac.kr.smu.prlab_server.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PathVariable

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClientResponseException


@RestController
@RequestMapping("/login")
class LoginController(
    private val userService: UserService,
    private val OAuthService: OAuthService,
    private val idTokenService: IDTokenService,
    private val tokenProvider: JWTTokenProvider,
    private val passwordEncoder: PasswordEncoder) {
    @PostMapping
    fun postLogin(@RequestBody body: HashMap<String, String>): ResponseEntity<*>{
        val id = body["id"]
        val password = body["password"]

        if(id == null || password == null)
            return ResponseEntity.badRequest().build<Void>()

        val user = userService.findById(id) ?: return  ResponseEntity.notFound().build<Void>()
        
        if (passwordEncoder.matches(password, user.password)) {
            val token = tokenProvider.createToken(id)
            return ResponseEntity.ok().body(mapOf("accessToken" to token))
        } else
            return ResponseEntity<Void>(HttpStatus.CONFLICT)
    }

    @PostMapping("{SNS}")
    suspend fun postSNSLogin(@RequestBody body: HashMap<String, String>, @PathVariable SNS: String): ResponseEntity<Any>{
        val code = body["code"] ?: return ResponseEntity.badRequest().build()

        val tokenResponseResult = OAuthService.oauth(SNS,code)

        return tokenResponseResult
            .fold(onSuccess = {
                val socialUserNumber = idTokenService.parseSocialUserNumber(UserType.valueOf(SNS.uppercase()), it.idToken)
                val user = userService.findById(socialUserNumber) ?: return@fold ResponseEntity.notFound().build()

                ResponseEntity.ok(tokenProvider.createToken(user.id))
            }){
                when(it){
                    is WebClientResponseException -> {
                        ResponseEntity<Any>(it.getResponseBodyAs(Map::class.java), it.statusCode)
                    }
                    else -> ResponseEntity.internalServerError().build<Any>()
                }
            }
    }
}
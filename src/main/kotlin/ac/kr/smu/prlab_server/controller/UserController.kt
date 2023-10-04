package ac.kr.smu.prlab_server.controller

import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.jwt.JWTTokenProvider
import ac.kr.smu.prlab_server.service.OAuthService
import ac.kr.smu.prlab_server.service.UserService
import com.fasterxml.jackson.databind.JsonSerializer
import com.nimbusds.jose.shaded.gson.Gson
import org.apache.coyote.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClientResponseException

@RequestMapping("/users")
@RestController
class UserController(
    private val service: UserService,
    private val OAuthService: OAuthService,
    private val tokenProvider: JWTTokenProvider
) {
    @PostMapping
    fun postUser(@RequestBody body: HashMap<String, Any>): ResponseEntity<Any> {
        val user = Gson().fromJson(
            if (body.containsKey("user"))
                body["user"].toString()
            else
                body.toString(), User::class.java
        )

        val code = body["code"]

        if (code == null)
            return saveUser(user)
        else
            return saveSNSUser(user, code.toString())
    }

    private fun saveUser(user: User): ResponseEntity<Any> {
        val email = service.findIdByEmail(user.email)

        when {
            email == null -> {
                service.save(user)
                return ResponseEntity(HttpStatus.CREATED)
            }

            else -> return ResponseEntity(HttpStatus.CONFLICT)
        }
    }

    private  fun saveSNSUser(user: User, code: String): ResponseEntity<Any> {
        val oauthResult = OAuthService.oauth(user.type.name.lowercase(), code)

        return oauthResult.fold({
            val id = it.idToken
            val user = User(id, user.password,user.email,user.birthday,user.gender, user.type)

            saveUser(user)
        }){
            when(it){
                is WebClientResponseException -> {
                    ResponseEntity<Any>(it.getResponseBodyAs(Map::class.java), it.statusCode)
                }
                else -> ResponseEntity.internalServerError().build<Any>()
            }
        }
    }

    @GetMapping(params = ["email"])
    fun getUserByEmail(@RequestParam("email") email: String): ResponseEntity<Any> {
        val id = service.findIdByEmail(email)

        when {
            id == null -> return ResponseEntity.notFound().build()
            else -> return ResponseEntity.ok(mapOf("id" to id))
        }
    }

    @GetMapping(params = ["id"])
    fun getIsIdExistById(@RequestParam("id") id: String): ResponseEntity<Void> {
        when {
            service.isIdExist(id) -> return ResponseEntity.noContent().build()
            else -> return ResponseEntity.notFound().build()
        }
    }

    @GetMapping(params = ["id", "email"])
    fun getIsMatchIdAndEmail(
        @RequestParam("id") id: String,
        @RequestParam("email") email: String
    ): ResponseEntity<Any> {
        val user = service.findById(id)

        when {
            user == null -> return ResponseEntity.notFound().build()
            email != user.email -> return ResponseEntity(HttpStatus.CONFLICT)
            else -> return ResponseEntity.ok(mapOf("token" to tokenProvider.createToken(id, 1000L * 60 * 5)))
        }
    }

    @PatchMapping
    fun patchUserPassword(
        @RequestBody json: HashMap<String, String>,
        @RequestHeader("AUTH-TOKEN") token: String
    ): ResponseEntity<Void> {
        val id = tokenProvider.getId(token)
        val password = json["password"]

        when {
            password == null -> return ResponseEntity.badRequest().build()
            else -> {
                service.changePassword(id, password)
                return ResponseEntity.noContent().build()
            }
        }
    }
}
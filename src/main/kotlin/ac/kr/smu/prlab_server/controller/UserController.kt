package ac.kr.smu.prlab_server.controller

import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.jwt.JWTTokenProvider
import ac.kr.smu.prlab_server.service.UserService
import org.apache.coyote.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/users")
@RestController
class UserController(private val service: UserService, private val tokenProvider: JWTTokenProvider) {
    @PostMapping
    fun postUser(@RequestBody user: User): ResponseEntity<Void>{
        val email = service.findIdByEmail(user.email)
        when{
            email == null -> {
                service.save(user)
                return ResponseEntity(HttpStatus.CREATED)
            }
            else -> return ResponseEntity(HttpStatus.CONFLICT)
        }
    }

    @GetMapping(params = ["email"])
    fun getUserByEmail(@RequestParam("email") email: String): ResponseEntity<Any>{
        val id = service.findIdByEmail(email)
        when{
            id == null -> return ResponseEntity.notFound().build()
            else -> return ResponseEntity.ok(mapOf("id" to id))
        }
    }

    @GetMapping(params = ["id"])
    fun getIsIdExistById(@RequestParam("id") id: String): ResponseEntity<Void>{
        when{
            service.isIdExist(id) ->  return ResponseEntity.noContent().build()
            else -> return ResponseEntity.notFound().build()
        }
    }
    @GetMapping(params = ["id","email"])
    fun getIsMatchIdAndEmail(@RequestParam("id") id: String, @RequestParam("email") email: String): ResponseEntity<Any>{
        val user =  service.findById(id)
        when {
            user == null -> return ResponseEntity.notFound().build()
            email != user.email -> return ResponseEntity(HttpStatus.CONFLICT)
            else -> return ResponseEntity.ok(mapOf("token" to tokenProvider.createToken(id)))
        }
    }
}
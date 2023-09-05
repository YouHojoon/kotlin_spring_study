package ac.kr.smu.prlab_server.controller

import ac.kr.smu.prlab_server.jwt.JWTTokenProvider
import ac.kr.smu.prlab_server.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/login")
class LoginController(private val userService: UserService, private val tokenProvider: JWTTokenProvider) {
    @PostMapping
    fun postLogin(@RequestBody body: HashMap<String, String>): ResponseEntity<Void>{
        val id = body["id"]
        val password = body["password"]

        if(id == null || password == null)
            return ResponseEntity.badRequest().build()

        val user = userService.findById(id) ?: return  ResponseEntity.notFound().build()

        if (password == user.password) {
            val token = tokenProvider.createToken(id)
            return ResponseEntity.ok().header("AUTH-TOKEN", token).build()
        } else
            return ResponseEntity(HttpStatus.CONFLICT)
    }
}
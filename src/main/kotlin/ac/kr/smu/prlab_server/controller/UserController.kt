package ac.kr.smu.prlab_server.controller

import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/users")
@RestController
class UserController(private val service: UserService) {
    @PostMapping
    fun postUser(@RequestBody user: User): ResponseEntity<Void>{
        if (service.findIdByEmail(user.email) != null){
            return ResponseEntity(HttpStatus.CONFLICT)
        }
        else{
            service.save(user)
            return ResponseEntity(HttpStatus.CREATED)
        }
    }
}
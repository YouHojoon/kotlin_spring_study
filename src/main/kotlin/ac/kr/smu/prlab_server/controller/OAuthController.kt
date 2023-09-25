package ac.kr.smu.prlab_server.controller

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oauth")
class OAuthController {
    @GetMapping("{SNS}/callback")
    fun getOAuth(@PathVariable SNS: String, @RequestParam code: String): ResponseEntity<Void>{
        println(code)
        return ResponseEntity.ok().build()
    }
}
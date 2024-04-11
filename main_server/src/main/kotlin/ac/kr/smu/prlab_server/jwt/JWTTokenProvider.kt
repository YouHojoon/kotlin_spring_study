package ac.kr.smu.prlab_server.jwt

import ac.kr.smu.prlab_server.service.UserService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Base64
import java.util.Date
import javax.crypto.spec.SecretKeySpec

@Component
class JWTTokenProvider(
    @Value("\${jwt.secretKey}")
    private val secretKey: String,
    private val userService: UserService
) {
    private val parser: JwtParser by lazy {
        Jwts.parserBuilder().setSigningKey(key).build()
    }
    private val key:Key
        get(){
            return SecretKeySpec(Base64.getEncoder().encodeToString(secretKey.toByteArray()).toByteArray()
                , SignatureAlgorithm.HS256.jcaName)
        }
    companion object{
        private const val HEADER_KEY = "Auth-Token"
    }

    fun createToken(id: String, validTime: Long = 1000L * 60 * 60): String{
        val now = Date()
        val claims =  Jwts.claims().setSubject(id)
        return Jwts
            .builder().setClaims(claims)
            .setIssuedAt(now).setExpiration(Date(now.time + validTime))
            .signWith(key).compact()
    }

    fun getAuthentication(token: String): Authentication{
        val user = userService.findById(getId(token))

        return UsernamePasswordAuthenticationToken(user!!,null,user!!.authorities)
    }
    fun getId(token: String): String{
        return parser.parseClaimsJws(token).body.subject
    }
    fun resolveToken(request: HttpServletRequest): String?{
        return request.getHeader(HEADER_KEY)
    }
    fun validateToken(token: String): Boolean{
        try{
            parser.parseClaimsJws(token)
        }catch (e: ExpiredJwtException){
            return false
        }

        return true
    }
}
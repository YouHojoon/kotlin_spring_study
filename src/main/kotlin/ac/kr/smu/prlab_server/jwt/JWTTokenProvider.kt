package ac.kr.smu.prlab_server.jwt

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Base64
import java.util.Date
import javax.crypto.spec.SecretKeySpec

@Component
class JWTTokenProvider(
    @Value("\${jwt.secret.key}")
    private val secretKey: String,
    private val userDetailsService: UserDetailsService
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
        private const val TOKEN_VALID_MILISECOND = 1000L * 60 * 60 // 1시간
        private const val HEADER_KEY = "AUTH-TOKEN"
    }

    fun createToken(id: String): String{
        val now = Date()
        val claims =  Jwts.claims().setSubject(id)
        return Jwts
            .builder().setClaims(claims)
            .setIssuedAt(now).setExpiration(Date(now.time + TOKEN_VALID_MILISECOND))
            .signWith(key).compact()
    }

    fun getAuthentication(token: String): Authentication{
        val userDetail = userDetailsService.loadUserByUsername(token)
        return UsernamePasswordAuthenticationToken(userDetail,"")
    }
    fun getId(token: String): String{
        return parser.parseClaimsJws(token).body.subject
    }
    fun resolveToken(request: HttpServletRequest): String{
        return request.getHeader(HEADER_KEY)
    }
    fun validateToken(token: String): Boolean{
        return parser.parseClaimsJws(token).body.expiration.after(Date())
    }
}
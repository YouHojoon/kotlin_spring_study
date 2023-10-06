package ac.kr.smu.prlab_server.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

class JWTAuthenticationFilter(private val tokenProvider: JWTTokenProvider): GenericFilterBean() {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val token = tokenProvider.resolveToken(request as HttpServletRequest)

        if(token != null && tokenProvider.validateToken(token)){
            val auth = tokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = auth
        }
        chain?.doFilter(request,response)
    }
}
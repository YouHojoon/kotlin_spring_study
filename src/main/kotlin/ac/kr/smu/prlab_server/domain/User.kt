package ac.kr.smu.prlab_server.domain

import ac.kr.smu.prlab_server.enum.Gender
import ac.kr.smu.prlab_server.enum.UserType
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.Date

@Entity
class User(
    @Id val id: String,
    @Column(name = "password") var _password: String,
    @Column val email: String,
    @Column val birthday: Date,
    @Enumerated(EnumType.STRING) @Column val gender: Gender,
    @Enumerated(EnumType.STRING) @Column val type: UserType
): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return AuthorityUtils.createAuthorityList()
    }

    override fun getPassword(): String {
        return _password
    }

    override fun getUsername(): String {
        return id
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
    fun setPassword(password: String){
        _password = password
    }
}
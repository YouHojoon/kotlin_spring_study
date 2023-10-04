package ac.kr.smu.prlab_server.domain

import ac.kr.smu.prlab_server.enum.Gender
import ac.kr.smu.prlab_server.enum.UserType
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.Date

@Entity
class User(
    @Column(length = 900, updatable = false) val id: String = "default",
    @Column @JvmField var password: String = "default",
    @Column(updatable = false) val email: String,
    @Column(updatable = false) val birthday: Date,
    @Enumerated(EnumType.STRING) @Column(updatable = false) val gender: Gender,
    @Enumerated(EnumType.STRING) @Column(updatable = false) val type: UserType,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val uid: Long = 0
): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return AuthorityUtils.createAuthorityList()
    }


    override fun getPassword(): String {
        return password
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
}
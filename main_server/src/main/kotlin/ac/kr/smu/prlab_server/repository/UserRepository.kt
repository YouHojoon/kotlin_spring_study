package ac.kr.smu.prlab_server.repository

import ac.kr.smu.prlab_server.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun findById(id: String): Optional<User>

    fun deleteUserById(id:String)
}
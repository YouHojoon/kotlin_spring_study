package ac.kr.smu.prlab_server.service.impl

import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.repository.UserRepository
import ac.kr.smu.prlab_server.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class UserServiceImpl(
    private val repo: UserRepository,
    private val encoder: PasswordEncoder
): UserService {
    override fun save(user: User) {
        user.password = encoder.encode(user.password)
        repo.save(user)
    }

    override fun changePassword(id: String, password: String) {
        repo.findById(id).getOrNull()?.let {
            it.password = encoder.encode(password)
            repo.save(it)
        }
    }

    override fun findIdByEmail(email: String): String? {
        return repo.findByEmail(email).getOrNull()?.let { it.id }
    }

    override fun isIdExist(id: String): Boolean {
        return findById(id) != null
    }

    override fun findById(id: String): User? {
        return repo.findById(id).getOrNull()
    }
    override fun deleteUser(id:String){
        repo.deleteUserById(id)
    }
}
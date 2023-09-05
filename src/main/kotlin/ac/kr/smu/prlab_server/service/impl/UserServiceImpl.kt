package ac.kr.smu.prlab_server.service.impl

import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.repository.UserRepository
import ac.kr.smu.prlab_server.service.UserService
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class UserServiceImpl(private val repo: UserRepository): UserService {
    override fun save(user: User) {
        repo.save(user)
    }

    override fun changePassword(id: String, password: String) {
        repo.findById(id).getOrNull()?.let {
            it.password = password
            repo.save(it)
        }
    }

    override fun findIdByEmail(email: String): String? {
        return repo.findByEmail(email).getOrNull()?.let { email }
    }

    override fun isIdAlreadyExist(id: String): Boolean {
        return findById(id) == null
    }

    override fun findById(id: String): User? {
        return repo.findById(id).getOrNull()
    }
}
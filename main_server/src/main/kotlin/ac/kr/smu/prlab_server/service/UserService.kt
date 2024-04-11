package ac.kr.smu.prlab_server.service

import ac.kr.smu.prlab_server.domain.User

interface UserService {
    fun save(user: User)
    fun changePassword(id: String, password: String)
    fun findIdByEmail(email: String): String?
    fun isIdExist(id: String): Boolean
    fun findById(id: String): User?

    fun deleteUser(id: String)
}
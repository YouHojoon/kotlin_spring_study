package ac.kr.smu.prlab_server.domain

import ac.kr.smu.prlab_server.enum.Gender
import ac.kr.smu.prlab_server.enum.UserType
import jakarta.persistence.*
import java.util.Date

@Entity
class User(
    @Id val id: String,
    @Column var password: String,
    @Column val email: String,
    @Column val birthday: Date,
    @Enumerated(EnumType.STRING) @Column val gender: Gender,
    @Enumerated(EnumType.STRING) @Column val type: UserType
) {

}
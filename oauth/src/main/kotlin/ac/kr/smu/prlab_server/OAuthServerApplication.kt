package ac.kr.smu.prlab_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@EnableJpaRepositories("ac.kr.smu.prlab_server.repository")
@EntityScan("ac.kr.smu.prlab_server.domain")
@SpringBootApplication
class OAuthServerApplication

fun main(args: Array<String>) {
    runApplication<OAuthServerApplication>(*args)
}

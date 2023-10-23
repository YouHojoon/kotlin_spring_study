package ac.kr.smu.prlab_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
//@EnableDiscoveryClient
class PrlabServerApplication

fun main(args: Array<String>) {
    runApplication<PrlabServerApplication>(*args)
}

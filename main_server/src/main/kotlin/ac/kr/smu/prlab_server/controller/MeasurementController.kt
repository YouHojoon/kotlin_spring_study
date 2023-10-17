package ac.kr.smu.prlab_server.controller

import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.enums.Metric
import ac.kr.smu.prlab_server.enums.Period
import ac.kr.smu.prlab_server.service.MeasurementDataService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
@RequestMapping("/measurements")
class MeasurementController(private val service: MeasurementDataService) {
    @GetMapping("recent")
    fun getRecentData(@AuthenticationPrincipal user: User): ResponseEntity<Any>{
        return ResponseEntity.ok(service.findRecentData(user))
    }

    @GetMapping("{id}")
    fun getMeasurementData(@PathVariable id: Long): ResponseEntity<Any>{
        val data = service.findById(id) ?:  return ResponseEntity.notFound().build()
        return ResponseEntity.ok(data)
    }
    @GetMapping("{metric}/{period}/{date}")
    fun getMetricDatas(@PathVariable("metric") metric: Metric, @PathVariable("period") period: Period, @PathVariable("date") date: Date): ResponseEntity<Any>{
        return ResponseEntity.ok(service.findMetricDatasByPeriodAndDate(metric,period,date))
    }
    @DeleteMapping("{id}")
    fun deleteMeasurementData(@PathVariable id: Long): ResponseEntity<Void>{
        service.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
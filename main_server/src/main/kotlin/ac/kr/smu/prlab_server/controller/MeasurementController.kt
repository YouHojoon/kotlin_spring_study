package ac.kr.smu.prlab_server.controller

import ac.kr.smu.prlab_server.domain.User
import ac.kr.smu.prlab_server.enums.Metric
import ac.kr.smu.prlab_server.enums.Period
import ac.kr.smu.prlab_server.exception.MeasurementDataPermissionException
import ac.kr.smu.prlab_server.service.MeasurementDataService
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
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
class MeasurementController(
    private val service: MeasurementDataService
) {
    @GetMapping("recent")
    fun getRecentData(@AuthenticationPrincipal user: User): ResponseEntity<Any>{
        return ResponseEntity.ok(service.findRecentData(user.uid))
    }

    @GetMapping("{id}")
    fun getMeasurementData(@AuthenticationPrincipal user: User, @PathVariable id: Long): ResponseEntity<Any>{
        try {
            val data = service.findById(user.uid, id) ?: return ResponseEntity.notFound().build()
            return ResponseEntity.ok(data)
        }catch (e: MeasurementDataPermissionException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }
    @GetMapping("{metric}/{period}/{date}")
    fun getMetricDatas(@PathVariable("metric") metric: Metric, @PathVariable("period") period: Period, @PathVariable("date") date: Date, @AuthenticationPrincipal user: User): ResponseEntity<Any>{
        return ResponseEntity.ok(service.findMetricDatasByPeriodAndDate(user.uid,metric,period,date))
    }
    @DeleteMapping("{id}")
    fun deleteMeasurementData(@AuthenticationPrincipal user: User, @PathVariable id: Long): ResponseEntity<Void>{
        try {
            service.deleteById(user.uid, id)
        }catch (e: IllegalStateException){
            return ResponseEntity.notFound().build()
        }
        catch (e: MeasurementDataPermissionException){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        return ResponseEntity.noContent().build()
    }
}
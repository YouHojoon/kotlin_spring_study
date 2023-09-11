package ac.kr.smu.prlab_server.controller

import ac.kr.smu.prlab_server.domain.FaceMeasurementData
import ac.kr.smu.prlab_server.domain.FingerMeasurementData
import ac.kr.smu.prlab_server.enum.MeasurementTarget
import ac.kr.smu.prlab_server.repository.MeasurementDataRepository
import ac.kr.smu.prlab_server.service.MeasurementDataService
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.Timestamp
import java.util.Date

@RestController
@RequestMapping("/measurements")
class MeasurementController(private val service: MeasurementDataService) {
    @GetMapping("recent")
    fun getRecentData(): ResponseEntity<Any>{
        return ResponseEntity.ok(service.findRecentData())
    }

    @GetMapping("{id}")
    fun getMeasurementData(@PathVariable id: Long): ResponseEntity<Any>{
        val data = service.findById(id) ?:  return ResponseEntity.notFound().build()
        return ResponseEntity.ok(data)
    }
}
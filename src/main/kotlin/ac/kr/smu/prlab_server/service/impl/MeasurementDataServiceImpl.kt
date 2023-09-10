package ac.kr.smu.prlab_server.service.impl

import ac.kr.smu.prlab_server.domain.FaceMeasurementData
import ac.kr.smu.prlab_server.domain.FingerMeasurementData
import ac.kr.smu.prlab_server.enum.MeasurementTarget
import ac.kr.smu.prlab_server.repository.MeasurementDataRepository
import ac.kr.smu.prlab_server.service.MeasurementDataService
import ac.kr.smu.prlab_server.util.RecentData
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class MeasurementDataServiceImpl(
    private val repo: MeasurementDataRepository
): MeasurementDataService {
    override fun findRecentData(): RecentData {
        val pageable = PageRequest.of(0,1)

        val faceMeasurementDataList = repo.findByTargetOrderByMeasurementDateDesc(MeasurementTarget.FACE, pageable).content
        val fingerMeasurementDataList = repo.findByTargetOrderByMeasurementDateDesc(MeasurementTarget.FINGER, pageable).content

        when{
            faceMeasurementDataList.isEmpty() && fingerMeasurementDataList.isEmpty() -> return RecentData()
            fingerMeasurementDataList.isEmpty() -> {
                val data = faceMeasurementDataList.first() as FaceMeasurementData
                return RecentData(data)
            }
            faceMeasurementDataList.isEmpty() -> {
                val data = fingerMeasurementDataList.first() as FingerMeasurementData
                return RecentData(data)
            }
            else -> {
                val faceData = faceMeasurementDataList.first() as FaceMeasurementData
                val fingerData = fingerMeasurementDataList.first() as FingerMeasurementData
                return RecentData(faceData,fingerData)
            }
        }
    }
}
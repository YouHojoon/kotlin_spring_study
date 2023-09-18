package ac.kr.smu.prlab_server.util

import ac.kr.smu.prlab_server.domain.FaceMeasurementData
import ac.kr.smu.prlab_server.domain.FingerMeasurementData

class RecentData {
    val bpm: Int?
    val SpO2: Int?
    val RR: Int?
    val stress: Int?

    val BMI: Int?
    val expressionAnalysis: ExpressionAnalysis?

    val bloodPressure: BloodPressure?
    val bloodSugar: Int?

    constructor(){
        bpm = null
        SpO2 = null
        RR = null
        stress = null
        BMI = null
        expressionAnalysis = null
        bloodPressure = null
        bloodSugar = null
    }
    constructor(faceData: FaceMeasurementData){
        bpm = faceData.bpm
        SpO2 = faceData.SpO2
        RR = faceData.RR
        stress = faceData.stress
        BMI = faceData.BMI
        expressionAnalysis = ExpressionAnalysis(faceData.valence, faceData.arousal)

        bloodPressure = null
        bloodSugar = null
    }

    constructor(fingerData: FingerMeasurementData){
        bpm = fingerData.bpm
        SpO2 = fingerData.SpO2
        RR = fingerData.RR
        stress = fingerData.stress
        bloodPressure = BloodPressure(fingerData.SYS, fingerData.DIA)
        bloodSugar = fingerData.bloodSugar

        BMI = null
        expressionAnalysis = null
    }

    constructor(faceData: FaceMeasurementData, fingerData: FingerMeasurementData){
        if (faceData.measurementDate.isAfter(fingerData.measurementDate)){
            bpm = faceData.bpm
            SpO2 = faceData.SpO2
            RR = faceData.RR
            stress = faceData.stress
        }
        else{
            bpm = fingerData.bpm
            SpO2 = fingerData.SpO2
            RR = fingerData.RR
            stress = fingerData.stress

        }

        BMI = faceData.BMI
        expressionAnalysis = ExpressionAnalysis(faceData.valence, faceData.arousal)
        bloodPressure = BloodPressure(fingerData.SYS, fingerData.DIA)
        bloodSugar = fingerData.bloodSugar
    }

}

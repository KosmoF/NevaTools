package ru.neva.drivers.ui.main_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val fptrHolder: FptrHolder
): ViewModel()
{
    fun checkConnection() : Boolean{
        return fptrHolder.fptr.isOpened
    }
    fun getDriverVersion(): String {
        return fptrHolder.fptr.version()
    }
    fun getKKTName(): String{
        if (!checkConnection()){
            return "Нет подключения"
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS)
        fptrHolder.fptr.queryData()
        return fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_MODEL_NAME)
    }
    fun getKKTSoftwareVersion(): String{
        if (!checkConnection()){
            return "Нет подключения"
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS)
        fptrHolder.fptr.queryData()
        return fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_UNIT_VERSION)
    }
    fun getKKTSerial(): String{
        if (!checkConnection()){
            return "Нет подключения"
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS)
        fptrHolder.fptr.queryData()
        return fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SERIAL_NUMBER)
    }
    fun getKKTDatetime(): String {
        if (!checkConnection()){
            return "Нет подключения"
        }
        return fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME).toString()
    }
    fun getShiftState(): String {
        if (!checkConnection()){
            return "Нет подключения"
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS)
        fptrHolder.fptr.queryData()
        var shiftState = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SHIFT_STATE).toInt()
        when(shiftState){
            IFptr.LIBFPTR_SS_OPENED -> return "Смена открыта"
            IFptr.LIBFPTR_SS_CLOSED -> return "Смена закрыта"
            IFptr.LIBFPTR_SS_EXPIRED -> return "Смена истекла"
        }
        return ""
    }
    fun getCapState(): String {
        if (!checkConnection()){
            return "Нет подключения"
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS)
        fptrHolder.fptr.queryData()
        var capState = fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_COVER_OPENED)
        when(capState){
            false -> return "Закрыта"
            true -> return "Открыта"
        }
        return ""
    }
    fun getPaperState(): String {
        if (!checkConnection()){
            return "Нет подключения"
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS)
        fptrHolder.fptr.queryData()
        var paperState = fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_RECEIPT_PAPER_PRESENT)
        when(paperState){
            false -> return "Нет"
            true -> return "Есть"
        }
        return ""
    }
    fun getUnsentDocsCount(): String {
        if (!checkConnection()){
            return "Нет подключения"
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_OFD_EXCHANGE_STATUS)
        fptrHolder.fptr.fnQueryData()
        var unsentDocsCount = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENTS_COUNT)
        return unsentDocsCount.toString()
    }
    fun getFirstUnsentDoc(): String {
        if (!checkConnection()){
            return "Нет подключения"
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_OFD_EXCHANGE_STATUS)
        fptrHolder.fptr.fnQueryData()
        var unsentDocsCount = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER)
        return unsentDocsCount.toString()
    }

    fun importImg(pixels: ByteArray?, width: Int): Pair<Int, String>{
//        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FILENAME, filePath)
//        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SCALE_PERCENT, 50.0)
//        val res = fptrHolder.fptr.uploadPictureMemory()
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PIXEL_BUFFER, pixels)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_WIDTH, width)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SCALE_PERCENT, 100.0)
        val res = fptrHolder.fptr.uploadPixelBufferMemory()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Картинка успешно загружена. Номер картинки в ККТ: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_PICTURE_NUMBER)
        return Pair(0, result)
    }

    fun clearImgs(): String{
        val res = fptrHolder.fptr.clearPictures()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return ""
    }
    fun getKKTSettings(): ArrayList<Any>{
        val params: ArrayList<Any> = ArrayList()

        // kktNumber (mainSettings)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 0)
        val res = fptrHolder.fptr.readDeviceSetting()
        if(res == -1){
            val msg = "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
            return arrayListOf(msg)
        }
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        // clicheSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 14)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 35)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 36)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 75)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 62)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 69)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        for(i in 0..19){
            fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 184 + i)
            fptrHolder.fptr.readDeviceSetting()
            params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))
        }

        // connectionSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 49)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 71)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 72)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 73)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        // diagnosticSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 278)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 279)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 280)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 283)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        // fontSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 13)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  12)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 23)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        // kmSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 1000)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  1001)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 1002)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 1003)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        // mainSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 4)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 9)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 32)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 50)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 52)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 56)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 63)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 66)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 67)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 68)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        // ofdSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 273)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  274)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 275)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 276)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        // paymentsSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 240)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 249)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 241)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 250)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 242)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 251)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 243)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 252)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 244)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 253)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 245)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 254)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 246)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 255)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        // printSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 19)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 34)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 47)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 57)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 58)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 332)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 333)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 345)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        // reportsSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 6)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 15)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 27)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 41)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 3)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 42)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 43)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 45)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 46)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 59)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 60)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 61)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        // sectionsSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 204)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  220)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 205)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  221)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 206)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  222)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 207)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  223)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 208)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  224)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        // serviceDocsSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  29)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  30)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  31)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  301)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        // usersSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 122)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 94)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 150)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 123)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 95)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 151)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 124)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 96)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 152)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 125)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 97)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 153)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 126)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 98)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 154)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 179)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 178)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 180)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 182)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 181)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 183)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        return params
    }

    fun setKKTSettings(params: ArrayList<String>): String{
        var i: Int = 0
        // kktNumber (mainSettings)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 0)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        val res = fptrHolder.fptr.writeDeviceSetting()
        i++
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }

        // clicheSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 14)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 35)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 36)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 75)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 62)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 69)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        for(j in 0..19){
            fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 184 + j)
            fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
            fptrHolder.fptr.writeDeviceSetting()
            i++
        }

        // connectionSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 49)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 71)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 72)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 73)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        // diagnosticSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 278)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 279)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 280)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 283)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++
        
        // fontSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 13)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 12)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 23)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++
        
        // kmSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 1000)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 1001)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 1002)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 1003)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++
        
        // mainSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 4)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 9)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 32)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 50)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 52)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 56)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 63)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 66)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 67)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 68)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++
        
        // ofdSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 273)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 274)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 275)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 276)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++
        
        // paymentsSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 240)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 249)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 241)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 250)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 242)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 251)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 243)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 252)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 244)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 253)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 245)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 254)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 246)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 255)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++
        
        // printSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 19)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 34)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 47)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 57)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 58)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 332)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 333)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 345)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++
        
        // reportsSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 6)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 15)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 27)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 41)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 3)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 42)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 43)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 45)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 46)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 59)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 60)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 61)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++
        
        // sectionsSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 204)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 220)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 205)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 221)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 206)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 222)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 207)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 223)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 208)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 224)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++
        
        // serviceDocsSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 29)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 30)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 31)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 301)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i].toInt())
        fptrHolder.fptr.writeDeviceSetting()
        i++
        
        // usersSettings
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 122)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 94)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 150)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 123)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 95)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 151)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 124)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 96)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 152)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 125)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 97)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 153)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 126)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 98)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 154)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 179)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 178)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 180)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 182)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 181)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 183)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, params[i])
        fptrHolder.fptr.writeDeviceSetting()
        i++
        
        fptrHolder.fptr.commitSettings()

        return ""
    }
}
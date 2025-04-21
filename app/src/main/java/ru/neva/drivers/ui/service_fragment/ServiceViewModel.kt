package ru.neva.drivers.ui.service_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val fptrHolder: FptrHolder
): ViewModel() {
    fun checkConnection() : Boolean{
        return fptrHolder.fptr.isOpened
    }
    fun getKKTSerial(): String{
        if (!checkConnection()){
            return "Нет подключения"
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS)
        fptrHolder.fptr.queryData()
        return fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SERIAL_NUMBER)
    }
    fun getErrorCode(): Int{
        return fptrHolder.fptr.errorCode()
    }
    fun getDatetime(): Any?{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_DATE_TIME)
        if(fptrHolder.fptr.queryData() == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        else{
            return fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)
        }
    }

    fun setDatetime(datetimeTextVal: Date): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATE_TIME, datetimeTextVal)
        if(fptrHolder.fptr.writeDateTime() == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        else{
            return ""
        }
    }
    fun beep(): String{
        if(fptrHolder.fptr.beep() == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        else{
            return ""
        }
    }

    fun cutTape(): String{
        if(fptrHolder.fptr.cut() == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        else{
            return ""
        }
    }

    fun openCashbox(): String{
        if(fptrHolder.fptr.openDrawer() == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        else{
            return ""
        }
    }

    fun rewindTape(): String{
        if(fptrHolder.fptr.lineFeed() == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        else{
            return ""
        }
    }

    fun rebootKKT(): String{
        if(fptrHolder.fptr.deviceReboot() == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        else{
            return ""
        }
    }
}
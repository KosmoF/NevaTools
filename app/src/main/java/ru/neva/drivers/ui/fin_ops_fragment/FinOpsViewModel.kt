package ru.neva.drivers.ui.fin_ops_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import javax.inject.Inject


@HiltViewModel
class FinOpsViewModel @Inject constructor(
    private var fptrHolder: FptrHolder
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
    fun deposit(summVal: Double, doNotPrintVal: Int): Pair<Int,String>{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SUM, summVal)
        when(doNotPrintVal){
            1 -> fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DOCUMENT_ELECTRONICALLY, true)
            0 -> fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DOCUMENT_ELECTRONICALLY, false)
        }
        if(fptrHolder.fptr.cashIncome() == -1){
            return Pair(-1,"Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        else{
            fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 66)
            when(fptrHolder.fptr.readDeviceSetting()){
                0 -> return Pair(0, "Сумма внесения: $summVal.\nПросмотр суммы наличных в ДЯ недоступен. \nУчёт наличности отключён в настройках.")
                1 -> {
                    fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_CASH_SUM)
                    fptrHolder.fptr.queryData()
                    val cashSum: Double = fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM)

                    return Pair(0, "Сумма внесения: $summVal.\nСумма наличных в ДЯ: $cashSum")
                }
            }
            return Pair(0, "")
        }
    }

    fun payment(summVal: Double, doNotPrintVal: Int): Pair<Int,String>{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SUM, summVal)
        when(doNotPrintVal){
            1 -> fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DOCUMENT_ELECTRONICALLY, true)
            0 -> fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DOCUMENT_ELECTRONICALLY, false)
        }
        if(fptrHolder.fptr.cashOutcome() == -1){
            return Pair(-1,"Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        else{
            fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 66)
            when(fptrHolder.fptr.readDeviceSetting()){
                0 -> return Pair(0, "Сумма выплаты: $summVal.\nПросмотр суммы наличных в ДЯ недоступен. \nУчёт наличности отключён в настройках.")
                1 -> {
                    fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_CASH_SUM)
                    fptrHolder.fptr.queryData()
                    val cashSum: Double = fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM)

                    return Pair(0, "Сумма выплаты: $summVal.\nСумма наличных в ДЯ: $cashSum")
                }
            }

            return Pair(0, "")
        }
    }
}
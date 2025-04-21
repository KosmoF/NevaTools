package ru.neva.drivers.ui.reports_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
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
    private fun checkDocClosed(): String{
        val res = fptrHolder.fptr.checkDocumentClosed()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        if (!fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_DOCUMENT_CLOSED)) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return ""
    }
    fun closeShift(): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_CLOSE_SHIFT)
        val res = fptrHolder.fptr.report()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }

    fun printXReport(): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_X)
        val res = fptrHolder.fptr.report()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }

    fun printLastDoc(): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_LAST_DOCUMENT)
        val res = fptrHolder.fptr.report()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }

    fun printOFDStatus(): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_OFD_EXCHANGE_STATUS)
        val res = fptrHolder.fptr.report()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }

    fun printKKTInfo(): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_KKT_INFO)
        val res = fptrHolder.fptr.report()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }

    fun printOFDTest(): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_OFD_TEST)
        val res = fptrHolder.fptr.report()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }

    fun printDocByNumber(docNumberVal: Int): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_FN_DOC_BY_NUMBER)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER, docNumberVal)
        val res = fptrHolder.fptr.report()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }

    fun printRegReport(): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_FN_REGISTRATIONS)
        val res = fptrHolder.fptr.report()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }

    fun printLicenseInfo(): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_LICENSE_INFO);
        val res = fptrHolder.fptr.report()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }
}
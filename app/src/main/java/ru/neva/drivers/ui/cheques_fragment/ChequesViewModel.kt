package ru.neva.drivers.ui.cheques_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import javax.inject.Inject

@HiltViewModel
class ChequesViewModel @Inject constructor(
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
    private fun checkDocClosed(): String {
        val res = fptrHolder.fptr.checkDocumentClosed()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        if (!fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_DOCUMENT_CLOSED)) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return ""
    }
    fun openShift(): String{
        val res = fptrHolder.fptr.openShift()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }

    fun openCheque(chequeTypeVal: Int, chequeTaxVal: Int, defaultTaxVal: Int): String {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal)
        if (chequeTaxVal == 0){
            fptrHolder.fptr.setParam(1055, defaultTaxVal)
        }
        else{
            fptrHolder.fptr.setParam(1055, chequeTaxVal)
        }
        val res = fptrHolder.fptr.openReceipt()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return ""
    }

    fun regPosition(positionNameVal: String, positionDepNumberVal: Int, positionCountVal: Int, positionPriceVal: Double, positionTaxTypeVal: Int, positionDiscountVal: Double): String {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_COMMODITY_NAME, positionNameVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DEPARTMENT, positionDepNumberVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PRICE, positionPriceVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_QUANTITY, positionCountVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_TAX_TYPE, positionTaxTypeVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_INFO_DISCOUNT_SUM, positionDiscountVal)
        val res = fptrHolder.fptr.registration()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return ""
    }

    fun regPayment(paymentSummVal: Double, paymentMethodVal: Int): String {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, paymentMethodVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_SUM, paymentSummVal)
        val res = fptrHolder.fptr.payment()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return ""
    }

    fun closeCheque(): String{
        val res = fptrHolder.fptr.closeReceipt()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }

    fun cancelCheque(): String{
        val res = fptrHolder.fptr.cancelReceipt()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return ""
    }
}
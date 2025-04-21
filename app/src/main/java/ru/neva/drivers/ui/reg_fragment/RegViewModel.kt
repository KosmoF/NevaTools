package ru.neva.drivers.ui.reg_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import ru.neva.drivers.utils.Utils
import javax.inject.Inject

@HiltViewModel
class RegViewModel @Inject constructor(
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

    fun calcRegNumber(orgINNVal: String): Pair<Int,String>{
        var res: Int = 0
//        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_FN_INFO)
//        res = fptrHolder.fptr.fnQueryData()
//        if (res == -1) {
//            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
//        }
//        val fnType = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_FN_TYPE)
//        if (fnType.toInt() == IFptr.LIBFPTR_FNT_RELEASE){
//            return Pair(-1, "Расчёт рег. номера доступен только для ККТ с отладочной версией ФН (МГМ ФН)")
//        }
        var INN: String = orgINNVal
        if(!Utils.checkINN(orgINNVal)){
            return Pair(-1, "ИНН неверен")
        }
        var regNumber: String = ""
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_SERIAL_NUMBER)
        res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var kktSerial = fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SERIAL_NUMBER)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS);
        res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var kktNum = fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_LOGICAL_NUMBER)
//        var kktNum = "1"
//        var INN = "5273964331"
//        var kktSerial = "1100701994"
//        var regNumber = ""
        kktNum = Utils.padWithZeros(kktNum.trim(), 10)
        INN = Utils.padWithZeros(INN.trim(), 12)
        kktSerial = Utils.padWithZeros(kktSerial.trim(), 20)
        regNumber = kktNum + INN + kktSerial
        println(regNumber)
        regNumber = Utils.calculateCCITT(regNumber).toString()
        regNumber = kktNum + Utils.padWithZeros(regNumber, 6)
        return Pair(0, regNumber)
    }

    fun kktRegistration(regNumberVal: String,
                         orgNameVal: String,
                         orgINNVal: String,
                         orgAddressVal: String,
                         orgEmailVal: String,
                         osnTaxVal: Int,
                         usn1TaxVal: Int,
                         usn2TaxVal: Int,
                         eshnTaxVal: Int,
                         patentTaxVal: Int,
                         offlineModeVal: Int,
                         autoModeVal: Int,
                         machineNumberVal: String,
                         dataEncryptionVal: Int,
                         machineInstallationVal: Int,
                         internetPaymentsOnlyVal: Int,
                         markedGoodsVal: Int,
                         gamblingModeVal: Int,
                         pawnBusinessVal: Int,
                         vendingMachineVal: Int,
                         wholesaleTradeVal: Int,
                         bsoOnlyVal: Int,
                         exciseDutyVal: Int,
                         servicePaymentVal: Int,
                         lotteryModeVal: Int,
                         insuranceBusinessVal: Int,
                         cateringServiceVal: Int,
                         ffdVersionVal: Int,
                         fnsAddressVal: String,
                         ofdNameVal: String,
                         ofdINNVal: String) : String{
        var res : Int = 0
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_OPERATION_TYPE, IFptr.LIBFPTR_FNOP_REGISTRATION)

        fptrHolder.fptr.setParam(1037, regNumberVal)

        fptrHolder.fptr.setParam(1048, orgNameVal)
        fptrHolder.fptr.setParam(1018, orgINNVal)
        fptrHolder.fptr.setParam(1187, orgAddressVal)
        fptrHolder.fptr.setParam(1009, orgAddressVal)
        fptrHolder.fptr.setParam(1117, orgEmailVal)

        println(osnTaxVal or usn1TaxVal or usn2TaxVal or eshnTaxVal or patentTaxVal)
        fptrHolder.fptr.setParam(1062, osnTaxVal or usn1TaxVal or usn2TaxVal or eshnTaxVal or patentTaxVal)

        fptrHolder.fptr.setParam(1002, offlineModeVal)
        fptrHolder.fptr.setParam(1001, autoModeVal)
        if(autoModeVal == 1){
            fptrHolder.fptr.setParam(1036, machineNumberVal)
        }
        fptrHolder.fptr.setParam(1056, dataEncryptionVal)
        fptrHolder.fptr.setParam(1221, machineInstallationVal)
        fptrHolder.fptr.setParam(1108, internetPaymentsOnlyVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_TRADE_MARKED_PRODUCTS, markedGoodsVal)
        fptrHolder.fptr.setParam(1193, gamblingModeVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PAWN_SHOP_ACTIVITY, pawnBusinessVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_VENDING_MACHINE_ACTIVITY, vendingMachineVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_WHOLESALE_ACTIVITY, wholesaleTradeVal)
        fptrHolder.fptr.setParam(1110, bsoOnlyVal)
        fptrHolder.fptr.setParam(1207, exciseDutyVal)
        fptrHolder.fptr.setParam(1109, servicePaymentVal)
        fptrHolder.fptr.setParam(1126, lotteryModeVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_INSURANCE_ACTIVITY, insuranceBusinessVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_CATERING_ACTIVITY, cateringServiceVal)
        when(ffdVersionVal){
            0 -> fptrHolder.fptr.setParam(1209, IFptr.LIBFPTR_FFD_1_1)
            1 -> fptrHolder.fptr.setParam(1209, IFptr.LIBFPTR_FFD_1_2)
        }
        fptrHolder.fptr.setParam(1060, fnsAddressVal)

        fptrHolder.fptr.setParam(1046, ofdNameVal)
        fptrHolder.fptr.setParam(1017, ofdINNVal)

        res = fptrHolder.fptr.fnOperation()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_FN_REGISTRATIONS)
        res = fptrHolder.fptr.report()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }

    fun kktReRegistration(regNumberVal: String,
                           reregReasonVal: Long,
                           orgNameVal: String,
                           orgINNVal: String,
                           orgAddressVal: String,
                           orgEmailVal: String,
                           osnTaxVal: Int,
                           usn1TaxVal: Int,
                           usn2TaxVal: Int,
                           eshnTaxVal: Int,
                           patentTaxVal: Int,
                           offlineModeVal: Int,
                           autoModeVal: Int,
                           machineNumberVal: String,
                           dataEncryptionVal: Int,
                           machineInstallationVal: Int,
                           internetPaymentsOnlyVal: Int,
                           markedGoodsVal: Int,
                           gamblingModeVal: Int,
                           pawnBusinessVal: Int,
                           vendingMachineVal: Int,
                           wholesaleTradeVal: Int,
                           bsoOnlyVal: Int,
                           exciseDutyVal: Int,
                           servicePaymentVal: Int,
                           lotteryModeVal: Int,
                           insuranceBusinessVal: Int,
                           cateringServiceVal: Int,
                           ffdVersionVal: Int,
                           fnsAddressVal: String,
                           ofdNameVal: String,
                           ofdINNVal: String) : String{
        var res : Int = 0
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_OPERATION_TYPE, IFptr.LIBFPTR_FNOP_CHANGE_PARAMETERS)

        fptrHolder.fptr.setParam(1205, reregReasonVal)
        fptrHolder.fptr.setParam(1037, regNumberVal)

        fptrHolder.fptr.setParam(1048, orgNameVal)
        fptrHolder.fptr.setParam(1018, orgINNVal)
        fptrHolder.fptr.setParam(1187, orgAddressVal)
        fptrHolder.fptr.setParam(1009, "Офис")
        fptrHolder.fptr.setParam(1117, orgEmailVal)

        fptrHolder.fptr.setParam(1062, osnTaxVal or usn1TaxVal or usn2TaxVal or eshnTaxVal or patentTaxVal)

        fptrHolder.fptr.setParam(1002, offlineModeVal)
        fptrHolder.fptr.setParam(1001, autoModeVal)
        fptrHolder.fptr.setParam(1036, machineNumberVal)
        fptrHolder.fptr.setParam(1056, dataEncryptionVal)
        fptrHolder.fptr.setParam(1221, machineInstallationVal)
        fptrHolder.fptr.setParam(1108, internetPaymentsOnlyVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_TRADE_MARKED_PRODUCTS, markedGoodsVal)
        fptrHolder.fptr.setParam(1193, gamblingModeVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PAWN_SHOP_ACTIVITY, pawnBusinessVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_VENDING_MACHINE_ACTIVITY, vendingMachineVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_WHOLESALE_ACTIVITY, wholesaleTradeVal)
        fptrHolder.fptr.setParam(1110, bsoOnlyVal)
        fptrHolder.fptr.setParam(1207, exciseDutyVal)
        fptrHolder.fptr.setParam(1109, servicePaymentVal)
        fptrHolder.fptr.setParam(1126, lotteryModeVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_INSURANCE_ACTIVITY, insuranceBusinessVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_CATERING_ACTIVITY, cateringServiceVal)
        when(ffdVersionVal){
            0 -> fptrHolder.fptr.setParam(1209, IFptr.LIBFPTR_FFD_1_1)
            1 -> fptrHolder.fptr.setParam(1209, IFptr.LIBFPTR_FFD_1_2)
        }
        fptrHolder.fptr.setParam(1060, fnsAddressVal)

        fptrHolder.fptr.setParam(1046, ofdNameVal)
        fptrHolder.fptr.setParam(1017, ofdINNVal)

        res = fptrHolder.fptr.fnOperation()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_FN_REGISTRATIONS)
        res = fptrHolder.fptr.report()
        if (res == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        return checkDocClosed()
    }
}
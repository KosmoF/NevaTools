package ru.neva.drivers.ui.km_check_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import ru.neva.drivers.utils.Utils
import javax.inject.Inject


@HiltViewModel
class KmCheckViewModel @Inject constructor(
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
    fun checkKM(kmTextVal: String, kmTypeVal: Int): Pair<Int, String>{
        fptrHolder.fptr.updateFnmKeys()
        val kmText = Utils.markingCodeFromViewType(kmTextVal, kmTypeVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_MARKING_CODE_TYPE, IFptr.LIBFPTR_MCT12_AUTO)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_MARKING_CODE, kmText)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_MARKING_CODE_STATUS, IFptr.LIBFPTR_MES_PIECE_SOLD)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_MARKING_PROCESSING_MODE, 0)
        val res = fptrHolder.fptr.beginMarkingCodeValidation()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        while (true) {
            fptrHolder.fptr.markingCodeValidationStatus
            if (fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_MARKING_CODE_VALIDATION_READY)) break
        }
        var result: String = ""
        val validationResult: Int = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_MARKING_CODE_ONLINE_VALIDATION_RESULT).toInt()
        when (validationResult){
            0 -> result += "Проверка КП КМ не выполнена, статус товара ОИСМ не проверен [М]"
            1 -> result += "Проверка КП КМ выполнена в ФН с отрицательным результатом, статус товара ОИСМ не проверен [М-]"
            3 -> result += "Проверка КП КМ выполнена с положительным результатом, статус товара ОИСМ не проверен [М]"
            16 -> result += "Проверка КП КМ не выполнена, статус товара ОИСМ не проверен (ККТ функционирует в автономном режиме) [М]"
            17 -> result += "Проверка КП КМ выполнена в ФН с отрицательным результатом, статус товара ОИСМ не проверен (ККТ функционирует в автономном режиме) [М-]"
            19 -> result += "Проверка КП КМ выполнена в ФН с положительным результатом, статус товара ОИСМ не проверен (ККТ функционирует в автономном режиме) [М]"
            5 -> result += "Проверка КП КМ выполнена с отрицательным результатом, статус товара у ОИСМ некорректен [М-]"
            7 -> result += "Проверка КП КМ выполнена с положительным результатом, статус товара у ОИСМ некорректен [М-]"
            15 -> result += "Проверка КП КМ выполнена с положительным результатом, статус товара у ОИСМ корректен [M+]"
        }
        return Pair(0, result)
    }

    fun acceptKM(): Pair <Int, String>{
        val res = fptrHolder.fptr.acceptMarkingCode()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        return Pair(0, "")
    }

    fun rejectKM(): Pair <Int, String>{
        val res = fptrHolder.fptr.declineMarkingCode()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        return Pair(0, "")
    }
}

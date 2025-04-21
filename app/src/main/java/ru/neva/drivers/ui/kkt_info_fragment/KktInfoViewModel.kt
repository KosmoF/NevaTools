package ru.neva.drivers.ui.kkt_info_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class KktInfoViewModel @Inject constructor(
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
    fun getKKTStatus(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Номер ККТ в магазине: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_LOGICAL_NUMBER).toString() + "\n" +
                "Дата и время ККТ: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!) + "\n" +
                "ККТ зарегистрирована: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_FISCAL)) + "\n" +
                "ФН фискализирован: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_FN_FISCAL)) + "\n" +
                "ФН установлен: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_FN_PRESENT)) + "\n" +
                "Состояние ФН некорректно: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_INVALID_FN)) + "\n" +
                "Состояние смены: "
        val shiftState = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SHIFT_STATE)
        when (shiftState.toInt()) {
            IFptr.LIBFPTR_SS_CLOSED -> result += "Закрыта" + "\n"
            IFptr.LIBFPTR_SS_OPENED -> result += "Открыта" + "\n"
            IFptr.LIBFPTR_SS_EXPIRED -> result += "Истекла" + "\n"
        }
        result += "Денежный ящик открыт: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_CASHDRAWER_OPENED)) + "\n" +
                "Бумага в наличии: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_RECEIPT_PAPER_PRESENT)) + "\n" +
                "Крышка открыта: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_COVER_OPENED)) + "\n" +
                "Заводской номер ККТ: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SERIAL_NUMBER) + "\n" +
                "Номер модели ККТ: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_MODEL).toString() + "\n" +
                "Текущий номер чека: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_RECEIPT_NUMBER).toString() + "\n" +
                "Текущий номер документа: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER).toString() + "\n" +
                "Текущий номер смены: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SHIFT_NUMBER).toString() + "\n" +
                "Название ККТ: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_MODEL_NAME) + "\n" +
                "Версия ПО ККТ: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_UNIT_VERSION)

        return Pair(0, result)
    }

    fun getCashSum(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_CASH_SUM)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Сумма наличных в ДЯ: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        return Pair(0, result)
    }

    fun getImgInfo(imgNumberVal: Int): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_PICTURE_INFO)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PICTURE_NUMBER, imgNumberVal)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Ширина картинки, пикс.: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_WIDTH).toString() + "\n" +
                "Высота картинки, пикс.: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_HEIGHT).toString()
        return Pair(0, result)
    }

    fun getRegSumm(chequeTypeVal: Int): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_REGISTRATIONS_SUM)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Сумма регистраций: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        return Pair(0, result)
    }

    fun getRegCount(chequeTypeVal: Int): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_REGISTRATIONS_COUNT)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Кол-во регистраций: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_COUNT).toString()
        return Pair(0, result)
    }

    fun getPaymentsSumm(chequeTypeVal: Int): Pair <Int, String> {
        var result: String = ""
        var res: Int = 0
        result += "Сумма оплат за смену: \n"
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_PAYMENT_SUM)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, IFptr.LIBFPTR_PT_CASH)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal)
        fptrHolder.fptr.queryData()
        res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        result += " - Наличными: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_PAYMENT_SUM)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, IFptr.LIBFPTR_PT_ELECTRONICALLY)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal)
        fptrHolder.fptr.queryData()
        res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        result += " - Безналичными: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_PAYMENT_SUM)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, IFptr.LIBFPTR_PT_PREPAID)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal)
        fptrHolder.fptr.queryData()
        res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        result += " - Предварительная оплата (аванс): " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_PAYMENT_SUM)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, IFptr.LIBFPTR_PT_PREPAID)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal)
        fptrHolder.fptr.queryData()
        res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        result += " - Последующая оплата (кредит): " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_PAYMENT_SUM)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, IFptr.LIBFPTR_PT_PREPAID)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal)
        fptrHolder.fptr.queryData()
        res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        result += " - Иная форма оплаты (встречное предоставление): " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        return Pair(0, result)
    }

    fun getCashInSumm(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_CASHIN_SUM)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Сумма внесений: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        return Pair(0, result)
    }

    fun getCashInCount(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_CASHIN_COUNT)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Кол-во внесений: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_COUNT).toString()
        return Pair(0, result)
    }

    fun getCashOutSumm(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_CASHOUT_SUM)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Сумма выплат: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        return Pair(0, result)
    }

    fun getCashOutCount(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_CASHOUT_COUNT)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Кол-во выплат: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_COUNT).toString()
        return Pair(0, result)
    }

    fun getRevenue(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_REVENUE)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Выручка: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        return Pair(0, result)
    }

    fun getKKTDatetime(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_DATE_TIME)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Дата и время ККТ: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!)
        return Pair(0, result)
    }

    fun getShiftState(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_SHIFT_STATE)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Состояние смены: "
        val shiftState = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SHIFT_STATE)
        when (shiftState.toInt()) {
            IFptr.LIBFPTR_SS_CLOSED -> result += "Закрыта" + "\n"
            IFptr.LIBFPTR_SS_OPENED -> result += "Открыта" + "\n"
            IFptr.LIBFPTR_SS_EXPIRED -> result += "Истекла" + "\n"
        }
        result += "Номер смены: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SHIFT_NUMBER).toString() + "\n" +
                "Дата и время истечения смены: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!)
        return Pair(0, result)
    }

    fun getChequeState(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_RECEIPT_STATE)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Тип чека: "
        val chequeType = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE)
        when (chequeType.toInt()) {
            IFptr.LIBFPTR_RT_CLOSED -> result += "Чек закрыт" + "\n"
            IFptr.LIBFPTR_RT_SELL -> result += "Чек прихода (продажи)" + "\n"
            IFptr.LIBFPTR_RT_SELL_RETURN -> result += "Чек возврата прихода (продажи)" + "\n"
            IFptr.LIBFPTR_RT_SELL_CORRECTION -> result += "Чек коррекции прихода (продажи)" + "\n"
            IFptr.LIBFPTR_RT_SELL_RETURN_CORRECTION -> result += "Чек коррекции возврата прихода (продажи)" + "\n"
            IFptr.LIBFPTR_RT_BUY -> result += "Чек расхода (покупки)" + "\n"
            IFptr.LIBFPTR_RT_BUY_RETURN -> result += "Чек возврата расхода (покупки)" + "\n"
            IFptr.LIBFPTR_RT_BUY_CORRECTION -> result += "Чек коррекции расхода (покупки)" + "\n"
            IFptr.LIBFPTR_RT_BUY_RETURN_CORRECTION -> result += "Чек коррекции возврата расхода (покупки)" + "\n"
        }
        result += "Номер чека: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_RECEIPT_NUMBER).toString() + "\n" +
                "Номер документа: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER).toString() + "\n" +
                "Текущая сумма чека: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_RECEIPT_SUM).toString() + "\n" +
                "Неоплаченный остаток: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_REMAINDER).toString() + "\n" +
                "Сдача по чеку: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_CHANGE).toString() + "\n" +
        return Pair(0, result)
    }

    fun getSerialNumber(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_SERIAL_NUMBER)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Серийный номер ККТ: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SERIAL_NUMBER)
        return Pair(0, result)
    }

    fun getModelInfo(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_MODEL_INFO)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Номер модели ККТ: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_MODEL).toString() + "\n" +
                "Наименование ККТ: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_MODEL_NAME) + "\n" +
                "Версия ПО ККТ: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_UNIT_VERSION) + "\n" +
        return Pair(0, result)
    }

    fun getLineLength(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_RECEIPT_LINE_LENGTH)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Ширина чековой ленты, симв.: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_RECEIPT_LINE_LENGTH).toString() + "\n" +
                "Ширина чековой ленты, пикс.: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_RECEIPT_LINE_LENGTH_PIX).toString()
        return Pair(0, result)
    }

    fun getShiftTax(chequeTypeVal: Int, taxTypeVal: Int): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_SHIFT_TAX_SUM)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_TAX_TYPE, taxTypeVal)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Сумма налога: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        return Pair(0, result)
    }

    fun getChequeTax(taxTypeVal: Int): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_RECEIPT_TAX_SUM)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_TAX_TYPE, taxTypeVal)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Сумма налога: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        return Pair(0, result)
    }

    fun getNotNullableSumm(chequeTypeVal: Int): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_NON_NULLABLE_SUM)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Необнуляемая сумма: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        return Pair(0, result)
    }

    fun getChequesCount(chequeTypeVal: Int): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_RECEIPT_COUNT)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Кол-во чеков: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENTS_COUNT).toString()
        return Pair(0, result)
    }

    fun getMACAddress(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_MAC_ADDRESS)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "MAC-адрес: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_MAC_ADDRESS)
        return Pair(0, result)
    }

    fun getKKTUptime(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_DEVICE_UPTIME)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Время работы ККТ: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DEVICE_UPTIME).toString()
        return Pair(0, result)
    }

    fun getDiscountSumm(chequeTypeVal: Int): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_DISCOUNT_AND_SURCHARGE_SUM)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal);
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Сумма скидок: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_DISCOUNT_SUM).toString() + "\n" +
                "Сумма надбавок: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SURCHARGE_SUM).toString()
        return Pair(0, result)
    }

    fun getLKUserCode(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_LK_USER_CODE)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Код привязки к ЛК: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_LK_USER_CODE)
        return Pair(0, result)
    }

    fun getOFDDatetime(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_LAST_SENT_OFD_DOCUMENT_DATE_TIME)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Дата и время последней успешной отправки документа в ОФД: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!)
        return Pair(0, result)
    }

    fun getKKTStatusShort(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_SHORT_STATUS)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Денежный ящик открыт: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_CASHDRAWER_OPENED)) + "\n" +
                "Бумага в наличии: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_RECEIPT_PAPER_PRESENT)) + "\n" +
                "Крышка открыта: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_COVER_OPENED))
        return Pair(0, result)
    }

    fun getImgsInfo(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_PICTURES_ARRAY_INFO)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Кол-во картинок: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_COUNT).toString()
        return Pair(0, result)
    }

    fun getShiftTotals(chequeTypeVal: Int): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_SHIFT_TOTALS)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, chequeTypeVal)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Сумма: " + fptrHolder.fptr.getParamDouble(IFptr.LIBFPTR_PARAM_SUM).toString()
        return Pair(0, result)
    }

    fun getISMDatetime(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_LAST_SENT_ISM_NOTICE_DATE_TIME)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Дата и время последней успешной отправки документа в ОФД: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!)
        return Pair(0, result)
    }

    fun getLKStatus(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_LK_EXCHANGE_STATUS)
        val res = fptrHolder.fptr.queryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Время последней синхронизации: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!) + "\n" +
                "Время последней попытки подключения: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_CONNECT_DATE_TIME)!!) + "\n" +
                "Этап работы с ЛК: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SUBMODE).toString() + "\n" +
                "Код ошибки связи: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_NETWORK_ERROR).toString() + "\n" +
                "Текст ошибки связи: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_NETWORK_ERROR_TEXT) + "\n" +
                "Соединение установлено: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_IS_REQUEST_SENT))
        return Pair(0, result)
    }
}
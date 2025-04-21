package ru.neva.drivers.ui.fn_info_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class FnInfoViewModel @Inject constructor(
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
    fun getOFDStatus(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_OFD_EXCHANGE_STATUS)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Код статуса обмена с ОФД: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_OFD_EXCHANGE_STATUS).toString() + "\n" +
                "Кол-во неотправленных док-ов: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENTS_COUNT).toString() + "\n" +
                "Номер первого неотправленного док-та: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENTS_COUNT).toString() + "\n" +
                "Дата и время первого неотправленного док-та: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!)

        return Pair(0, result)
    }

    fun getFnInfo(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_FN_INFO)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Серийный номер ФН: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SERIAL_NUMBER) + "\n" +
                "Версия ФН: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_FN_VERSION) + "\n" +
                "Тип ФН: "
        val fnType = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_FN_TYPE)
        when (fnType.toInt()) {
            IFptr.LIBFPTR_FNT_UNKNOWN -> result += "Не удалось получить" + "\n"
            IFptr.LIBFPTR_FNT_DEBUG -> result += "Отладочная версия (МГМ)" + "\n"
            IFptr.LIBFPTR_FNT_RELEASE -> result += "Стандартный ФН" + "\n"
        }
        result += "Состояние ФН: "
        val fnState = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_FN_STATE)
        when (fnState.toInt()) {
            IFptr.LIBFPTR_FNS_INITIAL -> result += "Настройка ФН" + "\n"
            IFptr.LIBFPTR_FNS_CONFIGURED -> result += "Готов к активации" + "\n"
            IFptr.LIBFPTR_FNS_FISCAL_MODE -> result += "Фискальный режим" + "\n"
            IFptr.LIBFPTR_FNS_POSTFISCAL_MODE -> result += "Постфискальный режим" + "\n"
            IFptr.LIBFPTR_FNS_ACCESS_ARCHIVE -> result += "Режим архива" + "\n"
        }
        result += "Требуется срочная замена ФН: " + Utils.booleanToString(
            fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_FN_NEED_REPLACEMENT)) + "\n" +
                "Исчерпан ресурс ФН: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_FN_RESOURCE_EXHAUSTED)) + "\n" +
                "Память ФН переполнена: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_FN_MEMORY_OVERFLOW)) + "\n" +
                "Превышено время ожидания ответа от ОФД : " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_FN_OFD_TIMEOUT)) + "\n" +
                "Обнаружена критическая ошибка ФН : " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_FN_CRITICAL_ERROR))

        return Pair(0, result)
    }

    fun getLastRegInfo(): Pair <Int, String> {
        fptrHolder.fptr.setParam(
            IFptr.LIBFPTR_PARAM_FN_DATA_TYPE,
            IFptr.LIBFPTR_FNDT_LAST_REGISTRATION
        )
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Номер документа в ФН: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER)
            .toString() + "\n" +
                "Номер регистрации / перерегистрации: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_REGISTRATIONS_COUNT)
            .toString() + "\n" +
                "Дата и время регистрации / перерегистрации: " + sdf.format(
            fptrHolder.fptr.getParamDateTime(
                IFptr.LIBFPTR_PARAM_DATE_TIME
            )!!
        )

        return Pair(0, result)
    }

    fun getLastDocInfo(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_LAST_DOCUMENT)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Номер документа в ФН: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER)
            .toString() + "\n" +
                "Фискальный признак документа: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_FISCAL_SIGN) + "\n" +
                "Дата и время документа: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!)

        return Pair(0, result)
    }

    fun getShiftInfo(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_SHIFT)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Количество чеков за смену: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_RECEIPT_NUMBER)
            .toString() + "\n" +
                "Номер смены: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SHIFT_NUMBER)
            .toString()

        return Pair(0, result)
    }

    fun getFFDInfo(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_FFD_VERSIONS)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Версия ФФД ККТ: "
        val kktFFDVersion = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DEVICE_FFD_VERSION)
        when (kktFFDVersion.toInt()) {
            IFptr.LIBFPTR_FFD_UNKNOWN -> result += "Не удалось получить" + "\n"
            IFptr.LIBFPTR_FFD_1_0_5 -> result += "1.05" + "\n"
            IFptr.LIBFPTR_FFD_1_1 -> result += "1.1" + "\n"
            IFptr.LIBFPTR_FFD_1_2 -> result += "1.2" + "\n"
        }
        result += "Версия ФФД ФН: "
        val fnFFDVersion = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_FN_FFD_VERSION)
        when (fnFFDVersion.toInt()) {
            IFptr.LIBFPTR_FFD_UNKNOWN -> result += "Не удалось получить" + "\n"
            IFptr.LIBFPTR_FFD_1_0_5 -> result += "1.05" + "\n"
            IFptr.LIBFPTR_FFD_1_1 -> result += "1.1" + "\n"
            IFptr.LIBFPTR_FFD_1_2 -> result += "1.2" + "\n"
        }

        return Pair(0, result)
    }

    fun getFNExpiration(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_VALIDITY)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Осталось перерегистраций: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_REGISTRATIONS_REMAIN).toString() + "\n" +
                "Сделано перерегистраций: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_REGISTRATIONS_COUNT).toString() + "\n" +
                "Дата и время документа: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!)

        return Pair(0, result)
    }

    fun getRegInfo(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_REG_INFO)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Регистрационный номер устройства: " + fptrHolder.fptr.getParamString(1037) + "\n" +
                "Адрес сайта ФНС: " + fptrHolder.fptr.getParamString(1060) + "\n" +
                "Название организации: " + fptrHolder.fptr.getParamString(1048) + "\n" +
                "ИНН организации: " + fptrHolder.fptr.getParamString(1018) + "\n" +
                "Адрес организации: " + fptrHolder.fptr.getParamString(1009) + "\n" +
                "E-mail организации: " + fptrHolder.fptr.getParamString(1117) + "\n" +
                "Версия ФФД: " + fptrHolder.fptr.getParamInt(1209).toString() + "\n" +
                "Название ОФД: " + fptrHolder.fptr.getParamString(1046)+ "\n" +
                "ИНН ОФД: " + fptrHolder.fptr.getParamString(1017) + "\n" +

        return Pair(0, result)
    }

    fun getOFDErrors(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_ERRORS)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Дата и время последнего успешного соединения с ОФД: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!) + "\n" +
                "Код ошибки сети: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_NETWORK_ERROR).toString() + "\n" +
                "Текст ошибки сети: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_NETWORK_ERROR_TEXT) + "\n" +
                "Код ошибки ОФД: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_OFD_ERROR).toString() + "\n" +
                "Текст ошибки ОФД: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_OFD_ERROR_TEXT) + "\n" +
                "Код ошибки ФН: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_FN_ERROR).toString() + "\n" +
                "Текст ошибки ФН: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_FN_ERROR_TEXT) + "\n" +
                "Номер ФД, на котором произошла ошибка: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER).toString() + "\n" +
                "Команда ФН, на которой произошла ошибка: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_COMMAND_CODE)

        return Pair(0, result)
    }

    fun getOFDReceipt(docNumberVal: Int): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_TICKET_BY_DOC_NUMBER)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER, docNumberVal)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Дата и время из квитанции ОФД: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!) + "\n" +
                "Номер ФД из квитанции: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER).toString() + "\n" +
                "Фискальный признак ОФД: " + fptrHolder.fptr.getParamByteArray(IFptr.LIBFPTR_PARAM_OFD_FISCAL_SIGN)

        return Pair(0, result)
    }

    fun getDocInfo(docNumberVal: Int): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_DOCUMENT_BY_NUMBER)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER, docNumberVal)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Тип документа: "
        val docType = fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_FN_DOCUMENT_TYPE)
        when (docType.toInt()) {
            IFptr.LIBFPTR_FN_DOC_REGISTRATION -> result += "Отчет о регистрации" + "\n"
            IFptr.LIBFPTR_FN_DOC_OPEN_SHIFT -> result += "Отчет об открытии смены" + "\n"
            IFptr.LIBFPTR_FN_DOC_RECEIPT -> result += "Кассовый чек" + "\n"
            IFptr.LIBFPTR_FN_DOC_BSO -> result += "Бланк строгой отчетности" + "\n"
            IFptr.LIBFPTR_FN_DOC_CLOSE_SHIFT -> result += " Отчет о закрытии смены" + "\n"
            IFptr.LIBFPTR_FN_DOC_CLOSE_FN -> result += "Отчет о закрытии фискального накопителя" + "\n"
            IFptr.LIBFPTR_FN_DOC_OPERATOR_CONFIRMATION -> result += "Подтверждение оператора" + "\n"
            IFptr.LIBFPTR_FN_DOC_REREGISTRATION -> result += "Отчет об изменении параметров регистрации" + "\n"
            IFptr.LIBFPTR_FN_DOC_EXCHANGE_STATUS -> result += "Отчет о текущем состоянии расчетов" + "\n"
            IFptr.LIBFPTR_FN_DOC_CORRECTION -> result += "Кассовый чек коррекции" + "\n"
            IFptr.LIBFPTR_FN_DOC_BSO_CORRECTION -> result += "Бланк строгой отчетности коррекции" + "\n"

        }
        result += "Дата и время документа: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!) + "\n" +
                "Номер документа: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER).toString() + "\n" +
                "Фискальный признак: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_FISCAL_SIGN) + "\n" +
                "Подтверждён ОФД: " + Utils.booleanToString(fptrHolder.fptr.getParamBool(IFptr.LIBFPTR_PARAM_HAS_OFD_TICKET))

        return Pair(0, result)
    }

    fun getFNErrorDetail(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_ERROR_DETAIL)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += fptrHolder.fptr.getParamByteArray(IFptr.LIBFPTR_PARAM_FN_ERROR_DATA)

        return Pair(0, result)
    }

    fun getFNMemory(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_FREE_MEMORY)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        var result: String = ""
        result += "Ресурс данных 5-летнего хранения: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENTS_COUNT).toString() + "\n" +
                "Ресурс данных 30-дневного хранения: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_FREE_MEMORY).toString()

        return Pair(0, result)
    }

    fun getISMErrors(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_ISM_ERRORS)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair(-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Дата и время последнего успешного соединения с ОФД: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!) + "\n" +
                "Код ошибки сети: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_NETWORK_ERROR).toString() + "\n" +
                "Текст ошибки сети: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_NETWORK_ERROR_TEXT) + "\n" +
                "Код ошибки ИСМ: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_ISM_ERROR).toString() + "\n" +
                "Текст ошибки ИСМ: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_ISM_ERROR_TEXT) + "\n" +
                "Код ошибки ФН: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_FN_ERROR).toString() + "\n" +
                "Текст ошибки ФН: " + fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_FN_ERROR_TEXT) + "\n" +
                "Номер ФД, на котором произошла ошибка: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER).toString() + "\n" +
                "Команда ФН, на которой произошла ошибка: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_COMMAND_CODE)

        return Pair(0, result)
    }

    fun getISMStatus(): Pair <Int, String> {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_ISM_EXCHANGE_STATUS)
        val res = fptrHolder.fptr.fnQueryData()
        if (res == -1) {
            return Pair (-1, "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var result: String = ""
        result += "Кол-во неотправленных уведослений: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENTS_COUNT).toString() + "\n" +
                "Номер первого неотправленного уведомления: " + fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENT_NUMBER).toString() + "\n" +
                "Дата и время первого неотправленного уведомления: " + sdf.format(fptrHolder.fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME)!!)

        return Pair(0, result)
    }
}
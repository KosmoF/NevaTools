package ru.neva.drivers.ui.settings_fragment.service_docs_settings_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import javax.inject.Inject

@HiltViewModel
class ServiceDocsSettingsViewModel @Inject constructor(
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
    fun setServiceDocsSettings(printReadyDocVal: Int,
                               printClicheServiceVal: Int,
                               printDatetimeDocVal: Int,
                               printIPDocVal: Int) : String
    {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 29)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, printReadyDocVal)
        val res = fptrHolder.fptr.writeDeviceSetting()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 30)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, printClicheServiceVal)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 31)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, printDatetimeDocVal)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 301)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, printIPDocVal)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.commitSettings()

        return ""
    }

    fun getServiceDocsSettings(): ArrayList<Any>{
        val params: ArrayList<Any> = ArrayList()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  29)
        val res = fptrHolder.fptr.readDeviceSetting()
        if (res == -1) {
            return arrayListOf("Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
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

        return params
    }
}
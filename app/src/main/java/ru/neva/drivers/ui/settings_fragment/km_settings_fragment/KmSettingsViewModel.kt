package ru.neva.drivers.ui.settings_fragment.km_settings_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import javax.inject.Inject

@HiltViewModel
class KmSettingsViewModel @Inject constructor(
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
    fun setKMSettings(ismAddressVal: String,
                        ismPortVal: Int,
                        okpAddressVal: String,
                        okpPortVal: Int) : String
    {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 1000)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, ismAddressVal)
        val res = fptrHolder.fptr.writeDeviceSetting()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 1001)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, ismPortVal)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 1002)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, okpAddressVal)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 1003)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, okpPortVal)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.commitSettings()

        return ""
    }

    fun getKMSettings(): ArrayList<Any>{
        val params: ArrayList<Any> = ArrayList()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 1000)
        val res = fptrHolder.fptr.readDeviceSetting()
        if (res == -1) {
            return arrayListOf("Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
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

        return params
    }
}
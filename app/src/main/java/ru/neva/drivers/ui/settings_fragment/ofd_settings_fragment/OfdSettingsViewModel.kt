package ru.neva.drivers.ui.settings_fragment.ofd_settings_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import javax.inject.Inject

@HiltViewModel
class OfdSettingsViewModel @Inject constructor(
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
    fun setOFDSettings(ofdAddressVal: String,
                       ofdPortVal: Int,
                       ofdDNSVal: String,
                       ofdChannelVal: Int) : String
    {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 273)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, ofdAddressVal)
        val res = fptrHolder.fptr.writeDeviceSetting()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 274)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, ofdPortVal)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 275)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, ofdDNSVal)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 276)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, ofdChannelVal)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.commitSettings()

        return ""
    }

    fun getOFDSettings(): ArrayList<Any>{
        val params: ArrayList<Any> = ArrayList()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 273)
        val res = fptrHolder.fptr.readDeviceSetting()
        if (res == -1) {
            return arrayListOf("Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID,  274)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 275)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 276)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        return params
    }
}
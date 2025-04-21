package ru.neva.drivers.ui.settings_fragment.cliche_settings_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import javax.inject.Inject


@HiltViewModel
class ClicheSettingsViewModel @Inject constructor(
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
    fun setClicheSettings(footerSizeVal: Int,
                          headerSizeVal: Int,
                          cutterDistance1Val: Int,
                          cutterDistance2Val: Int,
                          clicheAutoprintVal: Int,
                          clicheAutocutVal: Int,
                          clicheTextVal: Array<String>) : String
    {
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 14)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, footerSizeVal)
        val res = fptrHolder.fptr.writeDeviceSetting()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 35)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, headerSizeVal)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 36)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, cutterDistance1Val)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 75)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, cutterDistance2Val)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 62)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, clicheAutoprintVal)
        fptrHolder.fptr.writeDeviceSetting()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 69)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, clicheAutocutVal)
        fptrHolder.fptr.writeDeviceSetting()

        for(i in clicheTextVal.indices){
            fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 184 + i)
            fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, clicheTextVal[i])
            fptrHolder.fptr.writeDeviceSetting()
        }

        fptrHolder.fptr.commitSettings()

        return ""
    }

    fun getClicheSettings(): ArrayList<Any>{
        val params: ArrayList<Any> = ArrayList()

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 14)
        val res = fptrHolder.fptr.readDeviceSetting()
        if (res == -1) {
            return arrayListOf("Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription())
        }
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 35)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 36)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 75)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 62)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 69)
        fptrHolder.fptr.readDeviceSetting()
        params.add(fptrHolder.fptr.getParamInt(IFptr.LIBFPTR_PARAM_SETTING_VALUE))

        for(i in 0..19){
            fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 184 + i)
            fptrHolder.fptr.readDeviceSetting()
            params.add(fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SETTING_VALUE))
        }

        return params
    }
}
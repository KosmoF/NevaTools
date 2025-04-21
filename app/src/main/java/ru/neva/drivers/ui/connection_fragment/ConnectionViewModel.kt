package ru.neva.drivers.ui.connection_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val fptrHolder: FptrHolder
): ViewModel() {
    fun checkConnection() : Boolean{
        return fptrHolder.fptr.isOpened
    }
    fun closeConnection(){
        fptrHolder.fptr.close()
    }
    fun getKKTSerial(): String{
        if (!checkConnection()){
            return "Нет подключения"
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS)
        fptrHolder.fptr.queryData()
        return fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_SERIAL_NUMBER)
    }
    fun getKKTModel(): String{
        if (!checkConnection()){
            return "Нет подключения"
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS)
        fptrHolder.fptr.queryData()
        return fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_MODEL_NAME)
    }
    fun getKKTSoftwareVersion(): String{
        if (!checkConnection()){
            return "Нет подключения"
        }
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS)
        fptrHolder.fptr.queryData()
        return fptrHolder.fptr.getParamString(IFptr.LIBFPTR_PARAM_UNIT_VERSION)
    }
    fun connectTcpIp(reconnectVal: String, kktModelVal: Int, ofdChannelVal: Int, kktIPVal: String, kktPortVal: String): String {
        fptrHolder.fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_AUTO_RECONNECT, reconnectVal)
        fptrHolder.fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_PORT, IFptr.LIBFPTR_PORT_TCPIP.toString())
        fptrHolder.fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_MODEL, kktModelVal.toString())
        fptrHolder.fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_OFD_CHANNEL, ofdChannelVal.toString())
        fptrHolder.fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_IPADDRESS, kktIPVal)
        fptrHolder.fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_IPPORT, kktPortVal)
        fptrHolder.fptr.applySingleSettings()
        var res: Int = 0
        res = fptrHolder.fptr.open()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        } else {
            return ""
        }
    }
    fun connectComVcom(reconnectVal: String, kktModelVal: Int, ofdChannelVal: Int, kktComVal: String, kktBaudrateVal: Int): String{
        fptrHolder.fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_AUTO_RECONNECT, reconnectVal)
        fptrHolder.fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_PORT, IFptr.LIBFPTR_PORT_COM.toString())
        fptrHolder.fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_MODEL, kktModelVal.toString())
        fptrHolder.fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_OFD_CHANNEL, ofdChannelVal.toString())
        fptrHolder.fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_COM_FILE, "COM$kktComVal")
        fptrHolder.fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_IPPORT, kktBaudrateVal.toString())
        fptrHolder.fptr.applySingleSettings()
        var res: Int = 0
        res = fptrHolder.fptr.open()
        if (res == -1) {
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        } else {
            return ""
        }
    }
}
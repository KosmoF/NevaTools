package ru.neva.drivers.ui.test_print_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.fptr.IFptr
import javax.inject.Inject

@HiltViewModel
class TestPrintViewModel @Inject constructor(
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
    fun printText(textToPrintVal: String): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_TEXT, textToPrintVal)
        if(fptrHolder.fptr.printText() == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        else{
            return ""
        }
    }

    fun printCliche(): String{
        if(fptrHolder.fptr.printCliche() == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        else{
            return ""
        }
    }

    fun printBarcode(barcodeTextVal: String,
                     barcodeTypeVal: Int): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_BARCODE, barcodeTextVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_BARCODE_TYPE, barcodeTypeVal)
        if(fptrHolder.fptr.printBarcode() == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        else{
            return ""
        }
    }

    fun printImageFromKKT(imageNumberVal: Int): String{
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_PICTURE_NUMBER, imageNumberVal)
        fptrHolder.fptr.setParam(IFptr.LIBFPTR_PARAM_ALIGNMENT, IFptr.LIBFPTR_ALIGNMENT_CENTER)
        if(fptrHolder.fptr.printPictureByNumber() == -1){
            return "Код ошибки: " + fptrHolder.fptr.errorCode().toString() + "\n" + "Текст ошибки: " + fptrHolder.fptr.errorDescription()
        }
        else{
            return ""
        }
    }
}
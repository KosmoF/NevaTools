package ru.neva.drivers.ui.test_print_fragment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentRegBinding
import ru.neva.drivers.databinding.FragmentTestPrintBinding
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class TestPrintFragment : Fragment() {

    companion object {
        fun newInstance() = TestPrintFragment()
    }

    private lateinit var viewModel: TestPrintViewModel
    private var _binding: FragmentTestPrintBinding? = null
    private val binding get() = _binding!!

    private lateinit var textToPrint: EditText
    private lateinit var printTextButton: Button
    private lateinit var printClicheButton: Button
    private lateinit var barcodeText: EditText
    private lateinit var barcodeType: Spinner
    private lateinit var printBarcodeButton: Button
    private lateinit var imageNumber: EditText
    private lateinit var printImageButton: Button

    private var textToPrintVal: String = ""
    private var barcodeTextVal: String = ""
    private var barcodeTypeVal: Int = 1
    private var imageNumberVal: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestPrintBinding.inflate(inflater, container, false)

        this.textToPrint = _binding!!.etTextToPrint
        this.printTextButton = _binding!!.textButton
        this.printClicheButton = _binding!!.setClicheButton
        this.barcodeText = _binding!!.etBarcodeText
        this.barcodeType = _binding!!.spBarcodeType
        this.printBarcodeButton = _binding!!.barcodeButton
        this.imageNumber = _binding!!.etImageNumber
        this.printImageButton = _binding!!.imageButton

        val barcodeTypes = arrayOf("1 - EAN-13",
            "11 - QR-код")

        val barcodeTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, barcodeTypes)
        barcodeType.adapter = barcodeTypeAdapter
        barcodeType.setSelection(0)

        val testPrintPrefs = requireContext().getSharedPreferences("testPrintPrefs", Context.MODE_PRIVATE)

        textToPrintVal = testPrintPrefs.getString("textToPrintVal", "").toString()
        textToPrint.text = Editable.Factory.getInstance().newEditable(textToPrintVal)

        barcodeTextVal = testPrintPrefs.getString("barcodeTextVal", "").toString()
        barcodeText.text = Editable.Factory.getInstance().newEditable(barcodeTextVal)

        barcodeTypeVal = testPrintPrefs.getInt("barcodeTypeVal", 1)
        when(barcodeTypeVal){
            1 -> barcodeType.setSelection(0)
            11 -> barcodeType.setSelection(1)
        }

        imageNumberVal = testPrintPrefs.getInt("imageNumberVal", 1)
        imageNumber.text = Editable.Factory.getInstance().newEditable(imageNumberVal.toString())

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        textToPrint.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                textToPrintVal = s.toString()
                if(textToPrintVal == ""){
                    textToPrint.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        printTextButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "textToPrintVal :$textToPrintVal\n"
            val result = viewModel.printText(textToPrintVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "printText", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "printText")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "printText", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "printText")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        printClicheButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.printCliche()
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "printCliche", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "printCliche")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "printCliche", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "printCliche")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        barcodeText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                barcodeTextVal = s.toString()
                if(barcodeTextVal == ""){
                    barcodeText.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        barcodeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> barcodeTypeVal = 1
                    1 -> barcodeTypeVal = 11
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        printBarcodeButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "barcodeTextVal :$barcodeTextVal\n" +
                    "barcodeTypeVal :$barcodeTypeVal\n"
            val result = viewModel.printBarcode(barcodeTextVal, 
                barcodeTypeVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "printBarcode", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "printBarcode")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "printBarcode", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "printBarcode")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        imageNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    imageNumberVal = 1
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                imageNumberVal = num

                if (num < 1 || num > 255) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        imageNumberVal = 1
                    } else {
                        s.replace(0, s.length, "255")
                        imageNumberVal = 255
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        printImageButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "imageNumberVal :$imageNumberVal\n"
            val result = viewModel.printImageFromKKT(imageNumberVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "printImageFromKKT", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "printImageFromKKT")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "printImageFromKKT", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "printImageFromKKT")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val testPrintPrefs = requireContext().getSharedPreferences("testPrintPrefs", Context.MODE_PRIVATE)

        with(testPrintPrefs.edit()){
            putString("textToPrintVal", textToPrintVal)
            putString("barcodeTextVal", barcodeTextVal)
            putInt("barcodeTypeVal", barcodeTypeVal)
            putInt("imageNumberVal", imageNumberVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TestPrintViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
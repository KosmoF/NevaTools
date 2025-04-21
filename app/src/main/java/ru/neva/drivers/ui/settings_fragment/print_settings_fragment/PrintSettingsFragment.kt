package ru.neva.drivers.ui.settings_fragment.print_settings_fragment

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
import android.widget.CheckBox
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentPaymentsSettingsBinding
import ru.neva.drivers.databinding.FragmentPrintSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class PrintSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = PrintSettingsFragment()
    }

    private lateinit var viewModel: PrintSettingsViewModel
    private var _binding: FragmentPrintSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton

    private lateinit var allowPrinting: CheckBox
    private lateinit var qrCorrection: Spinner
    private lateinit var printKKTNumber: CheckBox
    private lateinit var printFullPayment: CheckBox
    private lateinit var printPaymentSubject: Spinner
    private lateinit var printOFDName: CheckBox
    private lateinit var printEmail: CheckBox
    private lateinit var printVendorINN: CheckBox

    private var allowPrintingVal: Int = 0
    private var qrCorrectionVal: Int = 0
    private var printKKTNumberVal: Int = 0
    private var printFullPaymentVal: Int = 0
    private var printPaymentSubjectVal: Int = 0
    private var printOFDNameVal: Int = 0
    private var printEmailVal: Int = 0
    private var printVendorINNVal: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrintSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad

        this.allowPrinting = _binding!!.cbAllowPrinting
        this.qrCorrection = _binding!!.spQrCorrection
        this.printKKTNumber = _binding!!.cbPrintKktNumber
        this.printFullPayment = _binding!!.cbPrintFullPayment
        this.printPaymentSubject = _binding!!.spPrintPaymentSubject
        this.printOFDName = _binding!!.cbPrintOfdName
        this.printEmail = _binding!!.cbPrintEmail
        this.printVendorINN = _binding!!.cbPrintVendorInn

        val qrCorrectionLevels = arrayOf(0,1,2,4,16,32)
        val printPaymentSubjectLevels = arrayOf("0 - Не печатать",
            "1 - Печатать полное наименование",
            "3 - Печатать краткое наименование")

        val qrCorrectionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, qrCorrectionLevels)
        qrCorrection.adapter = qrCorrectionAdapter
        qrCorrection.setSelection(0)

        val printPaymentSubjectAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, printPaymentSubjectLevels)
        printPaymentSubject.adapter = printPaymentSubjectAdapter
        printPaymentSubject.setSelection(0)

        val printSettingsPrefs = requireContext().getSharedPreferences("printSettingsPrefs", Context.MODE_PRIVATE)
        
        allowPrintingVal = printSettingsPrefs.getInt("allowPrintingVal", 0)
        when(allowPrintingVal){
            1 -> allowPrinting.isChecked = true
            0 -> allowPrinting.isChecked = false
        }

        qrCorrectionVal = printSettingsPrefs.getInt("qrCorrectionVal", 0)
        when(qrCorrectionVal){
            0 -> qrCorrection.setSelection(0)
            1 -> qrCorrection.setSelection(1)
            2 -> qrCorrection.setSelection(2)
            4 -> qrCorrection.setSelection(3)
            16 -> qrCorrection.setSelection(4)
            32 -> qrCorrection.setSelection(5)
        }
        
        printKKTNumberVal = printSettingsPrefs.getInt("printKKTNumberVal", 0)
        when(printKKTNumberVal){
            1 -> printKKTNumber.isChecked = true
            0 -> printKKTNumber.isChecked = false
        }

        printFullPaymentVal = printSettingsPrefs.getInt("printFullPaymentVal", 0)
        when(printFullPaymentVal){
            1 -> printFullPayment.isChecked = true
            0 -> printFullPayment.isChecked = false
        }

        printPaymentSubjectVal = printSettingsPrefs.getInt("printPaymentSubjectVal", 0)
        when(printPaymentSubjectVal){
            0 -> printPaymentSubject.setSelection(0)
            1 -> printPaymentSubject.setSelection(1)
            3 -> printPaymentSubject.setSelection(2)
        }

        printOFDNameVal = printSettingsPrefs.getInt("printOFDNameVal", 0)
        when(printOFDNameVal){
            1 -> printOFDName.isChecked = true
            0 -> printOFDName.isChecked = false
        }

        printEmailVal = printSettingsPrefs.getInt("printEmailVal", 0)
        when(printEmailVal){
            1 -> printEmail.isChecked = true
            0 -> printEmail.isChecked = false
        }

        printVendorINNVal = printSettingsPrefs.getInt("printVendorINNVal", 0)
        when(printVendorINNVal){
            1 -> printVendorINN.isChecked = true
            0 -> printVendorINN.isChecked = false
        }

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        allowPrinting.setOnClickListener(View.OnClickListener { view ->
            when(allowPrinting.isChecked){
                true -> allowPrintingVal = 1
                false -> allowPrintingVal = 0
            }
        })

        qrCorrection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> qrCorrectionVal = 0
                    1 -> qrCorrectionVal = 1
                    2 -> qrCorrectionVal = 2
                    3 -> qrCorrectionVal = 4
                    4 -> qrCorrectionVal = 16
                    5 -> qrCorrectionVal = 32
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        printKKTNumber.setOnClickListener(View.OnClickListener { view ->
            when(printKKTNumber.isChecked){
                true -> printKKTNumberVal = 1
                false -> printKKTNumberVal = 0
            }
        })

        printFullPayment.setOnClickListener(View.OnClickListener { view ->
            when(printFullPayment.isChecked){
                true -> printFullPaymentVal = 1
                false -> printFullPaymentVal = 0
            }
        })

        printPaymentSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> printPaymentSubjectVal = 0
                    1 -> printPaymentSubjectVal = 1
                    2 -> printPaymentSubjectVal = 3
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        printOFDName.setOnClickListener(View.OnClickListener { view ->
            when(printFullPayment.isChecked){
                true -> printOFDNameVal = 1
                false -> printOFDNameVal = 0
            }
        })

        printEmail.setOnClickListener(View.OnClickListener { view ->
            when(printFullPayment.isChecked){
                true -> printEmailVal = 1
                false -> printEmailVal = 0
            }
        })

        printVendorINN.setOnClickListener(View.OnClickListener { view ->
            when(printFullPayment.isChecked){
                true ->  printVendorINNVal = 1
                false ->  printVendorINNVal = 0
            }
        })

        FABSave.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "allowPrintingVal :$allowPrintingVal\n" +
                    "qrCorrectionVal :$qrCorrectionVal\n" +
                    "printKKTNumberVal :$printKKTNumberVal\n" +
                    "printFullPaymentVal :$printFullPaymentVal\n" +
                    "printPaymentSubjectVal :$printPaymentSubjectVal\n" +
                    "printOFDNameVal :$printOFDNameVal\n" +
                    "printEmailVal :$printEmailVal\n" +
                    "printVendorINNVal :$printVendorINNVal\n"
            val result = viewModel.setPrintSettings(allowPrintingVal,
                qrCorrectionVal,
                printKKTNumberVal,
                printFullPaymentVal,
                printPaymentSubjectVal,
                printOFDNameVal,
                printEmailVal,
                printVendorINNVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "setPrintSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setPrintSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "setPrintSettings", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setPrintSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        FABLoad.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.getPrintSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getPrintSettings", paramsString, result[0].toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getPrintSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getPrintSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getPrintSettings")}}
                allowPrintingVal = result[0].toString().toInt()
                when(allowPrintingVal){
                    1 -> allowPrinting.isChecked = true
                    0 -> allowPrinting.isChecked = false
                }

                qrCorrectionVal = result[1].toString().toInt()
                when(qrCorrectionVal){
                    0 -> qrCorrection.setSelection(0)
                    1 -> qrCorrection.setSelection(1)
                    2 -> qrCorrection.setSelection(2)
                    4 -> qrCorrection.setSelection(3)
                    16 -> qrCorrection.setSelection(4)
                    32 -> qrCorrection.setSelection(5)
                }

                printKKTNumberVal = result[2].toString().toInt()
                when(printKKTNumberVal){
                    1 -> printKKTNumber.isChecked = true
                    0 -> printKKTNumber.isChecked = false
                }

                printFullPaymentVal = result[3].toString().toInt()
                when(printFullPaymentVal){
                    1 -> printFullPayment.isChecked = true
                    0 -> printFullPayment.isChecked = false
                }

                printPaymentSubjectVal = result[4].toString().toInt()
                when(printPaymentSubjectVal){
                    0 -> printPaymentSubject.setSelection(0)
                    1 -> printPaymentSubject.setSelection(1)
                    3 -> printPaymentSubject.setSelection(2)
                }

                printOFDNameVal = result[5].toString().toInt()
                when(printOFDNameVal){
                    1 -> printOFDName.isChecked = true
                    0 -> printOFDName.isChecked = false
                }

                printEmailVal = result[6].toString().toInt()
                when(printEmailVal){
                    1 -> printEmail.isChecked = true
                    0 -> printEmail.isChecked = false
                }

                printVendorINNVal = result[7].toString().toInt()
                when(printVendorINNVal){
                    1 -> printVendorINN.isChecked = true
                    0 -> printVendorINN.isChecked = false
                }

                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val printSettingsPrefs = requireContext().getSharedPreferences("printSettingsPrefs", Context.MODE_PRIVATE)

        with(printSettingsPrefs.edit()){
            putInt("allowPrintingVal", allowPrintingVal)
            putInt("qrCorrectionVal", qrCorrectionVal)
            putInt("printKKTNumberVal", printKKTNumberVal)
            putInt("printFullPaymentVal", printFullPaymentVal)
            putInt("printPaymentSubjectVal", printPaymentSubjectVal)
            putInt("printOFDNameVal", printOFDNameVal)
            putInt("printEmailVal", printEmailVal)
            putInt("printVendorINNVal", printVendorINNVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PrintSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
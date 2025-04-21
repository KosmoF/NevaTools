package ru.neva.drivers.ui.reports_fragment

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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.createViewModelLazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentRegBinding
import ru.neva.drivers.databinding.FragmentReportsBinding
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class ReportsFragment : Fragment() {

    companion object {
        fun newInstance() = ReportsFragment()
    }

    private lateinit var viewModel: ReportsViewModel
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private lateinit var reportType: Spinner
    private lateinit var docNumber: EditText
    private lateinit var makeReportButton: Button

    private var reportTypeVal: Int = 0
    private var docNumberVal: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)

        this.reportType = _binding!!.spReportType
        this.docNumber = _binding!!.etDocNumber
        this.makeReportButton = _binding!!.makeReportButton

        val reportTypes = arrayOf("0 - Отчёт о закрытии смены",
            "1 - X-отчёт",
            "2 - Копия последнего документа",
            "3 - Отчёт о состоянии расчётов",
            "5 - Информация о ККТ",
            "6 - Тест связи с ОФД",
            "7 - Печать документа по номеру",
            "12 - Итоги регистрации/перерегистрации ККТ",
            "31 - Информация о подписках")

        val reportTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, reportTypes)
        reportType.adapter = reportTypeAdapter
        reportType.setSelection(0)

        val reportsPrefs = requireContext().getSharedPreferences("reportsPrefs", Context.MODE_PRIVATE)

        reportTypeVal = reportsPrefs.getInt("reportTypeVal", 0)
        when(reportTypeVal){
            0 -> reportType.setSelection(0)
            1 -> reportType.setSelection(1)
            2 -> reportType.setSelection(2)
            3 -> reportType.setSelection(3)
            5 -> reportType.setSelection(4)
            6 -> reportType.setSelection(5)
            7 -> reportType.setSelection(6)
            12 -> reportType.setSelection(7)
            31 -> reportType.setSelection(8)
        }

        docNumberVal = reportsPrefs.getInt("docNumberVal", 1)
        docNumber.text = Editable.Factory.getInstance().newEditable(docNumberVal.toString())

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        reportType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> reportTypeVal = 0
                    1 -> reportTypeVal = 1
                    2 -> reportTypeVal = 2
                    3 -> reportTypeVal = 3
                    4 -> reportTypeVal = 5
                    5 -> reportTypeVal = 6
                    6 -> reportTypeVal = 7
                    7 -> reportTypeVal = 12
                    8 -> reportTypeVal = 31
                }
                if(position == 6){
                    docNumber.isEnabled = true
                }
                else{
                    docNumber.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        docNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    docNumberVal = 1
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                docNumberVal = num.toString().toInt()

                if (num < 1 || num > 65535) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        docNumberVal = 1
                    } else {
                        s.replace(0, s.length, "65535")
                        docNumberVal = 65535
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        makeReportButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "reportTypeVal :$reportTypeVal\n"
            var result : String = ""
            when (reportTypeVal){
                0 -> result = viewModel.closeShift()
                1 -> result = viewModel.printXReport()
                2 -> result = viewModel.printLastDoc()
                3 -> result = viewModel.printOFDStatus()
                5 -> result = viewModel.printKKTInfo()
                6 -> result = viewModel.printOFDTest()
                7 -> result = viewModel.printDocByNumber(docNumberVal)
                12 -> result = viewModel.printRegReport()
                31 -> result = viewModel.printLicenseInfo()
            }
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "makeReport", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "makeReport")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "makeReport", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "makeReport")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val reportsPrefs = requireContext().getSharedPreferences("reportsPrefs", Context.MODE_PRIVATE)

        with(reportsPrefs.edit()){
            putInt("reportTypeVal", reportTypeVal)
            putInt("docNumberVal", docNumberVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReportsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
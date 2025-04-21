package ru.neva.drivers.ui.fn_info_fragment

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
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentFnInfoBinding
import ru.neva.drivers.databinding.FragmentKktInfoBinding
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class FnInfoFragment : Fragment() {

    companion object {
        fun newInstance() = FnInfoFragment()
    }

    private var _binding: FragmentFnInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FnInfoViewModel

    private lateinit var requestType: Spinner
    private lateinit var docNumber: EditText
    private lateinit var readInfoButton: Button
    private lateinit var requestResult: TextView

    private var requestTypeVal: Int = 1
    private var docNumberVal: Int = 1
    private var requestResultVal: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFnInfoBinding.inflate(inflater, container, false)

        this.requestType = _binding!!.spRequestType
        this.docNumber = _binding!!.etDocNumber
        this.readInfoButton = _binding!!.readInfoButton
        this.requestResult = _binding!!.tvResultText

        val requestTypes = arrayOf("1 - Статус информационного обмена",
            "2 - Информация о ФН",
            "3 - Информация о последней регистрации/перерегистрации",
            "5 - Информация о последнем документе",
            "6 - Информация о смене",
            "7 - Информация о версиях ФФД",
            "8 - Срок действия ФН",
            "9 - Краткие регистрационные данные",
            "11 - Ошибки ФН / ОФД / сети",
            "12 - Квитанция ОФД по номеру документа",
            "13 - Информация о документе по номеру",
            "15 - Детализация ошибки ФН",
            "17 - Ресурс свободной памяти",
            "19 - Ошибки ИСМ",
            "20 - Статус информационного обмена с ИСМ")

        val requestTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, requestTypes)
        requestType.adapter = requestTypeAdapter
        requestType.setSelection(0)

        val fnInfoPrefs = requireContext().getSharedPreferences("fnInfoPrefs", Context.MODE_PRIVATE)

        requestTypeVal = fnInfoPrefs.getInt("requestTypeVal", 1)
        when(requestTypeVal){
            1 -> requestType.setSelection(0)
            2 -> requestType.setSelection(1)
            3 -> requestType.setSelection(2)
            5 -> requestType.setSelection(3)
            6 -> requestType.setSelection(4)
            7 -> requestType.setSelection(5)
            8 -> requestType.setSelection(6)
            9 -> requestType.setSelection(7)
            11 -> requestType.setSelection(8)
            12 -> requestType.setSelection(9)
            13 -> requestType.setSelection(10)
            15 -> requestType.setSelection(11)
            17 -> requestType.setSelection(12)
            19 -> requestType.setSelection(13)
            20 -> requestType.setSelection(14)
        }

        docNumberVal = fnInfoPrefs.getInt("docNumberVal", 1)
        docNumber.text = Editable.Factory.getInstance().newEditable(docNumberVal.toString())

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        requestType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> requestTypeVal = 1
                    1 -> requestTypeVal = 2
                    2 -> requestTypeVal = 3
                    3 -> requestTypeVal = 5
                    4 -> requestTypeVal = 6
                    5 -> requestTypeVal = 7
                    6 -> requestTypeVal = 8
                    7 -> requestTypeVal = 9
                    8 -> requestTypeVal = 11
                    9 -> requestTypeVal = 12
                    10 -> requestTypeVal = 13
                    11-> requestTypeVal = 15
                    12 -> requestTypeVal = 17
                    13 -> requestTypeVal = 19
                    14 -> requestTypeVal = 20
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
                docNumberVal = num

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

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        readInfoButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "requestTypeVal :$requestTypeVal\n"
            var result : Pair<Int,String> = Pair(0,"")
            when (requestTypeVal){
                1 -> result = viewModel.getOFDStatus()
                2 -> result = viewModel.getFnInfo()
                3 -> result = viewModel.getLastRegInfo()
                5 -> result = viewModel.getLastDocInfo()
                6 -> result = viewModel.getShiftInfo()
                7 -> result = viewModel.getFFDInfo()
                8 -> result = viewModel.getFNExpiration()
                9 -> result = viewModel.getRegInfo()
                11 -> result = viewModel.getOFDErrors()
                12 -> result = viewModel.getOFDReceipt(docNumberVal)
                13 -> result = viewModel.getDocInfo(docNumberVal)
                15 -> result = viewModel.getFNErrorDetail()
                17 -> result = viewModel.getFNMemory()
                19 -> result = viewModel.getISMErrors()
                20 -> result = viewModel.getISMStatus()
            }
            if(result.first != -1){
                logger.log(Logger.LogLevel.SUCCESS, "readFNInfo", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "readFNInfo")}}
                requestResult.text = ""
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
                requestResult.text = result.second
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "readFNInfo", paramsString, result.second)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "readFNInfo")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result.second)
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

        val fnInfoPrefs = requireContext().getSharedPreferences("fnInfoPrefs", Context.MODE_PRIVATE)

        with(fnInfoPrefs.edit()){
            putInt("requestTypeVal", requestTypeVal)
            putInt("docNumberVal", docNumberVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FnInfoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
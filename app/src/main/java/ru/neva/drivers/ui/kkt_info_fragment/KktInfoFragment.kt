package ru.neva.drivers.ui.kkt_info_fragment

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
import ru.neva.drivers.databinding.FragmentConnectionBinding
import ru.neva.drivers.databinding.FragmentKktInfoBinding
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class KktInfoFragment : Fragment() {

    companion object {
        fun newInstance() = KktInfoFragment()
    }

    private var _binding: FragmentKktInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: KktInfoViewModel

    private lateinit var requestType: Spinner
    private lateinit var chequeType: Spinner
    private lateinit var taxType: Spinner
    private lateinit var imgNumber: EditText
    private lateinit var readInfoButton: Button
    private lateinit var requestResult: TextView

    private var requestTypeVal: Int = 0
    private var chequeTypeVal: Int = 1
    private var taxTypeVal: Int = 2
    private var imgNumberVal: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKktInfoBinding.inflate(inflater, container, false)

        this.requestType = _binding!!.spRequestType
        this.chequeType = _binding!!.spChequeType
        this.taxType = _binding!!.spTaxType
        this.imgNumber = _binding!!.etImgNumber
        this.readInfoButton = _binding!!.readInfoButton
        this.requestResult = _binding!!.tvResultText

        val requestTypes = arrayOf("0 - Общая информация и статус ККТ",
            "1 - Сумма наличных в ДЯ",
            "3 - Параметры картинки в памяти",
            "5 - Сумма регистраций",
            "6 - Кол-во регистраций",
            "7 - Сумма платежей",
            "8 - Сумма внесений",
            "9 - Кол-во внесений",
            "10 - Сумма выплат",
            "11 - Кол-во выплат",
            "12 - Выручка",
            "13 - Текущие дата и время ККТ",
            "14 - Состояние смены",
            "15 - Состояние чека",
            "16 - Заводской номер",
            "17 - Информация о модели ККТ",
            "18 - Ширина ленты",
            "23 - Сумма налога за смену", // Чек, налог
            "24 - Сумма налога на чек", // Налог
            "25 - Необнуляемая сумма", // Чек
            "26 - Количество чеков за смену", // Чек
            "35 - MAC-адрес",
            "36 - Время работы ККТ",
            "38 - Сумма скидок и сумма надбавок за смену", // Чек
            "39 - Код привязки к ЛК",
            "40 - Дата и время последней успешной отправки документа в ОФД",
            "41 - Короткий запрос статуса",
            "42 - Состояние массива картинок",
            "45 - Сменный итог", // Чек
            "49 - Дата и время последней успешной отправки документа в ОИСМ",
            "98 - Статус информационного обмена с ЛК")

        val chequeTypes = arrayOf("1 - Чек прихода",
            "2 - Чек возврата прихода",
            "4 - Чек расхода",
            "5 - Чек возврата расхода")

        val taxTypes = arrayOf("2 - 10%",
            "4 - 10/100",
            "5 - 0%",
            "6 - Не облагается",
            "7 - 20%",
            "8 - 20/120")

        val requestTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, requestTypes)
        requestType.adapter = requestTypeAdapter
        requestType.setSelection(0)

        val chequeTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, chequeTypes)
        chequeType.adapter = chequeTypeAdapter
        requestType.setSelection(0)

        val taxTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, taxTypes)
        taxType.adapter = taxTypeAdapter
        taxType.setSelection(0)

        val kktInfoPrefs = requireContext().getSharedPreferences("kktInfoPrefs", Context.MODE_PRIVATE)

        requestTypeVal = kktInfoPrefs.getInt("requestTypeVal", 1)
        when(requestTypeVal){
            0 -> requestType.setSelection(0)
            1 -> requestType.setSelection(1)
            3 -> requestType.setSelection(2)
            5 -> requestType.setSelection(3)
            6 -> requestType.setSelection(4)
            7 -> requestType.setSelection(5)
            8 -> requestType.setSelection(6)
            9 -> requestType.setSelection(7)
            10 -> requestType.setSelection(8)
            11 -> requestType.setSelection(9)
            12 -> requestType.setSelection(10)
            13 -> requestType.setSelection(11)
            14 -> requestType.setSelection(12)
            15 -> requestType.setSelection(13)
            16 -> requestType.setSelection(14)
            17 -> requestType.setSelection(15)
            18 -> requestType.setSelection(16)
            23 -> requestType.setSelection(17)
            24 -> requestType.setSelection(18)
            25 -> requestType.setSelection(19)
            26 -> requestType.setSelection(20)
            35 -> requestType.setSelection(21)
            36 -> requestType.setSelection(22)
            38 -> requestType.setSelection(23)
            39 -> requestType.setSelection(24)
            40 -> requestType.setSelection(25)
            41 -> requestType.setSelection(26)
            42 -> requestType.setSelection(27)
            45 -> requestType.setSelection(28)
            49 -> requestType.setSelection(29)
            98 -> requestType.setSelection(30)
        }

        chequeTypeVal = kktInfoPrefs.getInt("chequeTypeVal", 1)
        when(chequeTypeVal){
            1 -> chequeType.setSelection(0)
            2 -> chequeType.setSelection(1)
            4 -> chequeType.setSelection(2)
            5 -> chequeType.setSelection(3)
        }

        taxTypeVal = kktInfoPrefs.getInt("taxTypeVal", 2)
        when(taxTypeVal){
            2 -> taxType.setSelection(0)
            4 -> taxType.setSelection(1)
            5 -> taxType.setSelection(2)
            6 -> taxType.setSelection(3)
            7 -> taxType.setSelection(4)
            8 -> taxType.setSelection(5)
        }

        imgNumberVal = kktInfoPrefs.getInt("imgNumberVal", 1)
        imgNumber.text = Editable.Factory.getInstance().newEditable(imgNumberVal.toString())

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
                    0 -> requestTypeVal = 0
                    1 -> requestTypeVal = 1
                    2 -> requestTypeVal = 3
                    3 -> requestTypeVal = 5
                    4 -> requestTypeVal = 6
                    5 -> requestTypeVal = 7
                    6 -> requestTypeVal = 8
                    7 -> requestTypeVal = 9
                    8 -> requestTypeVal = 10
                    9 -> requestTypeVal = 11
                    10 -> requestTypeVal = 12
                    11 -> requestTypeVal = 13
                    12 -> requestTypeVal = 14
                    13 -> requestTypeVal = 15
                    14 -> requestTypeVal = 16
                    15 -> requestTypeVal = 17
                    16 -> requestTypeVal = 18
                    17 -> requestTypeVal = 23
                    18 -> requestTypeVal = 24
                    19 -> requestTypeVal = 25
                    20 -> requestTypeVal = 26
                    21 -> requestTypeVal = 35
                    22 -> requestTypeVal = 36
                    23 -> requestTypeVal = 38
                    24 -> requestTypeVal = 39
                    25 -> requestTypeVal = 40
                    26 -> requestTypeVal = 41
                    27 -> requestTypeVal = 42
                    28 -> requestTypeVal = 45
                    29 -> requestTypeVal = 49
                    30 -> requestTypeVal = 98
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        chequeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> chequeTypeVal = 1
                    1 -> chequeTypeVal = 2
                    2 -> chequeTypeVal = 4
                    3 -> chequeTypeVal = 5
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        taxType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> taxTypeVal = 2
                    1 -> taxTypeVal = 4
                    2 -> taxTypeVal = 5
                    3 -> taxTypeVal = 6
                    4 -> taxTypeVal = 7
                    5 -> taxTypeVal = 8
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        imgNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    imgNumberVal = 1
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                imgNumberVal = num

                if (num < 1 || num > 255) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        imgNumberVal = 1
                    } else {
                        s.replace(0, s.length, "255")
                        imgNumberVal = 255
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        readInfoButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "requestTypeVal :$requestTypeVal\n"
            var result : Pair<Int,String> = Pair(0,"")
            when (requestTypeVal){
                0 -> result = viewModel.getKKTStatus()
                1 -> result = viewModel.getCashSum()
                3 -> result = viewModel.getImgInfo(imgNumberVal)
                5 -> result = viewModel.getRegSumm(chequeTypeVal)
                6 -> result = viewModel.getRegCount(chequeTypeVal)
                7 -> result = viewModel.getPaymentsSumm(chequeTypeVal)
                8 -> result = viewModel.getCashInSumm()
                9 -> result = viewModel.getCashInCount()
                10 -> result = viewModel.getCashOutSumm()
                11 -> result = viewModel.getCashOutCount()
                12 -> result = viewModel.getRevenue()
                13 -> result = viewModel.getKKTDatetime()
                14 -> result = viewModel.getShiftState()
                15 -> result = viewModel.getChequeState()
                16 -> result = viewModel.getSerialNumber()
                17 -> result = viewModel.getModelInfo()
                18 -> result = viewModel.getLineLength()
                23 -> result = viewModel.getShiftTax(chequeTypeVal, taxTypeVal)
                24 -> result = viewModel.getChequeTax(taxTypeVal)
                25 -> result = viewModel.getNotNullableSumm(chequeTypeVal)
                26 -> result = viewModel.getChequesCount(chequeTypeVal)
                35 -> result = viewModel.getMACAddress()
                36 -> result = viewModel.getKKTUptime()
                38 -> result = viewModel.getDiscountSumm(chequeTypeVal)
                39 -> result = viewModel.getLKUserCode()
                40 -> result = viewModel.getOFDDatetime()
                41 -> result = viewModel.getKKTStatusShort()
                42 -> result = viewModel.getImgsInfo()
                45 -> result = viewModel.getShiftTotals(chequeTypeVal)
                49 -> result = viewModel.getISMDatetime()
                98 -> result = viewModel.getLKStatus()
            }
            if(result.first != -1){
                logger.log(Logger.LogLevel.SUCCESS, "readKKTInfo", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "readKKTInfo")}}
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
                logger.log(Logger.LogLevel.ERROR, "readKKTInfo", paramsString, result.second)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "readKKTInfo")}}
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

        val kktInfoPrefs = requireContext().getSharedPreferences("kktInfoPrefs", Context.MODE_PRIVATE)

        with(kktInfoPrefs.edit()){
            putInt("requestTypeVal", requestTypeVal)
            putInt("chequeTypeVal", chequeTypeVal)
            putInt("taxTypeVal", taxTypeVal)
            putInt("imgNumberVal",imgNumberVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(KktInfoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
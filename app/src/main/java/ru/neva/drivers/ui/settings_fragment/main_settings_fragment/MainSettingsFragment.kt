package ru.neva.drivers.ui.settings_fragment.main_settings_fragment

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
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentKmSettingsBinding
import ru.neva.drivers.databinding.FragmentMainSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class MainSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = MainSettingsFragment()
    }

    private lateinit var viewModel: MainSettingsViewModel
    private var _binding: FragmentMainSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton

    private lateinit var kktNumber: EditText
    private lateinit var encashment: CheckBox
    private lateinit var openCashbox: CheckBox
    private lateinit var kktProtocol: Spinner
    private lateinit var defaultSNO: Spinner
    private lateinit var paymentMethod: Spinner
    private lateinit var cashAccounting: CheckBox
    private lateinit var paymentSubject: Spinner
    private lateinit var cutMethod: Spinner
    private lateinit var cutCheques: CheckBox
    private lateinit var cutReports: CheckBox

    private var kktNumberVal: Int = 1
    private var encashmentVal: Int = 0
    private var openCashboxVal: Int = 0
    private var kktProtocolVal: Int = 1
    private var defaultSNOVal: Int = 0
    private var paymentMethodVal: Int = 1
    private var cashAccountingVal: Int = 0
    private var paymentSubjectVal: Int = 1
    private var cutMethodVal: Int = 1
    private var cutChequesVal: Int = 0
    private var cutReportsVal: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad

        this.kktNumber = _binding!!.etKktNumber
        this.encashment = _binding!!.cbEncashment
        this.openCashbox = _binding!!.cbOpenCashbox
        this.kktProtocol = _binding!!.spKktProtocol
        this.defaultSNO = _binding!!.spDefaultSno
        this.paymentMethod = _binding!!.spPaymentMethod
        this.cashAccounting = _binding!!.cbCashAccounting
        this.paymentSubject = _binding!!.spPaymentSubject
        this.cutMethod = _binding!!.spCutMethod
        this.cutCheques = _binding!!.cbCutCheques
        this.cutReports = _binding!!.cbCutReports

        val kktProtocols = arrayOf("1 - Автоматически",
            "2 - Нева 1.0")

        val defaultSNOs = arrayOf("0 - Не выбрана",
            "1 - Традиционная СНО",
            "2 - Упрощенная СНО (Доход)",
            "4 - Упрощенная СНО (Доход минус Расход)",
            "16 - Единый сельскохозяйственный налог",
            "32 - Патентная система налогообложения")

        val paymentMethods = arrayOf("1 - Предоплата 100%",
            "2 - Предоплата",
            "3 - Аванс",
            "4 - Полный расчет",
            "5 - Частичный расчет и кредит",
            "6 - Передача в кредит",
            "7 - Оплата кредита")

        val paymentSubjects = arrayOf("1 - Товар",
            "2 - Подакцизный товар",
            "3 - Работа",
            "4 - Услуга",
            "5 - Ставка азартной игры",
            "6 - Выигрыш азартной игры",
            "7 - Лотерейный билет",
            "8 - Выигрыш лотереи",
            "9 - Предоставление РИД",
            "10 - Платеж",
            "11 - Агентское вознаграждение",
            "12 - Выплата",
            "13 - Иной предмет расчета",
            "14 - Имущественное право",
            "15 - Внереализационный доход",
            "16 - Иные платежи и взносы",
            "17 - Торговый сбор",
            "18 - Курортный сбор",
            "19 - Залог",
            "20 - Расход",
            "21 - Взносы на ОПС ИП",
            "22 - Взносы на ОПС",
            "23 - Взносы на ОМС ИП",
            "24 - Взносы на ОМС",
            "25 - Взносы на ОСС",
            "26 - Платеж казино",
            "27 - Выдача денежных средств",
            "30 - Подакцизный товар, не имеющий КМ",
            "31 - Подакцизный товар, имеющий КМ",
            "32 - Товар, не имеющий КМ, за исключением подакцизного",
            "33 - Товар, имеющий КМ, за исключением подакцизного")

        val cutMethods = arrayOf("0 - Не отрезать",
            "1 - Не полностью",
            "2 - Полностью")

        val kktProtocolAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kktProtocols)
        kktProtocol.adapter = kktProtocolAdapter
        kktProtocol.setSelection(1)

        val defaultSNOAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultSNOs)
        defaultSNO.adapter = defaultSNOAdapter
        defaultSNO.setSelection(0)

        val paymentMethodAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentMethods)
        paymentMethod.adapter = paymentMethodAdapter
        paymentMethod.setSelection(0)

        val paymentSubjectAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentSubjects)
        paymentSubject.adapter = paymentSubjectAdapter
        paymentSubject.setSelection(0)

        val cutMethodAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cutMethods)
        cutMethod.adapter = cutMethodAdapter
        cutMethod.setSelection(0)

        val mainSettingsPrefs = requireContext().getSharedPreferences("mainSettingsPrefs", Context.MODE_PRIVATE)

        kktNumberVal = mainSettingsPrefs.getInt("kktNumberVal", 1)
        kktNumber.text = Editable.Factory.getInstance().newEditable(kktNumberVal.toString())
        
        encashmentVal = mainSettingsPrefs.getInt("encashmentVal", 0)
        when(encashmentVal){
            1 -> encashment.isChecked = true
            0 -> encashment.isChecked = false
        }

        openCashboxVal = mainSettingsPrefs.getInt("openCashboxVal", 0)
        when(openCashboxVal){
            1 -> openCashbox.isChecked = true
            0 -> openCashbox.isChecked = false
        }

        kktProtocolVal = mainSettingsPrefs.getInt("kktProtocolVal", 1)
        when(kktProtocolVal){
            1 -> kktProtocol.setSelection(0)
            2 -> kktProtocol.setSelection(1)
        }

        defaultSNOVal = mainSettingsPrefs.getInt("defaultSNOVal", 0)
        when(defaultSNOVal){
            0 -> defaultSNO.setSelection(0)
            1 -> defaultSNO.setSelection(1)
            2 -> defaultSNO.setSelection(2)
            4 -> defaultSNO.setSelection(3)
            16 -> defaultSNO.setSelection(4)
            32 -> defaultSNO.setSelection(5)
        }

        paymentMethodVal = mainSettingsPrefs.getInt("paymentMethodVal", 1)
        when(paymentMethodVal){
            1 -> paymentMethod.setSelection(0)
            2 -> paymentMethod.setSelection(1)
            3 -> paymentMethod.setSelection(2)
            4 -> paymentMethod.setSelection(3)
            5 -> paymentMethod.setSelection(4)
            6 -> paymentMethod.setSelection(5)
            7 -> paymentMethod.setSelection(6)
        }

        cashAccountingVal = mainSettingsPrefs.getInt("cashAccountingVal", 0)
        when(cashAccountingVal){
            1 -> cashAccounting.isChecked = true
            0 -> cashAccounting.isChecked = false
        }

        paymentSubjectVal = mainSettingsPrefs.getInt("paymentSubjectVal", 1)
        when(paymentSubjectVal){
            1 -> paymentSubject.setSelection(0)
            2 -> paymentSubject.setSelection(1)
            3 -> paymentSubject.setSelection(2)
            4 -> paymentSubject.setSelection(3)
            5 -> paymentSubject.setSelection(4)
            6 -> paymentSubject.setSelection(5)
            7 -> paymentSubject.setSelection(6)
            8 -> paymentSubject.setSelection(7)
            9 -> paymentSubject.setSelection(8)
            10 -> paymentSubject.setSelection(9)
            11 -> paymentSubject.setSelection(10)
            12 -> paymentSubject.setSelection(11)
            13 -> paymentSubject.setSelection(12)
            14 -> paymentSubject.setSelection(13)
            15 -> paymentSubject.setSelection(14)
            16 -> paymentSubject.setSelection(15)
            17 -> paymentSubject.setSelection(16)
            18 -> paymentSubject.setSelection(17)
            19 -> paymentSubject.setSelection(18)
            20 -> paymentSubject.setSelection(19)
            21 -> paymentSubject.setSelection(20)
            22 -> paymentSubject.setSelection(21)
            23 -> paymentSubject.setSelection(22)
            24 -> paymentSubject.setSelection(23)
            25 -> paymentSubject.setSelection(24)
            26 -> paymentSubject.setSelection(25)
            27 -> paymentSubject.setSelection(26)
            30 -> paymentSubject.setSelection(27)
            31 -> paymentSubject.setSelection(28)
            32 -> paymentSubject.setSelection(29)
            33 -> paymentSubject.setSelection(30)
        }

        cutMethodVal = mainSettingsPrefs.getInt("cutMethodVal", 0)
        when(cutMethodVal){
            0 -> cutMethod.setSelection(0)
            1 -> cutMethod.setSelection(1)
            2 -> cutMethod.setSelection(2)
        }

        cutChequesVal = mainSettingsPrefs.getInt("cutChequesVal", 0)
        when(cutChequesVal){
            1 -> cutCheques.isChecked = true
            0 -> cutCheques.isChecked = false
        }

        cutReportsVal = mainSettingsPrefs.getInt("cutReportsVal", 0)
        when(cutReportsVal){
            1 -> cutReports.isChecked = true
            0 -> cutReports.isChecked = false
        }

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        kktNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    kktNumberVal = 1
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                kktNumberVal = num

                if (num < 1 || num > 255) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        kktNumberVal = 1
                    } else {
                        s.replace(0, s.length, "255")
                        kktNumberVal = 255
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        encashment.setOnClickListener(View.OnClickListener { view ->
            when(encashment.isChecked){
                true -> encashmentVal = 1
                false -> encashmentVal = 0
            }
        })

        openCashbox.setOnClickListener(View.OnClickListener { view ->
            when(openCashbox.isChecked){
                true -> openCashboxVal = 1
                false -> openCashboxVal = 0
            }
        })

        kktProtocol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> kktProtocolVal = 1
                    1 -> kktProtocolVal = 2
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        defaultSNO.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> defaultSNOVal = 0
                    1 -> defaultSNOVal = 1
                    2 -> defaultSNOVal = 2
                    3 -> defaultSNOVal = 4
                    4 -> defaultSNOVal = 16
                    5 -> defaultSNOVal = 32
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        paymentMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> paymentMethodVal = 1
                    1 -> paymentMethodVal = 2
                    2 -> paymentMethodVal = 3
                    3 -> paymentMethodVal = 4
                    4 -> paymentMethodVal = 5
                    5 -> paymentMethodVal = 6
                    6 -> paymentMethodVal = 7
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        cashAccounting.setOnClickListener(View.OnClickListener { view ->
            when(cashAccounting.isChecked){
                true -> cashAccountingVal = 1
                false -> cashAccountingVal = 0
            }
        })

        paymentSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> paymentSubjectVal = 1
                    1 -> paymentSubjectVal = 2
                    2 -> paymentSubjectVal = 3
                    3 -> paymentSubjectVal = 4
                    4 -> paymentSubjectVal = 5
                    5 -> paymentSubjectVal = 6
                    6 -> paymentSubjectVal = 7
                    7 -> paymentSubjectVal = 8
                    8 -> paymentSubjectVal = 9
                    9 -> paymentSubjectVal = 10
                    10 -> paymentSubjectVal = 11
                    11 -> paymentSubjectVal = 12
                    12 -> paymentSubjectVal = 13
                    13 -> paymentSubjectVal = 14
                    14 -> paymentSubjectVal = 15
                    15 -> paymentSubjectVal = 16
                    16 -> paymentSubjectVal = 17
                    17 -> paymentSubjectVal = 18
                    18 -> paymentSubjectVal = 19
                    19 -> paymentSubjectVal = 20
                    20 -> paymentSubjectVal = 21
                    21 -> paymentSubjectVal = 22
                    22 -> paymentSubjectVal = 23
                    23 -> paymentSubjectVal = 24
                    24 -> paymentSubjectVal = 25
                    25 -> paymentSubjectVal = 26
                    26 -> paymentSubjectVal = 27
                    27 -> paymentSubjectVal = 30
                    28 -> paymentSubjectVal = 31
                    29 -> paymentSubjectVal = 32
                    30 -> paymentSubjectVal = 33
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        cutMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> cutMethodVal = 0
                    1 -> cutMethodVal = 1
                    2 -> cutMethodVal = 2
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        cutCheques.setOnClickListener(View.OnClickListener { view ->
            when(cutCheques.isChecked){
                true -> cutChequesVal = 1
                false -> cutChequesVal = 0
            }
        })

        cutReports.setOnClickListener(View.OnClickListener { view ->
            when(cutReports.isChecked){
                true -> cutReportsVal = 1
                false -> cutReportsVal = 0
            }
        })

        FABSave.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "kktNumberVal :$kktNumberVal\n" +
                    "encashmentVal :$encashmentVal\n" +
                    "openCashboxVal :$openCashboxVal\n" +
                    "kktProtocolVal :$kktProtocolVal\n" +
                    "defaultSNOVal :$defaultSNOVal\n" +
                    "paymentMethodVal :$paymentMethodVal\n" +
                    "cashAccountingVal :$cashAccountingVal\n" +
                    "paymentSubjectVal :$paymentSubjectVal\n" +
                    "cutMethodVal :$cutMethodVal\n" +
                    "cutChequesVal :$cutChequesVal\n"
            val result = viewModel.setMainSettings(kktNumberVal,
                encashmentVal,
                openCashboxVal,
                kktProtocolVal,
                defaultSNOVal,
                paymentMethodVal,
                cashAccountingVal,
                paymentSubjectVal,
                cutMethodVal,
                cutChequesVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "setMainSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setMainSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "setMainSettings", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setMainSettings")}}
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
            val result = viewModel.getMainSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getMainSettings", paramsString, result[0].toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setMainSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getMainSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setMainSettings")}}
                kktNumberVal = result[0].toString().toInt()
                kktNumber.text = Editable.Factory.getInstance().newEditable(kktNumberVal.toString())

                encashmentVal = result[1].toString().toInt()
                when(encashmentVal){
                    1 -> encashment.isChecked = true
                    0 -> encashment.isChecked = false
                }

                openCashboxVal = result[2].toString().toInt()
                when(openCashboxVal){
                    1 -> openCashbox.isChecked = true
                    0 -> openCashbox.isChecked = false
                }

                kktProtocolVal = result[3].toString().toInt()
                when(kktProtocolVal){
                    1 -> kktProtocol.setSelection(0)
                    2 -> kktProtocol.setSelection(1)
                }

                kktProtocolVal = result[4].toString().toInt()
                when(kktProtocolVal){
                    0 -> kktProtocol.setSelection(0)
                    1 -> kktProtocol.setSelection(1)
                    2 -> kktProtocol.setSelection(2)
                    4 -> kktProtocol.setSelection(3)
                    16 -> kktProtocol.setSelection(4)
                    32 -> kktProtocol.setSelection(5)
                }

                kktProtocolVal = result[5].toString().toInt()
                when(kktProtocolVal){
                    1 -> kktProtocol.setSelection(0)
                    2 -> kktProtocol.setSelection(1)
                    3 -> kktProtocol.setSelection(2)
                    4 -> kktProtocol.setSelection(3)
                    5 -> kktProtocol.setSelection(4)
                    6 -> kktProtocol.setSelection(5)
                    7 -> kktProtocol.setSelection(6)
                }

                cashAccountingVal = result[6].toString().toInt()
                when(cashAccountingVal){
                    1 -> cashAccounting.isChecked = true
                    0 -> cashAccounting.isChecked = false
                }

                kktProtocolVal = result[7].toString().toInt()
                when(kktProtocolVal){
                    1 -> kktProtocol.setSelection(0)
                    2 -> kktProtocol.setSelection(1)
                    3 -> kktProtocol.setSelection(2)
                    4 -> kktProtocol.setSelection(3)
                    5 -> kktProtocol.setSelection(4)
                    6 -> kktProtocol.setSelection(5)
                    7 -> kktProtocol.setSelection(6)
                    8 -> kktProtocol.setSelection(7)
                    9 -> kktProtocol.setSelection(8)
                    10 -> kktProtocol.setSelection(9)
                    11 -> kktProtocol.setSelection(10)
                    12 -> kktProtocol.setSelection(11)
                    13 -> kktProtocol.setSelection(12)
                    14 -> kktProtocol.setSelection(13)
                    15 -> kktProtocol.setSelection(14)
                    16 -> kktProtocol.setSelection(15)
                    17 -> kktProtocol.setSelection(16)
                    18 -> kktProtocol.setSelection(17)
                    19 -> kktProtocol.setSelection(18)
                    20 -> kktProtocol.setSelection(19)
                    21 -> kktProtocol.setSelection(20)
                    22 -> kktProtocol.setSelection(21)
                    23 -> kktProtocol.setSelection(22)
                    24 -> kktProtocol.setSelection(23)
                    25 -> kktProtocol.setSelection(24)
                    26 -> kktProtocol.setSelection(25)
                    27 -> kktProtocol.setSelection(26)
                    30 -> kktProtocol.setSelection(27)
                    31 -> kktProtocol.setSelection(28)
                    32 -> kktProtocol.setSelection(29)
                    33 -> kktProtocol.setSelection(30)
                }

                kktProtocolVal = result[8].toString().toInt()
                when(kktProtocolVal){
                    0 -> kktProtocol.setSelection(0)
                    1 -> kktProtocol.setSelection(1)
                    2 -> kktProtocol.setSelection(2)
                }

                cutChequesVal = result[9].toString().toInt()
                when(cutChequesVal){
                    1 -> cutCheques.isChecked = true
                    0 -> cutCheques.isChecked = false
                }

                cutReportsVal = result[10].toString().toInt()
                when(cutReportsVal){
                    1 -> cutReports.isChecked = true
                    0 -> cutReports.isChecked = false
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

        val mainSettingsPrefs = requireContext().getSharedPreferences("mainSettingsPrefs", Context.MODE_PRIVATE)

        with(mainSettingsPrefs.edit()){
            putInt("kktNumberVal", kktNumberVal)
            putInt("encashmentVal", encashmentVal)
            putInt("openCashboxVal", openCashboxVal)
            putInt("kktProtocolVal", kktProtocolVal)
            putInt("defaultSNOVal", defaultSNOVal)
            putInt("paymentMethodVal", paymentMethodVal)
            putInt("cashAccountingVal", cashAccountingVal)
            putInt("paymentSubjectVal", paymentSubjectVal)
            putInt("cutMethodVal", cutMethodVal)
            putInt("cutChequesVal", cutChequesVal)
            putInt("cutReportsVal", cutReportsVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
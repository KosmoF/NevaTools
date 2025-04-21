package ru.neva.drivers.ui.cheques_fragment

import android.content.Context
import android.icu.text.Transliterator.Position
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentChequesBinding
import ru.neva.drivers.databinding.FragmentRegBinding
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class ChequesFragment : Fragment() {

    companion object {
        fun newInstance() = ChequesFragment()
    }

    private lateinit var viewModel: ChequesViewModel
    private var _binding: FragmentChequesBinding? = null
    private val binding get() = _binding!!

    private lateinit var chequeType: Spinner
    private lateinit var chequeTax: Spinner

    private lateinit var openShift: Button
    private lateinit var openCheque: Button

    private lateinit var positionName: EditText
    private lateinit var positionDepNumber: EditText
    private lateinit var positionCount: EditText
    private lateinit var positionPrice: EditText
    private lateinit var positionSumm: TextView
    private lateinit var positionTaxType: Spinner
    private lateinit var positionDiscount: EditText
    private lateinit var regPosition: Button

    private lateinit var paymentSumm: EditText
    private lateinit var paymentMethod: Spinner

    private lateinit var payment: Button

    private lateinit var closeCheque: Button
    private lateinit var cancelCheque: Button
    
    private var chequeTypeVal: Int = 1
    private var chequeTaxVal: Int = 0
    private var defaultTaxVal: Int = 1
    
    private var positionNameVal: String = ""
    private var positionDepNumberVal: Int = 1
    private var positionCountVal: Int = 1
    private var positionPriceVal: Double = 0.01
    private var positionSummVal: Double = 0.01
    private var positionTaxTypeVal: Int = 2
    private var positionDiscountVal: Double = 0.0

    private var paymentSummVal: Double = 0.0
    private var paymentMethodVal: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChequesBinding.inflate(inflater, container, false)

        this.chequeType = _binding!!.spChequeType
        this.chequeTax = _binding!!.spChequeTax

        this.openShift = _binding!!.openShiftButton
        this.openCheque = _binding!!.openChequeButton

        this.positionName = _binding!!.etPositionName
        this.positionDepNumber = _binding!!.etPositionDepNumber
        this.positionCount = _binding!!.etPositionCount
        this.positionPrice = _binding!!.etPositionPrice
        this.positionSumm = _binding!!.etPositionSumm
        this.positionTaxType = _binding!!.spPositionTaxType
        this.positionDiscount = _binding!!.etPositionDiscont
        this.regPosition = _binding!!.regPositionButton

        this.paymentSumm = _binding!!.etPaymentSumm
        this.paymentMethod = _binding!!.spPaymentMethod

        this.payment = _binding!!.paymentButton

        this.closeCheque = _binding!!.closeChequeButton
        this.cancelCheque = _binding!!.cancelChequeButton

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

        val chequeTaxes = arrayOf("0 - По-умолчанию",
            "1 - Традиционная СНО",
            "2 - Упрощенная СНО (Доход)",
            "4 - Упрощенная СНО (Доход минус Расход)",
            "16 - Единый сельскохозяйственный налог",
            "32 - Патентная система налогообложения")

        val paymentTypes = arrayOf("1 - Предоплата 100%",
            "2 - Предоплата",
            "3 - Аванс",
            "4 - Полный расчет",
            "5 - Частичный расчет и кредит",
            "6 - Передача в кредит",
            "7 - Оплата кредита")

        val paymentMethods = arrayOf("0 - Наличными",
            "1 - Безналичными",
            "2 - Предварительная оплата",
            "3 - Последующая оплата",
            "4 - Иная форма оплаты",
            "5 - Тип оплаты 6",
            "6 - Тип оплаты 7",
            "7 - Тип оплаты 8")

        val chequeTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, chequeTypes)
        chequeType.adapter = chequeTypeAdapter
        chequeType.setSelection(0)

        val chequeTaxAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, chequeTaxes)
        chequeTax.adapter = chequeTaxAdapter
        chequeTax.setSelection(0)

        val positionTaxTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, taxTypes)
        positionTaxType.adapter = positionTaxTypeAdapter
        positionTaxType.setSelection(0)


        val paymentMethodAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentMethods)
        paymentMethod.adapter = paymentMethodAdapter
        paymentMethod.setSelection(0)

        val regPrefs = requireContext().getSharedPreferences("regPrefs", Context.MODE_PRIVATE)

        defaultTaxVal = regPrefs.getInt("defaultTaxVal", 1)

        val chequesPrefs = requireContext().getSharedPreferences("chequesPrefs", Context.MODE_PRIVATE)

        chequeTypeVal = chequesPrefs.getInt("chequeTypeVal", 1)
        when(chequeTypeVal){
            1 -> chequeType.setSelection(0)
            2 -> chequeType.setSelection(1)
            4 -> chequeType.setSelection(2)
            5 -> chequeType.setSelection(3)
        }

        chequeTaxVal = chequesPrefs.getInt("chequeTaxVal", 0)
        when(chequeTaxVal){
            0 -> chequeTax.setSelection(0)
            1 -> chequeTax.setSelection(1)
            2 -> chequeTax.setSelection(2)
            4 -> chequeTax.setSelection(3)
            16 -> chequeTax.setSelection(4)
            32 -> chequeTax.setSelection(5)
        }

        positionNameVal = chequesPrefs.getString("positionNameVal", "").toString()
        positionName.text = Editable.Factory.getInstance().newEditable(positionNameVal)

        positionDepNumberVal = chequesPrefs.getInt("positionDepNumberVal", 1)
        positionDepNumber.text = Editable.Factory.getInstance().newEditable(positionDepNumberVal.toString())

        positionCountVal = chequesPrefs.getInt("positioCountVal", 1)
        positionCount.text = Editable.Factory.getInstance().newEditable(positionCountVal.toString())

        positionPriceVal = chequesPrefs.getString("positionPriceVal", "0.01")?.toDouble()!!
        positionPrice.text = Editable.Factory.getInstance().newEditable(positionPriceVal.toString())

        positionSumm.text = chequesPrefs.getString("positionSummVal", "0.01")

        positionTaxTypeVal = chequesPrefs.getInt("positionTaxTypeVal", 2)
        when(positionTaxTypeVal){
            2 -> positionTaxType.setSelection(0)
            4 -> positionTaxType.setSelection(1)
            5 -> positionTaxType.setSelection(2)
            6 -> positionTaxType.setSelection(3)
            7 -> positionTaxType.setSelection(4)
            8 -> positionTaxType.setSelection(5)
        }

        positionDiscountVal = chequesPrefs.getString("positionDiscountVal", "0.01")?.toDouble()!!
        positionDiscount.text = Editable.Factory.getInstance().newEditable(positionDiscountVal.toString())

        paymentSummVal = chequesPrefs.getString("paymentSummVal", "0.01")?.toDouble()!!
        paymentSumm.text = Editable.Factory.getInstance().newEditable(paymentSummVal.toString())

        paymentMethodVal = chequesPrefs.getInt("paymentMethodVal", 0)
        when(paymentMethodVal){
            0 -> paymentMethod.setSelection(0)
            1 -> paymentMethod.setSelection(1)
            2 -> paymentMethod.setSelection(2)
            3 -> paymentMethod.setSelection(3)
            4 -> paymentMethod.setSelection(4)
            5 -> paymentMethod.setSelection(5)
            6 -> paymentMethod.setSelection(6)
            7 -> paymentMethod.setSelection(7)
        }

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()
        
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

        chequeTax.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> chequeTaxVal = 0
                    1 -> chequeTaxVal = 1
                    2 -> chequeTaxVal = 2
                    3 -> chequeTaxVal = 4
                    4 -> chequeTaxVal = 16
                    5 -> chequeTaxVal = 32
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        openShift.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.openShift()
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "openShift", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "openShift")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "openShift", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "openShift")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
        })

        openCheque.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "chequeTypeVal :$chequeTypeVal\n" +
                    "chequeTaxVal :$chequeTaxVal\n" +
                    "defaultTaxVal :$defaultTaxVal\n"
            val result = viewModel.openCheque(chequeTypeVal,
                chequeTaxVal,
                defaultTaxVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "openCheque", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "openCheque")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "openCheque", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "openCheque")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
        })

        positionName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                positionNameVal = s.toString()
                if(positionNameVal == ""){
                    positionName.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        positionDepNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    positionDepNumberVal = 1
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                positionDepNumberVal = num

                if (num < 1 || num > 65535) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        positionDepNumberVal = 1
                    } else {
                        s.replace(0, s.length, "65535")
                        positionDepNumberVal = 65535
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        positionCount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    positionCountVal = 1
                    s?.replace(0, s.length, "0.001")
                    return
                }

                if (!s.toString().matches(Regex("^\\d+(\\.\\d{0,3})?$"))) {
                    s?.delete(s.length - 1, s.length)
                }

                val num = s.toString().toInt()
                positionCountVal = num
                if (num < 1 || num > 1000) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        positionCountVal = 1
                    } else {
                        s.replace(0, s.length, "1000")
                        positionCountVal = 1000
                    }
                }

                if(positionPriceVal != 0.0){
                    positionSummVal = positionPriceVal * positionCountVal
                    positionSumm.setText(positionSummVal.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        positionPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    positionPriceVal = 0.01
                    s?.replace(0, s.length, "0.01")
                    return
                }

                if (!s.toString().matches(Regex("^\\d+(\\.\\d{0,2})?$"))) {
                    s?.delete(s.length - 1, s.length)
                }

                val num = s.toString().toDouble()
                positionPriceVal = num
                if (num < 0.01 || num > 1000000.0) {
                    if (num < 0.01) {
                        s.replace(0, s.length, "0.01")
                        positionPriceVal = 0.01
                    } else {
                        s.replace(0, s.length, "1000000.0")
                        positionPriceVal = 1000000.0
                    }
                }

                if(positionCountVal != 0){
                    positionSummVal = positionPriceVal * positionCountVal
                    positionSumm.setText(positionSummVal.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        positionTaxType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> positionTaxTypeVal = 2
                    1 -> positionTaxTypeVal = 4
                    2 -> positionTaxTypeVal = 5
                    3 -> positionTaxTypeVal = 6
                    4 -> positionTaxTypeVal = 7
                    5 -> positionTaxTypeVal = 8
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        positionDiscount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    positionDiscountVal = 0.0
                    s?.replace(0, s.length, "0.0")
                    return
                }
                if (!s.toString().matches(Regex("^\\d+(\\.\\d{0,2})?$"))) {
                    s?.delete(s.length - 1, s.length)
                }
                positionDiscountVal = s.toString().toDouble()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        regPosition.setOnClickListener(View.OnClickListener { view ->
            if(positionNameVal == ""){
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Некоторые данные не введены или введены неверно. Проверьте данные и повторите попытку")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                val paramsString = "positionNameVal :$positionNameVal\n" +
                        "positionDepNumberVal :$positionDepNumberVal\n" +
                        "positionCountVal :$positionCountVal\n" +
                        "positionPriceVal :$positionPriceVal\n" +
                        "positionTaxTypeVal :$positionTaxTypeVal\n" +
                        "positionDiscountVal :$positionDiscountVal\n"
                val result = viewModel.regPosition(positionNameVal,
                    positionDepNumberVal,
                    positionCountVal,
                    positionPriceVal,
                    positionTaxTypeVal,
                    positionDiscountVal)
                if(result == ""){
                    logger.log(Logger.LogLevel.SUCCESS, "regPosition", paramsString, "0")
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "regPosition")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Уведомление")
                    alertDialog.setMessage("Операция выполнена успешно")
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        // Действие при нажатии на кнопку
                    }
                    alertDialog.show()
                }
                else{
                    logger.log(Logger.LogLevel.ERROR, "regPosition", paramsString, result)
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    val errorCode = viewModel.getErrorCode()
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "regPosition")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Ошибка")
                    alertDialog.setMessage(result)
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        // Действие при нажатии на кнопку
                    }
                    alertDialog.show()
                }
            }
        })

        paymentSumm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    paymentSummVal = 0.0
                    s?.replace(0, s.length, "0.0")
                    return
                }
                if (!s.toString().matches(Regex("^\\d+(\\.\\d{0,2})?$"))) {
                    s?.delete(s.length - 1, s.length)
                }
                paymentSummVal = s.toString().toDouble()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        paymentMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> paymentMethodVal = 0
                    1 -> paymentMethodVal = 1
                    2 -> paymentMethodVal = 2
                    3 -> paymentMethodVal = 3
                    4 -> paymentMethodVal = 4
                    5 -> paymentMethodVal = 5
                    6 -> paymentMethodVal = 6
                    7 -> paymentMethodVal = 7
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        payment.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "paymentSummVal :$paymentSummVal\n" +
                    "paymentMethodVal :$paymentMethodVal\n"
            val result = viewModel.regPayment(paymentSummVal, paymentMethodVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "regPayment", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "regPayment")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "regPayment", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "regPayment")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
        })

        closeCheque.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.closeCheque()
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "closeCheque", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "closeCheque")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "closeCheque", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "closeCheque")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
        })

        cancelCheque.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.cancelCheque()
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "cancelCheque", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "cancelCheque")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "cancelCheque", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "cancelCheque")}}
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

        val chequesPrefs = requireContext().getSharedPreferences("chequesPrefs", Context.MODE_PRIVATE)

        with(chequesPrefs.edit()){
            putInt("chequeTypeVal", chequeTypeVal)
            putInt("chequeTaxVal", chequeTaxVal)
            putString("positionNameVal", positionNameVal)
            putInt("positionDepNumberVal", positionDepNumberVal)
            putInt("positionCountVal", positionCountVal)
            putString("positionPriceVal", positionPriceVal.toString())
            putString("positionSummVal", positionSummVal.toString())
            putInt("positionTaxTypeVal", positionTaxTypeVal)
            putString("positionDiscountVal", positionDiscountVal.toString())
            putString("paymentSummVal", paymentSummVal.toString())
            putInt("paymentMethodVal", paymentMethodVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChequesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
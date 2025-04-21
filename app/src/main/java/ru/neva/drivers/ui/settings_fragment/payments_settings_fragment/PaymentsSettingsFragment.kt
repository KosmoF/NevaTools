package ru.neva.drivers.ui.settings_fragment.payments_settings_fragment

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
import ru.neva.drivers.databinding.FragmentOfdSettingsBinding
import ru.neva.drivers.databinding.FragmentPaymentsSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class PaymentsSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = PaymentsSettingsFragment()
    }

    private lateinit var viewModel: PaymentsSettingsViewModel
    private var _binding: FragmentPaymentsSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton

    private lateinit var payment2Typename: EditText
    private lateinit var payment2Type: Spinner
    private lateinit var payment3Typename: EditText
    private lateinit var payment3Type: Spinner
    private lateinit var payment4Typename: EditText
    private lateinit var payment4Type: Spinner
    private lateinit var payment5Typename: EditText
    private lateinit var payment5Type: Spinner
    private lateinit var payment6Typename: EditText
    private lateinit var payment6Type: Spinner
    private lateinit var payment7Typename: EditText
    private lateinit var payment7Type: Spinner
    private lateinit var payment8Typename: EditText
    private lateinit var payment8Type: Spinner

    private var payment2TypenameVal: String = "Безналичными"
    private var payment2TypeVal: Int = 2
    private var payment3TypenameVal: String = "Аванс"
    private var payment3TypeVal: Int = 3
    private var payment4TypenameVal: String = "Кредит"
    private var payment4TypeVal: Int = 4
    private var payment5TypenameVal: String = "Иная форма оплаты"
    private var payment5TypeVal: Int = 5
    private var payment6TypenameVal: String = "Тип 6"
    private var payment6TypeVal: Int = 2
    private var payment7TypenameVal: String = "Тип 7"
    private var payment7TypeVal: Int = 2
    private var payment8TypenameVal: String = "Тип 8"
    private var payment8TypeVal: Int = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentsSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad

        this.payment2Typename = _binding!!.etKktPayment2Typename
        this.payment2Type = _binding!!.spKktPayment2Type
        this.payment3Typename = _binding!!.etKktPayment3Typename
        this.payment3Type = _binding!!.spKktPayment3Type
        this.payment4Typename = _binding!!.etKktPayment4Typename
        this.payment4Type = _binding!!.spKktPayment4Type
        this.payment5Typename = _binding!!.etKktPayment5Typename
        this.payment5Type = _binding!!.spKktPayment5Type
        this.payment6Typename = _binding!!.etKktPayment6Typename
        this.payment6Type = _binding!!.spKktPayment6Type
        this.payment7Typename = _binding!!.etKktPayment7Typename
        this.payment7Type = _binding!!.spKktPayment7Type
        this.payment8Typename = _binding!!.etKktPayment8Typename
        this.payment8Type = _binding!!.spKktPayment8Type

        val paymentTypes = arrayOf("2 - Безналичными",
            "3 - Предварительная оплата (аванс)",
            "4 - Последующая оплата (кредит)",
            "5 - Иная форма оплаты (встречное предоставление)")

        val payment2TypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentTypes)
        payment2Type.adapter = payment2TypeAdapter
        payment2Type.setSelection(0)

        val payment3TypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentTypes)
        payment3Type.adapter = payment3TypeAdapter
        payment3Type.setSelection(0)

        val payment4TypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentTypes)
        payment4Type.adapter = payment4TypeAdapter
        payment4Type.setSelection(0)

        val payment5TypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentTypes)
        payment5Type.adapter = payment5TypeAdapter
        payment5Type.setSelection(0)

        val payment6TypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentTypes)
        payment6Type.adapter = payment6TypeAdapter
        payment6Type.setSelection(0)

        val payment7TypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentTypes)
        payment7Type.adapter = payment7TypeAdapter
        payment7Type.setSelection(0)

        val payment8TypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentTypes)
        payment8Type.adapter = payment8TypeAdapter
        payment8Type.setSelection(0)

        val paymentsSettingsPrefs = requireContext().getSharedPreferences("paymentsSettingsPrefs", Context.MODE_PRIVATE)

        payment2TypenameVal = paymentsSettingsPrefs.getString("payment2TypenameVal", "").toString()
        payment2Typename.text = Editable.Factory.getInstance().newEditable(payment2TypenameVal)

        payment2TypeVal = paymentsSettingsPrefs.getInt("payment2TypeVal", 2)
        when(payment2TypeVal){
            2 -> payment2Type.setSelection(0)
            3 -> payment2Type.setSelection(1)
            4 -> payment2Type.setSelection(2)
            5 -> payment2Type.setSelection(3)
        }

        payment3TypenameVal = paymentsSettingsPrefs.getString("payment3TypenameVal", "").toString()
        payment3Typename.text = Editable.Factory.getInstance().newEditable(payment3TypenameVal)

        payment3TypeVal = paymentsSettingsPrefs.getInt("payment3TypeVal", 3)
        when(payment3TypeVal){
            2 -> payment3Type.setSelection(0)
            3 -> payment3Type.setSelection(1)
            4 -> payment3Type.setSelection(2)
            5 -> payment3Type.setSelection(3)
        }

        payment4TypenameVal = paymentsSettingsPrefs.getString("payment4TypenameVal", "").toString()
        payment4Typename.text = Editable.Factory.getInstance().newEditable(payment4TypenameVal)

        payment4TypeVal = paymentsSettingsPrefs.getInt("payment4TypeVal", 4)
        when(payment4TypeVal){
            2 -> payment4Type.setSelection(0)
            3 -> payment4Type.setSelection(1)
            4 -> payment4Type.setSelection(2)
            5 -> payment4Type.setSelection(3)
        }

        payment5TypenameVal = paymentsSettingsPrefs.getString("payment5TypenameVal", "").toString()
        payment5Typename.text = Editable.Factory.getInstance().newEditable(payment5TypenameVal)

        payment5TypeVal = paymentsSettingsPrefs.getInt("payment5TypeVal", 5)
        when(payment5TypeVal){
            2 -> payment5Type.setSelection(0)
            3 -> payment5Type.setSelection(1)
            4 -> payment5Type.setSelection(2)
            5 -> payment5Type.setSelection(3)
        }

        payment6TypenameVal = paymentsSettingsPrefs.getString("payment6TypenameVal", "").toString()
        payment6Typename.text = Editable.Factory.getInstance().newEditable(payment6TypenameVal)

        payment6TypeVal = paymentsSettingsPrefs.getInt("payment6TypeVal", 2)
        when(payment6TypeVal){
            2 -> payment6Type.setSelection(0)
            3 -> payment6Type.setSelection(1)
            4 -> payment6Type.setSelection(2)
            5 -> payment6Type.setSelection(3)
        }

        payment7TypenameVal = paymentsSettingsPrefs.getString("payment7TypenameVal", "").toString()
        payment7Typename.text = Editable.Factory.getInstance().newEditable(payment7TypenameVal)

        payment7TypeVal = paymentsSettingsPrefs.getInt("payment7TypeVal", 2)
        when(payment7TypeVal){
            2 -> payment7Type.setSelection(0)
            3 -> payment7Type.setSelection(1)
            4 -> payment7Type.setSelection(2)
            5 -> payment7Type.setSelection(3)
        }

        payment8TypenameVal = paymentsSettingsPrefs.getString("payment8TypenameVal", "").toString()
        payment8Typename.text = Editable.Factory.getInstance().newEditable(payment8TypenameVal)

        payment8TypeVal = paymentsSettingsPrefs.getInt("payment8TypeVal", 2)
        when(payment8TypeVal){
            2 -> payment8Type.setSelection(0)
            3 -> payment8Type.setSelection(1)
            4 -> payment8Type.setSelection(2)
            5 -> payment8Type.setSelection(3)
        }

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        payment2Typename.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                payment2TypenameVal = s.toString()
                if(payment2TypenameVal == ""){
                    payment2Typename.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        payment2Type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> payment2TypeVal = 2
                    1 -> payment2TypeVal = 3
                    2 -> payment2TypeVal = 4
                    3 -> payment2TypeVal = 5
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        payment3Typename.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                payment3TypenameVal = s.toString()
                if(payment3TypenameVal == ""){
                    payment3Typename.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        payment3Type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> payment3TypeVal = 2
                    1 -> payment3TypeVal = 3
                    2 -> payment3TypeVal = 4
                    3 -> payment3TypeVal = 5
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        payment4Typename.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                payment4TypenameVal = s.toString()
                if(payment4TypenameVal == ""){
                    payment4Typename.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        payment4Type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> payment4TypeVal = 2
                    1 -> payment4TypeVal = 3
                    2 -> payment4TypeVal = 4
                    3 -> payment4TypeVal = 5
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        payment5Typename.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                payment5TypenameVal = s.toString()
                if(payment5TypenameVal == ""){
                    payment5Typename.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        payment5Type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> payment5TypeVal = 2
                    1 -> payment5TypeVal = 3
                    2 -> payment5TypeVal = 4
                    3 -> payment5TypeVal = 5
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        payment6Typename.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                payment6TypenameVal = s.toString()
                if(payment6TypenameVal == ""){
                    payment6Typename.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        payment6Type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> payment6TypeVal = 2
                    1 -> payment6TypeVal = 3
                    2 -> payment6TypeVal = 4
                    3 -> payment6TypeVal = 5
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        payment7Typename.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                payment7TypenameVal = s.toString()
                if(payment7TypenameVal == ""){
                    payment7Typename.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        payment7Type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> payment7TypeVal = 2
                    1 -> payment7TypeVal = 3
                    2 -> payment7TypeVal = 4
                    3 -> payment7TypeVal = 5
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        payment8Typename.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                payment8TypenameVal = s.toString()
                if(payment8TypenameVal == ""){
                    payment8Typename.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        payment8Type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> payment8TypeVal = 2
                    1 -> payment8TypeVal = 3
                    2 -> payment8TypeVal = 4
                    3 -> payment8TypeVal = 5
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        FABSave.setOnClickListener(View.OnClickListener { view ->
            if (payment2TypenameVal == ""||
                payment3TypenameVal == ""||
                payment4TypenameVal == ""||
                payment5TypenameVal == ""||
                payment6TypenameVal == ""||
                payment7TypenameVal == ""||
                payment8TypenameVal == ""){
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Некоторые данные не введены или введены неверно. Проверьте данные и повторите попытку")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                val paramsString = "payment2TypenameVal :$payment2TypenameVal\n" +
                        "payment2TypeVal :$payment2TypeVal\n" +
                        "payment3TypenameVal :$payment3TypenameVal\n" +
                        "payment3TypeVal :$payment3TypeVal\n" +
                        "payment4TypenameVal :$payment2TypenameVal\n" +
                        "payment4TypeVal :$payment3TypeVal\n" +
                        "payment5TypenameVal :$payment5TypenameVal\n" +
                        "payment5TypeVal :$payment5TypeVal\n" +
                        "payment6TypenameVal :$payment6TypenameVal\n" +
                        "payment6TypeVal :$payment6TypeVal\n" +
                        "payment7TypenameVal :$payment7TypenameVal\n" +
                        "payment7TypeVal :$payment7TypeVal\n" +
                        "payment8TypenameVal :$payment8TypenameVal\n" +
                        "payment8TypeVal :$payment8TypeVal\n"
                val result = viewModel.setPaymentsSettings(payment2TypenameVal,
                    payment2TypeVal,
                    payment3TypenameVal,
                    payment3TypeVal,
                    payment4TypenameVal,
                    payment4TypeVal,
                    payment5TypenameVal,
                    payment5TypeVal,
                    payment6TypenameVal,
                    payment6TypeVal,
                    payment7TypenameVal,
                    payment7TypeVal,
                    payment8TypenameVal,
                    payment8TypeVal)
                if(result == ""){
                    logger.log(Logger.LogLevel.SUCCESS, "setPaymentsSettings", paramsString, "0")
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setPaymentsSettings")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Уведомление")
                    alertDialog.setMessage("Операция выполнена успешно")
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                    }
                    alertDialog.show()
                }
                else{
                    logger.log(Logger.LogLevel.ERROR, "setPaymentsSettings", paramsString, result)
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    val errorCode = viewModel.getErrorCode()
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setPaymentsSettings")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Ошибка")
                    alertDialog.setMessage(result)
                    alertDialog.setPositiveButton("OK") { dialog, which ->

                    }
                    alertDialog.show()
                }
            }
        })

        FABLoad.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.getPaymentsSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getPaymentsSettings", paramsString, result[0].toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getPaymentsSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getPaymentsSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getPaymentsSettings")}}
                payment2TypenameVal = result[0].toString()
                payment2Typename.text = Editable.Factory.getInstance().newEditable(payment2TypenameVal)

                payment2TypeVal = result[1].toString().toInt()
                when(payment2TypeVal){
                    2 -> payment2Type.setSelection(0)
                    3 -> payment2Type.setSelection(1)
                    4 -> payment2Type.setSelection(2)
                    5 -> payment2Type.setSelection(3)
                }

                payment3TypenameVal = result[2].toString()
                payment3Typename.text = Editable.Factory.getInstance().newEditable(payment3TypenameVal)

                payment3TypeVal = result[3].toString().toInt()
                when(payment3TypeVal){
                    2 -> payment3Type.setSelection(0)
                    3 -> payment3Type.setSelection(1)
                    4 -> payment3Type.setSelection(2)
                    5 -> payment3Type.setSelection(3)
                }

                payment4TypenameVal = result[4].toString()
                payment4Typename.text = Editable.Factory.getInstance().newEditable(payment4TypenameVal)

                payment2TypeVal = result[5].toString().toInt()
                when(payment4TypeVal){
                    2 -> payment4Type.setSelection(0)
                    3 -> payment4Type.setSelection(1)
                    4 -> payment4Type.setSelection(2)
                    5 -> payment4Type.setSelection(3)
                }

                payment5TypenameVal = result[6].toString()
                payment5Typename.text = Editable.Factory.getInstance().newEditable(payment5TypenameVal)

                payment5TypeVal = result[7].toString().toInt()
                when(payment5TypeVal){
                    2 -> payment5Type.setSelection(0)
                    3 -> payment5Type.setSelection(1)
                    4 -> payment5Type.setSelection(2)
                    5 -> payment5Type.setSelection(3)
                }

                payment6TypenameVal = result[8].toString()
                payment6Typename.text = Editable.Factory.getInstance().newEditable(payment6TypenameVal)

                payment6TypeVal = result[9].toString().toInt()
                when(payment6TypeVal){
                    2 -> payment6Type.setSelection(0)
                    3 -> payment6Type.setSelection(1)
                    4 -> payment6Type.setSelection(2)
                    5 -> payment6Type.setSelection(3)
                }

                payment7TypenameVal = result[10].toString()
                payment7Typename.text = Editable.Factory.getInstance().newEditable(payment7TypenameVal)

                payment7TypeVal = result[11].toString().toInt()
                when(payment7TypeVal){
                    2 -> payment7Type.setSelection(0)
                    3 -> payment7Type.setSelection(1)
                    4 -> payment7Type.setSelection(2)
                    5 -> payment7Type.setSelection(3)
                }

                payment8TypenameVal = result[12].toString()
                payment8Typename.text = Editable.Factory.getInstance().newEditable(payment8TypenameVal)

                payment8TypeVal = result[13].toString().toInt()
                when(payment8TypeVal){
                    2 -> payment8Type.setSelection(0)
                    3 -> payment8Type.setSelection(1)
                    4 -> payment8Type.setSelection(2)
                    5 -> payment8Type.setSelection(3)
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

        val paymentsSettingsPrefs = requireContext().getSharedPreferences("paymentsSettingsPrefs", Context.MODE_PRIVATE)

        with(paymentsSettingsPrefs.edit()){
            putString("payment2TypenameVal", payment2TypenameVal)
            putInt("payment2TypeVal", payment2TypeVal)
            putString("payment3TypenameVal", payment3TypenameVal)
            putInt("payment3TypeVal", payment3TypeVal)
            putString("payment4TypenameVal", payment4TypenameVal)
            putInt("payment4TypeVal", payment4TypeVal)
            putString("payment5TypenameVal", payment5TypenameVal)
            putInt("payment5TypeVal", payment5TypeVal)
            putString("payment6TypenameVal", payment6TypenameVal)
            putInt("payment6TypeVal", payment6TypeVal)
            putString("payment7TypenameVal", payment7TypenameVal)
            putInt("payment7TypeVal", payment7TypeVal)
            putString("payment8TypenameVal", payment8TypenameVal)
            putInt("payment8TypeVal", payment8TypeVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PaymentsSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
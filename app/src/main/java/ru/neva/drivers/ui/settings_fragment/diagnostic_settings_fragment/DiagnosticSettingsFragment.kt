package ru.neva.drivers.ui.settings_fragment.diagnostic_settings_fragment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.databinding.FragmentDiagnosticSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class DiagnosticSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = DiagnosticSettingsFragment()
    }

    private lateinit var viewModel: DiagnosticSettingsViewModel
    private var _binding: FragmentDiagnosticSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton

    private lateinit var lkAddress: EditText
    private lateinit var lkPort: EditText
    private lateinit var msgInterval: EditText
    private lateinit var userCode: EditText

    private var lkAddressVal: String = ""
    private var lkPortVal: Int = 1
    private var msgIntervalVal: Int = 1
    private var userCodeVal: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiagnosticSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad
        
        this.lkAddress = _binding!!.etLkAddress
        this.lkPort = _binding!!.etLkPort
        this.msgInterval = _binding!!.etMsgInterval
        this.userCode = _binding!!.etUserCode

        val diagnosticSettingsPrefs = requireContext().getSharedPreferences("diagnosticSettingsPrefs", Context.MODE_PRIVATE)

        lkAddressVal = diagnosticSettingsPrefs.getString("lkAddressVal", "").toString()
        lkAddress.text = Editable.Factory.getInstance().newEditable(lkAddressVal)

        lkPortVal = diagnosticSettingsPrefs.getInt("lkPortVal", 1)
        lkPort.text = Editable.Factory.getInstance().newEditable(lkPortVal.toString())

        msgIntervalVal= diagnosticSettingsPrefs.getInt("msgIntervalVal", 1)
        msgInterval.text = Editable.Factory.getInstance().newEditable(msgIntervalVal.toString())

        userCodeVal = diagnosticSettingsPrefs.getString("userCodeVal", "").toString()
        userCode.text = Editable.Factory.getInstance().newEditable(userCodeVal)

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        lkAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                lkAddressVal = s.toString()
                if(lkAddressVal == ""){
                    lkAddress.error = "Поле не должно быть пустым"
                    return
                }
                if (!Utils.isValidURL(lkAddressVal)) {
                    lkAddress.error = "Введите корректный IP-адрес"
                    return
                }
            }
        })

        lkPort.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    lkPortVal = 1
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                lkPortVal = num

                if (num < 1 || num > 65535) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        lkPortVal = 1
                    } else {
                        s.replace(0, s.length, "65535")
                        lkPortVal = 65535
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        msgInterval.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    msgIntervalVal = 1
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                msgIntervalVal = num

                if (num < 1 || num > 65535) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        msgIntervalVal = 1
                    } else {
                        s.replace(0, s.length, "65535")
                        msgIntervalVal = 65535
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        userCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                userCodeVal = s.toString()
                if (userCodeVal == ""){
                    userCode.error = "Поле не должно быть пустым"
                    return
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        FABSave.setOnClickListener(View.OnClickListener { view ->
            if (!Utils.isValidURL(lkAddressVal) || userCodeVal == ""){
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Некоторые данные не введены или введены неверно. Проверьте данные и повторите попытку")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                val paramsString = "lkAddressVal :$lkAddressVal\n" +
                        "lkPortVal :$lkPortVal\n" +
                        "msgIntervalVal :$msgIntervalVal\n" +
                        "userCodeVal :$userCodeVal\n"
                val result = viewModel.setDiagnosticSettings(lkAddressVal,
                    lkPortVal,
                    msgIntervalVal,
                    userCodeVal)
                if(result == ""){
                    logger.log(Logger.LogLevel.SUCCESS, "setDiagnosticSettings", paramsString, "0")
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setDiagnosticSettings")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Уведомление")
                    alertDialog.setMessage("Операция выполнена успешно")
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                    }
                    alertDialog.show()
                }
                else{
                    logger.log(Logger.LogLevel.ERROR, "setDiagnosticSettings", paramsString, result)
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    val errorCode = viewModel.getErrorCode()
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setDiagnosticSettings")}}
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
            val result = viewModel.getDiagnosticSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getDiagnosticSettings", paramsString, result[0].toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getDiagnosticSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getDiagnosticSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getDiagnosticSettings")}}
                lkAddressVal = result[0].toString()
                lkAddress.text = Editable.Factory.getInstance().newEditable(lkAddressVal)

                lkPortVal = result[1].toString().toInt()
                lkPort.text = Editable.Factory.getInstance().newEditable(lkPortVal.toString())

                msgIntervalVal = result[2].toString().toInt()
                msgInterval.text = Editable.Factory.getInstance().newEditable(msgIntervalVal.toString())

                userCodeVal = result[3].toString()
                userCode.text = Editable.Factory.getInstance().newEditable(userCodeVal)

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

        val diagnosticSettingsPrefs = requireContext().getSharedPreferences("diagnosticSettingsPrefs", Context.MODE_PRIVATE)

        with(diagnosticSettingsPrefs.edit()){
            putString("lkAddressVal", lkAddressVal)
            putInt("lkPortVal", lkPortVal)
            putInt("msgIntervalVal", msgIntervalVal)
            putString("userCodeVal", userCodeVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DiagnosticSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
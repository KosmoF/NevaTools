package ru.neva.drivers.ui.settings_fragment.connection_settings_fragment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.databinding.FragmentConnectionSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.regex.Pattern

@AndroidEntryPoint
class ConnectionSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = ConnectionSettingsFragment()
    }

    private lateinit var viewModel: ConnectionSettingsViewModel
    private var _binding: FragmentConnectionSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton

    private lateinit var dhcpEnabled: CheckBox
    private lateinit var kktIP: EditText
    private lateinit var kktNetmask: EditText
    private lateinit var kktGateway: EditText

    private var dhcpEnabledVal: Int = 0
    private var kktIPVal: String = ""
    private var kktNetmaskVal: String = ""
    private var kktGatewayVal: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConnectionSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad

        this.dhcpEnabled = _binding!!.cbDhcpEnabled
        this.kktIP = _binding!!.etKktIp
        this.kktNetmask = _binding!!.etKktNetmask
        this.kktGateway = _binding!!.etKktGateway

        val connectionSettingsPrefs = requireContext().getSharedPreferences("connectionSettingsPrefs", Context.MODE_PRIVATE)
        
        dhcpEnabledVal = connectionSettingsPrefs.getInt("dhcpEnabledVal", 0)
        when(dhcpEnabledVal){
            1 -> dhcpEnabled.isChecked = true
            0 -> dhcpEnabled.isChecked = false
        }

        kktIPVal = connectionSettingsPrefs.getString("kktIPVal", "").toString()
        kktIP.text = Editable.Factory.getInstance().newEditable(kktIPVal)

        kktNetmaskVal = connectionSettingsPrefs.getString("kktNetmaskVal", "").toString()
        kktNetmask.text = Editable.Factory.getInstance().newEditable(kktNetmaskVal)

        kktGatewayVal = connectionSettingsPrefs.getString("kktGatewayVal", "").toString()
        kktGateway.text = Editable.Factory.getInstance().newEditable(kktGatewayVal)

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        dhcpEnabled.setOnClickListener(View.OnClickListener { view ->
            when(dhcpEnabled.isChecked){
                true -> dhcpEnabledVal = 1
                false -> dhcpEnabledVal = 0
            }
        })

        kktIP.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                kktIPVal = s.toString()
                if(kktIPVal == ""){
                    kktIP.error = "Поле не должно быть пустым"
                    return
                }
                if (!Utils.isValidIpAddress(kktIPVal)) {
                    kktIP.error = "Введите корректный IP-адрес"
                    return
                }
            }
        })

        kktNetmask.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                kktNetmaskVal = s.toString()
                if(kktNetmaskVal == ""){
                    kktNetmask.error = "Поле не должно быть пустым"
                    return
                }
            }
        })

        kktGateway.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                kktGatewayVal = s.toString()
                if(kktGatewayVal == ""){
                    kktGateway.error = "Поле не должно быть пустым"
                    return
                }
                if (!Utils.isValidIpAddress(kktGatewayVal)) {
                    kktGateway.error = "Введите корректный шлюз"
                    return
                }
            }
        })

        FABSave.setOnClickListener(View.OnClickListener { view ->
            if (!Utils.isValidIpAddress(kktIPVal) || kktNetmaskVal == "" || !Utils.isValidIpAddress(kktGatewayVal)){
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Некоторые данные не введены или введены неверно. Проверьте данные и повторите попытку")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                val paramsString = "dhcpEnabledVal :$dhcpEnabledVal\n" +
                        "kktIPVal :$kktIPVal\n" +
                        "kktNetmaskVal :$kktNetmaskVal\n" +
                        "kktGatewayVal :$kktGatewayVal\n"
                val result = viewModel.setConnectionSettings(dhcpEnabledVal,
                    kktIPVal,
                    kktNetmaskVal,
                    kktGatewayVal)
                if(result == ""){
                    logger.log(Logger.LogLevel.SUCCESS, "setConnectionSettings", paramsString, "0")
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setConnectionSettings")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Уведомление")
                    alertDialog.setMessage("Операция выполнена успешно")
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                    }
                    alertDialog.show()
                }
                else{
                    logger.log(Logger.LogLevel.ERROR, "setConnectionSettings", paramsString, result)
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    val errorCode = viewModel.getErrorCode()
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setConnectionSettings")}}
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
            val result = viewModel.getConnectionSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getConnectionSettings", paramsString, result[0].toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getConnectionSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getConnectionSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getConnectionSettings")}}
                dhcpEnabledVal = result[0].toString().toInt()
                when(dhcpEnabledVal){
                    1 -> dhcpEnabled.isChecked = true
                    0 -> dhcpEnabled.isChecked = false
                }

                kktIPVal = result[1].toString()
                kktIP.text = Editable.Factory.getInstance().newEditable(kktIPVal)

                kktNetmaskVal = result[2].toString()
                kktNetmask.text = Editable.Factory.getInstance().newEditable(kktNetmaskVal)

                kktGatewayVal = result[3].toString()
                kktGateway.text = Editable.Factory.getInstance().newEditable(kktGatewayVal)

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

        val connectionSettingsPrefs = requireContext().getSharedPreferences("connectionSettingsPrefs", Context.MODE_PRIVATE)

        with(connectionSettingsPrefs.edit()){
            putInt("dhcpEnabledVal", dhcpEnabledVal)
            putString("kktIPVal", kktIPVal)
            putString("kktNetmaskVal", kktNetmaskVal)
            putString("kktGatewayVal", kktGatewayVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConnectionSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
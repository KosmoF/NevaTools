package ru.neva.drivers.ui.settings_fragment.ofd_settings_fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.Provides
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.databinding.FragmentOfdSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar


@AndroidEntryPoint
class OfdSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = OfdSettingsFragment()
    }

    private lateinit var viewModel: OfdSettingsViewModel
    private var _binding: FragmentOfdSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton

    private lateinit var ofdAddress: EditText
    private lateinit var ofdPort: EditText
    private lateinit var ofdDNS: EditText
    private lateinit var ofdChannel: Spinner

    private var ofdAddressVal: String = ""
    private var ofdPortVal: Int = 1
    private var ofdDNSVal: String = ""
    private var ofdChannelVal: Int = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOfdSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad

        this.ofdAddress = _binding!!.etOfdAddress
        this.ofdPort = _binding!!.etOfdPort
        this.ofdDNS = _binding!!.etOfdDns
        this.ofdChannel = _binding!!.spOfdChannel

        val ofdChannels = arrayOf("2 - Ethernet",
            "5 - EthernetOverTransport")

        val ofdChannelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ofdChannels)
        ofdChannel.adapter = ofdChannelAdapter
        ofdChannel.setSelection(0)

        val ofdSettingsPrefs = requireContext().getSharedPreferences("ofdSettingsPrefs", Context.MODE_PRIVATE)
        
        ofdAddressVal = ofdSettingsPrefs.getString("ofdAddressVal", "").toString()
        ofdAddress.text = Editable.Factory.getInstance().newEditable(ofdAddressVal)

        ofdPortVal = ofdSettingsPrefs.getInt("ofdPortVal", 1)
        ofdPort.text = Editable.Factory.getInstance().newEditable(ofdPortVal.toString())

        ofdDNSVal = ofdSettingsPrefs.getString("ofdDNSVal", "").toString()
        ofdDNS.text = Editable.Factory.getInstance().newEditable(ofdDNSVal)

        ofdChannelVal = ofdSettingsPrefs.getInt("ofdChannelVal", 1)
        when(ofdChannelVal){
            2 -> ofdChannel.setSelection(0)
            5 -> ofdChannel.setSelection(1)
        }

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        ofdAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                ofdAddressVal = s.toString()
                if(ofdAddressVal == ""){
                    ofdAddress.error = "Поле не должно быть пустым"
                    return
                }
                if (!Utils.isValidURL(ofdAddressVal)) {
                    ofdAddress.error = "Введите корректный адрес"
                    return
                }
            }
        })

        ofdPort.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    ofdPortVal = 1
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                ofdPortVal = num

                if (num < 1 || num > 65535) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        ofdPortVal = 1
                    } else {
                        s.replace(0, s.length, "65535")
                        ofdPortVal = 65535
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        ofdDNS.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                ofdDNSVal = s.toString()
                if(ofdDNSVal == ""){
                    ofdDNS.error = "Поле не должно быть пустым"
                    return
                }
                if (!Utils.isValidIpAddress(ofdDNSVal)) {
                    ofdDNS.error = "Введите корректный адрес"
                    return
                }
            }
        })

        ofdChannel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> ofdChannelVal = 2
                    1 -> ofdChannelVal = 5
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        FABSave.setOnClickListener(View.OnClickListener { view ->
            if (!Utils.isValidURL(ofdAddressVal) || !Utils.isValidIpAddress(ofdDNSVal)){
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Некоторые данные не введены или введены неверно. Проверьте данные и повторите попытку")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                val paramsString = "ofdAddressVal :$ofdAddressVal\n" +
                        "ofdPortVal :$ofdPortVal\n" +
                        "ofdDNSVal :$ofdDNSVal\n" +
                        "ofdChannelVal :$ofdChannelVal\n"
                val result = viewModel.setOFDSettings(ofdAddressVal,
                    ofdPortVal,
                    ofdDNSVal,
                    ofdChannelVal)
                if(result == ""){
                    logger.log(Logger.LogLevel.SUCCESS, "setOFDSettings", paramsString, "0")
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setOFDSettings")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Уведомление")
                    alertDialog.setMessage("Операция выполнена успешно")
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                    }
                    alertDialog.show()
                }
                else{
                    logger.log(Logger.LogLevel.ERROR, "setOFDSettings", paramsString, result)
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    val errorCode = viewModel.getErrorCode()
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setOFDSettings")}}
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
            val result = viewModel.getOFDSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getOFDSettings", paramsString, result[0].toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getOFDSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getOFDSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getOFDSettings")}}
                ofdAddressVal = result[0].toString()
                ofdAddress.text = Editable.Factory.getInstance().newEditable(ofdAddressVal)

                ofdPortVal = result[1].toString().toInt()
                ofdPort.text = Editable.Factory.getInstance().newEditable(ofdPortVal.toString())

                ofdDNSVal = result[2].toString()
                ofdDNS.text = Editable.Factory.getInstance().newEditable(ofdDNSVal)

                ofdChannelVal = result[3].toString().toInt()
                when(ofdChannelVal){
                    2 -> ofdChannel.setSelection(0)
                    5 -> ofdChannel.setSelection(1)
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

        val ofdSettingsPrefs = requireContext().getSharedPreferences("ofdSettingsPrefs", Context.MODE_PRIVATE)

        with(ofdSettingsPrefs.edit()){
            putString("ofdAddressVal", ofdAddressVal)
            putInt("ofdPortVal", ofdPortVal)
            putString("ofdDNSVal", ofdDNSVal)
            putInt("ofdChannelVal", ofdChannelVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OfdSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
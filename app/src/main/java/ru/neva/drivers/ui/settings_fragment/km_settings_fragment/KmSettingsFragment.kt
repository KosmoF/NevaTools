package ru.neva.drivers.ui.settings_fragment.km_settings_fragment

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.databinding.FragmentKmSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class KmSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = KmSettingsFragment()
    }

    private lateinit var viewModel: KmSettingsViewModel
    private var _binding: FragmentKmSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton

    private lateinit var ismAddress: EditText
    private lateinit var ismPort: EditText
    private lateinit var okpAddress: EditText
    private lateinit var okpPort: EditText

    private var ismAddressVal: String = ""
    private var ismPortVal: Int = 1
    private var okpAddressVal: String = ""
    private var okpPortVal: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKmSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad
        
        this.ismAddress = _binding!!.etIsmAddress
        this.ismPort = _binding!!.etIsmPort
        this.okpAddress = _binding!!.etOkpAddress
        this.okpPort = _binding!!.etOkpPort

        val kmSettingsPrefs = requireContext().getSharedPreferences("kmSettingsPrefs", Context.MODE_PRIVATE)

        ismAddressVal = kmSettingsPrefs.getString("ismAddressVal", "").toString()
        ismAddress.text = Editable.Factory.getInstance().newEditable(ismAddressVal)

        ismPortVal = kmSettingsPrefs.getInt("ismPortVal", 1)
        ismPort.text = Editable.Factory.getInstance().newEditable(ismPortVal.toString())

        okpAddressVal = kmSettingsPrefs.getString("okpAddressVal", "").toString()
        okpAddress.text = Editable.Factory.getInstance().newEditable(okpAddressVal)

        okpPortVal = kmSettingsPrefs.getInt("okpPortVal", 1)
        okpPort.text = Editable.Factory.getInstance().newEditable(okpPortVal.toString())

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        ismAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                ismAddressVal = s.toString()
                if(ismAddressVal == ""){
                    ismAddress.error = "Поле не должно быть пустым"
                    return
                }
                if (!Utils.isValidURL(ismAddressVal)) {
                    ismAddress.error = "Введите корректный адрес"
                    return
                }
            }
        })

        ismPort.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    ismPortVal = 1
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                ismPortVal = num

                if (num < 1 || num > 65535) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        ismPortVal = 1
                    } else {
                        s.replace(0, s.length, "65535")
                        ismPortVal = 65535
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        okpAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                okpAddressVal = s.toString()
                if(okpAddressVal == ""){
                    okpAddress.error = "Поле не должно быть пустым"
                    return
                }
                if (!Utils.isValidURL(okpAddressVal)) {
                    okpAddress.error = "Введите корректный адрес"
                    return
                }
            }
        })

        okpPort.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    okpPortVal = 1
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                okpPortVal = num

                if (num < 1 || num > 65535) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        okpPortVal = 1
                    } else {
                        s.replace(0, s.length, "65535")
                        okpPortVal = 65535
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        FABSave.setOnClickListener(View.OnClickListener { view ->
            if (!Utils.isValidURL(ismAddressVal) || !Utils.isValidURL(okpAddressVal)){
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Некоторые данные не введены или введены неверно. Проверьте данные и повторите попытку")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                val paramsString = "ismAddressVal :$ismAddressVal\n" +
                        "ismPortVal :$ismPortVal\n" +
                        "okpAddressVal :$okpAddressVal\n" +
                        "okpPortVal :$okpPortVal\n"
                val result = viewModel.setKMSettings(ismAddressVal,
                    ismPortVal,
                    okpAddressVal,
                    okpPortVal)
                if(result == ""){
                    logger.log(Logger.LogLevel.SUCCESS, "setKMSettings", paramsString, "0")
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setKMSettings")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Уведомление")
                    alertDialog.setMessage("Операция выполнена успешно")
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                    }
                    alertDialog.show()
                }
                else{
                    logger.log(Logger.LogLevel.ERROR, "setKMSettings", paramsString, result)
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    val errorCode = viewModel.getErrorCode()
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setKMSettings")}}
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
            val result = viewModel.getKMSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getKMSettings", paramsString, result[0].toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getKMSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getKMSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getKMSettings")}}
                ismAddressVal = result[0].toString()
                ismAddress.text = Editable.Factory.getInstance().newEditable(ismAddressVal)

                ismPortVal = result[1].toString().toInt()
                ismPort.text = Editable.Factory.getInstance().newEditable(ismPortVal.toString())

                okpAddressVal = result[2].toString()
                okpAddress.text = Editable.Factory.getInstance().newEditable(okpAddressVal)

                okpPortVal = result[3].toString().toInt()
                okpPort.text = Editable.Factory.getInstance().newEditable(okpPortVal.toString())

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

        val kmSettingsPrefs = requireContext().getSharedPreferences("kmSettingsPrefs", Context.MODE_PRIVATE)

        with(kmSettingsPrefs.edit()){
            putString("ismAddressVal", ismAddressVal)
            putInt("ismPortVal", ismPortVal)
            putString("okpAddressVal", okpAddressVal)
            putInt("okpPortVal", okpPortVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(KmSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
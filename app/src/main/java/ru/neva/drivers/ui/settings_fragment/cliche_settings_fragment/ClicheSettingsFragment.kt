package ru.neva.drivers.ui.settings_fragment.cliche_settings_fragment

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
import ru.neva.drivers.databinding.FragmentClicheSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.main_fragment.MainFragment
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class ClicheSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = ClicheSettingsFragment()
    }

    private lateinit var viewModel: ClicheSettingsViewModel
    private var _binding: FragmentClicheSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton

    private lateinit var footerSize: EditText
    private lateinit var headerSize: EditText
    private lateinit var cutterDistance1: EditText
    private lateinit var cutterDistance2: EditText
    private lateinit var clicheAutoprint: CheckBox
    private lateinit var clicheAutocut: CheckBox
    private lateinit var clicheText: EditText

    private var footerSizeVal: Int = 1
    private var headerSizeVal: Int = 0
    private var cutterDistance1Val: Int = 0
    private var cutterDistance2Val: Int = 0
    private var clicheAutoprintVal: Int = 0
    private var clicheAutocutVal: Int = 0
    private var clicheTextVal: Array<String> = Array(20) {""}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClicheSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad

        this.footerSize = _binding!!.etFooterSize
        this.headerSize = _binding!!.etHeaderSize
        this.cutterDistance1 = _binding!!.etCutterDistance1
        this.cutterDistance2 = _binding!!.etCutterDistance2
        this.clicheAutoprint = _binding!!.cbClicheAutoprint
        this.clicheAutocut = _binding!!.cbClicheAutocut
        this.clicheText = _binding!!.etClicheTextInput

        val clicheSettingsPrefs = requireContext().getSharedPreferences("clicheSettingsPrefs", Context.MODE_PRIVATE)
        
        footerSizeVal = clicheSettingsPrefs.getInt("footerSizeVal", 0)
        footerSize.text = Editable.Factory.getInstance().newEditable(footerSizeVal.toString())

        headerSizeVal = clicheSettingsPrefs.getInt("headerSizeVal", 0)
        headerSize.text = Editable.Factory.getInstance().newEditable(headerSizeVal.toString())

        cutterDistance1Val = clicheSettingsPrefs.getInt("cutterDistance1Val", 0)
        cutterDistance1.text = Editable.Factory.getInstance().newEditable(cutterDistance1Val.toString())

        cutterDistance2Val = clicheSettingsPrefs.getInt("cutterDistance2Val", 0)
        cutterDistance2.text = Editable.Factory.getInstance().newEditable(cutterDistance2Val.toString())

        clicheAutoprintVal = clicheSettingsPrefs.getInt("clicheAutoprintVal", 0)
        when(clicheAutoprintVal){
            1 -> clicheAutoprint.isChecked = true
            0 -> clicheAutoprint.isChecked = false
        }

        clicheAutocutVal = clicheSettingsPrefs.getInt("clicheAutocutVal", 0)
        when(clicheAutocutVal){
            1 -> clicheAutocut.isChecked = true
            0 -> clicheAutocut.isChecked = false
        }

        for (i in clicheTextVal.indices) {
            clicheTextVal[i] = clicheSettingsPrefs.getString("clicheTextVal_$i", "") ?: ""
        }
        clicheText.text = Editable.Factory.getInstance().newEditable(clicheTextVal.joinToString(separator = "\n"))

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        footerSize.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    footerSizeVal = 1
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                footerSizeVal = num

                if (num < 1 || num > 20) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        footerSizeVal = 1
                    } else {
                        s.replace(0, s.length, "20")
                        footerSizeVal = 20
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        headerSize.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    footerSizeVal = 0
                    s?.replace(0, s.length, "0")
                    return
                }

                val num = s.toString().toInt()
                headerSizeVal = num

                if (num < 0 || num > 20) {
                    if (num < 0) {
                        s.replace(0, s.length, "0")
                        headerSizeVal = 0
                    } else {
                        s.replace(0, s.length, "20")
                        headerSizeVal = 20
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cutterDistance1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    footerSizeVal = 0
                    s?.replace(0, s.length, "0")
                    return
                }

                val num = s.toString().toInt()
                cutterDistance1Val = num

                if (num < 0 || num > 255) {
                    if (num < 0) {
                        s.replace(0, s.length, "0")
                        cutterDistance1Val = 0
                    } else {
                        s.replace(0, s.length, "255")
                        cutterDistance1Val = 255
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cutterDistance2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    footerSizeVal = 0
                    s?.replace(0, s.length, "0")
                    return
                }

                val num = s.toString().toInt()
                cutterDistance2Val = num

                if (num < 0 || num > 255) {
                    if (num < 0) {
                        s.replace(0, s.length, "0")
                        cutterDistance2Val = 0
                    } else {
                        s.replace(0, s.length, "255")
                        cutterDistance2Val = 255
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        clicheAutoprint.setOnClickListener(View.OnClickListener { view ->
            when(clicheAutoprint.isChecked){
                true -> clicheAutoprintVal = 1
                false -> clicheAutoprintVal = 0
            }
        })

        clicheAutocut.setOnClickListener(View.OnClickListener { view ->
            when(clicheAutocut.isChecked){
                true -> clicheAutocutVal = 1
                false -> clicheAutocutVal = 0
            }
        })

        clicheText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (clicheText.lineCount > 20) {
                    clicheText.setText(s?.substring(0, clicheText.length() - 1))
                    clicheText.setSelection(clicheText.length())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val text = clicheText.text.toString()
                val lines = text.split("\n")
                lines.take(20).forEachIndexed { index, line ->
                    clicheTextVal[index] = line
                    println(clicheTextVal[index])
                }
            }
        })

        FABSave.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "footerSizeVal :$footerSizeVal\n" +
                    "headerSizeVal :$headerSizeVal\n" +
                    "cutterDistance1Val :$cutterDistance1Val\n" +
                    "cutterDistance2Val :$cutterDistance2Val\n" +
                    "clicheAutoprintVal :$clicheAutoprintVal\n" +
                    "clicheAutocutVal :$clicheAutocutVal\n"
            val result = viewModel.setClicheSettings(footerSizeVal,
                headerSizeVal,
                cutterDistance1Val,
                cutterDistance2Val,
                clicheAutoprintVal,
                clicheAutocutVal,
                clicheTextVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "setClicheSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setClicheSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "setClicheSettings", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setClicheSettings")}}
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
            val result = viewModel.getClicheSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getClicheSettings", paramsString, result.toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getClicheSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getClicheSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getClicheSettings")}}
                footerSizeVal = result[0].toString().toInt()
                footerSize.text = Editable.Factory.getInstance().newEditable(footerSizeVal.toString())

                headerSizeVal = result[1].toString().toInt()
                headerSize.text = Editable.Factory.getInstance().newEditable(headerSizeVal.toString())

                cutterDistance1Val = result[2].toString().toInt()
                cutterDistance1.text = Editable.Factory.getInstance().newEditable(cutterDistance1Val.toString())

                cutterDistance2Val = result[3].toString().toInt()
                cutterDistance2.text = Editable.Factory.getInstance().newEditable(cutterDistance2Val.toString())

                clicheAutoprintVal = result[4].toString().toInt()
                when(clicheAutoprintVal){
                    1 -> clicheAutoprint.isChecked = true
                    0 -> clicheAutoprint.isChecked = false
                }

                clicheAutocutVal = result[5].toString().toInt()
                when(clicheAutocutVal){
                    1 -> clicheAutocut.isChecked = true
                    0 -> clicheAutocut.isChecked = false
                }

                for(i in clicheTextVal.indices){
                    clicheTextVal[i] = result[6 + i].toString()
                }
                clicheText.text = Editable.Factory.getInstance().newEditable(clicheTextVal.joinToString(separator = "\n"))

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

        val clicheSettingsPrefs = requireContext().getSharedPreferences("clicheSettingsPrefs", Context.MODE_PRIVATE)

        with(clicheSettingsPrefs.edit()){
            putInt("footerSizeVal", footerSizeVal)
            putInt("headerSizeVal", headerSizeVal)
            putInt("cutterDistance1Val", cutterDistance1Val)
            putInt("cutterDistance2Val", cutterDistance2Val)
            putInt("clicheAutoprintVal", clicheAutoprintVal)
            putInt("clicheAutocutVal", clicheAutocutVal)
            for (i in clicheTextVal.indices) {
                putString("clicheTextVal_$i", clicheTextVal[i])
            }
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ClicheSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
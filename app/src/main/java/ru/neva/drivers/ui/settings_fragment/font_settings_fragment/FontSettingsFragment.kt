package ru.neva.drivers.ui.settings_fragment.font_settings_fragment

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.databinding.FragmentFontSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class FontSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = FontSettingsFragment()
    }

    private lateinit var viewModel: FontSettingsViewModel
    private var _binding: FragmentFontSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton

    private lateinit var chequePattern: Spinner
    private lateinit var fontSize: Spinner
    private lateinit var lineSplit: EditText
    private lateinit var symbolsCount: EditText

    private var chequePatternVal: Int = 0
    private var fontSizeVal: Int = 1
    private var lineSplitVal: Int = 1
    private var symbolsCountVal: Int = 24

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFontSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad

        this.chequePattern = _binding!!.spChequePattern
        this.fontSize = _binding!!.spFontSize
        this.lineSplit = _binding!!.etLineSplit
        this.symbolsCount = _binding!!.etSymbolsCount

        val chequePatterns = arrayOf("Универсальный",
            "Короткий 80мм",
            "Короткий 57мм",
            "Стандартный 80мм",
            "Стандартный 57мм",
            "Крупный 80мм")

        val fontSizes = arrayOf(1,2,3,4,5,6,7)

        val chequePatternAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, chequePatterns)
        chequePattern.adapter = chequePatternAdapter
        chequePattern.setSelection(0)

        val fontSizeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, fontSizes)
        fontSize.adapter = fontSizeAdapter
        fontSize.setSelection(0)

        val fontSettingsPrefs = requireContext().getSharedPreferences("fontSettingsPrefs", Context.MODE_PRIVATE)

        chequePatternVal = fontSettingsPrefs.getInt("chequePatternVal", 0)
        when(chequePatternVal){
            0 -> chequePattern.setSelection(0)
            1 -> chequePattern.setSelection(1)
            2 -> chequePattern.setSelection(2)
            3 -> chequePattern.setSelection(3)
            4 -> chequePattern.setSelection(4)
            5 -> chequePattern.setSelection(5)
        }

        fontSizeVal = fontSettingsPrefs.getInt("fontSizeVal", 1)
        when(fontSizeVal){
            1 -> fontSize.setSelection(0)
            2 -> fontSize.setSelection(1)
            3 -> fontSize.setSelection(2)
            4 -> fontSize.setSelection(3)
            5 -> fontSize.setSelection(4)
            6 -> fontSize.setSelection(5)
            7 -> fontSize.setSelection(6)
        }

        lineSplitVal = fontSettingsPrefs.getInt("lineSplitVal", 1)
        lineSplit.text = Editable.Factory.getInstance().newEditable(lineSplitVal.toString())

        symbolsCountVal = fontSettingsPrefs.getInt("symbolsCountVal", 24)
        symbolsCount.text = Editable.Factory.getInstance().newEditable(symbolsCountVal.toString())

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        chequePattern.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> {chequePatternVal = 0
                        symbolsCount.isEnabled = true
                        fontSize.isEnabled = true
                        fontSizeVal = 2
                        symbolsCountVal = 24
                        fontSize.setSelection(1)
                        symbolsCount.setText("24")

                    }
                    1 -> {chequePatternVal = 1
                        symbolsCount.isEnabled = false
                        fontSize.isEnabled = false
                        fontSizeVal = 7
                        symbolsCountVal = 64
                        fontSize.setSelection(6)
                        symbolsCount.setText("64")
                    }
                    2 -> {chequePatternVal = 2
                        symbolsCount.isEnabled = false
                        fontSize.isEnabled = false
                        fontSizeVal = 7
                        symbolsCountVal = 46
                        fontSize.setSelection(6)
                        symbolsCount.setText("46")
                    }
                    3 -> {chequePatternVal = 3
                        symbolsCount.isEnabled = false
                        fontSize.isEnabled = false
                        fontSizeVal = 2
                        symbolsCountVal = 48
                        fontSize.setSelection(1)
                        symbolsCount.setText("48")
                    }
                    4 -> {chequePatternVal = 4
                        symbolsCount.isEnabled = false
                        fontSize.isEnabled = false
                        fontSizeVal = 2
                        symbolsCountVal = 34
                        fontSize.setSelection(1)
                        symbolsCount.setText("34")
                    }
                    5 -> {chequePatternVal = 5
                        symbolsCount.isEnabled = false
                        fontSize.isEnabled = false
                        fontSizeVal = 1
                        symbolsCountVal = 32
                        fontSize.setSelection(0)
                        symbolsCount.setText("32")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        fontSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> fontSizeVal = 1
                    1 -> fontSizeVal = 2
                    2 -> fontSizeVal = 3
                    3 -> fontSizeVal = 4
                    4 -> fontSizeVal = 5
                    5 -> fontSizeVal = 6
                    6 -> fontSizeVal = 7
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        lineSplit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    lineSplitVal = 1
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                lineSplitVal = num

                if (num < 1 || num > 15) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        lineSplitVal = 1
                    } else {
                        s.replace(0, s.length, "15")
                        lineSplitVal = 15
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        symbolsCount.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (symbolsCount.text.toString() == ""){
                    symbolsCount.setText("24")
                    symbolsCountVal = 24
                    return@setOnFocusChangeListener
                }

                val num = symbolsCount.text.toString().toInt()
                symbolsCountVal = num

                if (num < 24 || num > 64) {
                    if (num < 24) {
                        symbolsCount.setText("24")
                        symbolsCountVal = 24
                    } else {
                        symbolsCount.setText("64")
                        symbolsCountVal = 64
                    }
                }

            }
        }

        FABSave.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "fontSizeVal :$fontSizeVal\n" +
                    "lineSplitVal :$lineSplitVal\n" +
                    "symbolsCountVal :$symbolsCountVal\n"
            val result = viewModel.setFontSettings(fontSizeVal,
                lineSplitVal,
                symbolsCountVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "setFontSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setFontSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "setFontSettings", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setFontSettings")}}
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
            val result = viewModel.getFontSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getFontSettings", paramsString, result[0].toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getFontSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getFontSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getFontSettings")}}
                fontSizeVal = result[0].toString().toInt()
                when(fontSizeVal){
                    1 -> fontSize.setSelection(0)
                    2 -> fontSize.setSelection(1)
                    3 -> fontSize.setSelection(2)
                    4 -> fontSize.setSelection(3)
                    5 -> fontSize.setSelection(4)
                    6 -> fontSize.setSelection(5)
                    7 -> fontSize.setSelection(6)
                }

                lineSplitVal = result[1].toString().toInt()
                lineSplit.text = Editable.Factory.getInstance().newEditable(lineSplitVal.toString())

                symbolsCountVal = result[2].toString().toInt()
                symbolsCount.text = Editable.Factory.getInstance().newEditable(symbolsCountVal.toString())

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

        val fontSettingsPrefs = requireContext().getSharedPreferences("fontSettingsPrefs", Context.MODE_PRIVATE)

        with(fontSettingsPrefs.edit()){
            putInt("chequePatternVal", chequePatternVal)
            putInt("fontSizeVal", fontSizeVal)
            putInt("lineSplitVal", lineSplitVal)
            putInt("symbolsCountVal", symbolsCountVal)
            apply()
        }
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FontSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
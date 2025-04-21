package ru.neva.drivers.ui.fin_ops_fragment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentFinOpsBinding
import ru.neva.drivers.databinding.FragmentRegBinding
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class FinOpsFragment : Fragment() {

    companion object {
        fun newInstance() = FinOpsFragment()
    }

    private lateinit var viewModel: FinOpsViewModel
    private var _binding: FragmentFinOpsBinding? = null
    private val binding get() = _binding!!

    private lateinit var summ: EditText
    private lateinit var doNotPrint: CheckBox
    private lateinit var depositButton: Button
    private lateinit var paymentButton: Button
    private lateinit var resultText: TextView

    private var summVal: Double = 0.01
    private var doNotPrintVal: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFinOpsBinding.inflate(inflater, container, false)

        this.summ = _binding!!.etSumm
        this.doNotPrint = _binding!!.cbFinOpsDoNotPrint
        this.depositButton = _binding!!.depositButton
        this.paymentButton = _binding!!.paymentButton
        this.resultText = _binding!!.tvResultText

        resultText.text = ""

        val finOpsPrefs = requireContext().getSharedPreferences("finOpsPrefs", Context.MODE_PRIVATE)

        summVal = finOpsPrefs.getString("summVal", "0.01")?.toDouble()!!
        summ.text = Editable.Factory.getInstance().newEditable(summVal.toString())

        doNotPrintVal = finOpsPrefs.getInt("doNotPrintVal", 0)
        when(doNotPrintVal){
            1 -> doNotPrint.isChecked = true
            0 -> doNotPrint.isChecked = false
        }

        resultText.text = finOpsPrefs.getString("resultText", "")

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        summ.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    summVal = 0.01
                    s?.replace(0, s.length, "0.01")
                    return
                }

                if (!s.toString().matches(Regex("^\\d+(\\.\\d{0,2})?$"))) {
                    s?.delete(s.length - 1, s.length)
                }

                val num = s.toString().toDouble()
                summVal = num
                if (num < 0.01 || num > 1000000.0) {
                    if (num < 0.01) {
                        s.replace(0, s.length, "0.01")
                        summVal = 0.01
                    } else {
                        s.replace(0, s.length, "1000000.0")
                        summVal = 1000000.0
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        doNotPrint.setOnClickListener(View.OnClickListener { view ->
            when(doNotPrint.isChecked){
                true -> doNotPrintVal = 1
                false -> doNotPrintVal = 0
            }
        })

        depositButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "summVal :$summVal\n" +
                    "doNotPrintVal :$doNotPrintVal\n"
            val result = viewModel.deposit(summVal,
                doNotPrintVal)
            if(result.first != -1){
                logger.log(Logger.LogLevel.SUCCESS, "deposit", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "deposit")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
                resultText.text = result.second
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "deposit", paramsString, result.second)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "deposit")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result.second)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        paymentButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "summVal :$summVal\n" +
                    "doNotPrintVal :$doNotPrintVal\n"
            val result = viewModel.payment(summVal,
                doNotPrintVal)
            if(result.first != -1){
                logger.log(Logger.LogLevel.SUCCESS, "payment", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "payment")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
                resultText.text = result.second
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "payment", paramsString, result.second)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "payment")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result.second)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val finOpsPrefs = requireContext().getSharedPreferences("finOpsPrefs", Context.MODE_PRIVATE)

        with(finOpsPrefs.edit()){
            putString("summVal", summVal.toString())
            putInt("doNotPrintVal", doNotPrintVal)
            putString("resultText", resultText.text.toString())
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FinOpsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
package ru.neva.drivers.ui.cashier_fragment

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
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentCashierBinding
import ru.neva.drivers.databinding.FragmentRegBinding
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class CashierFragment : Fragment() {

    companion object {
        fun newInstance() = CashierFragment()
    }

    private lateinit var viewModel: CashierViewModel
    private var _binding: FragmentCashierBinding? = null
    private val binding get() = _binding!!

    private lateinit var cashierName: EditText
    private lateinit var cashierINN: EditText
    private lateinit var cashierSaveButton: Button

    private var cashierNameVal: String = ""
    private var cashierINNVal: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCashierBinding.inflate(inflater, container, false)

        this.cashierName = _binding!!.etCashierFio
        this.cashierINN = _binding!!.etCashierInn
        this.cashierSaveButton = _binding!!.cashierSaveButton

        val cashierPrefs = requireContext().getSharedPreferences("cashierPrefs", Context.MODE_PRIVATE)

        cashierNameVal = cashierPrefs.getString("cashierNameVal", "").toString()
        cashierName.text = Editable.Factory.getInstance().newEditable(cashierNameVal)

        cashierINNVal = cashierPrefs.getString("cashierINNVal", "").toString()
        cashierINN.text = Editable.Factory.getInstance().newEditable(cashierINNVal)

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        cashierName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashierNameVal = s.toString()
                if(cashierNameVal == ""){
                    cashierName.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashierINN.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashierINNVal = s.toString()
                if(cashierINNVal == ""){
                    cashierINN.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashierSaveButton.setOnClickListener(View.OnClickListener { view ->
            if(cashierNameVal == "" || cashierINNVal == ""){
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Некоторые данные не введены или введены неверно. Проверьте данные и повторите попытку")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                val paramsString = "cashierNameVal :$cashierNameVal\n" +
                        "cashierINNVal :$cashierINNVal\n"
                val result = viewModel.cashierSave(cashierNameVal,
                    cashierINNVal)
                if(result == ""){
                    logger.log(Logger.LogLevel.SUCCESS, "cashierSave", paramsString, "0")
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "cashierSave")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Уведомление")
                    alertDialog.setMessage("Операция выполнена успешно")
                    alertDialog.setPositiveButton("OK") { dialog, which ->

                    }
                    alertDialog.show()
                }
                else{
                    logger.log(Logger.LogLevel.ERROR, "cashierSave", paramsString, result)
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    val errorCode = viewModel.getErrorCode()
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "cashierSave")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Ошибка")
                    alertDialog.setMessage(result)
                    alertDialog.setPositiveButton("OK") { dialog, which ->

                    }
                    alertDialog.show()
                }
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val cashierPrefs = requireContext().getSharedPreferences("cashierPrefs", Context.MODE_PRIVATE)

        with(cashierPrefs.edit()){
            putString("cashierNameVal", cashierNameVal)
            putString("cashierINNVal", cashierINNVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CashierViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
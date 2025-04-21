package ru.neva.drivers.ui.km_check_fragment

import android.app.ProgressDialog
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
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.databinding.FragmentKmCheckBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.main_fragment.MainFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class KmCheckFragment : Fragment() {

    companion object {
        fun newInstance() = KmCheckFragment()
    }

    private lateinit var viewModel: KmCheckViewModel
    private var _binding: FragmentKmCheckBinding? = null
    private val binding get() = _binding!!

    private lateinit var kmText: EditText
    private lateinit var kmType: Spinner
    private lateinit var sendButton: Button
    private lateinit var acceptButton: Button
    private lateinit var rejectButton: Button
    private lateinit var resultText: TextView

    private var kmTextVal: String = ""
    private var kmTypeVal: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKmCheckBinding.inflate(inflater, container, false)

        this.kmText = _binding!!.etKmText
        this.kmType = _binding!!.spKmType
        this.sendButton = _binding!!.sendButton
        this.acceptButton = _binding!!.acceptButton
        this.rejectButton = _binding!!.rejectButton
        this.resultText = _binding!!.tvResultText

        val kmTypes = arrayOf("0 - Текст (GS1 = \"{FNC1}\")",
            "1 - Base64",
            "2 - Массив байт",
            "3 - Текст (GS1 = \"\\u001d\")")

        val kmTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kmTypes)
        kmType.adapter =kmTypeAdapter
        kmType.setSelection(0)

        val kmCheckPrefs = requireContext().getSharedPreferences("kmCheckPrefs", Context.MODE_PRIVATE)

        kmTextVal = kmCheckPrefs.getString("kmTextVal", "").toString()
        kmText.text = Editable.Factory.getInstance().newEditable(kmTextVal)

        kmTypeVal = kmCheckPrefs.getInt("kmTypeVal", 0)
        when(kmTypeVal){
            0 -> kmType.setSelection(0)
            1 -> kmType.setSelection(1)
            2 -> kmType.setSelection(2)
            3 -> kmType.setSelection(3)
        }

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        kmText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                kmTextVal = s.toString()
                if(kmTextVal == ""){
                    kmText.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        kmType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> kmTypeVal = 0
                    1 -> kmTypeVal = 1
                    2 -> kmTypeVal = 2
                    3 -> kmTypeVal = 3
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        sendButton.setOnClickListener(View.OnClickListener { view ->
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Проверка КМ")
            progressDialog.setMessage("Идет проверка КМ. Пожалуйста, подождите.")
            progressDialog.setCancelable(false)
            progressDialog.show()
            if (kmTextVal == ""){
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Некоторые данные не введены или введены неверно. Проверьте данные и повторите попытку")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                Thread {
                    val paramsString = "kmTextVal :$kmTextVal\n" +
                            "kmTypeVal :$kmTypeVal\n"
                    val result = viewModel.checkKM(kmTextVal, kmTypeVal)
                    requireActivity().runOnUiThread {
                        progressDialog.dismiss()
                        if(result.first != -1){
                            logger.log(Logger.LogLevel.SUCCESS, "checkKM", paramsString, "0")
                            val timestampString = dbSDF.format(Calendar.getInstance().time)
                            runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "checkKM")}}
                            resultText.text = ""
                            val alertDialog = AlertDialog.Builder(requireContext())
                            alertDialog.setTitle("Уведомление")
                            alertDialog.setMessage("Операция выполнена успешно")
                            alertDialog.setPositiveButton("OK") { dialog, which ->
                                // Действие при нажатии на кнопку
                            }
                            alertDialog.show()
                            resultText.text = result.second
                        }
                        else{
                            logger.log(Logger.LogLevel.ERROR, "checkKM", paramsString, result.second)
                            val timestampString = dbSDF.format(Calendar.getInstance().time)
                            val errorCode = viewModel.getErrorCode()
                            runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "checkKM")}}
                            val alertDialog = AlertDialog.Builder(requireContext())
                            alertDialog.setTitle("Ошибка")
                            alertDialog.setMessage(result.second)
                            alertDialog.setPositiveButton("OK") { dialog, which ->
                                // Действие при нажатии на кнопку
                            }
                            alertDialog.show()
                        }
                    }
                }.start()
            }
        })

        acceptButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.acceptKM()
            if(result.first != -1){
                logger.log(Logger.LogLevel.SUCCESS, "acceptKM", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "acceptKM")}}
                resultText.text = ""
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
                resultText.text = result.second
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "acceptKM", paramsString, result.second)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "acceptKM")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result.second)
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
        })

        rejectButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.rejectKM()
            if(result.first != -1){
                logger.log(Logger.LogLevel.SUCCESS, "rejectKM", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "rejectKM")}}
                resultText.text = ""
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
                resultText.text = result.second
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "rejectKM", paramsString, result.second)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "rejectKM")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result.second)
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

        val kmCheckPrefs = requireContext().getSharedPreferences("kmCheckPrefs", Context.MODE_PRIVATE)

        with(kmCheckPrefs.edit()){
            putString("kmTextVal", kmTextVal)
            putInt("kmTypeVal", kmTypeVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(KmCheckViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
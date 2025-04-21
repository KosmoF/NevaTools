package ru.neva.drivers.ui.service_fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentRegBinding
import ru.neva.drivers.databinding.FragmentServiceBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.main_fragment.MainFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ServiceFragment : Fragment() {

    companion object {
        fun newInstance() = ServiceFragment()
    }

    private lateinit var viewModel: ServiceViewModel
    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!

    private lateinit var datetimeText: TextView
    private lateinit var getDatetimeButton: Button
    private lateinit var setDatetimeButton: Button
    private lateinit var beepButton: Button
    private lateinit var cutTapeButton: Button
    private lateinit var openCashboxButton: Button
    private lateinit var rewindTapeButton: Button
    private lateinit var rebootKKTButton: Button

    private var datetimeVal: Date = Calendar.getInstance().time

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServiceBinding.inflate(inflater, container, false)

        this.datetimeText = _binding!!.etDatetimeText
        this.getDatetimeButton = _binding!!.getDatetimeButton
        this.setDatetimeButton = _binding!!.setDatetimeButton
        this.beepButton = _binding!!.beepButton
        this.cutTapeButton = _binding!!.cutTapeButton
        this.openCashboxButton = _binding!!.openCashboxButton
        this.rewindTapeButton = _binding!!.rewindTapeButton
        this.rebootKKTButton = _binding!!.rebootKktButton

        val servicePrefs = requireContext().getSharedPreferences("servicePrefs", Context.MODE_PRIVATE)

        datetimeVal = Date(servicePrefs.getLong("datetimeVal", 0))
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        datetimeText.text = sdf.format(datetimeVal)

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        datetimeText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    datetimeVal = calendar.time
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    datetimeText.text = sdf.format(datetimeVal)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

                timePickerDialog.show()
            }, year, month, day)

            datePickerDialog.show()
        }

        getDatetimeButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.getDatetime()
            if(result is Date){
                logger.log(Logger.LogLevel.SUCCESS, "getDatetime", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getDatetime")}}
                datetimeVal = result
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                datetimeText.text = sdf.format(datetimeVal)
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "getDatetime", paramsString, result.toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getDatetime")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result.toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        setDatetimeButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "datetimeVal :$datetimeVal\n"
            val result = viewModel.setDatetime(datetimeVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "setDatetime", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setDatetime")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "setDatetime", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setDatetime")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        beepButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.beep()
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "beep", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "beep")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "beep", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "beep")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        cutTapeButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.cutTape()
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "cutTape", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "cutTape")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "cutTape", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "cutTape")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        openCashboxButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.openCashbox()
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "openCashbox", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "openCashbox")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "openCashbox", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "openCashbox")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        rewindTapeButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.rewindTape()
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "rewindTape", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "rewindTape")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "rewindTape", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "rewindTape")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        rebootKKTButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.rebootKKT()
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "rebootKKT", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "rebootKKT")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    //Закрытие приложения
                    finishAffinity(requireActivity())
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "rebootKKT", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "rebootKKT")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val servicePrefs = requireContext().getSharedPreferences("servicePrefs", Context.MODE_PRIVATE)

        with(servicePrefs.edit()){
            putLong("datetimeVal", datetimeVal.time)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ServiceViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
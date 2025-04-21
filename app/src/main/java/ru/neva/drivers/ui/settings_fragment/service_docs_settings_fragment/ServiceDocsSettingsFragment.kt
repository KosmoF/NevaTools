package ru.neva.drivers.ui.settings_fragment.service_docs_settings_fragment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentPrintSettingsBinding
import ru.neva.drivers.databinding.FragmentSectionsSettingsBinding
import ru.neva.drivers.databinding.FragmentServiceDocsSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class ServiceDocsSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = ServiceDocsSettingsFragment()
    }

    private lateinit var viewModel: ServiceDocsSettingsViewModel
    private var _binding: FragmentServiceDocsSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton

    private lateinit var printReadyDoc: CheckBox
    private lateinit var printClicheService: CheckBox
    private lateinit var printDatetimeDoc: CheckBox
    private lateinit var printIPDoc: CheckBox

    private var printReadyDocVal: Int = 0
    private var printClicheServiceVal: Int = 0
    private var printDatetimeDocVal: Int = 0
    private var printIPDocVal: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServiceDocsSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad

        this.printReadyDoc = _binding!!.cbPrintReadyDoc
        this.printClicheService = _binding!!.cbPrintClicheService
        this.printDatetimeDoc = _binding!!.cbPrintDatetimeDoc
        this.printIPDoc = _binding!!.cbPrintIPDoc

        val serviceDocsSettingsPrefs = requireContext().getSharedPreferences("serviceDocsSettingsPrefs", Context.MODE_PRIVATE)

        printReadyDocVal = serviceDocsSettingsPrefs.getInt("printReadyDocVal", 0)
        when(printReadyDocVal){
            1 -> printReadyDoc.isChecked = true
            0 -> printReadyDoc.isChecked = false
        }

        printClicheServiceVal = serviceDocsSettingsPrefs.getInt("printClicheServiceVal", 0)
        when(printClicheServiceVal){
            1 -> printClicheService.isChecked = true
            0 -> printClicheService.isChecked = false
        }

        printDatetimeDocVal = serviceDocsSettingsPrefs.getInt("printDatetimeDocVal", 0)
        when(printDatetimeDocVal){
            1 -> printDatetimeDoc.isChecked = true
            0 -> printDatetimeDoc.isChecked = false
        }

        printIPDocVal = serviceDocsSettingsPrefs.getInt("printIPDocVal", 0)
        when(printIPDocVal){
            1 -> printIPDoc.isChecked = true
            0 -> printIPDoc.isChecked = false
        }

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        printReadyDoc.setOnClickListener(View.OnClickListener { view ->
            when(printReadyDoc.isChecked){
                true -> printReadyDocVal = 1
                false -> printReadyDocVal = 0
            }
        })

        printClicheService.setOnClickListener(View.OnClickListener { view ->
            when(printClicheService.isChecked){
                true -> printClicheServiceVal = 1
                false -> printClicheServiceVal = 0
            }
        })

        printDatetimeDoc.setOnClickListener(View.OnClickListener { view ->
            when(printDatetimeDoc.isChecked){
                true -> printDatetimeDocVal = 1
                false -> printDatetimeDocVal = 0
            }
        })

        printIPDoc.setOnClickListener(View.OnClickListener { view ->
            when(printIPDoc.isChecked){
                true -> printIPDocVal = 1
                false -> printIPDocVal = 0
            }
        })

        FABSave.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "printReadyDocVal :$printReadyDocVal\n" +
                    "printClicheServiceVal :$printClicheServiceVal\n" +
                    "printDatetimeDocVal :$printDatetimeDocVal\n" +
                    "printIPDocVal :$printIPDocVal\n"
            val result = viewModel.setServiceDocsSettings(printReadyDocVal,
                printClicheServiceVal,
                printDatetimeDocVal,
                printIPDocVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "setServiceDocsSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setServiceDocsSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "setServiceDocsSettings", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setServiceDocsSettings")}}
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
            val result = viewModel.getServiceDocsSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getServiceDocsSettings", paramsString, result[0].toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getServiceDocsSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getServiceDocsSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getServiceDocsSettings")}}
                printReadyDocVal = result[0].toString().toInt()
                when(printReadyDocVal){
                    1 -> printReadyDoc.isChecked = true
                    0 -> printReadyDoc.isChecked = false
                }

                printClicheServiceVal = result[1].toString().toInt()
                when(printClicheServiceVal){
                    1 -> printClicheService.isChecked = true
                    0 -> printClicheService.isChecked = false
                }

                printDatetimeDocVal = result[2].toString().toInt()
                when(printDatetimeDocVal){
                    1 -> printDatetimeDoc.isChecked = true
                    0 -> printDatetimeDoc.isChecked = false
                }

                printIPDocVal = result[3].toString().toInt()
                when(printIPDocVal){
                    1 -> printIPDoc.isChecked = true
                    0 -> printIPDoc.isChecked = false
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

        val serviceDocsSettingsPrefs = requireContext().getSharedPreferences("serviceDocsSettingsPrefs", Context.MODE_PRIVATE)

        with(serviceDocsSettingsPrefs.edit()){
            putInt("printReadyDocVal", printReadyDocVal)
            putInt("printclicheServiceVal", printClicheServiceVal)
            putInt("printDatetimeDocVal", printDatetimeDocVal)
            putInt("printIPDocVal", printIPDocVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ServiceDocsSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
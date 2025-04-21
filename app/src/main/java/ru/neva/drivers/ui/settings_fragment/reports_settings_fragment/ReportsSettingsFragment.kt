package ru.neva.drivers.ui.settings_fragment.reports_settings_fragment

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
import ru.neva.drivers.databinding.FragmentReportsSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class ReportsSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = ReportsSettingsFragment()
    }

    private lateinit var viewModel: ReportsSettingsViewModel
    private var _binding: FragmentReportsSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton
    
    private lateinit var printChequesCount: CheckBox
    private lateinit var printSplits: CheckBox
    private lateinit var printTaxes: CheckBox
    private lateinit var printNullTaxes: CheckBox
    private lateinit var printProfitlossSumm: CheckBox
    private lateinit var printProfitReturn: CheckBox
    private lateinit var printLossReturn: CheckBox
    private lateinit var printExtraBlocks: CheckBox
    private lateinit var printNullSumm: CheckBox
    private lateinit var printZReport: CheckBox
    private lateinit var printSummProfit: CheckBox
    private lateinit var printSummLoss: CheckBox

    private var printChequesCountVal: Int = 0
    private var printSplitsVal: Int = 0
    private var printTaxesVal: Int = 0
    private var printNullTaxesVal: Int = 0
    private var printProfitlossSummVal: Int = 0
    private var printProfitReturnVal: Int = 0
    private var printLossReturnVal: Int = 0
    private var printExtraBlocksVal: Int = 0
    private var printNullSummVal: Int = 0
    private var printZReportVal: Int = 0
    private var printSummProfitVal: Int = 0
    private var printSummLossVal: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportsSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad
        
        this.printChequesCount = _binding!!.cbPrintChequesCount
        this.printSplits = _binding!!.cbPrintSplits
        this.printTaxes = _binding!!.cbPrintTaxes
        this.printNullTaxes = _binding!!.cbPrintNullTaxes
        this.printProfitlossSumm = _binding!!.cbPrintProfitlossSumm
        this.printProfitReturn = _binding!!.cbPrintProfitReturn
        this.printLossReturn = _binding!!.cbPrintLossReturn
        this.printExtraBlocks = _binding!!.cbPrintExtraBlocks
        this.printNullSumm = _binding!!.cbPrintNullSumm
        this.printZReport = _binding!!.cbPrintZReport
        this.printSummProfit = _binding!!.cbPrintSummProfit
        this.printSummLoss = _binding!!.cbPrintSummLoss

        val reportsSettingsPrefs = requireContext().getSharedPreferences("reportsSettingsPrefs", Context.MODE_PRIVATE)
        
        printChequesCountVal = reportsSettingsPrefs.getInt("printChequesCountVal", 0)
        when(printChequesCountVal){
            1 -> printChequesCount.isChecked = true
            0 -> printChequesCount.isChecked = false
        }

        printSplitsVal = reportsSettingsPrefs.getInt("printSplitsVal", 0)
        when(printSplitsVal){
            1 -> printSplits.isChecked = true
            0 -> printSplits.isChecked = false
        }

        printTaxesVal = reportsSettingsPrefs.getInt("printTaxesVal", 0)
        when(printTaxesVal){
            1 -> printTaxes.isChecked = true
            0 -> printTaxes.isChecked = false
        }

        printNullTaxesVal = reportsSettingsPrefs.getInt("printNullTaxesVal", 0)
        when(printNullTaxesVal){
            1 -> printNullTaxes.isChecked = true
            0 -> printNullTaxes.isChecked = false
        }

        printProfitlossSummVal = reportsSettingsPrefs.getInt("printProfitlossSummVal", 0)
        when(printProfitlossSummVal){
            1 -> printProfitlossSumm.isChecked = true
            0 -> printProfitlossSumm.isChecked = false
        }

        printProfitReturnVal = reportsSettingsPrefs.getInt("printProfitReturnVal", 0)
        when(printProfitReturnVal){
            1 -> printProfitReturn.isChecked = true
            0 -> printProfitReturn.isChecked = false
        }

        printLossReturnVal = reportsSettingsPrefs.getInt("printLossReturnVal", 0)
        when(printLossReturnVal){
            1 -> printLossReturn.isChecked = true
            0 -> printLossReturn.isChecked = false
        }

        printExtraBlocksVal = reportsSettingsPrefs.getInt("printExtraBlocksVal", 0)
        when(printExtraBlocksVal){
            1 -> printExtraBlocks.isChecked = true
            0 -> printExtraBlocks.isChecked = false
        }

        printNullSummVal = reportsSettingsPrefs.getInt("printNullSummVal", 0)
        when(printNullSummVal){
            1 -> printNullSumm.isChecked = true
            0 -> printNullSumm.isChecked = false
        }

        printZReportVal = reportsSettingsPrefs.getInt("printZReportVal", 0)
        when(printZReportVal){
            1 -> printZReport.isChecked = true
            0 -> printZReport.isChecked = false
        }

        printSummProfitVal = reportsSettingsPrefs.getInt("printSummProfitVal", 0)
        when(printSummProfitVal){
            1 -> printSummProfit.isChecked = true
            0 -> printSummProfit.isChecked = false
        }

        printSummLossVal = reportsSettingsPrefs.getInt("printSummLossVal", 0)
        when(printSummLossVal){
            1 -> printSummLoss.isChecked = true
            0 -> printSummLoss.isChecked = false
        }

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        printChequesCount.setOnClickListener(View.OnClickListener { view ->
            when(printChequesCount.isChecked){
                true -> printChequesCountVal = 1
                false -> printChequesCountVal = 0
            }
        })

        printSplits.setOnClickListener(View.OnClickListener { view ->
            when(printSplits.isChecked){
                true -> printSplitsVal = 1
                false -> printSplitsVal = 0
            }
        })

        printTaxes.setOnClickListener(View.OnClickListener { view ->
            when(printTaxes.isChecked){
                true -> printTaxesVal = 1
                false -> printTaxesVal = 0
            }
        })

        printNullTaxes.setOnClickListener(View.OnClickListener { view ->
            when(printNullTaxes.isChecked){
                true -> printNullTaxesVal = 1
                false -> printNullTaxesVal = 0
            }
        })

        printProfitlossSumm.setOnClickListener(View.OnClickListener { view ->
            when(printProfitlossSumm.isChecked){
                true -> printProfitlossSummVal = 1
                false -> printProfitlossSummVal = 0
            }
        })

        printProfitReturn.setOnClickListener(View.OnClickListener { view ->
            when(printProfitReturn.isChecked){
                true -> printProfitReturnVal = 1
                false -> printProfitReturnVal = 0
            }
        })

        printLossReturn.setOnClickListener(View.OnClickListener { view ->
            when(printLossReturn.isChecked){
                true -> printLossReturnVal = 1
                false -> printLossReturnVal = 0
            }
        })

        printExtraBlocks.setOnClickListener(View.OnClickListener { view ->
            when(printExtraBlocks.isChecked){
                true -> printExtraBlocksVal = 1
                false -> printExtraBlocksVal = 0
            }
        })

        printNullSumm.setOnClickListener(View.OnClickListener { view ->
            when(printNullSumm.isChecked){
                true -> printNullSummVal = 1
                false -> printNullSummVal = 0
            }
        })

        printZReport.setOnClickListener(View.OnClickListener { view ->
            when(printZReport.isChecked){
                true -> printZReportVal = 1
                false -> printZReportVal = 0
            }
        })

        printSummProfit.setOnClickListener(View.OnClickListener { view ->
            when(printSummProfit.isChecked){
                true -> printSummProfitVal = 1
                false -> printSummProfitVal = 0
            }
        })

        printSummLoss.setOnClickListener(View.OnClickListener { view ->
            when(printSummLoss.isChecked){
                true -> printSummLossVal = 1
                false -> printSummLossVal = 0
            }
        })

        FABSave.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "printChequesCountVal :$printChequesCountVal\n" +
                    "printSplitsVal :$printSplitsVal\n" +
                    "printTaxesVal :$printTaxesVal\n" +
                    "printNullTaxesVal :$printNullTaxesVal\n" +
                    "printProfitlossSummVal :$printProfitlossSummVal\n" +
                    "printProfitReturnVal :$printProfitReturnVal\n" +
                    "printLossReturnVal :$printLossReturnVal\n" +
                    "printExtraBlocksVal :$printExtraBlocksVal\n" +
                    "printNullSummVal :$printNullSummVal\n" +
                    "printZReportVal :$printZReportVal\n" +
                    "printSummProfitVal :$printSummProfitVal\n" +
                    "printSummLossVal :$printSummLossVal\n"
            val result = viewModel.setReportsSettings(printChequesCountVal,
                printSplitsVal,
                printTaxesVal,
                printNullTaxesVal,
                printProfitlossSummVal,
                printProfitReturnVal,
                printLossReturnVal,
                printExtraBlocksVal,
                printNullSummVal,
                printZReportVal,
                printSummProfitVal,
                printSummLossVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "setReportsSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setReportsSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "setReportsSettings", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setReportsSettings")}}
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
            val result = viewModel.getReportsSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getReportsSettings", paramsString, result[0].toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getReportsSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getReportsSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getReportsSettings")}}
                printChequesCountVal = result[0].toString().toInt()
                when(printChequesCountVal){
                    1 -> printChequesCount.isChecked = true
                    0 -> printChequesCount.isChecked = false
                }

                printSplitsVal = result[1].toString().toInt()
                when(printSplitsVal){
                    1 -> printSplits.isChecked = true
                    0 -> printSplits.isChecked = false
                }

                printTaxesVal = result[2].toString().toInt()
                when(printTaxesVal){
                    1 -> printTaxes.isChecked = true
                    0 -> printTaxes.isChecked = false
                }

                printNullTaxesVal = result[3].toString().toInt()
                when(printNullTaxesVal){
                    1 -> printNullTaxes.isChecked = true
                    0 -> printNullTaxes.isChecked = false
                }

                printProfitlossSummVal = result[4].toString().toInt()
                when(printProfitlossSummVal){
                    1 -> printProfitlossSumm.isChecked = true
                    0 -> printProfitlossSumm.isChecked = false
                }

                printProfitReturnVal = result[5].toString().toInt()
                when(printProfitReturnVal){
                    1 -> printProfitReturn.isChecked = true
                    0 -> printProfitReturn.isChecked = false
                }

                printLossReturnVal = result[6].toString().toInt()
                when(printLossReturnVal){
                    1 -> printLossReturn.isChecked = true
                    0 -> printLossReturn.isChecked = false
                }

                printExtraBlocksVal = result[7].toString().toInt()
                when(printExtraBlocksVal){
                    1 -> printExtraBlocks.isChecked = true
                    0 -> printExtraBlocks.isChecked = false
                }

                printNullSummVal = result[8].toString().toInt()
                when(printNullSummVal){
                    1 -> printNullSumm.isChecked = true
                    0 -> printNullSumm.isChecked = false
                }

                printZReportVal = result[9].toString().toInt()
                when(printZReportVal){
                    1 -> printZReport.isChecked = true
                    0 -> printZReport.isChecked = false
                }

                printSummProfitVal = result[10].toString().toInt()
                when(printSummProfitVal){
                    1 -> printSummProfit.isChecked = true
                    0 -> printSummProfit.isChecked = false
                }

                printSummLossVal = result[11].toString().toInt()
                when(printSummLossVal){
                    1 -> printSummLoss.isChecked = true
                    0 -> printSummLoss.isChecked = false
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

        val reportsSettingsPrefs = requireContext().getSharedPreferences("reportsSettingsPrefs", Context.MODE_PRIVATE)

        with(reportsSettingsPrefs.edit()){
            putInt("printChequesCountVal", printChequesCountVal)
            putInt("printSplitsVal", printSplitsVal)
            putInt("printTaxesVal", printTaxesVal)
            putInt("printNullTaxesVal", printNullTaxesVal)
            putInt("printProfitlossSummVal", printProfitlossSummVal)
            putInt("printProfitReturnVal", printProfitReturnVal)
            putInt("printLossReturnVal", printLossReturnVal)
            putInt("printExtraBlocksVal", printExtraBlocksVal)
            putInt("printNullSummVal", printNullSummVal)
            putInt("printZReportVal", printZReportVal)
            putInt("printSummProfitVal", printSummProfitVal)
            putInt("printSummLossVal", printSummLossVal)
            apply()
        }
        
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReportsSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
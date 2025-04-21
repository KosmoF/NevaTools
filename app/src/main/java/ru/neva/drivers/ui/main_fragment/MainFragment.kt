package ru.neva.drivers.ui.main_fragment
import android.app.Activity
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ru.neva.drivers.databinding.FragmentMainBinding
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    @Inject
    lateinit var fptrHolder: FptrHolder

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    private lateinit var exportLogsButton : Button
    private lateinit var importImgButton : Button
    private lateinit var exportSettingsButton : Button
    private lateinit var importSettingsButton : Button
    private lateinit var clearImgsButton : Button
    private lateinit var readStatButton: Button

    private lateinit var driverVersion : TextView
    private lateinit var kktName : TextView
    private lateinit var kktSerial : TextView

    private lateinit var kktDatetime : TextView
    private lateinit var shiftState : TextView
    private lateinit var capState : TextView
    private lateinit var paperState : TextView

    private lateinit var unsentDocsCount : TextView
    private lateinit var firstUnsentDoc : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        this.exportLogsButton = _binding!!.exportLogsButton
        this.importImgButton = _binding!!.importImgButton
        this.exportSettingsButton = _binding!!.exportSettingsButton
        this.importSettingsButton = _binding!!.importSettingsButton
        this.clearImgsButton = _binding!!.clearImgsButton
        this.readStatButton = _binding!!.readStatButton

        this.driverVersion = _binding!!.tvDriverVersionData
        this.kktName = _binding!!.tvKktNameData
        this.kktSerial = _binding!!.tvKktSerialData

        this.kktDatetime = _binding!!.tvKktDatetimeData
        this.shiftState = _binding!!.tvKktShiftStateData
        this.capState = _binding!!.tvKktCapStateData
        this.paperState = _binding!!.tvKktPaperStateData

        this.unsentDocsCount = _binding!!.tvUnsentDocsCountData
        this.firstUnsentDoc = _binding!!.tvFirstUnsentDocData

        val logger = Logger(requireContext())

        val openImgLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedFileUri: Uri? = result.data?.data
                selectedFileUri?.let {uri ->
                    val paramsString = "FilePath: ${uri.path}\n"
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()

                    // Получаем ширину изображения
                    val width = bitmap.width

                    // Преобразуем Bitmap в byteArray
                    val pixels = Utils.getPixelsArray(bitmap)

                    val result = viewModel.importImg(pixels, width)
                    if(result.first != -1){
                        logger.log(Logger.LogLevel.SUCCESS, "importImg", paramsString, "0")
                        val alertDialog = AlertDialog.Builder(requireContext())
                        alertDialog.setTitle("Уведомление")
                        alertDialog.setMessage(result.second)
                        alertDialog.setPositiveButton("OK") { dialog, which ->
                            // Действие при нажатии на кнопку
                        }
                        alertDialog.show()
                    }
                    else{
                        logger.log(Logger.LogLevel.ERROR, "importImg", paramsString, result.second)
                        val alertDialog = AlertDialog.Builder(requireContext())
                        alertDialog.setTitle("Ошибка")
                        alertDialog.setMessage(result.second)
                        alertDialog.setPositiveButton("OK") { dialog, which ->
                            // Действие при нажатии на кнопку
                        }
                        alertDialog.show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Файл не был выбран", Toast.LENGTH_LONG).show()
            }
        }

        var params: ArrayList<String> = ArrayList()

        val openFileLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedFileUri: Uri? = result.data?.data
                selectedFileUri?.let {
                    try {
                        val inputStream = requireContext().contentResolver.openInputStream(it)
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val lines = ArrayList<String>()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            lines.add(line.toString())
                        }
                        params = lines.map { it.replace("\n", "") } as ArrayList<String>
                        val paramsString = "params: $params\n"
                        val result: String = viewModel.setKKTSettings(params)
                        if (result == "") {
                            logger.log(Logger.LogLevel.SUCCESS, "setKKTSettings", paramsString, "0")
                            val alertDialog = AlertDialog.Builder(requireContext())
                            alertDialog.setTitle("Уведомление")
                            alertDialog.setMessage("Операция выполнена успешно")
                            alertDialog.setPositiveButton("OK") { dialog, which ->
                                (activity as MainActivity).openFragment(
                                    MainFragment(),
                                    "NevaTools"
                                )
                            }
                            alertDialog.show()
                        } else {
                            logger.log(Logger.LogLevel.ERROR, "setKKTSettings", paramsString, result)
                            val alertDialog = AlertDialog.Builder(requireContext())
                            alertDialog.setTitle("Ошибка")
                            alertDialog.setMessage(result)
                            alertDialog.setPositiveButton("OK") { dialog, which ->

                            }
                            alertDialog.show()
                        }
                    } catch (e: Exception) {
                        val alertDialog = AlertDialog.Builder(requireContext())
                        alertDialog.setTitle("Ошибка")
                        alertDialog.setMessage("Не удалось открыть файл или файл содержит данные неверного формата")
                        alertDialog.setPositiveButton("OK") { dialog, which ->

                        }
                        alertDialog.show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Файл не был выбран", Toast.LENGTH_LONG).show()
            }
        }

        exportLogsButton.setOnClickListener(View.OnClickListener { view ->
            if(!(activity as MainActivity).checkPermission())
            {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Для выполнения данной функции необходимо предоставить приложению \"NevaTools\" разрешение на доступ к файлам на устройстве")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).requestPermission()
                }
                alertDialog.setNegativeButton("Отмена"){ dialog, which ->

                }
                alertDialog.show()
            }
            else {
                try {
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val downloadsPath = downloadsDir.absolutePath
                    val fileName = "logs.zip"
                    val dataDir = requireContext().applicationInfo.dataDir + "/logs"
                    val zipFilePath = "$downloadsDir/$fileName"
                    Utils.zipFolder(dataDir, zipFilePath)
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Уведомление")
                    alertDialog.setMessage("Файл с логами приложения и ККТ сохранен по пути: $downloadsPath/$fileName")
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        // Действие при нажатии на кнопку
                    }
                    alertDialog.setNeutralButton("Отправить") { dialog, which ->
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                        val uri = FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".provider", file)

                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        val shareIntent = Intent.createChooser(intent, "Поделиться через")

                        startActivity(shareIntent)

                    }
                    alertDialog.show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Не удалось сохранить файл", Toast.LENGTH_LONG).show()
                }
            }
        })

        importImgButton.setOnClickListener(View.OnClickListener { view ->
            if(!(activity as MainActivity).checkPermission())
            {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Для выполнения данной функции необходимо предоставить приложению \"NevaTools\" разрешение на доступ к файлам на устройстве")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).requestPermission()
                }
                alertDialog.setNegativeButton("Отмена"){ dialog, which ->

                }
                alertDialog.show()
            }
            else{

                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/png"
                }

                openImgLauncher.launch(intent)
            }
        })

        importSettingsButton.setOnClickListener(View.OnClickListener { view ->
            if(!(activity as MainActivity).checkPermission())
            {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Для выполнения данной функции необходимо предоставить приложению \"NevaTools\" разрешение на доступ к файлам на устройстве")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).requestPermission()
                }
                alertDialog.setNegativeButton("Отмена"){ dialog, which ->

                }
                alertDialog.show()
            }
            else{

                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "text/plain"
                }

                openFileLauncher.launch(intent)
            }
        })

        exportSettingsButton.setOnClickListener(View.OnClickListener { view ->
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Обработка")
            progressDialog.setMessage("Получение данных...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            if(!(activity as MainActivity).checkPermission())
            {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Для выполнения данной функции необходимо предоставить приложению \"NevaTools\" разрешение на доступ к файлам на устройстве")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).requestPermission()
                }
                alertDialog.setNegativeButton("Отмена"){ dialog, which ->

                }
                alertDialog.show()
            }
            else {
                Thread{
                    val paramsString = ""
                    val params: ArrayList<Any> = viewModel.getKKTSettings()
                    requireActivity().runOnUiThread {
                        progressDialog.dismiss()
                        if(params.size == 1){
                            logger.log(Logger.LogLevel.ERROR, "getKKTSettings", paramsString, params[0].toString())
                            val alertDialog = AlertDialog.Builder(requireContext())
                            alertDialog.setTitle("Ошибка")
                            alertDialog.setMessage(params[0].toString())
                            alertDialog.setPositiveButton("OK") { dialog, which ->

                            }
                            alertDialog.show()
                        }
                        else{
                            logger.log(Logger.LogLevel.SUCCESS, "getKKTSettings", paramsString, "0")
                            val strParams = StringBuilder()
                            for (param in params) {
                                strParams.append(param.toString()).append("\n")
                            }
                            try {
                                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                val downloadsPath = downloadsDir.absolutePath
                                val fileName = "settings.txt"
                                val file = File(downloadsDir, fileName)
                                file.writeText(strParams.toString())
                                val alertDialog = AlertDialog.Builder(requireContext())
                                alertDialog.setTitle("Уведомление")
                                alertDialog.setMessage("Файл с настройками ККТ сохранен по пути" + downloadsPath + "/" + fileName)
                                alertDialog.setPositiveButton("OK") { dialog, which ->
                                    // Действие при нажатии на кнопку
                                }
                                alertDialog.setNeutralButton("Отправить") { dialog, which ->
                                    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                                    val uri = FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".provider", file)

                                    val intent = Intent(Intent.ACTION_SEND)
                                    intent.type = "text/plain"
                                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                                    val shareIntent = Intent.createChooser(intent, "Поделиться через")

                                    startActivity(shareIntent)

                                }
                                alertDialog.show()
                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), "Не удалось сохранить файл", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }.start()
            }
        })

        clearImgsButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.clearImgs()
            if (result == "") {
                logger.log(Logger.LogLevel.SUCCESS, "clearImgs", paramsString, "0")
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            } else {
                logger.log(Logger.LogLevel.ERROR, "clearImgs", paramsString, result)
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result)
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
        })

        readStatButton.setOnClickListener(View.OnClickListener { view ->
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Обработка")
            progressDialog.setMessage("Получение данных...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            if(!(activity as MainActivity).checkPermission())
            {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Для выполнения данной функции необходимо предоставить приложению \"NevaTools\" разрешение на доступ к файлам на устройстве")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).requestPermission()
                }
                alertDialog.setNegativeButton("Отмена"){ dialog, which ->

                }
                alertDialog.show()
            }
            else {
                Thread {
                    val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
                    val URL = prefs.getString("serverURLVal", "").toString()
                    val username = prefs.getString("serverUsernameVal", "").toString()
                    val password = prefs.getString("serverPasswordVal", "").toString()
                    val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()
                    val result = runBlocking { withContext(Dispatchers.IO) {Requests.getKKTStat(URL, username, password, currentkktSerial) }}
                    requireActivity().runOnUiThread {
                        progressDialog.dismiss()
                        if (result.first == 200) {
                            try {
                                val kktModel = viewModel.getKKTName()
                                val kktSoftwareVersion = viewModel.getKKTSoftwareVersion()
                                val kktInfo = "Информация о подключённой ККТ:\n" +
                                        "Марка ККТ: $kktModel\n" +
                                        "Версия ПО: $kktSoftwareVersion\nСерийный номер: $currentkktSerial\n"
                                val stats = kktInfo + Utils.parseStatsFromJson(result.second)
                                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                val downloadsPath = downloadsDir.absolutePath
                                val fileName = "kktStats.txt"
                                val file = File(downloadsDir, fileName)
                                file.writeText(stats)
                                val alertDialog = AlertDialog.Builder(requireContext())
                                alertDialog.setTitle("Уведомление")
                                alertDialog.setMessage("Файл со статистикой ККТ сохранен по пути" + downloadsPath + "/" + fileName)
                                alertDialog.setPositiveButton("OK") { dialog, which ->
                                    // Действие при нажатии на кнопку
                                }
                                alertDialog.setNeutralButton("Отправить") { dialog, which ->
                                    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                                    val uri = FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".provider", file)

                                    val intent = Intent(Intent.ACTION_SEND)
                                    intent.type = "text/plain"
                                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                                    val shareIntent = Intent.createChooser(intent, "Поделиться через")

                                    startActivity(shareIntent)

                                }
                                alertDialog.show()
                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), "Не удалось сохранить файл", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            val alertDialog = AlertDialog.Builder(requireContext())
                            alertDialog.setTitle("Ошибка")
                            alertDialog.setMessage(result.second)
                            alertDialog.setPositiveButton("OK") { dialog, which ->

                            }
                            alertDialog.show()
                        }
                    }
                }.start()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
        (activity as MainActivity).setMenuVisible()
        if(!viewModel.checkConnection()){
            importImgButton.isEnabled = false
            clearImgsButton.isEnabled = false
            importSettingsButton.isEnabled = false
            exportSettingsButton.isEnabled = false
            readStatButton.isEnabled = false
        }
        else{
            importImgButton.isEnabled = true
            clearImgsButton.isEnabled = true
            importSettingsButton.isEnabled = true
            exportSettingsButton.isEnabled = true
            readStatButton.isEnabled = true
            driverVersion.text = viewModel.getDriverVersion()
            kktName.text = viewModel.getKKTName()
            kktSerial.text = viewModel.getKKTSerial()
            kktDatetime.text = viewModel.getKKTDatetime()
            shiftState.text = viewModel.getShiftState()
            capState.text = viewModel.getCapState()
            paperState.text = viewModel.getPaperState()
            unsentDocsCount.text = viewModel.getUnsentDocsCount()
            firstUnsentDoc.text = viewModel.getFirstUnsentDoc()
        }
    }
}
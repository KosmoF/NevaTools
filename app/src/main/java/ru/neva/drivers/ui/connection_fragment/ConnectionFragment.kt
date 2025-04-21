package ru.neva.drivers.ui.connection_fragment

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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.databinding.FragmentConnectionBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.connection_fragment.com_vcom_fragment.ComVcomFragment
import ru.neva.drivers.ui.main_fragment.MainFragment
import ru.neva.drivers.ui.connection_fragment.tcp_ip_fragment.TcpIpFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils

@AndroidEntryPoint
class ConnectionFragment : Fragment() {

    companion object {
        fun newInstance() = ConnectionFragment()
    }

    private var _binding: FragmentConnectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ConnectionViewModel

    private lateinit var reconnect : CheckBox
    private lateinit var kktModel : Spinner
    private lateinit var ofdChannel : Spinner
    private lateinit var kktConnectionType : Spinner
    private lateinit var kkt : TextView
    private lateinit var serverURL: EditText
    private lateinit var serverUsername: EditText
    private lateinit var serverPassword: EditText

    private lateinit var connectButton : Button

    private var reconnectVal : String = "0"
    private var kktModelVal : Int = 500
    private var ofdChannelVal : Int = 0
    private var kktConnectionTypeVal : Int = 0
    private var kktIPVal : String = ""
    private var kktPortVal : String = ""
    private var kktComVal : String = ""
    private var kktBaudrateVal : Int = 1200
    private var serverURLVal: String = ""
    private var serverUsernameVal: String = ""
    private var serverPasswordVal: String = ""

    private var currentKKTSerial: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConnectionBinding.inflate(inflater, container, false)

        this.reconnect = _binding!!.cbReconnect
        this.kktModel = _binding!!.spKktModel
        this.ofdChannel = _binding!!.spOfdChannel
        this.kktConnectionType = _binding!!.spKktConnectionType
        this.kkt = _binding!!.etKkt
        this.serverURL = _binding!!.etServerUrl
        this.serverUsername = _binding!!.etServerUsername
        this.serverPassword = _binding!!.etServerPassword

        this.connectButton = _binding!!.connectButton

        val kktModels = arrayOf("Автоматически (НЕВА)", "НЕВА-03-Ф")
        val ofdChannels = arrayOf("Нет", "Автоматически")
        val kktConnectionTypes = arrayOf("TCP/IP", "COM/VCOM")

        val kktModelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kktModels)
        kktModel.adapter = kktModelAdapter
        kktModel.setSelection(0)

        val ofdChannelsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ofdChannels)
        ofdChannel.adapter = ofdChannelsAdapter
        ofdChannel.setSelection(0)

        val kktConnectionTypesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kktConnectionTypes)
        kktConnectionType.adapter = kktConnectionTypesAdapter
        kktConnectionType.setSelection(0)

        val connectionPrefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val tcpIpPrefs = requireContext().getSharedPreferences("tcpIpPrefs", Context.MODE_PRIVATE)
        val comVcomPrefs = requireContext().getSharedPreferences("comVcomPrefs", Context.MODE_PRIVATE)

        reconnectVal = connectionPrefs.getString("reconnectVal", "0").toString()
        when(reconnectVal){
            "1" -> reconnect.isChecked = true
            "0" -> reconnect.isChecked = false
        }

        kktModelVal = connectionPrefs.getInt("kktModelVal", 500)
        when(kktModelVal){
            500 -> kktModel.setSelection(0)
            93 -> kktModel.setSelection(1)
        }

        ofdChannelVal = connectionPrefs.getInt("ofdChannelVal", 0)
        when(ofdChannelVal){
            0 -> ofdChannel.setSelection(0)
            2 -> ofdChannel.setSelection(1)
        }

        kktConnectionTypeVal = connectionPrefs.getInt("kktConnectionTypeVal", 0)
        if(kktConnectionTypeVal >= 0){
            kktConnectionType.setSelection(kktConnectionTypeVal)
        }

        kktIPVal = tcpIpPrefs.getString("kktIPVal", "").toString()
        kktPortVal = tcpIpPrefs.getString("kktPortVal", "").toString()

        kktComVal = comVcomPrefs.getString("kktComVal", "").toString()
        kktBaudrateVal = comVcomPrefs.getInt("kktBaudrateVal", -1)

        serverURLVal = connectionPrefs.getString("serverURLVal", "").toString()
        serverURL.text = Editable.Factory.getInstance().newEditable(serverURLVal.substringAfter("://"))

        serverUsernameVal = connectionPrefs.getString("serverUsernameVal", "").toString()
        serverUsername.text = Editable.Factory.getInstance().newEditable(serverUsernameVal)

        serverPasswordVal = connectionPrefs.getString("serverPasswordVal", "").toString()
        serverPassword.text = Editable.Factory.getInstance().newEditable(serverPasswordVal)

        currentKKTSerial = connectionPrefs.getString("currentKKTSerial", "").toString()
        val logger = Logger(requireContext())

        reconnect.setOnClickListener(View.OnClickListener { view ->
            when(reconnect.isChecked){
                true -> reconnectVal = "1"
                false -> reconnectVal = "0"
            }
        })

        kktModel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> kktModelVal = 500
                    1 -> kktModelVal = 93
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        ofdChannel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> ofdChannelVal = 0
                    1 -> ofdChannelVal = 2
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        kktConnectionType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                kktConnectionTypeVal = position
                when(kktConnectionTypeVal){
                    0 -> {
                        if(kktIPVal != "" && kktPortVal != ""){
                            kkt.text = "$kktIPVal : $kktPortVal"
                        }
                        else{
                            kkt.text = ""
                            kkt.hint = "192.168.1.1:5000"
                        }
                    }
                    1 -> {
                        if(kktComVal != "" && kktBaudrateVal >= 0){
                            kkt.text = "$kktComVal : $kktBaudrateVal"
                        }
                        else{
                            kkt.text = ""
                            kkt.hint = "1:57600"
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        kkt.setOnClickListener(View.OnClickListener { view ->
            when(kktConnectionTypeVal){
                0 -> (activity as MainActivity).openFragment(TcpIpFragment(), "TCP/IP")
                1 -> (activity as MainActivity).openFragment(ComVcomFragment(), "COM/VCOM")
            }
        })

        serverURL.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                serverURLVal = "http://" + s.toString()
                if (!Utils.isValidURL(serverURLVal)) {
                    serverURL.error = "Введите корректный адрес"
                    return
                }
            }
        })

        serverUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                serverUsernameVal = s.toString()
                if(serverUsernameVal == ""){
                    serverUsername.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        serverPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                serverPasswordVal = s.toString()
                if(serverPasswordVal == ""){
                    serverPassword.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

//        connectButton.setOnClickListener(View.OnClickListener { view ->
//            val progressDialog = ProgressDialog(requireContext())
//            progressDialog.setTitle("Подключение")
//            progressDialog.setMessage("Подключение к ККТ...")
//            progressDialog.setCancelable(false)
//            progressDialog.show()
//            if (viewModel.checkConnection()) {
//                val alertDialog = AlertDialog.Builder(requireContext())
//                alertDialog.setTitle("Уведомление")
//                alertDialog.setMessage("Соединение с ККТ уже установлено")
//                alertDialog.setPositiveButton("OK") { dialog, which ->
//                    (activity as MainActivity).openFragment(MainFragment(), "NevaTools")
//                }
//                alertDialog.show()
//            }
//            else{
//                if (kktConnectionTypeVal == 0) {
//                    if (!Utils.isValidIpAddress(kktIPVal) ||
//                        kktPortVal == "" ||
//                        !Utils.isValidURL(serverURLVal) ||
//                        serverUsernameVal == "" ||
//                        serverPasswordVal == "") {
//                        val alertDialog = AlertDialog.Builder(requireContext())
//                        alertDialog.setTitle("Ошибка")
//                        alertDialog.setMessage("Некоторые данные для подключения не введены или введены неверно. Проверьте данные для подключения и повторите попытку")
//                        alertDialog.setPositiveButton("OK") { dialog, which ->
//
//                        }
//                        alertDialog.show()
//                    } else {
//                        Thread {
//                            val paramsString = "reconnectVal :$reconnectVal\n" +
//                                    "kktModelVal :$kktModelVal\n" +
//                                    "ofdChannelVal :$ofdChannelVal\n" +
//                                    "kktIPVal :$kktIPVal\n" +
//                                    "kktPortVal :$kktPortVal\n"
//                            val result = viewModel.connectTcpIp(reconnectVal,
//                                kktModelVal,
//                                ofdChannelVal,
//                                kktIPVal,
//                                kktPortVal
//                            )
//                            requireActivity().runOnUiThread {
//                                progressDialog.dismiss()
//                                if (result == "") {
//                                    logger.log(Logger.LogLevel.SUCCESS, "connectTcpIp", paramsString, "0")
//                                    currentKKTSerial = viewModel.getKKTSerial()
//                                    val kktModel =viewModel.getKKTModel()
//                                    val kktSoftwareVersion = viewModel.getKKTSoftwareVersion()
//                                    val requestResult = runBlocking { withContext(Dispatchers.IO) { Requests.addKKT(serverURLVal,
//                                        serverUsernameVal,
//                                        serverPasswordVal,
//                                        currentKKTSerial,
//                                        kktModel,
//                                        kktSoftwareVersion) }
//                                    }
//                                    var message: String = ""
//                                    if(requestResult.first == 200)
//                                    {
//                                        message = "Соединение с ККТ установлено\nСоединение с сервером статистики установлено"
//                                    }
//                                    else{
//                                        message = "Соединение с ККТ установлено\nСоединение с сервером статистики не установлено"
//                                    }
//                                    val alertDialog = AlertDialog.Builder(requireContext())
//                                    alertDialog.setTitle("Уведомление")
//                                    alertDialog.setMessage(message)
//                                    alertDialog.setPositiveButton("OK") { dialog, which ->
//                                        (activity as MainActivity).openFragment(
//                                            MainFragment(),
//                                            "NevaTools"
//                                        )
//                                    }
//                                    alertDialog.show()
//                                } else {
//                                    logger.log(Logger.LogLevel.ERROR, "connectTcpIp", paramsString, result)
//                                    val alertDialog = AlertDialog.Builder(requireContext())
//                                    alertDialog.setTitle("Ошибка")
//                                    alertDialog.setMessage(result)
//                                    alertDialog.setPositiveButton("OK") { dialog, which ->
//
//                                    }
//                                    alertDialog.show()
//                                }
//                            }
//                        }.start()
//                    }
//                }
//                if(kktConnectionTypeVal == 1){
//                    if(kktComVal == "" ||
//                        !Utils.isValidURL(serverURLVal)||
//                        serverUsernameVal == "" ||
//                        serverPasswordVal == ""){
//                        val alertDialog = AlertDialog.Builder(requireContext())
//                        alertDialog.setTitle("Ошибка")
//                        alertDialog.setMessage("Некоторые данные для подклчения не введены или введены неверно. Проверьте данные для подключения и повторите попытку")
//                        alertDialog.setPositiveButton("OK") { dialog, which ->
//
//                        }
//                        alertDialog.show()
//                    }
//                    else{
//                        Thread {
//                            val paramsString = "reconnectVal :$reconnectVal\n" +
//                                    "kktModelVal :$kktModelVal\n" +
//                                    "ofdChannelVal :$ofdChannelVal\n" +
//                                    "kktComVal :$kktComVal\n" +
//                                    "kktBaudrateVal :$kktBaudrateVal\n"
//                            val result = viewModel.connectComVcom(reconnectVal,
//                                kktModelVal,
//                                ofdChannelVal,
//                                kktComVal,
//                                kktBaudrateVal)
//                            requireActivity().runOnUiThread {
//                                progressDialog.dismiss()
//                                if (result == "") {
//                                    logger.log(Logger.LogLevel.SUCCESS, "connectComVcom", paramsString, "0")
//                                    currentKKTSerial = viewModel.getKKTSerial()
//                                    val kktModel =viewModel.getKKTModel()
//                                    val kktSoftwareVersion = viewModel.getKKTSoftwareVersion()
//                                    val requestResult = runBlocking { withContext(Dispatchers.IO) { Requests.addKKT(serverURLVal,
//                                        serverUsernameVal,
//                                        serverPasswordVal,
//                                        currentKKTSerial,
//                                        kktModel,
//                                        kktSoftwareVersion) }
//                                    }
//                                    var message: String = ""
//                                    if(requestResult.first == 200)
//                                    {
//                                        message = "Соединение с ККТ установлено\nСоединение с сервером статистики установлено"
//                                    }
//                                    else{
//                                        message = "Соединение с ККТ установлено\nСоединение с сервером статистики не установлено"
//                                    }
//                                    val alertDialog = AlertDialog.Builder(requireContext())
//                                    alertDialog.setTitle("Уведомление")
//                                    alertDialog.setMessage(message)
//                                    alertDialog.setPositiveButton("OK") { dialog, which ->
//                                        (activity as MainActivity).openFragment(
//                                            MainFragment(),
//                                            "NevaTools"
//                                        )
//                                    }
//                                    alertDialog.show()
//                                } else {
//                                    logger.log(Logger.LogLevel.ERROR, "connectComVcom", paramsString, result)
//                                    val alertDialog = AlertDialog.Builder(requireContext())
//                                    alertDialog.setTitle("Ошибка")
//                                    alertDialog.setMessage(result)
//                                    alertDialog.setPositiveButton("OK") { dialog, which ->
//
//                                    }
//                                    alertDialog.show()
//                                }
//                            }
//                        }.start()
//                    }
//                }
//            }
//        })

        connectButton.setOnClickListener(View.OnClickListener { view ->
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Подключение")
            progressDialog.setMessage("Подключение к ККТ...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            if (viewModel.checkConnection()) {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Соединение с ККТ уже установлено")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).openFragment(MainFragment(), "NevaTools")
                }
                alertDialog.show()
            }
            else {
                if (kktConnectionTypeVal == 0) {
                    if (!Utils.isValidIpAddress(kktIPVal) ||
                        kktPortVal == "" ||
                        !Utils.isValidURL(serverURLVal) ||
                        serverUsernameVal == "" ||
                        serverPasswordVal == ""
                    ) {
                        val alertDialog = AlertDialog.Builder(requireContext())
                        alertDialog.setTitle("Ошибка")
                        alertDialog.setMessage("Некоторые данные для подключения не введены или введены неверно. Проверьте данные для подключения и повторите попытку")
                        alertDialog.setPositiveButton("OK") { dialog, which ->

                        }
                        alertDialog.show()
                    } else {
                        // Подключение к ККТ
                        progressDialog.setMessage("Подключение к ККТ...")
                        progressDialog.show()
                        Thread {
                            val paramsString = "reconnectVal :$reconnectVal\n" +
                                    "kktModelVal :$kktModelVal\n" +
                                    "ofdChannelVal :$ofdChannelVal\n" +
                                    "kktComVal :$kktComVal\n" +
                                    "kktBaudrateVal :$kktBaudrateVal\n"
                            val result = viewModel.connectTcpIp(reconnectVal,
                                kktModelVal,
                                ofdChannelVal,
                                kktIPVal,
                                kktPortVal
                            )
                            requireActivity().runOnUiThread {
                                if (result == "") {
                                    logger.log(Logger.LogLevel.SUCCESS, "connectComVcom", paramsString, "0")
                                    // Подключение к серверу
                                    progressDialog.setMessage("Подключение к серверу...")
                                    Thread {
                                        currentKKTSerial = viewModel.getKKTSerial()
                                        val kktModel =viewModel.getKKTModel()
                                        val kktSoftwareVersion = viewModel.getKKTSoftwareVersion()
                                        val requestResult = runBlocking {
                                            withContext(Dispatchers.IO) {
                                                Requests.addKKT(
                                                    serverURLVal,
                                                    serverUsernameVal,
                                                    serverPasswordVal,
                                                    currentKKTSerial,
                                                    kktModel,
                                                    kktSoftwareVersion
                                                )
                                            }
                                        }
                                        requireActivity().runOnUiThread {
                                            progressDialog.dismiss()
                                            var message: String = ""
                                            var title: String = ""
                                            if(requestResult.first == 200)
                                            {
                                                title = "Уведомление"
                                                message = "Соединение с ККТ установлено\nСоединение с сервером статистики установлено"
                                            }
                                            else{
                                                title = "Ошибка"
                                                message = "Соединение с сервером статистики не установлено. Соединение с ККТ разорвано после подключения для обеспечения корректного сбора статистики. Проверьте данные для подключения к серверу и повторите попытку"
                                            }
                                            val alertDialog = AlertDialog.Builder(requireContext())
                                            alertDialog.setTitle(title)
                                            alertDialog.setMessage(message)
                                            alertDialog.setPositiveButton("OK") { dialog, which ->
                                                (activity as MainActivity).openFragment(
                                                    MainFragment(),
                                                    "NevaTools"
                                                )
                                            }
                                            alertDialog.show()
                                        }
                                    }.start()
                                } else {
                                    progressDialog.dismiss()
                                    logger.log(Logger.LogLevel.ERROR, "connectComVcom", paramsString, result)
                                    val alertDialog = AlertDialog.Builder(requireContext())
                                    alertDialog.setTitle("Ошибка")
                                    alertDialog.setMessage(result)
                                    alertDialog.setPositiveButton("OK") { dialog, which ->

                                    }
                                    alertDialog.show()
                                }
                            }
                        }.start()
                    }
                }
                if (kktConnectionTypeVal == 1) {
                    if (kktComVal == "" ||
                        !Utils.isValidURL(serverURLVal)||
                        serverUsernameVal == "" ||
                        serverPasswordVal == ""
                    ) {
                        val alertDialog = AlertDialog.Builder(requireContext())
                        alertDialog.setTitle("Ошибка")
                        alertDialog.setMessage("Некоторые данные для подключения не введены или введены неверно. Проверьте данные для подключения и повторите попытку")
                        alertDialog.setPositiveButton("OK") { dialog, which ->

                        }
                        alertDialog.show()
                    } else {
                        // Подключение к ККТ
                        progressDialog.setMessage("Подключение к ККТ...")
                        progressDialog.show()
                        Thread {
                            val paramsString = "reconnectVal :$reconnectVal\n" +
                                    "kktModelVal :$kktModelVal\n" +
                                    "ofdChannelVal :$ofdChannelVal\n" +
                                    "kktComVal :$kktComVal\n" +
                                    "kktBaudrateVal :$kktBaudrateVal\n"
                            val result = viewModel.connectComVcom(reconnectVal,
                                kktModelVal,
                                ofdChannelVal,
                                kktComVal,
                                kktBaudrateVal)
                            requireActivity().runOnUiThread {
                                if (result == "") {
                                    logger.log(Logger.LogLevel.SUCCESS, "connectComVcom", paramsString, "0")
                                    // Подключение к серверу
                                    progressDialog.setMessage("Подключение к серверу...")
                                    Thread {
                                        currentKKTSerial = viewModel.getKKTSerial()
                                        val kktModel =viewModel.getKKTModel()
                                        val kktSoftwareVersion = viewModel.getKKTSoftwareVersion()
                                        val requestResult = runBlocking {
                                            withContext(Dispatchers.IO) {
                                                Requests.addKKT(
                                                    serverURLVal,
                                                    serverUsernameVal,
                                                    serverPasswordVal,
                                                    currentKKTSerial,
                                                    kktModel,
                                                    kktSoftwareVersion
                                                )
                                            }
                                        }
                                        requireActivity().runOnUiThread {
                                            progressDialog.dismiss()
                                            var message: String = ""
                                            var title: String = ""
                                            if(requestResult.first == 200)
                                            {
                                                title = "Уведомление"
                                                message = "Соединение с ККТ установлено\nСоединение с сервером статистики установлено"
                                            }
                                            else{
                                                title = "Ошибка"
                                                message = "Соединение с сервером статистики не установлено. Соединение с ККТ разорвано после подключения для обеспечения корректного сбора статистики. Проверьте данные для подключения к серверу и повторите попытку"
                                            }
                                            val alertDialog = AlertDialog.Builder(requireContext())
                                            alertDialog.setTitle(title)
                                            alertDialog.setMessage(message)
                                            alertDialog.setPositiveButton("OK") { dialog, which ->
                                                (activity as MainActivity).openFragment(
                                                    MainFragment(),
                                                    "NevaTools"
                                                )
                                            }
                                            alertDialog.show()
                                        }
                                    }.start()
                                } else {
                                    progressDialog.dismiss()
                                    logger.log(Logger.LogLevel.ERROR, "connectComVcom", paramsString, result)
                                    val alertDialog = AlertDialog.Builder(requireContext())
                                    alertDialog.setTitle("Ошибка")
                                    alertDialog.setMessage(result)
                                    alertDialog.setPositiveButton("OK") { dialog, which ->

                                    }
                                    alertDialog.show()
                                }
                            }
                        }.start()
                    }
                }
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val connectionPrefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)

        with(connectionPrefs.edit()){
            putString("reconnectVal", reconnectVal)
            putInt("kktModelVal", kktModelVal)
            putInt("ofdChannelVal", ofdChannelVal)
            putInt("kktConnectionTypeVal", kktConnectionTypeVal)
            putString("serverURLVal", serverURLVal)
            putString("serverUsernameVal", serverUsernameVal)
            putString("serverPasswordVal", serverPasswordVal)
            putString("currentKKTSerial", currentKKTSerial)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConnectionViewModel::class.java)
        if(viewModel.checkConnection()) {
            serverURL.isEnabled = false
            serverUsername.isEnabled = false
            serverPassword.isEnabled = false
        }
        else{
            serverURL.isEnabled = true
            serverUsername.isEnabled = true
            serverPassword.isEnabled = true
        }
    }

}
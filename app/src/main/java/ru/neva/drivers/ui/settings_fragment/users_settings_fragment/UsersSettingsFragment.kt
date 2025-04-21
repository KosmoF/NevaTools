package ru.neva.drivers.ui.settings_fragment.users_settings_fragment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.databinding.FragmentUsersSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class UsersSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = UsersSettingsFragment()
    }

    private lateinit var viewModel: UsersSettingsViewModel
    private var _binding: FragmentUsersSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton
    
    private lateinit var cashier1Name: EditText
    private lateinit var cashier1Password: EditText
    private lateinit var cashier1INN: EditText
    private lateinit var cashier2Name: EditText
    private lateinit var cashier2Password: EditText
    private lateinit var cashier2INN: EditText
    private lateinit var cashier3Name: EditText
    private lateinit var cashier3Password: EditText
    private lateinit var cashier3INN: EditText
    private lateinit var cashier4Name: EditText
    private lateinit var cashier4Password: EditText
    private lateinit var cashier4INN: EditText
    private lateinit var cashier5Name: EditText
    private lateinit var cashier5Password: EditText
    private lateinit var cashier5INN: EditText
    private lateinit var adminName: EditText
    private lateinit var adminPassword: EditText
    private lateinit var adminINN: EditText
    private lateinit var systemAdminName: EditText
    private lateinit var systemAdminPassword: EditText
    private lateinit var systemAdminINN: EditText

    private var cashier1NameVal: String = ""
    private var cashier1PasswordVal: String = ""
    private var cashier1INNVal: String = ""
    private var cashier2NameVal: String = ""
    private var cashier2PasswordVal: String = ""
    private var cashier2INNVal: String = ""
    private var cashier3NameVal: String = ""
    private var cashier3PasswordVal: String = ""
    private var cashier3INNVal: String = ""
    private var cashier4NameVal: String = ""
    private var cashier4PasswordVal: String = ""
    private var cashier4INNVal: String = ""
    private var cashier5NameVal: String = ""
    private var cashier5PasswordVal: String = ""
    private var cashier5INNVal: String = ""
    private var adminNameVal: String = ""
    private var adminPasswordVal: String = ""
    private var adminINNVal: String = ""
    private var systemAdminNameVal: String = ""
    private var systemAdminPasswordVal: String = ""
    private var systemAdminINNVal: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUsersSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad

        this.cashier1Name = _binding!!.etCashier1Name
        this.cashier1Password = _binding!!.etCashier1Password
        this.cashier1INN = _binding!!.etCashier1Inn
        this.cashier2Name = _binding!!.etCashier2Name
        this.cashier2Password = _binding!!.etCashier2Password
        this.cashier2INN = _binding!!.etCashier2Inn
        this.cashier3Name = _binding!!.etCashier3Name
        this.cashier3Password = _binding!!.etCashier3Password
        this.cashier3INN = _binding!!.etCashier3Inn
        this.cashier4Name = _binding!!.etCashier4Name
        this.cashier4Password = _binding!!.etCashier4Password
        this.cashier4INN = _binding!!.etCashier4Inn
        this.cashier5Name = _binding!!.etCashier5Name
        this.cashier5Password = _binding!!.etCashier5Password
        this.cashier5INN = _binding!!.etCashier5Inn
        this.adminName = _binding!!.etAdminName
        this.adminPassword = _binding!!.etAdminPassword
        this.adminINN = _binding!!.etAdminInn
        this.systemAdminName = _binding!!.etSystemAdminName
        this.systemAdminPassword = _binding!!.etSystemAdminPassword
        this.systemAdminINN = _binding!!.etSystemAdminInn

        val usersSettingsPrefs = requireContext().getSharedPreferences("usersSettingsPrefs", Context.MODE_PRIVATE)

        cashier1NameVal = usersSettingsPrefs.getString("cashier1NameVal", "").toString()
        cashier1Name.text = Editable.Factory.getInstance().newEditable(cashier1NameVal)

        cashier1PasswordVal = usersSettingsPrefs.getString("cashier1PasswordVal", "").toString()
        cashier1Password.text = Editable.Factory.getInstance().newEditable(cashier1PasswordVal)

        cashier1INNVal = usersSettingsPrefs.getString("cashier1INNVal", "").toString()
        cashier1INN.text = Editable.Factory.getInstance().newEditable(cashier1INNVal)

        cashier2NameVal = usersSettingsPrefs.getString("cashier2NameVal", "").toString()
        cashier2Name.text = Editable.Factory.getInstance().newEditable(cashier2NameVal)

        cashier2PasswordVal = usersSettingsPrefs.getString("cashier2PasswordVal", "").toString()
        cashier2Password.text = Editable.Factory.getInstance().newEditable(cashier2PasswordVal)

        cashier2INNVal = usersSettingsPrefs.getString("cashier2INNVal", "").toString()
        cashier2INN.text = Editable.Factory.getInstance().newEditable(cashier2INNVal)

        cashier3NameVal = usersSettingsPrefs.getString("cashier3NameVal", "").toString()
        cashier3Name.text = Editable.Factory.getInstance().newEditable(cashier3NameVal)

        cashier3PasswordVal = usersSettingsPrefs.getString("cashier3PasswordVal", "").toString()
        cashier3Password.text = Editable.Factory.getInstance().newEditable(cashier3PasswordVal)

        cashier3INNVal = usersSettingsPrefs.getString("cashier3INNVal", "").toString()
        cashier3INN.text = Editable.Factory.getInstance().newEditable(cashier3INNVal)

        cashier4NameVal = usersSettingsPrefs.getString("cashier4NameVal", "").toString()
        cashier4Name.text = Editable.Factory.getInstance().newEditable(cashier4NameVal)

        cashier4PasswordVal = usersSettingsPrefs.getString("cashier4PasswordVal", "").toString()
        cashier4Password.text = Editable.Factory.getInstance().newEditable(cashier4PasswordVal)

        cashier4INNVal = usersSettingsPrefs.getString("cashier4INNVal", "").toString()
        cashier4INN.text = Editable.Factory.getInstance().newEditable(cashier4INNVal)

        cashier5NameVal = usersSettingsPrefs.getString("cashier5NameVal", "").toString()
        cashier5Name.text = Editable.Factory.getInstance().newEditable(cashier5NameVal)

        cashier5PasswordVal = usersSettingsPrefs.getString("cashier5PasswordVal", "").toString()
        cashier5Password.text = Editable.Factory.getInstance().newEditable(cashier5PasswordVal)

        cashier5INNVal = usersSettingsPrefs.getString("cashier5INNVal", "").toString()
        cashier5INN.text = Editable.Factory.getInstance().newEditable(cashier5INNVal)

        adminNameVal = usersSettingsPrefs.getString("adminNameVal", "").toString()
        adminName.text = Editable.Factory.getInstance().newEditable(adminNameVal)

        adminPasswordVal = usersSettingsPrefs.getString("adminPasswordVal", "").toString()
        adminPassword.text = Editable.Factory.getInstance().newEditable(adminPasswordVal)

        adminINNVal = usersSettingsPrefs.getString("adminINNVal", "").toString()
        adminINN.text = Editable.Factory.getInstance().newEditable(adminINNVal)

        systemAdminNameVal = usersSettingsPrefs.getString("systemAdminNameVal", "").toString()
        systemAdminName.text = Editable.Factory.getInstance().newEditable(systemAdminNameVal)

        systemAdminPasswordVal = usersSettingsPrefs.getString("systemAdminPasswordVal", "").toString()
        systemAdminPassword.text = Editable.Factory.getInstance().newEditable(systemAdminPasswordVal)

        systemAdminINNVal = usersSettingsPrefs.getString("systemAdminINNVal", "").toString()
        systemAdminINN.text = Editable.Factory.getInstance().newEditable(systemAdminINNVal)

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        cashier1Name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier1NameVal = s.toString()
                if(cashier1NameVal == ""){
                    cashier1Name.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier1Password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier1PasswordVal = s.toString()
                if(cashier1PasswordVal == ""){
                    cashier1Password.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier1INN.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier1INNVal = s.toString()
                if(cashier1INNVal == ""){
                    cashier1INN.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier2Name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier2NameVal = s.toString()
                if(cashier2NameVal == ""){
                    cashier2Name.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier2Password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier2PasswordVal = s.toString()
                if(cashier2PasswordVal == ""){
                    cashier2Password.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier2INN.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier2INNVal = s.toString()
                if(cashier2INNVal == ""){
                    cashier2INN.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier3Name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier3NameVal = s.toString()
                if(cashier3NameVal == ""){
                    cashier3Name.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier3Password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier3PasswordVal = s.toString()
                if(cashier3PasswordVal == ""){
                    cashier3Password.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier3INN.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier3INNVal = s.toString()
                if(cashier3INNVal == ""){
                    cashier3INN.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier4Name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier4NameVal = s.toString()
                if(cashier4NameVal == ""){
                    cashier4Name.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier4Password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier4PasswordVal = s.toString()
                if(cashier4PasswordVal == ""){
                    cashier4Password.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier4INN.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier4INNVal = s.toString()
                if(cashier4INNVal == ""){
                    cashier4INN.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier5Name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier5NameVal = s.toString()
                if(cashier5NameVal == ""){
                    cashier5Name.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier5Password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier5PasswordVal = s.toString()
                if(cashier5PasswordVal == ""){
                    cashier5Password.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cashier5INN.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cashier5INNVal = s.toString()
                if(cashier5INNVal == ""){
                    cashier5INN.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        adminName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adminNameVal = s.toString()
                if(adminNameVal == ""){
                    adminName.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        adminPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adminPasswordVal = s.toString()
                if(adminPasswordVal == ""){
                    adminPassword.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        adminINN.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adminINNVal = s.toString()
                if(adminINNVal == ""){
                    adminINN.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        systemAdminName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                systemAdminNameVal = s.toString()
                if(systemAdminNameVal == ""){
                    systemAdminName.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        systemAdminPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                systemAdminPasswordVal = s.toString()
                if(systemAdminPasswordVal == ""){
                    systemAdminPassword.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        systemAdminINN.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                systemAdminINNVal = s.toString()
                if(systemAdminINNVal == ""){
                    systemAdminINN.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        FABSave.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "cashier1NameVal :$cashier1NameVal\n" +
                    "cashier1PasswordVal :$cashier1PasswordVal\n" +
                    "cashier1INNVal :$cashier1INNVal\n" +
                    "cashier2NameVal :$cashier2NameVal\n" +
                    "cashier2PasswordVal :$cashier2PasswordVal\n" +
                    "cashier2INNVal :$cashier2INNVal\n" +
                    "cashier3NameVal :$cashier3NameVal\n" +
                    "cashier3PasswordVal :$cashier3PasswordVal\n" +
                    "cashier3INNVal :$cashier3INNVal\n" +
                    "cashier4NameVal :$cashier4NameVal\n" +
                    "cashier4PasswordVal :$cashier4PasswordVal\n" +
                    "cashier4INNVal :$cashier4INNVal\n" +
                    "cashier5NameVal :$cashier5NameVal\n" +
                    "cashier5PasswordVal :$cashier5PasswordVal\n" +
                    "cashier5INNVal :$cashier5INNVal\n" + 
                    "adminNameVal :$adminNameVal\n" +
                    "adminPasswordVal :$adminPasswordVal\n" +
                    "adminINNVal :$adminINNVal\n" + 
                    "systemAdminNameVal :$systemAdminNameVal\n" +
                    "systemAdminPasswordVal :$systemAdminPasswordVal\n" +
                    "systemAdminINNVal :$systemAdminINNVal\n"
            val result = viewModel.setUsersSettings(cashier1NameVal,
                cashier1PasswordVal,
                cashier1INNVal,
                cashier2NameVal,
                cashier2PasswordVal,
                cashier2INNVal,
                cashier3NameVal,
                cashier3PasswordVal,
                cashier3INNVal,
                cashier4NameVal,
                cashier4PasswordVal,
                cashier4INNVal,
                cashier5NameVal,
                cashier5PasswordVal,
                cashier5INNVal,
                adminNameVal,
                adminPasswordVal,
                adminINNVal,
                systemAdminNameVal,
                systemAdminPasswordVal,
                systemAdminINNVal)
            if(result == ""){
                logger.log(Logger.LogLevel.SUCCESS, "setUsersSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setUsersSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "setUsersSettings", paramsString, result)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setUsersSettings")}}
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
            val result = viewModel.getUsersSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getUsersSettings", paramsString, result[0].toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getUsersSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getUsersSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getUsersSettings")}}
                cashier1NameVal = result[0].toString()
                cashier1Name.text = Editable.Factory.getInstance().newEditable(cashier1NameVal)

                cashier1PasswordVal = result[1].toString()
                cashier1Password.text = Editable.Factory.getInstance().newEditable(cashier1PasswordVal)

                cashier1INNVal = result[2].toString()
                cashier1INN.text = Editable.Factory.getInstance().newEditable(cashier1INNVal)

                cashier2NameVal = result[3].toString()
                cashier2Name.text = Editable.Factory.getInstance().newEditable(cashier2NameVal)

                cashier2PasswordVal = result[4].toString()
                cashier2Password.text = Editable.Factory.getInstance().newEditable(cashier2PasswordVal)

                cashier2INNVal = result[5].toString()
                cashier2INN.text = Editable.Factory.getInstance().newEditable(cashier2INNVal)

                cashier3NameVal = result[6].toString()
                cashier3Name.text = Editable.Factory.getInstance().newEditable(cashier3NameVal)

                cashier3PasswordVal = result[7].toString()
                cashier3Password.text = Editable.Factory.getInstance().newEditable(cashier3PasswordVal)

                cashier3INNVal = result[8].toString()
                cashier3INN.text = Editable.Factory.getInstance().newEditable(cashier3INNVal)

                cashier4NameVal = result[9].toString()
                cashier4Name.text = Editable.Factory.getInstance().newEditable(cashier4NameVal)

                cashier4PasswordVal = result[10].toString()
                cashier4Password.text = Editable.Factory.getInstance().newEditable(cashier4PasswordVal)

                cashier4INNVal = result[11].toString()
                cashier4INN.text = Editable.Factory.getInstance().newEditable(cashier4INNVal)

                cashier5NameVal = result[12].toString()
                cashier5Name.text = Editable.Factory.getInstance().newEditable(cashier5NameVal)

                cashier5PasswordVal = result[13].toString()
                cashier5Password.text = Editable.Factory.getInstance().newEditable(cashier5PasswordVal)

                cashier5INNVal = result[14].toString()
                cashier5INN.text = Editable.Factory.getInstance().newEditable(cashier5INNVal)

                adminNameVal = result[15].toString()
                adminName.text = Editable.Factory.getInstance().newEditable(adminNameVal)

                adminPasswordVal = result[16].toString()
                adminPassword.text = Editable.Factory.getInstance().newEditable(adminPasswordVal)

                adminINNVal = result[17].toString()
                adminINN.text = Editable.Factory.getInstance().newEditable(adminINNVal)

                systemAdminNameVal = result[18].toString()
                systemAdminName.text = Editable.Factory.getInstance().newEditable(systemAdminNameVal)

                systemAdminPasswordVal = result[19].toString()
                systemAdminPassword.text = Editable.Factory.getInstance().newEditable(systemAdminPasswordVal)

                systemAdminINNVal = result[20].toString()
                systemAdminINN.text = Editable.Factory.getInstance().newEditable(systemAdminINNVal)

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

        val usersSettingsPrefs = requireContext().getSharedPreferences("usersSettingsPrefs", Context.MODE_PRIVATE)

        with(usersSettingsPrefs.edit()){
            putString("cashier1NameVal", cashier1NameVal)
            putString("cashier1PasswordVal", cashier1PasswordVal)
            putString("cashier1INNVal", cashier1INNVal)
            putString("cashier2NameVal", cashier2NameVal)
            putString("cashier2PasswordVal", cashier2PasswordVal)
            putString("cashier2INNVal", cashier2INNVal)
            putString("cashier3NameVal", cashier3NameVal)
            putString("cashier3PasswordVal", cashier3PasswordVal)
            putString("cashier3INNVal", cashier3INNVal)
            putString("cashier4NameVal", cashier4NameVal)
            putString("cashier4PasswordVal", cashier4PasswordVal)
            putString("cashier4INNVal", cashier4INNVal)
            putString("cashier5NameVal", cashier5NameVal)
            putString("cashier5PasswordVal", cashier5PasswordVal)
            putString("cashier5INNVal", cashier5INNVal)
            putString("adminNameVal", adminNameVal)
            putString("adminPasswordVal", adminPasswordVal)
            putString("adminINNVal", adminINNVal)
            putString("systemAdminNameVal", systemAdminNameVal)
            putString("systemAdminPasswordVal", systemAdminPasswordVal)
            putString("systemAdminINNVal", systemAdminINNVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UsersSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
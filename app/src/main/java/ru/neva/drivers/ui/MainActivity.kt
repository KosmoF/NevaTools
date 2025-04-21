package ru.neva.drivers.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import ru.neva.drivers.R
import ru.neva.drivers.databinding.ActivityMainBinding
import ru.neva.drivers.fptr.FptrHolder
import ru.neva.drivers.ui.cashier_fragment.CashierFragment
import ru.neva.drivers.ui.cheques_fragment.ChequesFragment
import ru.neva.drivers.ui.connection_fragment.ConnectionFragment
import ru.neva.drivers.ui.fin_ops_fragment.FinOpsFragment
import ru.neva.drivers.ui.fn_info_fragment.FnInfoFragment
import ru.neva.drivers.ui.kkt_info_fragment.KktInfoFragment
import ru.neva.drivers.ui.km_check_fragment.KmCheckFragment
import ru.neva.drivers.ui.main_fragment.MainFragment
import ru.neva.drivers.ui.reg_fragment.RegFragment
import ru.neva.drivers.ui.reports_fragment.ReportsFragment
import ru.neva.drivers.ui.service_fragment.ServiceFragment
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.ui.test_print_fragment.TestPrintFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var networkChangeReceiver: BroadcastReceiver
    var URL: String = ""
    var currentFragmentTag: String? = null
    @Inject
    lateinit var fptrHolder : FptrHolder

    private companion object {
        private const val STORAGE_PERMISSION_CODE = 100
        private const val TAG = "PERMISSION_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(this, binding.mainDrawer, binding.toolbar, R.string.nav_open, R.string.nav_close)
        binding.mainDrawer.addDrawerListener(toggle)
        toggle.syncState()

        binding.menu.setNavigationItemSelectedListener(this)

        networkChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
                    val noConnectivity = intent.getBooleanExtra(
                        ConnectivityManager.EXTRA_NO_CONNECTIVITY, false
                    )

                    if (noConnectivity && fptrHolder.fptr.isOpened) {
                        fptrHolder.fptr.close()
                        runOnUiThread {
                            AlertDialog.Builder(this@MainActivity).apply {
                                setTitle("Уведомление")
                                setMessage("Соединение с ККТ было разорвано, так как были изменены параметры сетевого подключения.")
                                setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                                create()
                                show()
                            }
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)

        if(!checkPermission()){
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Уведомление")
            alertDialog.setMessage("Для выполнения файловых операций необходимо предоставить приложению \"NevaTools\" разрешение на доступ к файлам на устройстве")
            alertDialog.setPositiveButton("OK") { dialog, which ->
                requestPermission()
            }
            alertDialog.setNegativeButton("Отмена"){ dialog, which ->
            }
            alertDialog.show()
        }

        fragmentManager = supportFragmentManager

        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString("currentFragmentTag")
            currentFragmentTag?.let {
                val fragment = fragmentManager.findFragmentByTag(it)
                if (fragment != null) {
                    openFragment(fragment, it)
                }
            }
        } else {
            openFragment(MainFragment(), "NevaTools")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Проверка, был ли networkChangeReceiver инициализирован перед использованием
        if(this::networkChangeReceiver.isInitialized) {
            unregisterReceiver(networkChangeReceiver)
        }
    }

    fun requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            }
            catch (e: Exception){
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        }
        else{
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE)
        }
    }

    private val storageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if(Environment.isExternalStorageManager()){
                Toast.makeText(this, "Permission access", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        else{

        }
    }

    fun checkPermission(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            Environment.isExternalStorageManager()
        }
        else{
            val write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()){
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (write && read){
                    Toast.makeText(this, "Permission access", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentFragmentTag", currentFragmentTag)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_main -> openFragment(MainFragment(), "NevaTools")
            R.id.menu_connection -> openFragment(ConnectionFragment(), "Подключение к ККТ")
            R.id.menu_settings -> openFragment(SettingsFragment(), "Настройки ККТ")
            R.id.menu_kkt_info -> openFragment(KktInfoFragment(),"Информация о ККТ")
            R.id.menu_fn_info -> openFragment(FnInfoFragment(),"Информация о ФН")
            R.id.menu_reg_kkt -> openFragment(RegFragment(),"Регистрация ККТ")
            R.id.menu_cheques -> openFragment(ChequesFragment(),"Чеки")
            R.id.menu_reports -> openFragment(ReportsFragment(),"Отчёты")
            R.id.menu_cashiers -> openFragment(CashierFragment(),"Ввод данных кассира")
            R.id.menu_km_check -> openFragment(KmCheckFragment(),"Проверка КМ")
            R.id.menu_service -> openFragment(ServiceFragment(),"Служебные функции")
            R.id.menu_test_print -> openFragment(TestPrintFragment(),"Тестовая печать")
            R.id.menu_fin_ops -> openFragment(FinOpsFragment(),"Внесения/выплаты")
        }
        binding.mainDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setCheckedMenuItem(title: String){
        when(title){
            "NevaTools" -> binding.menu.setCheckedItem(R.id.menu_main)
            "Подключение к ККТ" -> binding.menu.setCheckedItem(R.id.menu_connection)
            "Настройки ККТ" -> binding.menu.setCheckedItem(R.id.menu_settings)
            "Информация о ККТ" -> binding.menu.setCheckedItem(R.id.menu_kkt_info)
            "Информация о ФН" -> binding.menu.setCheckedItem(R.id.menu_fn_info)
            "Регистрация ККТ" -> binding.menu.setCheckedItem(R.id.menu_reg_kkt)
            "Чеки" -> binding.menu.setCheckedItem(R.id.menu_cheques)
            "Отчёты" -> binding.menu.setCheckedItem(R.id.menu_reports)
            "Ввод данных кассира" -> binding.menu.setCheckedItem(R.id.menu_cashiers)
            "Проверка КМ" -> binding.menu.setCheckedItem(R.id.menu_km_check)
            "Служебные функции" -> binding.menu.setCheckedItem(R.id.menu_service)
            "Тестовая печать" -> binding.menu.setCheckedItem(R.id.menu_test_print)
            "Внесения/выплаты" -> binding.menu.setCheckedItem(R.id.menu_fin_ops)
        }
    }

    fun setMenuVisible(){
        if(!fptrHolder.fptr.isOpened){
            binding.menu.menu.findItem(R.id.menu_settings).isVisible = false
            binding.menu.menu.findItem(R.id.menu_kkt_info).isVisible = false
            binding.menu.menu.findItem(R.id.menu_fn_info).isVisible = false
            binding.menu.menu.findItem(R.id.menu_reg_kkt).isVisible = false
            binding.menu.menu.findItem(R.id.menu_cheques).isVisible = false
            binding.menu.menu.findItem(R.id.menu_reports).isVisible = false
            binding.menu.menu.findItem(R.id.menu_cashiers).isVisible = false
            binding.menu.menu.findItem(R.id.menu_km_check).isVisible = false
            binding.menu.menu.findItem(R.id.menu_service).isVisible = false
            binding.menu.menu.findItem(R.id.menu_test_print).isVisible = false
            binding.menu.menu.findItem(R.id.menu_fin_ops).isVisible = false
        }
        else{
            binding.menu.menu.findItem(R.id.menu_settings).isVisible = true
            binding.menu.menu.findItem(R.id.menu_kkt_info).isVisible = true
            binding.menu.menu.findItem(R.id.menu_fn_info).isVisible = true
            binding.menu.menu.findItem(R.id.menu_reg_kkt).isVisible = true
            binding.menu.menu.findItem(R.id.menu_cheques).isVisible = true
            binding.menu.menu.findItem(R.id.menu_reports).isVisible = true
            binding.menu.menu.findItem(R.id.menu_cashiers).isVisible = true
            binding.menu.menu.findItem(R.id.menu_km_check).isVisible = true
            binding.menu.menu.findItem(R.id.menu_service).isVisible = true
            binding.menu.menu.findItem(R.id.menu_test_print).isVisible = true
            binding.menu.menu.findItem(R.id.menu_fin_ops).isVisible = true
        }
    }

    fun openFragment(fragment: Fragment, title: String){
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        setCheckedMenuItem(title)
        supportActionBar?.title = title
        currentFragmentTag = title
    }
}
package ru.neva.drivers.ui.settings_fragment

import android.graphics.fonts.Font
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentSettingsBinding
import ru.neva.drivers.databinding.FragmentTcpIpBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.connection_fragment.ConnectionFragment
import ru.neva.drivers.ui.connection_fragment.com_vcom_fragment.ComVcomFragment
import ru.neva.drivers.ui.connection_fragment.tcp_ip_fragment.TcpIpFragment
import ru.neva.drivers.ui.km_check_fragment.KmCheckFragment
import ru.neva.drivers.ui.main_fragment.MainFragment
import ru.neva.drivers.ui.settings_fragment.cliche_settings_fragment.ClicheSettingsFragment
import ru.neva.drivers.ui.settings_fragment.connection_settings_fragment.ConnectionSettingsFragment
import ru.neva.drivers.ui.settings_fragment.diagnostic_settings_fragment.DiagnosticSettingsFragment
import ru.neva.drivers.ui.settings_fragment.font_settings_fragment.FontSettingsFragment
import ru.neva.drivers.ui.settings_fragment.km_settings_fragment.KmSettingsFragment
import ru.neva.drivers.ui.settings_fragment.main_settings_fragment.MainSettingsFragment
import ru.neva.drivers.ui.settings_fragment.ofd_settings_fragment.OfdSettingsFragment
import ru.neva.drivers.ui.settings_fragment.payments_settings_fragment.PaymentsSettingsFragment
import ru.neva.drivers.ui.settings_fragment.print_settings_fragment.PrintSettingsFragment
import ru.neva.drivers.ui.settings_fragment.reports_settings_fragment.ReportsSettingsFragment
import ru.neva.drivers.ui.settings_fragment.sections_settings_fragment.SectionsSettingsFragment
import ru.neva.drivers.ui.settings_fragment.service_docs_settings_fragment.ServiceDocsSettingsFragment
import ru.neva.drivers.ui.settings_fragment.users_settings_fragment.UsersSettingsFragment

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var viewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FAB : FloatingActionButton
    private lateinit var mainSettings : TextView
    private lateinit var connectionSettings : TextView
    private lateinit var paymentsSettings : TextView
    private lateinit var printSettings : TextView
    private lateinit var reportsSettings : TextView
    private lateinit var fontSettings : TextView
    private lateinit var usersSettings : TextView
    private lateinit var clicheSettings : TextView
    private lateinit var serviceDocsSettings : TextView
    private lateinit var ofdSettings : TextView
    private lateinit var sectionsSettings : TextView
    private lateinit var diagnosticSettings : TextView
    private lateinit var kmSettings : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        this.FAB = _binding!!.fab
        this.mainSettings = _binding!!.tvMainSettings
        this.connectionSettings = _binding!!.tvConnetionSettings
        this.paymentsSettings = _binding!!.tvPaymentsSettings
        this.printSettings = _binding!!.tvPrintSettings
        this.reportsSettings = _binding!!.tvReportsSettings
        this.fontSettings = _binding!!.tvFontSettings
        this.usersSettings = _binding!!.tvUsersSettings
        this.clicheSettings = _binding!!.tvClicheSettings
        this.serviceDocsSettings = _binding!!.tvServiceDocsSettings
        this.ofdSettings = _binding!!.tvOfdSettings
        this.sectionsSettings = _binding!!.tvSectionsSettings
        this.diagnosticSettings = _binding!!.tvDiagnosticSettings
        this.kmSettings = _binding!!.tvKmSettings


        mainSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(MainSettingsFragment(), "1 - Основные")
        })

        connectionSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(ConnectionSettingsFragment(), "2 - Канал связи")
        })

        paymentsSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(PaymentsSettingsFragment(), "3 - Оплаты")
        })

        printSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(PrintSettingsFragment(), "5 - Печать")
        })

        reportsSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(ReportsSettingsFragment(), "6 - Настройки отчётов")
        })

        fontSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(FontSettingsFragment(), "7 - Шрифт")
        })

        usersSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(UsersSettingsFragment(), "8 - Пользователи и пароли")
        })

        clicheSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(ClicheSettingsFragment(), "9 - Клише")
        })

        serviceDocsSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(ServiceDocsSettingsFragment(), "10 - Служебные документы")
        })

        ofdSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(OfdSettingsFragment(), "15 - ОФД")
        })

        sectionsSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(SectionsSettingsFragment(), "18 - Секции")
        })

        diagnosticSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(DiagnosticSettingsFragment(), "19 - Диагностика")
        })

        kmSettings.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(KmSettingsFragment(), "26 - Работа с КМ")
        })

        FAB.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(MainFragment(), "NevaTools")
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
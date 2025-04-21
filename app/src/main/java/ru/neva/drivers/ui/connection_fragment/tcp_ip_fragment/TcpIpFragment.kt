package ru.neva.drivers.ui.connection_fragment.tcp_ip_fragment

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.neva.drivers.databinding.FragmentTcpIpBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.connection_fragment.ConnectionFragment
import ru.neva.drivers.utils.Utils

class TcpIpFragment : Fragment() {

    companion object {
        fun newInstance() = TcpIpFragment()
    }

    private lateinit var viewModel: TcpIpViewModel
    private var _binding: FragmentTcpIpBinding? = null
    private val binding get() = _binding!!

    private lateinit var FAB : FloatingActionButton

    private lateinit var kktIP: EditText
    private lateinit var kktPort: EditText

    private var kktIPVal: String = ""
    private var kktPortVal: String = "1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTcpIpBinding.inflate(inflater, container, false)

        this.FAB = _binding!!.fab

        this.kktIP = _binding!!.etKktIp
        this.kktPort = _binding!!.etKktPort

        val tcpIpPrefs = requireContext().getSharedPreferences("tcpIpPrefs", Context.MODE_PRIVATE)

        kktIPVal = tcpIpPrefs.getString("kktIPVal", "").toString()
        kktIP.text = Editable.Factory.getInstance().newEditable(kktIPVal)
        if(kktIP.text.toString() == ""){
            kktIP.error = "Поле не должно быть пустым"
        }

        kktPortVal = tcpIpPrefs.getString("kktPortVal", "").toString()
        kktPort.text = Editable.Factory.getInstance().newEditable(kktPortVal)

        kktIP.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                kktIPVal = s.toString()
                if(kktIPVal == ""){
                    kktIP.error = "Поле не должно быть пустым"
                    return
                }
                if (!Utils.isValidIpAddress(kktIPVal)) {
                    kktIP.error = "Введите корректный IP-адрес"
                    return
                }
            }
        })

        kktPort.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    kktPortVal = "1"
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                kktPortVal = num.toString()

                if (num < 1 || num > 65535) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        kktPortVal = "1"
                    } else {
                        s.replace(0, s.length, "65535")
                        kktPortVal = "65535"
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        FAB.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(ConnectionFragment(), "Подключение к ККТ")
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val tcpIpPrefs = requireContext().getSharedPreferences("tcpIpPrefs", Context.MODE_PRIVATE)

        with(tcpIpPrefs.edit()){
            putString("kktIPVal", kktIPVal)
            putString("kktPortVal", kktPortVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TcpIpViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
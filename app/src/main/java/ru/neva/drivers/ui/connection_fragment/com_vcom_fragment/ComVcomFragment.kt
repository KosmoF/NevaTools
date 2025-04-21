package ru.neva.drivers.ui.connection_fragment.com_vcom_fragment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentComVcomBinding
import ru.neva.drivers.databinding.FragmentTcpIpBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.connection_fragment.ConnectionFragment

class ComVcomFragment : Fragment() {

    companion object {
        fun newInstance() = ComVcomFragment()
    }

    private lateinit var viewModel: ComVcomViewModel
    private var _binding: FragmentComVcomBinding? = null
    private val binding get() = _binding!!

    private lateinit var FAB : FloatingActionButton

    private lateinit var kktCom: EditText
    private lateinit var kktBaudrate: Spinner

    private var kktComVal: String = "1"
    private var kktBaudrateVal: Int = 1200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComVcomBinding.inflate(inflater, container, false)

        this.FAB = _binding!!.fab

        this.kktCom = _binding!!.etKktCom
        this.kktBaudrate = _binding!!.spKktBaudrate

        val kktBaudrates = arrayOf(1200,2400,4800,9600,19200,38400,57600,115200,230400,460800,921600)

        val kktBaudratesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kktBaudrates)
        kktBaudrate.adapter = kktBaudratesAdapter
        kktBaudrate.setSelection(0)

        val comVcomPrefs = requireContext().getSharedPreferences("comVcomPrefs", Context.MODE_PRIVATE)

        kktComVal = comVcomPrefs.getString("kktComVal", "").toString()
        kktCom.text = Editable.Factory.getInstance().newEditable(kktComVal)

        kktBaudrateVal = comVcomPrefs.getInt("kktBaudrateVal", 1200)
        when(kktBaudrateVal){
            1200 -> kktBaudrate.setSelection(0)
            2400 -> kktBaudrate.setSelection(1)
            4800 -> kktBaudrate.setSelection(2)
            9600 -> kktBaudrate.setSelection(3)
            19200 -> kktBaudrate.setSelection(4)
            38400 -> kktBaudrate.setSelection(5)
            57600 -> kktBaudrate.setSelection(6)
            115200 -> kktBaudrate.setSelection(7)
            230400 -> kktBaudrate.setSelection(8)
            460800 -> kktBaudrate.setSelection(9)
            921600 -> kktBaudrate.setSelection(10)
        }

        kktCom.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    kktComVal = "1"
                    s?.replace(0, s.length, "1")
                    return
                }

                val num = s.toString().toInt()
                kktComVal = num.toString()

                if (num < 1 || num > 255) {
                    if (num < 1) {
                        s.replace(0, s.length, "1")
                        kktComVal = "1"
                    } else {
                        s.replace(0, s.length, "255")
                        kktComVal = "255"
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        kktBaudrate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                kktBaudrateVal = parent?.getItemAtPosition(position).toString().toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        FAB.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(ConnectionFragment(), "Подключение к ККТ")
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val comVcomPrefs = requireContext().getSharedPreferences("comVcomPrefs", Context.MODE_PRIVATE)

        with(comVcomPrefs.edit()){
            putString("kktComVal", kktComVal)
            putInt("kktBaudrateVal", kktBaudrateVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ComVcomViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
package ru.neva.drivers.ui.reg_fragment.rereg_reasons_fragment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.neva.drivers.R
import ru.neva.drivers.databinding.FragmentComVcomBinding
import ru.neva.drivers.databinding.FragmentReregReasonsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.connection_fragment.ConnectionFragment
import ru.neva.drivers.ui.reg_fragment.RegFragment

class ReregReasonsFragment : Fragment() {

    companion object {
        fun newInstance() = ReregReasonsFragment()
    }

    private lateinit var viewModel: ReregReasonsViewModel
    private var _binding: FragmentReregReasonsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FAB : FloatingActionButton
    
    private lateinit var fnChange: CheckBox
    private lateinit var ofdChange: CheckBox
    private lateinit var userChange: CheckBox
    private lateinit var addressChange: CheckBox
    private lateinit var offlineOff: CheckBox
    private lateinit var offlineOn: CheckBox
    private lateinit var kktChange: CheckBox
    private lateinit var snoChange: CheckBox
    private lateinit var machineChange: CheckBox
    private lateinit var autoOff: CheckBox
    private lateinit var autoOn: CheckBox
    private lateinit var bsoOff: CheckBox
    private lateinit var bsoOn: CheckBox
    private lateinit var internetOff: CheckBox
    private lateinit var internetOn: CheckBox
    private lateinit var agentOff: CheckBox
    private lateinit var agentOn: CheckBox
    private lateinit var gamblingOff: CheckBox
    private lateinit var gamblingOn: CheckBox
    private lateinit var lotteryOff: CheckBox
    private lateinit var lotteryOn: CheckBox
    private lateinit var ffdChange: CheckBox
    private lateinit var other: CheckBox

    private var fnChangeVal: Int = 0
    private var ofdChangeVal: Int = 0
    private var userChangeVal: Int = 0
    private var addressChangeVal: Int = 0
    private var offlineOffVal: Int = 0
    private var offlineOnVal: Int = 0
    private var kktChangeVal: Int = 0
    private var snoChangeVal: Int = 0
    private var machineChangeVal: Int = 0
    private var autoOffVal: Int = 0
    private var autoOnVal: Int = 0
    private var bsoOffVal: Int = 0
    private var bsoOnVal: Int = 0
    private var internetOffVal: Int = 0
    private var internetOnVal: Int = 0
    private var agentOffVal: Int = 0
    private var agentOnVal: Int = 0
    private var gamblingOffVal: Int = 0
    private var gamblingOnVal: Int = 0
    private var lotteryOffVal: Int = 0
    private var lotteryOnVal: Int = 0
    private var ffdChangeVal: Int = 0
    private var otherVal: Int = 0

    private var reregReason: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReregReasonsBinding.inflate(inflater, container, false)

        this.FAB = _binding!!.fab

        this.fnChange = _binding!!.cbFnChange
        this.ofdChange = _binding!!.cbOfdChange
        this.userChange = _binding!!.cbUserChange
        this.addressChange = _binding!!.cbAddressChange
        this.offlineOff = _binding!!.cbOfflineOff
        this.offlineOn = _binding!!.cbOfflineOn
        this.kktChange = _binding!!.cbKktChange
        this.snoChange = _binding!!.cbSnoChange
        this.machineChange = _binding!!.cbMachineChange
        this.autoOff = _binding!!.cbAutoOff
        this.autoOn = _binding!!.cbAutoOn
        this.bsoOff = _binding!!.cbBsoOff
        this.bsoOn = _binding!!.cbBsoOn
        this.internetOff = _binding!!.cbInternetOff
        this.internetOn = _binding!!.cbInternetOn
        this.agentOff = _binding!!.cbAgentOff
        this.agentOn = _binding!!.cbAgentOn
        this.gamblingOff = _binding!!.cbGamblingOff
        this.gamblingOn = _binding!!.cbGamblingOn
        this.lotteryOff = _binding!!.cbLotteryOff
        this.lotteryOn = _binding!!.cbLotteryOn
        this.ffdChange = _binding!!.cbFfdChange
        this.other = _binding!!.cbOther

        val reregPrefs = requireContext().getSharedPreferences("reregPrefs", Context.MODE_PRIVATE)

        reregReason = reregPrefs.getLong("reregReason", 0)

        fnChangeVal = reregPrefs.getInt("fnChangeVal", 0)
        when(fnChangeVal){
            1 -> fnChange.isChecked = true
            0 -> fnChange.isChecked = false
        }

        ofdChangeVal = reregPrefs.getInt("ofdChangeVal", 0)
        when(ofdChangeVal){
            1 -> ofdChange.isChecked = true
            0 -> ofdChange.isChecked = false
        }

        userChangeVal = reregPrefs.getInt("userChangeVal", 0)
        when(userChangeVal){
            1 -> userChange.isChecked = true
            0 -> userChange.isChecked = false
        }

        addressChangeVal = reregPrefs.getInt("addressChangeVal", 0)
        when(addressChangeVal){
            1 -> addressChange.isChecked = true
            0 -> addressChange.isChecked = false
        }

        offlineOffVal = reregPrefs.getInt("offlineOffVal", 0)
        when(offlineOffVal){
            1 -> offlineOff.isChecked = true
            0 -> offlineOff.isChecked = false
        }

        offlineOnVal = reregPrefs.getInt("offlineOnVal", 0)
        when(offlineOnVal){
            1 -> offlineOn.isChecked = true
            0 -> offlineOn.isChecked = false
        }

        kktChangeVal = reregPrefs.getInt("kktChangeVal", 0)
        when(kktChangeVal){
            1 -> kktChange.isChecked = true
            0 -> kktChange.isChecked = false
        }

        snoChangeVal = reregPrefs.getInt("snoChangeVal", 0)
        when(snoChangeVal){
            1 -> snoChange.isChecked = true
            0 -> snoChange.isChecked = false
        }

        machineChangeVal = reregPrefs.getInt("machineChangeVal", 0)
        when(machineChangeVal){
            1 -> machineChange.isChecked = true
            0 -> machineChange.isChecked = false
        }

        autoOffVal = reregPrefs.getInt("autoOffVal", 0)
        when(autoOffVal){
            1 -> autoOff.isChecked = true
            0 -> autoOff.isChecked = false
        }

        autoOnVal = reregPrefs.getInt("autoOnVal", 0)
        when(autoOnVal){
            1 -> autoOn.isChecked = true
            0 -> autoOn.isChecked = false
        }

        bsoOffVal = reregPrefs.getInt("bsoOffVal", 0)
        when(bsoOffVal){
            1 -> bsoOff.isChecked = true
            0 -> bsoOff.isChecked = false
        }

        bsoOnVal = reregPrefs.getInt("bsoOnVal", 0)
        when(bsoOnVal){
            1 -> bsoOn.isChecked = true
            0 -> bsoOn.isChecked = false
        }

        internetOffVal = reregPrefs.getInt("internetOffVal", 0)
        when(internetOffVal){
            1 -> internetOff.isChecked = true
            0 -> internetOff.isChecked = false
        }

        internetOnVal = reregPrefs.getInt("internetOnVal", 0)
        when(internetOnVal){
            1 -> internetOn.isChecked = true
            0 -> internetOn.isChecked = false
        }

        agentOffVal = reregPrefs.getInt("agentOffVal", 0)
        when(agentOffVal){
            1 -> agentOff.isChecked = true
            0 -> agentOff.isChecked = false
        }

        agentOnVal = reregPrefs.getInt("agentOnVal", 0)
        when(agentOnVal){
            1 -> agentOn.isChecked = true
            0 -> agentOn.isChecked = false
        }

        gamblingOffVal = reregPrefs.getInt("gamblingOffVal", 0)
        when(gamblingOffVal){
            1 -> gamblingOff.isChecked = true
            0 -> gamblingOff.isChecked = false
        }

        gamblingOnVal = reregPrefs.getInt("gamblingOnVal", 0)
        when(gamblingOnVal){
            1 -> gamblingOn.isChecked = true
            0 -> gamblingOn.isChecked = false
        }

        lotteryOffVal = reregPrefs.getInt("lotteryOffVal", 0)
        when(lotteryOffVal){
            1 -> lotteryOff.isChecked = true
            0 -> lotteryOff.isChecked = false
        }

        lotteryOnVal = reregPrefs.getInt("lotteryOnVal", 0)
        when(lotteryOnVal){
            1 -> lotteryOn.isChecked = true
            0 -> lotteryOn.isChecked = false
        }

        ffdChangeVal = reregPrefs.getInt("ffdChangeVal", 0)
        when(ffdChangeVal){
            1 -> ffdChange.isChecked = true
            0 -> ffdChange.isChecked = false
        }

        otherVal = reregPrefs.getInt("otherVal", 0)
        when(otherVal){
            1 -> other.isChecked = true
            0 -> other.isChecked = false
        }

        FAB.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(RegFragment(), "Регистрация ККТ")
        })

        fnChange.setOnClickListener(View.OnClickListener { view ->
            when(fnChange.isChecked){
                true -> {reregReason += 1
                    fnChangeVal = 1}
                false -> {reregReason -= 1
                    fnChangeVal = 0}
            }
        })

        ofdChange.setOnClickListener(View.OnClickListener { view ->
            when(ofdChange.isChecked){
                true -> {reregReason += 2
                    ofdChangeVal = 1}
                false -> {reregReason -= 2
                    ofdChangeVal = 0}
            }
        })

        userChange.setOnClickListener(View.OnClickListener { view ->
            when(userChange.isChecked){
                true -> {reregReason += 4
                    userChangeVal = 1}
                false -> {reregReason -= 4
                    userChangeVal = 0}
            }
        })

        addressChange.setOnClickListener(View.OnClickListener { view ->
            when(addressChange.isChecked){
                true -> {reregReason += 8
                    addressChangeVal = 1}
                false -> {reregReason -= 8
                    addressChangeVal = 0}
            }
        })

        offlineOff.setOnClickListener(View.OnClickListener { view ->
            when(offlineOff.isChecked){
                true -> {reregReason += 16
                    offlineOffVal = 1}
                false -> {reregReason -= 16
                    offlineOffVal = 0}
            }
        })

        offlineOn.setOnClickListener(View.OnClickListener { view ->
            when(offlineOn.isChecked){
                true -> {reregReason += 32
                    offlineOnVal = 1}
                false -> {reregReason -= 32
                    offlineOnVal = 0}
            }
        })

        kktChange.setOnClickListener(View.OnClickListener { view ->
            when(kktChange.isChecked){
                true -> {reregReason += 64
                    kktChangeVal = 1}
                false -> {reregReason -= 64
                    kktChangeVal = 0}
            }
        })

        snoChange.setOnClickListener(View.OnClickListener { view ->
            when(snoChange.isChecked){
                true -> {reregReason += 128
                    snoChangeVal = 1}
                false -> {reregReason -= 128
                    snoChangeVal = 0}
            }
        })

        machineChange.setOnClickListener(View.OnClickListener { view ->
            when(machineChange.isChecked){
                true -> {reregReason += 256
                    machineChangeVal = 1}
                false -> {reregReason -= 256
                    machineChangeVal = 0}
            }
        })

        autoOff.setOnClickListener(View.OnClickListener { view ->
            when(autoOff.isChecked){
                true -> {reregReason += 512
                    autoOffVal = 1}
                false -> {reregReason -= 512
                    autoOffVal = 0}
            }
        })

        autoOn.setOnClickListener(View.OnClickListener { view ->
            when(autoOn.isChecked){
                true -> {reregReason += 1024
                    autoOnVal = 1}
                false -> {reregReason -= 1024
                    autoOnVal = 0}
            }
        })

        bsoOff.setOnClickListener(View.OnClickListener { view ->
            when(bsoOff.isChecked){
                true -> {reregReason += 2048
                    bsoOffVal = 1}
                false -> {reregReason -= 2048
                    bsoOffVal = 0}
            }
        })

        bsoOn.setOnClickListener(View.OnClickListener { view ->
            when(bsoOn.isChecked){
                true -> {reregReason += 4096
                    bsoOnVal = 1}
                false -> {reregReason -= 4096
                    bsoOnVal = 0}
            }
        })

        internetOff.setOnClickListener(View.OnClickListener { view ->
            when(internetOff.isChecked){
                true -> {reregReason += 8192
                    internetOffVal = 1}
                false -> {reregReason -= 8192
                    internetOffVal = 0}
            }
        })

        internetOn.setOnClickListener(View.OnClickListener { view ->
            when(internetOn.isChecked){
                true -> {reregReason += 16384
                    internetOnVal = 1}
                false -> {reregReason -= 16384
                    internetOnVal = 0}
            }
        })

        agentOff.setOnClickListener(View.OnClickListener { view ->
            when(agentOff.isChecked){
                true -> {reregReason += 32768
                    agentOffVal = 1}
                false -> {reregReason -= 32768
                    agentOffVal = 0}
            }
        })

        agentOn.setOnClickListener(View.OnClickListener { view ->
            when(agentOn.isChecked){
                true -> {reregReason += 65536
                    agentOnVal = 1}
                false -> {reregReason -= 65536
                    agentOnVal = 0}
            }
        })

        gamblingOff.setOnClickListener(View.OnClickListener { view ->
            when(gamblingOff.isChecked){
                true -> {reregReason += 131072
                    gamblingOffVal = 1}
                false -> {reregReason -= 131072
                    gamblingOffVal = 0}
            }
        })

        gamblingOn.setOnClickListener(View.OnClickListener { view ->
            when(gamblingOn.isChecked){
                true -> {reregReason += 262144
                    gamblingOnVal = 1}
                false -> {reregReason -= 262144
                    gamblingOnVal = 0}
            }
        })

        lotteryOff.setOnClickListener(View.OnClickListener { view ->
            when(lotteryOff.isChecked){
                true -> {reregReason += 524288
                    lotteryOffVal = 1}
                false -> {reregReason -= 524288
                    lotteryOffVal = 0}
            }
        })

        lotteryOn.setOnClickListener(View.OnClickListener { view ->
            when(lotteryOn.isChecked){
                true -> {reregReason += 1048576
                    lotteryOnVal = 1}
                false -> {reregReason -= 1048576
                    lotteryOnVal = 0}
            }
        })

        ffdChange.setOnClickListener(View.OnClickListener { view ->
            when(ffdChange.isChecked){
                true -> {reregReason += 2097152
                    ffdChangeVal = 1}
                false -> {reregReason -= 2097152
                    ffdChangeVal = 0}
            }
        })

        other.setOnClickListener(View.OnClickListener { view ->
            when(other.isChecked){
                true -> {reregReason += 2147483648
                    otherVal = 1}
                false -> {reregReason -= 2147483648
                    otherVal = 0}
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val reregPrefs = requireContext().getSharedPreferences("reregPrefs", Context.MODE_PRIVATE)

        with(reregPrefs.edit()){
            putInt("fnChangeVal", fnChangeVal)
            putInt("ofdChangeVal", ofdChangeVal)
            putInt("userChangeVal", userChangeVal)
            putInt("addressChangeVal", addressChangeVal)
            putInt("offlineOffVal", offlineOffVal)
            putInt("offlineOnVal", offlineOnVal)
            putInt("kktChangeVal", kktChangeVal)
            putInt("snoChangeVal", snoChangeVal)
            putInt("machineChangeVal", machineChangeVal)
            putInt("autoOffVal", autoOffVal)
            putInt("autoOnVal", autoOnVal)
            putInt("bsoOffVal", bsoOffVal)
            putInt("bsoOnVal", bsoOnVal)
            putInt("internetOffVal", internetOffVal)
            putInt("internetOnVal", internetOnVal)
            putInt("agentOffVal", agentOffVal)
            putInt("agentOnVal", agentOnVal)
            putInt("gamblingOffVal", gamblingOffVal)
            putInt("gamblingOnVal", gamblingOnVal)
            putInt("lotteryOffVal", lotteryOffVal)
            putInt("lotteryOnVal", lotteryOnVal)
            putInt("ffdChangeVal", ffdChangeVal)
            putInt("otherVal", otherVal)
            putLong("reregReason", reregReason)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReregReasonsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
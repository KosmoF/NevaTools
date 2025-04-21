package ru.neva.drivers.ui.reg_fragment

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
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.databinding.FragmentRegBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.reg_fragment.rereg_reasons_fragment.ReregReasonsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class RegFragment : Fragment() {

    companion object {
        fun newInstance() = RegFragment()
    }

    private lateinit var viewModel: RegViewModel
    private var _binding: FragmentRegBinding? = null
    private val binding get() = _binding!!

    private lateinit var regNumber: TextView
    private lateinit var reregReason: TextView

    private lateinit var orgName: EditText
    private lateinit var orgINN: EditText
    private lateinit var orgAddress: EditText
    private lateinit var orgEmail: EditText

    private lateinit var osnTax: CheckBox
    private lateinit var usn1Tax: CheckBox
    private lateinit var usn2Tax: CheckBox
    private lateinit var eshnTax: CheckBox
    private lateinit var patentTax: CheckBox
    private lateinit var defaultTax: Spinner

    private lateinit var offlineMode: CheckBox
    private lateinit var autoMode: CheckBox
    private lateinit var machineNumber: EditText
    private lateinit var dataEncryption: CheckBox
    private lateinit var machineInstallation: CheckBox
    private lateinit var internetPaymentsOnly: CheckBox
    private lateinit var markedGoods: CheckBox
    private lateinit var gamblingMode: CheckBox
    private lateinit var pawnBusiness: CheckBox
    private lateinit var vendingMachine: CheckBox
    private lateinit var wholesaleTrade: CheckBox
    private lateinit var bsoOnly: CheckBox
    private lateinit var exciseDuty: CheckBox
    private lateinit var servicePayment: CheckBox
    private lateinit var lotteryMode: CheckBox
    private lateinit var insuranceBusiness: CheckBox
    private lateinit var cateringService: CheckBox
    private lateinit var ffdVersion: Spinner
    private lateinit var fnsAddress: EditText

    private lateinit var ofdName: EditText
    private lateinit var ofdINN: EditText

    private lateinit var regNumberButton: Button
    private lateinit var regButton: Button
    private lateinit var reregButton: Button

    private var regNumberVal: String = ""
    private var reregReasonVal: Long = 0

    private var orgNameVal: String = ""
    private var orgINNVal: String = ""
    private var orgAddressVal: String = ""
    private var orgEmailVal: String = ""

    private var osnTaxVal: Int = 1
    private var usn1TaxVal: Int = 0
    private var usn2TaxVal: Int = 0
    private var eshnTaxVal: Int = 0
    private var patentTaxVal: Int = 0
    private var defaultTaxVal: Int = 0

    private var offlineModeVal: Int = 0
    private var autoModeVal: Int = 0
    private var machineNumberVal: String = "1"
    private var dataEncryptionVal: Int = 0

    private var machineInstallationVal: Int = 0
    private var internetPaymentsOnlyVal: Int = 0
    private var markedGoodsVal: Int = 0
    private var gamblingModeVal: Int = 0
    private var pawnBusinessVal: Int = 0
    private var vendingMachineVal: Int = 0
    private var wholesaleTradeVal: Int = 0
    private var bsoOnlyVal: Int = 0
    private var exciseDutyVal: Int = 0
    private var servicePaymentVal: Int = 0
    private var lotteryModeVal: Int = 0
    private var insuranceBusinessVal: Int = 0
    private var cateringServiceVal: Int = 0
    private var ffdVersionVal: Int = 1
    private var fnsAddressVal: String = ""

    private var ofdNameVal: String = ""
    private var ofdINNVal: String = ""
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegBinding.inflate(inflater, container, false)

        this.regNumber = _binding!!.etRegNumber
        this.reregReason = _binding!!.etReregReason

        this.orgName = _binding!!.etOrgName
        this.orgINN = _binding!!.etOrgInn
        this.orgAddress = _binding!!.etOrgAddress
        this.orgEmail = _binding!!.etOrgEmail

        this.osnTax = _binding!!.cbOsnTax
        this.usn1Tax = _binding!!.cbUsn1Tax
        this.usn2Tax = _binding!!.cbUsn2Tax
        this.eshnTax = _binding!!.cbEshnTax
        this.patentTax = _binding!!.cbPatentTax
        this.defaultTax = _binding!!.spDefaultTax

        this.offlineMode = _binding!!.cbOfflineMode
        this.autoMode = _binding!!.cbAutoMode
        this.machineNumber = _binding!!.etMachineNumber
        this.dataEncryption = _binding!!.cbDataEncryption

        this.machineInstallation = _binding!!.cbMachineInstallation
        this.internetPaymentsOnly = _binding!!.cbInternetPaymentsOnly
        this.markedGoods = _binding!!.cbMarkedGoods
        this.gamblingMode = _binding!!.cbGamblingMode
        this.pawnBusiness = _binding!!.cbPawnBusiness
        this.vendingMachine = _binding!!.cbVendingMachine
        this.wholesaleTrade = _binding!!.cbWholesaleTrade
        this.bsoOnly = _binding!!.cbBsoOnly
        this.exciseDuty = _binding!!.cbExciseDuty
        this.servicePayment = _binding!!.cbServicePayment
        this.lotteryMode = _binding!!.cbLotteryMode
        this.insuranceBusiness = _binding!!.cbInsuranceBusiness
        this.cateringService = _binding!!.cbCateringService
        this.ffdVersion = _binding!!.spFfdVersion
        this.fnsAddress = _binding!!.etFnsAddress

        this.ofdName = _binding!!.etOfdName
        this.ofdINN = _binding!!.etOfdInn

        this.regNumberButton = _binding!!.regNumberButton
        this.regButton = _binding!!.regButton
        this.reregButton = _binding!!.reregButton

        val defaultTaxes = arrayOf("ОСН",
            "УСН доход",
            "УСН доход - расход",
            "ЕСХН",
            "Патент")

        val ffdVersions = arrayOf("1.1",
            "1.2")

        machineNumber.isEnabled = false

        val defaultTaxAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultTaxes)
        defaultTax.adapter = defaultTaxAdapter
        defaultTax.setSelection(0)

        val ffdVersionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ffdVersions)
        ffdVersion.adapter = ffdVersionAdapter
        ffdVersion.setSelection(1)
        
        val reregPrefs =  requireContext().getSharedPreferences("reregPrefs", Context.MODE_PRIVATE)

        reregReasonVal = reregPrefs.getLong("reregReason", 0)
        reregReason.text = reregReasonVal.toString()

        val regPrefs = requireContext().getSharedPreferences("regPrefs", Context.MODE_PRIVATE)

        regNumberVal = regPrefs.getString("regNumber", "").toString()
        regNumber.text = Editable.Factory.getInstance().newEditable(regNumberVal)

        orgNameVal = regPrefs.getString("orgNameVal", "").toString()
        orgName.text = Editable.Factory.getInstance().newEditable(orgNameVal)

        orgINNVal = regPrefs.getString("orgINNVal", "").toString()
        orgINN.text = Editable.Factory.getInstance().newEditable(orgINNVal)

        orgAddressVal = regPrefs.getString("orgAddressVal", "").toString()
        orgAddress.text = Editable.Factory.getInstance().newEditable(orgAddressVal)

        orgEmailVal = regPrefs.getString("orgEmailVal", "").toString()
        orgEmail.text = Editable.Factory.getInstance().newEditable(orgEmailVal)

        osnTaxVal = regPrefs.getInt("osnTaxVal", 1)
        when(osnTaxVal){
            1 -> osnTax.isChecked = true
            0 -> osnTax.isChecked = false
        }

        usn1TaxVal = regPrefs.getInt("usn1TaxVal", 0)
        when(usn1TaxVal){
            2 -> usn1Tax.isChecked = true
            0 -> usn1Tax.isChecked = false
        }

        usn2TaxVal = regPrefs.getInt("usn2TaxVal", 0)
        when(usn2TaxVal){
            4 -> usn2Tax.isChecked = true
            0 -> usn2Tax.isChecked = false
        }

        eshnTaxVal = regPrefs.getInt("eshnTaxVal", 0)
        when(eshnTaxVal){
            16 -> eshnTax.isChecked = true
            0 -> eshnTax.isChecked = false
        }

        patentTaxVal = regPrefs.getInt("patentTaxVal", 0)
        when(patentTaxVal){
            32 -> patentTax.isChecked = true
            0 -> patentTax.isChecked = false
        }

        defaultTaxVal = regPrefs.getInt("defaultTaxVal", 0)
        when(defaultTaxVal){
            1 -> defaultTax.setSelection(0)
            2 -> defaultTax.setSelection(1)
            4 -> defaultTax.setSelection(2)
            16 -> defaultTax.setSelection(3)
            32-> defaultTax.setSelection(4)
        }

        offlineModeVal = regPrefs.getInt("offlineModeVal", 0)
        when(offlineModeVal){
            1 -> offlineMode.isChecked = true
            0 -> offlineMode.isChecked = false
        }

        autoModeVal = regPrefs.getInt("autoModeVal", 0)
        when(autoModeVal){
            1 -> autoMode.isChecked = true
            0 -> autoMode.isChecked = false
        }

        machineNumberVal = regPrefs.getString("machineNumberVal", "").toString()
        machineNumber.text = Editable.Factory.getInstance().newEditable(machineNumberVal)

        dataEncryptionVal = regPrefs.getInt("dataEncryptionVal", 0)
        when(dataEncryptionVal){
            1 -> dataEncryption.isChecked = true
            0 -> dataEncryption.isChecked = false
        }

        machineInstallationVal = regPrefs.getInt("machineInstallationVal", 0)
        when(machineInstallationVal){
            1 -> machineInstallation.isChecked = true
            0 -> machineInstallation.isChecked = false
        }

        internetPaymentsOnlyVal = regPrefs.getInt("internetPaymentsOnlyVal", 0)
        when(internetPaymentsOnlyVal){
            1 -> internetPaymentsOnly.isChecked = true
            0 -> internetPaymentsOnly.isChecked = false
        }

        markedGoodsVal = regPrefs.getInt("markedGoodsVal", 0)
        when(markedGoodsVal){
            1 -> markedGoods.isChecked = true
            0 -> markedGoods.isChecked = false
        }

        gamblingModeVal = regPrefs.getInt("gamblingModeVal", 0)
        when(gamblingModeVal){
            1 -> gamblingMode.isChecked = true
            0 -> gamblingMode.isChecked = false
        }

        pawnBusinessVal = regPrefs.getInt("pawnBusinessVal", 0)
        when(pawnBusinessVal){
            1 -> pawnBusiness.isChecked = true
            0 -> pawnBusiness.isChecked = false
        }

        vendingMachineVal = regPrefs.getInt("vendingMachineVal", 0)
        when(vendingMachineVal){
            1 -> vendingMachine.isChecked = true
            0 -> vendingMachine.isChecked = false
        }

        wholesaleTradeVal = regPrefs.getInt("wholesaleTradeVal", 0)
        when(wholesaleTradeVal){
            1 -> wholesaleTrade.isChecked = true
            0 -> wholesaleTrade.isChecked = false
        }

        bsoOnlyVal = regPrefs.getInt("bsoOnlyVal", 0)
        when(bsoOnlyVal){
            1 -> bsoOnly.isChecked = true
            0 -> bsoOnly.isChecked = false
        }

        exciseDutyVal = regPrefs.getInt("exciseDutyVal", 0)
        when(exciseDutyVal){
            1 -> exciseDuty.isChecked = true
            0 -> exciseDuty.isChecked = false
        }

        servicePaymentVal = regPrefs.getInt("servicePaymentVal", 0)
        when(servicePaymentVal){
            1 -> servicePayment.isChecked = true
            0 -> servicePayment.isChecked = false
        }

        lotteryModeVal = regPrefs.getInt("lotteryModeVal", 0)
        when(lotteryModeVal){
            1 -> lotteryMode.isChecked = true
            0 -> lotteryMode.isChecked = false
        }

        insuranceBusinessVal = regPrefs.getInt("insuranceBusinessVal", 0)
        when(insuranceBusinessVal){
            1 -> insuranceBusiness.isChecked = true
            0 -> insuranceBusiness.isChecked = false
        }

        cateringServiceVal = regPrefs.getInt("cateringServiceVal", 0)
        when(cateringServiceVal){
            1 -> cateringService.isChecked = true
            0 -> cateringService.isChecked = false
        }

        ffdVersionVal = regPrefs.getInt("ffdVersionVal", 1)
        when(ffdVersionVal){
            0 -> ffdVersion.setSelection(0)
            1 -> ffdVersion.setSelection(1)
        }

        fnsAddressVal = regPrefs.getString("fnsAddressVal", "").toString()
        fnsAddress.text = Editable.Factory.getInstance().newEditable(fnsAddressVal)

        ofdNameVal = regPrefs.getString("ofdNameVal", "").toString()
        ofdName.text = Editable.Factory.getInstance().newEditable(ofdNameVal)

        ofdINNVal = regPrefs.getString("ofdINNVal", "").toString()
        ofdINN.text = Editable.Factory.getInstance().newEditable(ofdINNVal)

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        
        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        regNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                regNumberVal = s.toString()
                if(regNumberVal == ""){
                    regNumber.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        reregReason.setOnClickListener(View.OnClickListener { view ->
            (activity as MainActivity).openFragment(ReregReasonsFragment(), "Причины перерегистрации")
        })

        orgName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                orgNameVal = s.toString()
                if(orgNameVal == ""){
                    orgName.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        orgINN.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                orgINNVal = s.toString()
                if(orgINNVal == ""){
                    orgINN.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        orgAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                orgAddressVal = s.toString()
                if(orgAddressVal == ""){
                    orgAddress.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        orgEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                orgEmailVal = s.toString()
                if(orgEmailVal == ""){
                    orgEmail.error = "Поле не должно быть пустым"
                    return
                }
                if (!Utils.isValidEmail(orgEmailVal)) {
                    orgEmail.error = "Введите корректный адрес"
                    return
                }
            }
        })

        osnTax.setOnClickListener(View.OnClickListener { view ->
            when(osnTax.isChecked){
                true -> osnTaxVal = 1
                false -> osnTaxVal = 0
            }
        })

        usn1Tax.setOnClickListener(View.OnClickListener { view ->
            when(usn1Tax.isChecked){
                true -> usn1TaxVal = 2
                false -> usn1TaxVal = 0
            }
        })

        usn2Tax.setOnClickListener(View.OnClickListener { view ->
            when(usn2Tax.isChecked){
                true -> usn2TaxVal = 4
                false -> usn2TaxVal = 0
            }
        })

        eshnTax.setOnClickListener(View.OnClickListener { view ->
            when(eshnTax.isChecked){
                true -> eshnTaxVal = 16
                false -> eshnTaxVal = 0
            }
        })

        patentTax.setOnClickListener(View.OnClickListener { view ->
            when(patentTax.isChecked){
                true -> patentTaxVal = 32
                false -> patentTaxVal = 0
            }
        })

        defaultTax.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var checkedCount = 0
                if(osnTax.isChecked){
                    checkedCount += 1
                }
                if(usn1Tax.isChecked){
                    checkedCount += 1
                }
                if(usn2Tax.isChecked){
                    checkedCount += 1
                }
                if(eshnTax.isChecked){
                    checkedCount += 1
                }
                if(patentTax.isChecked){
                    checkedCount += 1
                }
                when(position){
                    0 -> {
                        defaultTaxVal = 1
                        if(checkedCount == 1){
                        osnTax.isChecked = true
                        usn1Tax.isChecked = false
                        usn2Tax.isChecked = false
                        eshnTax.isChecked = false
                        patentTax.isChecked = false
                        }
                        else{
                            osnTax.isChecked = true
                        }
                        osnTax.isEnabled = false
                        usn1Tax.isEnabled = true
                        usn2Tax.isEnabled = true
                        eshnTax.isEnabled = true
                        patentTax.isEnabled = true
                    }

                    1 -> {
                        defaultTaxVal = 2
                        if(checkedCount == 1){
                        osnTax.isChecked = false
                        usn1Tax.isChecked = true
                        usn2Tax.isChecked = false
                        eshnTax.isChecked = false
                        patentTax.isChecked = false
                        }
                        else{
                            usn1Tax.isChecked = true
                        }
                        osnTax.isEnabled = true
                        usn1Tax.isEnabled = false
                        usn2Tax.isEnabled = true
                        eshnTax.isEnabled = true
                        patentTax.isEnabled = true
                    }
                    2 -> {
                        defaultTaxVal = 4
                        if(checkedCount == 1){
                        osnTax.isChecked = false
                        usn1Tax.isChecked = false
                        usn2Tax.isChecked = true
                        eshnTax.isChecked = false
                        patentTax.isChecked = false
                        }
                        else{
                            usn2Tax.isChecked = true
                        }
                        osnTax.isEnabled = true
                        usn1Tax.isEnabled = true
                        usn2Tax.isEnabled = false
                        eshnTax.isEnabled = true
                        patentTax.isEnabled = true
                    }
                    3 -> {
                        defaultTaxVal = 16
                        if(checkedCount == 1){
                        osnTax.isChecked = false
                        usn1Tax.isChecked = false
                        usn2Tax.isChecked = false
                        eshnTax.isChecked = true
                        patentTax.isChecked = false
                        }
                        else{
                            eshnTax.isChecked = true
                        }
                        osnTax.isEnabled = true
                        usn1Tax.isEnabled = true
                        usn2Tax.isEnabled = true
                        eshnTax.isEnabled = false
                        patentTax.isEnabled = true
                    }
                    4 -> {
                        defaultTaxVal = 32
                        if(checkedCount == 1){
                        osnTax.isChecked = false
                        usn1Tax.isChecked = false
                        usn2Tax.isChecked = false
                        eshnTax.isChecked = false
                        patentTax.isChecked = true
                        }
                        else{
                            patentTax.isChecked = true
                        }
                        osnTax.isEnabled = true
                        usn1Tax.isEnabled = true
                        usn2Tax.isEnabled = true
                        eshnTax.isEnabled = true
                        patentTax.isEnabled = false
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        offlineMode.setOnClickListener(View.OnClickListener { view ->
            when(offlineMode.isChecked){
                true -> offlineModeVal = 1
                false -> offlineModeVal = 0
            }
        })

        autoMode.setOnClickListener(View.OnClickListener { view ->
            when(autoMode.isChecked){
                true -> {autoModeVal = 1
                    machineNumber.isEnabled = true
                }
                false -> {autoModeVal = 0
                    machineNumber.isEnabled = false
                    machineNumber.setText("")
                    machineNumberVal = ""
                }
            }
        })

        machineNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                machineNumberVal = s.toString()
                if(machineNumberVal == ""){
                    machineNumber.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        dataEncryption.setOnClickListener(View.OnClickListener { view ->
            when(dataEncryption.isChecked){
                true -> dataEncryptionVal = 1
                false -> dataEncryptionVal = 0
            }
        })

        machineInstallation.setOnClickListener(View.OnClickListener { view ->
            when(machineInstallation.isChecked){
                true -> machineInstallationVal = 1
                false -> machineInstallationVal = 0
            }
        })

        internetPaymentsOnly.setOnClickListener(View.OnClickListener { view ->
            when(internetPaymentsOnly.isChecked){
                true -> internetPaymentsOnlyVal = 1
                false -> internetPaymentsOnlyVal = 0
            }
        })

        markedGoods.setOnClickListener(View.OnClickListener { view ->
            when(markedGoods.isChecked){
                true -> markedGoodsVal = 1
                false -> markedGoodsVal = 0
            }
        })

        gamblingMode.setOnClickListener(View.OnClickListener { view ->
            when(gamblingMode.isChecked){
                true -> gamblingModeVal = 1
                false -> gamblingModeVal = 0
            }
        })

        pawnBusiness.setOnClickListener(View.OnClickListener { view ->
            when(pawnBusiness.isChecked){
                true -> pawnBusinessVal = 1
                false -> pawnBusinessVal = 0
            }
        })

        vendingMachine.setOnClickListener(View.OnClickListener { view ->
            when(vendingMachine.isChecked){
                true -> vendingMachineVal = 1
                false -> vendingMachineVal = 0
            }
        })

        wholesaleTrade.setOnClickListener(View.OnClickListener { view ->
            when(wholesaleTrade.isChecked){
                true -> wholesaleTradeVal = 1
                false -> wholesaleTradeVal = 0
            }
        })

        bsoOnly.setOnClickListener(View.OnClickListener { view ->
            when(bsoOnly.isChecked){
                true -> bsoOnlyVal = 1
                false -> bsoOnlyVal = 0
            }
        })

        exciseDuty.setOnClickListener(View.OnClickListener { view ->
            when(exciseDuty.isChecked){
                true -> exciseDutyVal = 1
                false -> exciseDutyVal = 0
            }
        })

        servicePayment.setOnClickListener(View.OnClickListener { view ->
            when(servicePayment.isChecked){
                true -> servicePaymentVal = 1
                false -> servicePaymentVal = 0
            }
        })

        lotteryMode.setOnClickListener(View.OnClickListener { view ->
            when(lotteryMode.isChecked){
                true -> lotteryModeVal = 1
                false -> lotteryModeVal = 0
            }
        })

        insuranceBusiness.setOnClickListener(View.OnClickListener { view ->
            when(insuranceBusiness.isChecked){
                true -> insuranceBusinessVal = 1
                false -> insuranceBusinessVal = 0
            }
        })

        cateringService.setOnClickListener(View.OnClickListener { view ->
            when(cateringService.isChecked){
                true -> cateringServiceVal = 1
                false -> cateringServiceVal = 0
            }
        })

        ffdVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                ffdVersionVal = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        fnsAddress.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                fnsAddressVal = s.toString()
                if(fnsAddressVal == ""){
                    fnsAddress.error = "Поле не должно быть пустым"
                    return
                }
                if (!Utils.isValidURL(fnsAddressVal)) {
                    fnsAddress.error = "Введите корректный адрес"
                    return
                }
            }
        })

        ofdName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                ofdNameVal = s.toString()
                if(ofdNameVal == ""){
                    ofdName.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        ofdINN.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                ofdINNVal = s.toString()
                if(ofdINNVal == ""){
                    ofdINN.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        regNumberButton.setOnClickListener(View.OnClickListener { view ->
            val paramsString = "orgINNVal :$orgINNVal\n"
            val result = viewModel.calcRegNumber(orgINNVal)
            if(result.first != -1){
                logger.log(Logger.LogLevel.SUCCESS, "calcRegNumber", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "calcRegNumber")}}
                regNumberVal = ""
                regNumber.text = ""
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Уведомление")
                alertDialog.setMessage("Операция выполнена успешно")
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
                regNumberVal = result.second
                regNumber.text = result.second
            }
            else{
                logger.log(Logger.LogLevel.ERROR, "calcRegNumber", paramsString, result.second)
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "calcRegNumber")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result.second)
                alertDialog.setPositiveButton("OK") { dialog, which ->
                    // Действие при нажатии на кнопку
                }
                alertDialog.show()
            }
        })

        regButton.setOnClickListener(View.OnClickListener { view ->
            if(regNumberVal == "" ||
                orgNameVal == "" ||
                orgINNVal == "" ||
                orgAddressVal == "" ||
                !Utils.isValidEmail(orgEmailVal) ||
                !Utils.isValidURL(fnsAddressVal) ||
                ofdNameVal == "" ||
                ofdINNVal == "" ||
                (autoModeVal == 1 && machineNumberVal == "")){
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Некоторые данные не введены или введены неверно. Проверьте данные и повторите попытку")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                val paramsString = "regNumberVal :$regNumberVal\n" +
                        "orgNameVal :$orgNameVal\n" +
                        "orgINNVal :$orgINNVal\n" +
                        "orgAddressVal :$orgAddressVal\n" +
                        "orgEmailVal :$orgEmailVal\n" +
                        "osnTaxVal :$osnTaxVal\n" +
                        "usn1TaxVal :$usn1TaxVal\n" +
                        "usn2TaxVal :$usn2TaxVal\n" +
                        "eshnTaxVal :$eshnTaxVal\n" +
                        "patentTaxVal :$patentTaxVal\n" +
                        "offlineModeVal :$offlineModeVal\n" +
                        "autoModeVal :$autoModeVal\n" +
                        "machineNumberVal :$machineNumberVal\n" +
                        "dataEncryptionVal :$dataEncryptionVal\n" +
                        "machineInstallationVal :$machineInstallationVal\n" +
                        "internetPaymentsOnlyVal :$internetPaymentsOnlyVal\n" +
                        "markedGoodsVal :$markedGoodsVal\n" +
                        "gamblingModeVal :$gamblingModeVal\n" +
                        "pawnBusinessVal :$pawnBusinessVal\n" +
                        "vendingMachineVal :$vendingMachineVal\n" +
                        "wholesaleTradeVal :$wholesaleTradeVal\n" +
                        "bsoOnlyVal :$bsoOnlyVal\n" +
                        "exciseDutyVal :$exciseDutyVal\n" +
                        "servicePaymentVal :$servicePaymentVal\n" +
                        "lotteryModeVal :$lotteryModeVal\n" +
                        "insuranceBusinessVal :$insuranceBusinessVal\n" +
                        "cateringServiceVal: $cateringServiceVal\n" +
                        "ffdVersionVal :$ffdVersionVal\n" +
                        "fnsAddressVal :$fnsAddressVal\n" +
                        "ofdNameVal :$ofdNameVal\n" +
                        "ofdINNVal :$ofdINNVal\n"
                val result = viewModel.kktRegistration(regNumberVal,
                    orgNameVal,
                    orgINNVal,
                    orgAddressVal,
                    orgEmailVal,
                    osnTaxVal,
                    usn1TaxVal,
                    usn2TaxVal,
                    eshnTaxVal,
                    patentTaxVal,
                    offlineModeVal,
                    autoModeVal,
                    machineNumberVal,
                    dataEncryptionVal,
                    machineInstallationVal,
                    internetPaymentsOnlyVal,
                    markedGoodsVal,
                    gamblingModeVal,
                    pawnBusinessVal,
                    vendingMachineVal,
                    wholesaleTradeVal,
                    bsoOnlyVal,
                    exciseDutyVal,
                    servicePaymentVal,
                    lotteryModeVal,
                    insuranceBusinessVal,
                    cateringServiceVal,
                    ffdVersionVal,
                    fnsAddressVal,
                    ofdNameVal,
                    ofdINNVal)
                if(result == ""){
                    logger.log(Logger.LogLevel.SUCCESS, "kktRegistration", paramsString, "0")
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "kktRegistration")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Уведомление")
                    alertDialog.setMessage("Операция выполнена успешно")
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        // Действие при нажатии на кнопку
                    }
                    alertDialog.show()
                }
                else{
                    logger.log(Logger.LogLevel.ERROR, "kktRegistration", paramsString, result)
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    val errorCode = viewModel.getErrorCode()
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "kktRegistration")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Ошибка")
                    alertDialog.setMessage(result)
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        // Действие при нажатии на кнопку
                    }
                    alertDialog.show()
                }
            }
        })

        reregButton.setOnClickListener(View.OnClickListener { view ->
            if(regNumberVal == "" ||
                reregReasonVal <= 0 ||
                orgNameVal == "" ||
                orgINNVal == "" ||
                orgAddressVal == "" ||
                !Utils.isValidEmail(orgEmailVal) ||
                !Utils.isValidURL(fnsAddressVal) ||
                ofdNameVal == "" ||
                ofdINNVal == "" ||
                (autoModeVal == 1 && machineNumberVal == "")){
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Некоторые данные не введены или введены неверно. Проверьте данные и повторите попытку")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                val paramsString = "regNumberVal :$regNumberVal\n" +
                        "reregReasonVal :$reregReasonVal\n" +
                        "orgNameVal :$orgNameVal\n" +
                        "orgINNVal :$orgINNVal\n" +
                        "orgAddressVal :$orgAddressVal\n" +
                        "orgEmailVal :$orgEmailVal\n" +
                        "osnTaxVal :$osnTaxVal\n" +
                        "usn1TaxVal :$usn1TaxVal\n" +
                        "usn2TaxVal :$usn2TaxVal\n" +
                        "eshnTaxVal :$eshnTaxVal\n" +
                        "patentTaxVal :$patentTaxVal\n" +
                        "offlineModeVal :$offlineModeVal\n" +
                        "autoModeVal :$autoModeVal\n" +
                        "machineNumberVal :$machineNumberVal\n" +
                        "dataEncryptionVal :$dataEncryptionVal\n" +
                        "machineInstallationVal :$machineInstallationVal\n" +
                        "internetPaymentsOnlyVal :$internetPaymentsOnlyVal\n" +
                        "markedGoodsVal :$markedGoodsVal\n" +
                        "gamblingModeVal :$gamblingModeVal\n" +
                        "pawnBusinessVal :$pawnBusinessVal\n" +
                        "vendingMachineVal :$vendingMachineVal\n" +
                        "wholesaleTradeVal :$wholesaleTradeVal\n" +
                        "bsoOnlyVal :$bsoOnlyVal\n" +
                        "exciseDutyVal :$exciseDutyVal\n" +
                        "servicePaymentVal :$servicePaymentVal\n" +
                        "lotteryModeVal :$lotteryModeVal\n" +
                        "insuranceBusinessVal :$insuranceBusinessVal\n" +
                        "cateringServiceVal: $cateringServiceVal\n" +
                        "ffdVersionVal :$ffdVersionVal\n" +
                        "fnsAddressVal :$fnsAddressVal\n" +
                        "ofdNameVal :$ofdNameVal\n" +
                        "ofdINNVal :$ofdINNVal\n"
                val result = viewModel.kktReRegistration(regNumberVal,
                    reregReasonVal,
                    orgNameVal,
                    orgINNVal,
                    orgAddressVal,
                    orgEmailVal,
                    osnTaxVal,
                    usn1TaxVal,
                    usn2TaxVal,
                    eshnTaxVal,
                    patentTaxVal,
                    offlineModeVal,
                    autoModeVal,
                    machineNumberVal,
                    dataEncryptionVal,
                    machineInstallationVal,
                    internetPaymentsOnlyVal,
                    markedGoodsVal,
                    gamblingModeVal,
                    pawnBusinessVal,
                    vendingMachineVal,
                    wholesaleTradeVal,
                    bsoOnlyVal,
                    exciseDutyVal,
                    servicePaymentVal,
                    lotteryModeVal,
                    insuranceBusinessVal,
                    cateringServiceVal,
                    ffdVersionVal,
                    fnsAddressVal,
                    ofdNameVal,
                    ofdINNVal)
                if(result == ""){
                    logger.log(Logger.LogLevel.SUCCESS, "kktReRegistration", paramsString, "0")
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "kktReRegistration")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Уведомление")
                    alertDialog.setMessage("Операция выполнена успешно")
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        // Действие при нажатии на кнопку
                    }
                    alertDialog.show()
                }
                else{
                    logger.log(Logger.LogLevel.ERROR, "kktReRegistration", paramsString, result)
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    val errorCode = viewModel.getErrorCode()
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "kktReRegistration")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Ошибка")
                    alertDialog.setMessage(result)
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        // Действие при нажатии на кнопку
                    }
                    alertDialog.show()
                }
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val regPrefs = requireContext().getSharedPreferences("regPrefs", Context.MODE_PRIVATE)

        with(regPrefs.edit()){
            putString("regNumberVal", regNumberVal)
            putString("orgNameVal",orgNameVal)
            putString("orgINNVal", orgINNVal)
            putString("orgAddressVal", orgAddressVal)
            putString("orgEmailVal", orgEmailVal)
            putInt("osnTaxVal", osnTaxVal)
            putInt("usn1TaxVal", usn1TaxVal)
            putInt("usn2TaxVal", usn2TaxVal)
            putInt("eshnTaxVal", eshnTaxVal)
            putInt("patentTaxVal", patentTaxVal)
            putInt("defaultTaxVal", defaultTaxVal)
            putInt("offlineModeVal", offlineModeVal)
            putInt("autoModeVal", autoModeVal)
            putString("machineNumberVal", machineNumberVal)
            putInt("dataEncryptionVal", dataEncryptionVal)
            putInt("machineInstallationVal", machineInstallationVal)
            putInt("internetPaymentsOnlyVal", internetPaymentsOnlyVal)
            putInt("markedGoodsVal", markedGoodsVal)
            putInt("gamblingModeVal", gamblingModeVal)
            putInt("pawnBusinessVal", pawnBusinessVal)
            putInt("vendingMachineVal", vendingMachineVal)
            putInt("wholesaleTradeVal", wholesaleTradeVal)
            putInt("bsoOnlyVal", bsoOnlyVal)
            putInt("exciseDutyVal", exciseDutyVal)
            putInt("servicePaymentVal", servicePaymentVal)
            putInt("lotteryModeVal", lotteryModeVal)
            putInt("insuranceBusinessVal", insuranceBusinessVal)
            putInt("cateringServiceVal", cateringServiceVal)
            putInt("ffdVersionVal", ffdVersionVal)
            putString("fnsAddressVal", fnsAddressVal)
            putString("ofdNameVal", ofdNameVal)
            putString("ofdINNVal", ofdINNVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
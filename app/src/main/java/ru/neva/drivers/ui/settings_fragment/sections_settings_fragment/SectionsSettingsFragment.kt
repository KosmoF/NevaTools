package ru.neva.drivers.ui.settings_fragment.sections_settings_fragment

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
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.neva.drivers.databinding.FragmentSectionsSettingsBinding
import ru.neva.drivers.ui.MainActivity
import ru.neva.drivers.ui.settings_fragment.SettingsFragment
import ru.neva.drivers.utils.Logger
import ru.neva.drivers.utils.Requests
import ru.neva.drivers.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class SectionsSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SectionsSettingsFragment()
    }

    private lateinit var viewModel: SectionsSettingsViewModel
    private var _binding: FragmentSectionsSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var FABSave : FloatingActionButton
    private lateinit var FABLoad : FloatingActionButton
    
    private lateinit var section1Name: EditText
    private lateinit var section1Tax: Spinner
    private lateinit var section2Name: EditText
    private lateinit var section2Tax: Spinner
    private lateinit var section3Name: EditText
    private lateinit var section3Tax: Spinner
    private lateinit var section4Name: EditText
    private lateinit var section4Tax: Spinner
    private lateinit var section5Name: EditText
    private lateinit var section5Tax: Spinner

    private var section1NameVal: String = ""
    private var section1TaxVal: Int = 1
    private var section2NameVal: String = ""
    private var section2TaxVal: Int = 1
    private var section3NameVal: String = ""
    private var section3TaxVal: Int = 1
    private var section4NameVal: String = ""
    private var section4TaxVal: Int = 1
    private var section5NameVal: String = ""
    private var section5TaxVal: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSectionsSettingsBinding.inflate(inflater, container, false)

        this.FABSave = _binding!!.fabSave
        this.FABLoad = _binding!!.fabLoad

        this.section1Name = _binding!!.etSection1Name
        this.section1Tax = _binding!!.spSection1Tax
        this.section2Name = _binding!!.etSection2Name
        this.section2Tax = _binding!!.spSection2Tax
        this.section3Name = _binding!!.etSection3Name
        this.section3Tax = _binding!!.spSection3Tax
        this.section4Name = _binding!!.etSection4Name
        this.section4Tax = _binding!!.spSection4Tax
        this.section5Name = _binding!!.etSection5Name
        this.section5Tax = _binding!!.spSection5Tax

        val sectionTaxes = arrayOf(1,2,3,4,5,6)

        val section1TaxAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sectionTaxes)
        section1Tax.adapter = section1TaxAdapter
        section1Tax.setSelection(0)

        val section2TaxAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sectionTaxes)
        section2Tax.adapter = section2TaxAdapter
        section2Tax.setSelection(0)

        val section3TaxAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sectionTaxes)
        section3Tax.adapter = section3TaxAdapter
        section3Tax.setSelection(0)

        val section4TaxAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sectionTaxes)
        section4Tax.adapter = section4TaxAdapter
        section4Tax.setSelection(0)

        val section5TaxAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sectionTaxes)
        section5Tax.adapter = section5TaxAdapter
        section5Tax.setSelection(0)

        val sectionsSettingsPrefs = requireContext().getSharedPreferences("sectionsSettingsPrefs", Context.MODE_PRIVATE)
        
        section1NameVal = sectionsSettingsPrefs.getString("section1NameVal", "").toString()
        section1Name.text = Editable.Factory.getInstance().newEditable(section1NameVal)
        
        section1TaxVal = sectionsSettingsPrefs.getInt("section1TaxVal", 1)
        when(section1TaxVal){
            1 -> section1Tax.setSelection(0)
            2 -> section1Tax.setSelection(1)
            3 -> section1Tax.setSelection(2)
            4 -> section1Tax.setSelection(3)
            5 -> section1Tax.setSelection(4)
            6 -> section1Tax.setSelection(5)
        }

        section2NameVal = sectionsSettingsPrefs.getString("section2NameVal", "").toString()
        section2Name.text = Editable.Factory.getInstance().newEditable(section2NameVal)

        section2TaxVal = sectionsSettingsPrefs.getInt("section2TaxVal", 1)
        when(section2TaxVal){
            1 -> section2Tax.setSelection(0)
            2 -> section2Tax.setSelection(1)
            3 -> section2Tax.setSelection(2)
            4 -> section2Tax.setSelection(3)
            5 -> section2Tax.setSelection(4)
            6 -> section2Tax.setSelection(5)
        }

        section3NameVal = sectionsSettingsPrefs.getString("section3NameVal", "").toString()
        section3Name.text = Editable.Factory.getInstance().newEditable(section3NameVal)

        section3TaxVal = sectionsSettingsPrefs.getInt("section3TaxVal", 1)
        when(section3TaxVal){
            1 -> section3Tax.setSelection(0)
            2 -> section3Tax.setSelection(1)
            3 -> section3Tax.setSelection(2)
            4 -> section3Tax.setSelection(3)
            5 -> section3Tax.setSelection(4)
            6 -> section3Tax.setSelection(5)
        }

        section4NameVal = sectionsSettingsPrefs.getString("section4NameVal", "").toString()
        section4Name.text = Editable.Factory.getInstance().newEditable(section4NameVal)

        section4TaxVal = sectionsSettingsPrefs.getInt("section4TaxVal", 1)
        when(section4TaxVal){
            1 -> section4Tax.setSelection(0)
            2 -> section4Tax.setSelection(1)
            3 -> section4Tax.setSelection(2)
            4 -> section4Tax.setSelection(3)
            5 -> section4Tax.setSelection(4)
            6 -> section4Tax.setSelection(5)
        }

        section5NameVal = sectionsSettingsPrefs.getString("section5NameVal", "").toString()
        section5Name.text = Editable.Factory.getInstance().newEditable(section5NameVal)

        section5TaxVal = sectionsSettingsPrefs.getInt("section5TaxVal", 1)
        when(section5TaxVal){
            1 -> section5Tax.setSelection(0)
            2 -> section5Tax.setSelection(1)
            3 -> section5Tax.setSelection(2)
            4 -> section5Tax.setSelection(3)
            5 -> section5Tax.setSelection(4)
            6 -> section5Tax.setSelection(5)
        }

        val logger = Logger(requireContext())

        val dbSDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val prefs = requireContext().getSharedPreferences("connectionPrefs", Context.MODE_PRIVATE)
        val URL = prefs.getString("serverURLVal", "").toString()
        val username = prefs.getString("serverUsernameVal", "").toString()
        val password = prefs.getString("serverPasswordVal", "").toString()
        val currentkktSerial = prefs.getString("currentKKTSerial", "").toString()

        section1Name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                section1NameVal = s.toString()
                if(section1NameVal == ""){
                    section1Name.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        section1Tax.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> section1TaxVal = 1
                    1 -> section1TaxVal = 2
                    2 -> section1TaxVal = 3
                    3 -> section1TaxVal = 4
                    4 -> section1TaxVal = 5
                    5 -> section1TaxVal = 6
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        section2Name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                section2NameVal = s.toString()
                if(section2NameVal == ""){
                    section2Name.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        section2Tax.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> section2TaxVal = 1
                    1 -> section2TaxVal = 2
                    2 -> section2TaxVal = 3
                    3 -> section2TaxVal = 4
                    4 -> section2TaxVal = 5
                    5 -> section2TaxVal = 6
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        section3Name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                section3NameVal = s.toString()
                if(section3NameVal == ""){
                    section3Name.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        section3Tax.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> section3TaxVal = 1
                    1 -> section3TaxVal = 2
                    2 -> section3TaxVal = 3
                    3 -> section3TaxVal = 4
                    4 -> section3TaxVal = 5
                    5 -> section3TaxVal = 6
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        section4Name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                section4NameVal = s.toString()
                if(section4NameVal == ""){
                    section4Name.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        section4Tax.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> section4TaxVal = 1
                    1 -> section4TaxVal = 2
                    2 -> section4TaxVal = 3
                    3 -> section4TaxVal = 4
                    4 -> section4TaxVal = 5
                    5 -> section4TaxVal = 6
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        section5Name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                section5NameVal = s.toString()
                if(section5NameVal == ""){
                    section5Name.error = "Поле не должно быть пустым"
                    return
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        section5Tax.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> section5TaxVal = 1
                    1 -> section5TaxVal = 2
                    2 -> section5TaxVal = 3
                    3 -> section5TaxVal = 4
                    4 -> section5TaxVal = 5
                    5 -> section5TaxVal = 6
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        FABSave.setOnClickListener(View.OnClickListener { view ->
            if (section1NameVal == "" ||
                section2NameVal == "" ||
                section3NameVal == "" ||
                section4NameVal == "" ||
                section5NameVal == ""){
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage("Некоторые данные не введены или введены неверно. Проверьте данные и повторите попытку")
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                val paramsString = "section1NameVal :$section1NameVal\n" +
                        "section1TaxVal :$section1TaxVal\n" +
                        "section2NameVal :$section2NameVal\n" +
                        "section2TaxVal :$section2TaxVal\n" +
                        "section3NameVal :$section3NameVal\n" +
                        "section3TaxVal :$section3TaxVal\n" +
                        "section4NameVal :$section4NameVal\n" +
                        "section4TaxVal :$section4TaxVal\n" +
                        "section5NameVal :$section5NameVal\n" +
                        "section5TaxVal :$section5TaxVal\n"
                val result = viewModel.setSectionsSettings(section1NameVal,
                    section1TaxVal,
                    section2NameVal,
                    section2TaxVal,
                    section3NameVal,
                    section3TaxVal,
                    section4NameVal,
                    section4TaxVal,
                    section5NameVal,
                    section5TaxVal)
                if(result == ""){
                    logger.log(Logger.LogLevel.SUCCESS, "setSectionsSettings", paramsString, "0")
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    runBlocking { withContext(Dispatchers.IO) { Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "setSectionsSettings")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Уведомление")
                    alertDialog.setMessage("Операция выполнена успешно")
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        (activity as MainActivity).openFragment(SettingsFragment(), "Настройки ККТ")
                    }
                    alertDialog.show()
                }
                else{
                    logger.log(Logger.LogLevel.ERROR, "setSectionsSettings", paramsString, result)
                    val timestampString = dbSDF.format(Calendar.getInstance().time)
                    val errorCode = viewModel.getErrorCode()
                    runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "setSectionsSettings")}}
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Ошибка")
                    alertDialog.setMessage(result)
                    alertDialog.setPositiveButton("OK") { dialog, which ->

                    }
                    alertDialog.show()
                }
            }
        })

        FABLoad.setOnClickListener(View.OnClickListener { view ->
            val paramsString = ""
            val result = viewModel.getSectionsSettings()
            if(result.size == 1){
                logger.log(Logger.LogLevel.ERROR, "getSectionsSettings", paramsString, result[0].toString())
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                val errorCode = viewModel.getErrorCode()
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, errorCode, currentkktSerial, timestampString, "getSectionsSettings")}}
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Ошибка")
                alertDialog.setMessage(result[0].toString())
                alertDialog.setPositiveButton("OK") { dialog, which ->

                }
                alertDialog.show()
            }
            else{
                logger.log(Logger.LogLevel.SUCCESS, "getSectionsSettings", paramsString, "0")
                val timestampString = dbSDF.format(Calendar.getInstance().time)
                runBlocking { withContext(Dispatchers.IO) {Requests.writeLog(URL, username, password, null, currentkktSerial, timestampString, "getSectionsSettings")}}
                section1NameVal = result[0].toString()
                section1Name.text = Editable.Factory.getInstance().newEditable(section1NameVal)

                section1TaxVal = result[1].toString().toInt()
                when(section1TaxVal){
                    1 -> section1Tax.setSelection(0)
                    2 -> section1Tax.setSelection(1)
                    3 -> section1Tax.setSelection(2)
                    4 -> section1Tax.setSelection(3)
                    5 -> section1Tax.setSelection(4)
                    6 -> section1Tax.setSelection(5)
                }

                section2NameVal = result[2].toString()
                section2Name.text = Editable.Factory.getInstance().newEditable(section2NameVal)

                section2TaxVal = result[3].toString().toInt()
                when(section2TaxVal){
                    1 -> section2Tax.setSelection(0)
                    2 -> section2Tax.setSelection(1)
                    3 -> section2Tax.setSelection(2)
                    4 -> section2Tax.setSelection(3)
                    5 -> section2Tax.setSelection(4)
                    6 -> section2Tax.setSelection(5)
                }

                section3NameVal = result[4].toString()
                section3Name.text = Editable.Factory.getInstance().newEditable(section3NameVal)

                section3TaxVal = result[5].toString().toInt()
                when(section3TaxVal){
                    1 -> section3Tax.setSelection(0)
                    2 -> section3Tax.setSelection(1)
                    3 -> section3Tax.setSelection(2)
                    4 -> section3Tax.setSelection(3)
                    5 -> section3Tax.setSelection(4)
                    6 -> section3Tax.setSelection(5)
                }

                section4NameVal = result[6].toString()
                section4Name.text = Editable.Factory.getInstance().newEditable(section4NameVal)

                section4TaxVal = result[7].toString().toInt()
                when(section4TaxVal){
                    1 -> section4Tax.setSelection(0)
                    2 -> section4Tax.setSelection(1)
                    3 -> section4Tax.setSelection(2)
                    4 -> section4Tax.setSelection(3)
                    5 -> section4Tax.setSelection(4)
                    6 -> section4Tax.setSelection(5)
                }

                section5NameVal = result[8].toString()
                section5Name.text = Editable.Factory.getInstance().newEditable(section5NameVal)

                section5TaxVal = result[9].toString().toInt()
                when(section5TaxVal){
                    1 -> section5Tax.setSelection(0)
                    2 -> section5Tax.setSelection(1)
                    3 -> section5Tax.setSelection(2)
                    4 -> section5Tax.setSelection(3)
                    5 -> section5Tax.setSelection(4)
                    6 -> section5Tax.setSelection(5)
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

        val sectionsSettingsPrefs = requireContext().getSharedPreferences("sectionsSettingsPrefs", Context.MODE_PRIVATE)

        with(sectionsSettingsPrefs.edit()){
            putString("section1NameVal", section1NameVal)
            putInt("section1TaxVal", section1TaxVal)
            putString("section2NameVal", section2NameVal)
            putInt("section2TaxVal", section2TaxVal)
            putString("section3NameVal", section3NameVal)
            putInt("section3TaxVal", section3TaxVal)
            putString("section4NameVal", section4NameVal)
            putInt("section4TaxVal", section4TaxVal)
            putString("section5NameVal", section5NameVal)
            putInt("section5TaxVal", section5TaxVal)
            apply()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SectionsSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
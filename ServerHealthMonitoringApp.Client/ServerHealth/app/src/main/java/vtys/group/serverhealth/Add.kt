import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import vtys.group.serverhealth.R
import vtys.group.serverhealth.service.impl.CreateService
import vtys.group.serverhealth.adapter.CityAdapter
import vtys.group.serverhealth.model.CityDataModel
import vtys.group.serverhealth.adapter.HospitalAdapter
import vtys.group.serverhealth.fileops.ServerDataManager
import vtys.group.serverhealth.model.HospitalDataModel
import vtys.group.serverhealth.model.ServerDataModelWithIntHospitalId
import vtys.group.serverhealth.service.impl.RetrofitService


class Add : Fragment() {
    private lateinit var createService: CreateService
    private lateinit var firstFrameLayout: FrameLayout
    private lateinit var secondFrameLayout: FrameLayout
    private lateinit var addServerButton: Button
    private lateinit var addHospitalButton: Button
    private lateinit var editTextServerName: EditText
    private lateinit var editTextServerIP: EditText
    private lateinit var radioGroupServerOS: RadioGroup
    private lateinit var radioGroupServerStorageType: RadioGroup
    private lateinit var editTextServerRAM: EditText
    private lateinit var editTextServerStorageCapacity: EditText
    private lateinit var hospitalSpinner: Spinner
    private lateinit var hospitalSaveButton: Button
    private lateinit var editTextHospitalName: EditText
    private lateinit var citySpinner: Spinner
    private lateinit var serverSaveButton: Button
    private lateinit var serverImportButton: Button
    private lateinit var hospitalImportButton: Button
    private val FILE_TYPE_KEY = "fileType"
    private var isContentOneVisible = true
    private lateinit var serverDataManager: ServerDataManager

    private lateinit var serverFileLauncher: ActivityResultLauncher<String>
    private lateinit var hospitalFileLauncher: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        val retrofitService = RetrofitService()

        firstFrameLayout = view.findViewById(R.id.first_frame_layout)
        secondFrameLayout = view.findViewById(R.id.second_frame_layout)
        addServerButton = view.findViewById(R.id.add_server_button)
        addHospitalButton = view.findViewById(R.id.add_hospital_button)
        editTextServerName = view.findViewById(R.id.editTextServerName)
        editTextServerIP = view.findViewById(R.id.editTextServerIP)
        radioGroupServerOS = view.findViewById(R.id.radioGroupServerOS)
        radioGroupServerStorageType = view.findViewById(R.id.radioGroupServerStorageType)
        editTextServerRAM = view.findViewById(R.id.editTextServerRAM)
        editTextServerStorageCapacity = view.findViewById(R.id.editTextServerStorageCapacity)
        hospitalSpinner = view.findViewById(R.id.hospitalSpinner)
        hospitalSaveButton = view.findViewById(R.id.hospital_save_button)
        editTextHospitalName = view.findViewById(R.id.editTextHospitalName)
        citySpinner = view.findViewById(R.id.citySpinner)
        serverSaveButton = view.findViewById(R.id.server_save_button)
        serverImportButton = view.findViewById(R.id.serverImportButton)
        hospitalImportButton = view.findViewById(R.id.hospitalImportButton)




        addServerButton.setOnClickListener {
            displayContent(true)
        }

        addHospitalButton.setOnClickListener {
            displayContent(false)
        }

        serverSaveButton.setOnClickListener {

            var selectedOS = ""
            var selectedStorageType = ""

            val serverName = editTextServerName.text.toString()
            val serverIP = editTextServerIP.text.toString()
            val selectedRadioButtonId = radioGroupServerOS.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = view.findViewById<RadioButton>(selectedRadioButtonId)
                selectedOS = selectedRadioButton.text.toString()
            } else {
                // Handle the case when no radio button is selected
            }

            val selectedStorageTypeId = radioGroupServerStorageType.checkedRadioButtonId
            if (selectedStorageTypeId != -1) {
                val selectedStorageTypeRadioButton =
                    view.findViewById<RadioButton>(selectedStorageTypeId)
                selectedStorageType = selectedStorageTypeRadioButton.text.toString()
            } else {
                // Handle the case when no storage type radio button is selected
            }

            val serverRAM = editTextServerRAM.text.toString()
            val serverStorageCapacity = editTextServerStorageCapacity.text.toString()
            val selectedHospital = hospitalSpinner.selectedItem as HospitalDataModel

            // Create a ServerDataModel object with the selected hospital name and server name
            // Get the selected hospital's ID
            val serverData = ServerDataModelWithIntHospitalId(
                0,
                serverName,
                serverIP,
                selectedOS,
                serverRAM,
                selectedStorageType,
                serverStorageCapacity,
                selectedHospital.hospitalid
            )

            // Call the createServer function from the CreateService interface to make the API request
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = createService.addServer(serverData).execute()

                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Server added successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Server could not be added",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Handle exceptions
                    e.printStackTrace()
                }
            }
        }

        hospitalSaveButton.setOnClickListener {
            val hospitalName = editTextHospitalName.text.toString()
            val selectedCity = citySpinner.selectedItem as CityDataModel

            // Create a HospitalDataModel object with the selected city name and hospital name
            val cityData = CityDataModel(selectedCity.cityid, selectedCity.cityname)
            val hospitalData = HospitalDataModel(0, hospitalName, cityData)

            // Call the createHospital function from the CreateService interface to make the API request
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = createService.addHospital(hospitalData).execute()

                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Hospital added successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Handle unsuccessful response
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Handle exceptions
                    e.printStackTrace()
                }
            }
        }

        // Retrofit objesini oluşturun ve ApiService'yi initialize edin
        val retrofit = retrofitService.getRetrofit()
        createService = retrofit.create(CreateService::class.java)

        serverDataManager = ServerDataManager(createService, requireContext())


        serverFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            handleFileSelection(uri, "server")
        }

        hospitalFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            handleFileSelection(uri, "hospital")
        }

        serverImportButton.setOnClickListener {
            serverFileLauncher.launch("*/*")
        }

        hospitalImportButton.setOnClickListener {
            hospitalFileLauncher.launch("*/*")
        }




        fetchCities()
        fetchHospitals()

        return view
    }

    private fun handleFileSelection(uri: Uri?, fileType: String) {
        CoroutineScope(Dispatchers.IO).launch {
            uri?.let {
                if (fileType == "server") {
                    serverDataManager.importServersFromCSV(it)
                } else {
                    serverDataManager.importHospitalsFromCSV(it)
                }
            }
        }
    }

    private fun displayContent(isFirstContentVisible: Boolean) {
        if (isFirstContentVisible) {
            firstFrameLayout.visibility = View.VISIBLE
            secondFrameLayout.visibility = View.GONE
        } else {
            firstFrameLayout.visibility = View.GONE
            secondFrameLayout.visibility = View.VISIBLE
        }
    }

    private fun fetchHospitals() {
        createService.getHospitals().enqueue(object : retrofit2.Callback<List<HospitalDataModel>> {
            override fun onResponse(
                call: Call<List<HospitalDataModel>>,
                response: Response<List<HospitalDataModel>>
            ) {
                if (isAdded) {
                    if (response.isSuccessful) {
                        val hospitals = response.body() ?: emptyList()

                        val adapter = HospitalAdapter(
                            requireContext().applicationContext,
                            android.R.layout.simple_spinner_item,
                            hospitals
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        val hospitalSpinner = view?.findViewById<Spinner>(R.id.hospitalSpinner)
                        hospitalSpinner?.adapter = adapter

                        hospitalSpinner?.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parentView: AdapterView<*>?,
                                    selectedItemView: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    // Handle the selected Hospital ID if needed
                                    val selectedHospitalId = adapter.getHospitalId(position)
                                }

                                override fun onNothingSelected(parentView: AdapterView<*>?) {
                                    // Handle when nothing is selected
                                }
                            }
                    } else {
                        // Handle unsuccessful response
                    }
                }
            }

            override fun onFailure(call: Call<List<HospitalDataModel>>, t: Throwable) {
                // Handle failure
                t.printStackTrace()
            }
        })
    }

    private fun fetchCities() {
        createService.getCities().enqueue(object : retrofit2.Callback<List<CityDataModel>> {
            override fun onResponse(
                call: Call<List<CityDataModel>>,
                response: Response<List<CityDataModel>>
            ) {
                if (isAdded) {
                    if (response.isSuccessful) {
                        val cities = response.body() ?: emptyList()

                        // Order the cities by cityid
                        val sortedCities = cities.sortedBy { it.cityid }

                        // Create a CityAdapter and set it to the citySpinner
                        val cityAdapter = CityAdapter(
                            requireContext().applicationContext,
                            android.R.layout.simple_spinner_item,
                            sortedCities
                        )
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        val citySpinner = view?.findViewById<Spinner>(R.id.citySpinner)
                        citySpinner?.adapter = cityAdapter

                        citySpinner?.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parentView: AdapterView<*>?,
                                    selectedItemView: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    // Handle the selected city name if needed
                                    val selectedCityName = sortedCities[position].cityname
                                }

                                override fun onNothingSelected(parentView: AdapterView<*>?) {
                                    // Handle when nothing is selected
                                }
                            }
                    } else {
                        // Handle unsuccessful response
                    }
                }
            }

            override fun onFailure(call: Call<List<CityDataModel>>, t: Throwable) {
                // Handle failure
                t.printStackTrace()
            }
        })
    }
}
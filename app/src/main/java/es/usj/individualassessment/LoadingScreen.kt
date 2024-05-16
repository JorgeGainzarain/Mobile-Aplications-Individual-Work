package es.usj.individualassessment


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import es.usj.individualassessment.databinding.ActivityLoadingScreenBinding
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.concurrent.CompletableFuture
import javax.net.ssl.HttpsURLConnection


class LoadingScreen : AppCompatActivity() {
    private val view by lazy { ActivityLoadingScreenBinding.inflate(layoutInflater) }

    private lateinit var tasks: Array<CompletableFuture<Void>>
    private lateinit var intent: Intent

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    //private var currentLocation: Location? = null

    private val cities = listOf(
        "Los Angeles",
        "New York City",
        "Rio de Janeiro",
        "Zaragoza",
        "Paris",
        "Rome",
        "Cape Town",
        "Istanbul",
        "Tokyo",
        "Sydney",
        "Buenos Aires",
        "Alofi",
        "Majuro",
        "Santa Fe",
        "Jujuy",
        "Salta",
        "Tucumán",
        "Tierra del Fuego",
        "Berlin",
        "CanBerra"
    )

    //private val apiKey = "79H5H5AZCYDV925YD497328BM"
    private val apiKey = "YSM2B6WBW6W4GYST2A5PL4PM8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.d("Debug", "LoadingScreen: onCreate started")

        setContentView(view.root)
        intent = Intent(this, MainMenu::class.java)

        tasks = arrayOf(
            fetchLocation(),
            loadCities()
        )

        // When all the tasks are finished start the MainMenu
        CompletableFuture.allOf(*tasks).thenAccept {

            // Start Main Menu and finish this activity
            intent.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(this)
                finish()
            }
        }
    }
    private fun fetchLocation(): CompletableFuture<Void> {
        Log.d("LocationDebug", "Fetching Start")

        val ret = CompletableFuture<Void>()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            requestLocationUpdates(ret)
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSIONS
            )
        }

        return ret
    }
    private fun requestLocationUpdates(ret: CompletableFuture<Void>) {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    Log.d("LocationDebug", "Fetching location Complete")
                    val latitude = it.latitude
                    val longitude = it.longitude
                    // Use latitude and longitude as needed
                    Log.d("LocationDebug", "Latitude: $latitude, Longitude: $longitude")

                    intent.putExtra("latitude", latitude)
                    intent.putExtra("longitude", longitude)

                    fusedLocationProviderClient.removeLocationUpdates(this)
                    ret.complete(null)
                }
            }
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
        else {
            Log.d("LocationDebug", "Error permisson")
        }
    }
    private fun loadCities(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {

            val directory = File(applicationContext.filesDir, "cities")

            if (directory.exists() && !directory.listFiles().isNullOrEmpty()) {

                // load the cities from the jsons to listCities
                listCities =  getCities(applicationContext)

                for(city in listCities) {
                    Log.d("LogicDebug", "Loading city ${city.name}")
                    val year = city.calendar.get(Calendar.YEAR)
                    val month = city.calendar.get(Calendar.MONTH)
                    val day = city.calendar.get(Calendar.DAY_OF_MONTH)
                    val monthString = if (month < 9) "0${month + 1}" else "${month + 1}"
                    val dayString = if (day < 10) "0$day" else "$day"
                    val dateString = "$year-$monthString-$dayString"
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val todayDate = LocalDate.parse(dateString, formatter)
                    // If we already have data from the month update the passed days, in case some days have passed.
                    // We wont update it in the same day as that would require to run the api each time we load the app
                    // So we will only update it once per day as minimum, if a few hours have passed we will keep the predictions

                    val lastHistDay = LocalDate.parse(city.lastHistoryDay.dateString, formatter)
                    // If it has passed 1 or more days since last historical (Not predicted) Day we update it
                    Log.d("LogicDebug", "$todayDate -> $lastHistDay")
                    if (todayDate.isAfter(lastHistDay)) {
                        Log.d("LogicDebug", "City ${city.name}: todayDate is After the lastHistDay")
                        val cityData = fetchWeatherForCity(city.name, lastHistDay.plusDays(1), todayDate)

                        // Add the new days data to the cities
                        city.addData(cityData)

                        // Save the modified city information in the json file
                        saveCityData(city.name, city.toJSON().toString())
                    }
                    Log.d("LogicDebug", "City ${city.name} loaded")
                    Log.d("LogicDebug", "\n")
                }
            }
            else {

                val currentDate = LocalDate.now()
                // If there's no data we load the currentMonth
                val firstDayOfMonth = currentDate.withDayOfMonth(1)
                val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth())

                directory.mkdirs()

                for (city in cities) {
                    saveCityData(city, fetchWeatherForCity(city, firstDayOfMonth, lastDayOfMonth ))
                }

                // load the cities from the jsons to listCities
                listCities = getCities(applicationContext)
            }
        }
    }
    private fun fetchWeatherForCity(city: String, startDay: LocalDate, endDay: LocalDate): String {

        Log.d("APIDebug", "LOADING FROM API ($startDay , $endDay)")
        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${city}" +
                "/${startDay}/${endDay}?unitGroup=metric&include=days&" +
                "key=${apiKey}&contentType=json"

        //val todayURL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" +
        //        "${city}/today?unitGroup=metric&include=days%2Chours&key=${apiKey}&contentType=json"



        /*
        Log.d("APIDebug", "LOADING FROM API (" + auxDay + " , " + auxDay + ")")
        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${city}" +
                "/today?unitGroup=metric&include=days&" +
                "key=${apiKey}&contentType=json"

         */
        val connection = URL(url).openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            Log.d("APIDebug", "API Loaded $responseCode")
            return connection.inputStream.bufferedReader().use { it.readText() }
        } else {
            Log.d("APIDebug", "Error loading from API $responseCode")
            throw Exception("Failed to fetch weather data for $city. Response code: $responseCode")
        }
    }
    private fun saveCityData(city: String, data: String): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            try {
                val directory = File(applicationContext.filesDir, "cities")

                val file = File(directory, "$city.json")
                val outputStream = FileOutputStream(file)
                outputStream.write(data.toByteArray())
                outputStream.close()

                Log.d("Debug:", "City: $city saved successfully")
            }
            catch (e: Error) {
                Log.d("Debug:", e.toString())
            }
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSIONS = 1001
    }

}






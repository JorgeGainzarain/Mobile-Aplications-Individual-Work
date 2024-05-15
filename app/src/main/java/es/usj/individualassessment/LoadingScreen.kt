package es.usj.individualassessment


import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import es.usj.individualassessment.Classes.City
import es.usj.individualassessment.databinding.ActivityLoadingScreenBinding
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.CompletableFuture
import javax.net.ssl.HttpsURLConnection
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date


class LoadingScreen : AppCompatActivity() {
    private val view by lazy { ActivityLoadingScreenBinding.inflate(layoutInflater) }

    private lateinit var tasks: Array<CompletableFuture<Void>>
    private lateinit var intent: Intent

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

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

    private val apiKey = "79H5H5AZCYDV925YD497328BM"
    //private val apiKey = "NADXVVDHQ4QHAGRB3TY73DQQN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.d("Debug", "LoadingScreen: onCreate started")

        setContentView(view.root)
        intent = Intent(this, MainMenu::class.java)

        tasks = arrayOf(
            //fetchLocation(),
            loadCities()
        )

        // When all the tasks are finished start the MainMenu
        CompletableFuture.allOf(*tasks).thenAccept {

            // Set the listCities reference
            listCities = getCities(applicationContext)

            // Remove location callback
            //fusedLocationProviderClient.removeLocationUpdates(locationCallback)

            // Start Main Menu and finish this activity
            intent.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(this)
                finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation(): CompletableFuture<Void> {
        val ret = CompletableFuture<Void>()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(1000)
            .setMaxUpdateDelayMillis(3000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    // Use latitude and longitude as needed
                    Log.d("LocationDebug", "Latitude: $latitude, Longitude: $longitude")

                    intent.putExtra("latitude", latitude)
                    intent.putExtra("longitude", longitude)

                    ret.complete(null)
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        return ret
    }
    private fun loadCities(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {

            val currentDate = LocalDate.now()

            val directory = File(applicationContext.filesDir, "cities")

            if (directory.exists() && !directory.listFiles().isNullOrEmpty()) {

                var auxCities: MutableList<City> = getCities(applicationContext)

                Log.d("Cities size", directory.listFiles()?.size.toString() + " -> " + auxCities.size)

                for(city in auxCities) {
                    val dateString = city.today.dateString
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val lastDate = LocalDate.parse(dateString, formatter)

                    if(currentDate.monthValue != lastDate.monthValue) {
                        // Load the new month
                        val firstDayOfMonth = lastDate.withDayOfMonth(1)
                        val lastDayOfMonth = lastDate.withDayOfMonth(lastDate.lengthOfMonth())
                        saveCityData(city.name, fetchWeatherForCity(city.name, firstDayOfMonth, lastDayOfMonth ))
                    }
                }
            }
            else {
                // If there's no data we load it all
                val firstDayOfMonth = currentDate.withDayOfMonth(1)
                val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth())

                directory.mkdirs()
                for (city in cities) {
                    saveCityData(city, fetchWeatherForCity(city, firstDayOfMonth, lastDayOfMonth ))
                }
            }
        }
    }
    private fun fetchWeatherForCity(city: String, startDay: LocalDate, endDay: LocalDate): String {
        Log.d("APIDebug", "LOADED FROM API (" + startDay + " , " + endDay + ")")
        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${city}" +
                "/${startDay}/${endDay}?unitGroup=metric&include=days%2Chours&" +
                "key=${apiKey}&contentType=json"

        val connection = URL(url).openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            return connection.inputStream.bufferedReader().use { it.readText() }
        } else {
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


}






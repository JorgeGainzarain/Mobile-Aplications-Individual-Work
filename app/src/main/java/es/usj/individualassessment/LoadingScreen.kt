package es.usj.individualassessment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import es.usj.individualassessment.databinding.ActivityLoadingScreenBinding
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.CompletableFuture
import javax.net.ssl.HttpsURLConnection


class LoadingScreen : AppCompatActivity() {
    private val view by lazy { ActivityLoadingScreenBinding.inflate(layoutInflater) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tasks: Array<CompletableFuture<Void>>
    private lateinit var intent: Intent

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


    //private val apiKey = "YSM2B6WBW6W4GYST2A5PL4PM8"
    private val apiKey = "NADXVVDHQ4QHAGRB3TY73DQQN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.d("Debug", "LoadingScreen: onCreate started")

        setContentView(view.root)

        intent = Intent(this, MainMenu::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tasks = arrayOf(
            fetchLocation(),
            //loadCities()
        )
        listCities = getCities(applicationContext)

        //Log.d("Debug", "LoadingScreen: Tasks started")

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

    private fun loadCities(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {

            Log.d("Debug", "LoadingScreen: loadCities started")

            resetDir("cities", applicationContext)

            for (city in cities) {
                val weatherData = fetchWeatherForCity(city)
                saveCitiesData(city, weatherData)
                Log.d("Debug", city + " loaded")
            }

            listCities = getCities(applicationContext)
        }
    }

    private fun fetchWeatherForCity(city: String): String {
        Log.d("Debug:", "City: $city loading")
        //val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${city}?unitGroup=metric&include=current%2Cdays%2Chours%2Calerts/${getCurrentDate()}&key=${apiKey}&contentType=json"
        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${city}/next7days?unitGroup=metric&include=current%2Cdays%2Chours&key=${apiKey}&contentType=json"

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

    private fun saveCitiesData(city: String, data: String): CompletableFuture<Void> {
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

    @SuppressLint("MissingPermission")
    private fun fetchLocation(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {

            //Log.d("Debug", "LoadingScreen: fetchLocation started")

            // Check for location permissions
            if (checkLocationPermissions()) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        // Got last known location
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude

                            intent.apply {
                                putExtra("latitude", latitude)
                                putExtra("longitude", longitude)
                            }
                        } else {
                            // Handle the situation where location is null
                            Toast.makeText(
                                this,
                                "Failed to get last known location",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
                    .addOnFailureListener { e ->
                        // Handle failure to get location
                        Toast.makeText(
                            this,
                            "Failed to get last known location: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        throw e
                    }
            } else {
                // Permissions denied, complete CompletableFuture exceptionally
                throw SecurityException("Location permissions denied")
            }

        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_LOCATION
            )
            false
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 100
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, continue with the operations
                fetchLocation()
            } else {
                // Permissions denied, show a message or handle accordingly
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

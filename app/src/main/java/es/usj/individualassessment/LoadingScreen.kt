package es.usj.individualassessment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

    private val apiKey = "YSM2B6WBW6W4GYST2A5PL4PM8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tasks = arrayOf(
            fetchLocation(),
            loadCities()
        )

        // When all the tasks are finished start the MainMenu
        CompletableFuture.allOf(*tasks).thenAccept {
            // Start Main Menu and finish this activity
            Intent(this, MainMenu::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(this)
                finish()
            }
        }
    }

    private fun loadCities(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            val cities = listOf(
                "Zaragoza",
                "Tokyo",
                "New York City",
                "Sydney",
                "Paris",
                "Cairo",
                "Cape Town",
                "Moscow",
                "Rio de Janeiro",
                "Mumbai"
            )

            for (city in cities) {
                val weatherData = fetchWeatherForCity(city)
                saveCitiesData(city, weatherData)
            }
        }
    }

    private fun fetchWeatherForCity(city: String): String {
        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$city?unitGroup=metric&include=events&key=$apiKey&contentType=json"

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
                    if (!directory.exists()) {
                        directory.mkdirs()
                    }

                    val file = File(directory, "$city.json")
                    val outputStream = FileOutputStream(file)
                    outputStream.write(data.toByteArray())
                    outputStream.close()

                    Log.d("WeatherSave:", "City: $city saved successfully")
                }
                catch (e: Error) {
                    Log.d("WeatherSave:", e.toString())
                }
            }
        }


    private fun fetchLocation(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            // Check for location permissions
            if (checkLocationPermissions()) {
                // If permissions are granted, get the last known location
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Permissions denied, complete CompletableFuture exceptionally
                    throw SecurityException("Location permissions denied")
                } else {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            // Got last known location
                            val latitude = location?.latitude ?: 0.0
                            val longitude = location?.longitude ?: 0.0
                            // Put location data into Intent
                            Intent(this, MainMenu::class.java).apply {
                                putExtra("latitude", latitude)
                                putExtra("longitude", longitude)
                                startActivity(this)
                            }
                        }
                        .addOnFailureListener { e ->
                            // Handle failure to get location
                            Toast.makeText(
                                this,
                                "Failed to get last known location: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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

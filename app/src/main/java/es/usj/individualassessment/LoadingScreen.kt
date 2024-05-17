package es.usj.individualassessment


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import es.usj.individualassessment.Classes.ListCities
import java.util.concurrent.CompletableFuture


class LoadingScreen : AppCompatActivity() {
    private val view by lazy { ActivityLoadingScreenBinding.inflate(layoutInflater) }

    private lateinit var tasks: Array<CompletableFuture<Void>>
    private lateinit var intent: Intent

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(view.root)
        view.appName.text = "Cities Weather App"

        intent = Intent(this, MainMenu::class.java)

        val listcities = ListCities(
            File(applicationContext.filesDir, "cities"),
            File(applicationContext.filesDir, "citiesCopy"),
            //true // Force load from copy
        )

        tasks = arrayOf(
            fetchLocation(),
            listcities.loadCities()
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

        // Run the location request asynchronously
        CompletableFuture.runAsync {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@LoadingScreen)

            if (ActivityCompat.checkSelfPermission(this@LoadingScreen, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this@LoadingScreen, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                requestLocationUpdates(ret)
            }
            else {
                ActivityCompat.requestPermissions(
                    this@LoadingScreen,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_LOCATION_PERMISSIONS
                )
            }
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


    companion object {
        private const val REQUEST_LOCATION_PERMISSIONS = 1001
    }

}






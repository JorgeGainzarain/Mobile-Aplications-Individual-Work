package es.usj.individualassessment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.usj.individualassessment.Classes.City
import es.usj.individualassessment.Classes.ListCities
import es.usj.individualassessment.databinding.ActivityDetailedCityBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DetailedCity : AppCompatActivity() {

    private val view by lazy { ActivityDetailedCityBinding.inflate(layoutInflater) }

    private lateinit var city: City

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        // Enable the home button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get the city index from the intent
        val cityIndex = intent.getIntExtra("CITY_INDEX", -1)
        if (cityIndex != -1) {
            city = ListCities.instance[cityIndex]
            setCityDetails()
        } else {
            // Handle the case where the index is invalid
            finish() // Close the activity if the index is invalid
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Navigate back to the CityList activity
                val intent = Intent(this, CityList::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setCityDetails() {
        val today = city.today

        val latitude = String.format("%.2f", city.latitude)
        val longitude = String.format("%.2f", city.longitude)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(city.calendar.time)

        view.textViewNameProvinceCountry.text = "${city.name}, ${city.province}, ${city.country}"
        view.location.text = "($latitude, $longitude)"
        view.Date.text = date

        val iconId = resources.getIdentifier(
            today.icon.replace("-", "_"), "drawable",
            packageName
        )
        view.weatherIcon.setImageResource(iconId) // Assuming weatherIcon is a drawable resource ID
        view.windSpeed.text = "${today.windspeed} km/h"
        view.dewValue.text = "${today.dew}"
        view.tempValue.text = "${today.temp}°"
        view.maxTemp.text = "${today.tempmax}°"
        view.minTemp.text = "${today.tempmin}°"
        view.avgWind.text = "Avg Wind\n${String.format("%.2f", city.avgWind)} km/h"
        view.avgTemp.text = "Avg Temp\n${String.format("%.2f", city.avgTemp)}°"
        view.avgDew.text = "Avg Dew\n${String.format("%.2f", city.avgDew)}°"
    }
}
